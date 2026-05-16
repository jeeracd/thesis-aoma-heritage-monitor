import argparse
import json
import math
import os
import shutil
from dataclasses import dataclass
from pathlib import Path

import numpy as np


@dataclass(frozen=True)
class OmaRunConfig:
    ssi_ordmax: int = 20
    ssi_br: int = 10
    max_modes_export: int = 8
    cmif_nsv: int = 4
    freqlim_min_hz: float | None = None
    freqlim_max_hz: float | None = None


def _ensure_dir(p: Path) -> None:
    p.mkdir(parents=True, exist_ok=True)


def _is_number(s: str) -> bool:
    try:
        float(s)
        return True
    except Exception:
        return False


def _parse_block_delimited(lines: list[str]) -> tuple[np.ndarray, list[str]]:
    header: list[str] = []
    data_values: list[list[float]] = []
    current: list[float] = []
    saw_separator = False

    for raw in lines:
        line = raw.strip()
        if not line:
            continue
        if line.startswith("########"):
            saw_separator = True
            if current:
                data_values.append(current)
                current = []
            continue
        if not saw_separator:
            header.append(line)
            continue
        if _is_number(line):
            current.append(float(line))
            continue

    if current:
        data_values.append(current)

    if not data_values:
        raise ValueError("No numeric blocks found in block-delimited CSV")

    width = max(len(r) for r in data_values)
    if width == 0:
        raise ValueError("Empty numeric blocks found in block-delimited CSV")
    fixed: list[list[float]] = []
    for r in data_values:
        if len(r) == width:
            fixed.append(r)
        else:
            fixed.append(r + [float("nan")] * (width - len(r)))

    arr = np.asarray(fixed, dtype=float)
    labels = []
    if header:
        tokens = []
        for h in header:
            tokens.extend([t for t in h.replace(",", " ").split() if t])
        labels = [t for t in tokens if t.lower() not in {"timestamp", "time"}]

    if len(labels) != width:
        labels = [f"ch{i+1}" for i in range(width)]

    finite_mask = np.isfinite(arr)
    if not finite_mask.all():
        col_means = np.nanmean(arr, axis=0)
        inds = np.where(~finite_mask)
        arr[inds] = np.take(col_means, inds[1])

    return arr, labels


def _infer_fs_from_timestamp(values: np.ndarray) -> float | None:
    if values.size < 3:
        return None
    diffs = np.diff(values)
    diffs = diffs[np.isfinite(diffs)]
    if diffs.size == 0:
        return None
    dt = float(np.median(diffs))
    if dt <= 0:
        return None
    return 1.0 / dt


def load_sensor_csv(path: Path, fs_hz: float | None = None) -> tuple[np.ndarray, float, list[str]]:
    if not path.exists() or not path.is_file():
        raise FileNotFoundError(str(path))

    text = path.read_text(encoding="utf-8", errors="replace")
    if "########" in text:
        data, labels = _parse_block_delimited(text.splitlines())
        if fs_hz is None:
            raise ValueError("Sampling rate fs_hz is required for block-delimited CSV")
        return data, float(fs_hz), labels

    import pandas as pd

    df = pd.read_csv(path)
    if df.shape[0] < 8:
        raise ValueError("Not enough samples to run OMA")

    cols = [c for c in df.columns]
    time_col = None
    for c in cols:
        if str(c).strip().lower() in {"timestamp", "time", "t"}:
            time_col = c
            break

    labels = []
    if time_col is not None:
        numeric_cols = [c for c in cols if c != time_col]
    else:
        numeric_cols = cols

    for c in numeric_cols:
        labels.append(str(c))

    data_df = df[numeric_cols].apply(pd.to_numeric, errors="coerce")
    data = data_df.to_numpy(dtype=float)

    if fs_hz is None and time_col is not None:
        t_series = df[time_col]
        t_numeric = pd.to_numeric(t_series, errors="coerce")
        if t_numeric.notna().all():
            inferred = _infer_fs_from_timestamp(t_numeric.to_numpy(dtype=float))
            if inferred is not None:
                fs_hz = inferred
        else:
            t_dt = pd.to_datetime(t_series, errors="coerce")
            if t_dt.notna().all():
                ts = t_dt.astype("int64").to_numpy(dtype=float) / 1e9
                inferred = _infer_fs_from_timestamp(ts)
                if inferred is not None:
                    fs_hz = inferred

    if fs_hz is None:
        raise ValueError("Sampling rate fs_hz could not be inferred; pass --fs")

    if not np.isfinite(data).all():
        col_means = np.nanmean(data, axis=0)
        inds = np.where(~np.isfinite(data))
        data[inds] = np.take(col_means, inds[1])

    return data, float(fs_hz), labels


def _safe_freqlim(cfg: OmaRunConfig) -> tuple[float, float] | None:
    if cfg.freqlim_min_hz is None or cfg.freqlim_max_hz is None:
        return None
    if cfg.freqlim_min_hz <= 0 or cfg.freqlim_max_hz <= 0:
        return None
    if cfg.freqlim_min_hz >= cfg.freqlim_max_hz:
        return None
    return float(cfg.freqlim_min_hz), float(cfg.freqlim_max_hz)


def run_pyoma2(data: np.ndarray, fs_hz: float, channel_labels: list[str], out_dir: Path, cfg: OmaRunConfig, source_csv: Path | None = None) -> dict[str, str]:
    import matplotlib
    matplotlib.use("Agg")
    import matplotlib.pyplot as plt

    from pyoma2.setup import SingleSetup
    from pyoma2.algorithms import FDD, FDDRunParams, SSI, SSIRunParams
    from pyoma2.functions.gen import MAC, MPC, MPD
    from pyoma2.functions.plot import CMIF_plot, plot_mac_matrix, stab_plot

    _ensure_dir(out_dir)

    raw_csv: Path | None = None
    if source_csv is not None and source_csv.exists() and source_csv.is_file():
        raw_csv = out_dir / "raw_input.csv"
        try:
            shutil.copy2(source_csv, raw_csv)
        except Exception:
            raw_csv = source_csv

    setup = SingleSetup(np.asarray(data, dtype=float), float(fs_hz))

    n = int(data.shape[0])
    ordmax = int(cfg.ssi_ordmax)
    ordmax = max(2, min(ordmax, max(2, n // 20)))
    br = int(cfg.ssi_br)
    br = max(2, min(br, max(2, n // 30)))

    ssi = SSI(SSIRunParams(ordmax=ordmax, br=br))
    fdd = FDD(FDDRunParams())
    setup.add_algorithms(ssi, fdd)
    setup.run_all()

    ssi_result = setup.algorithms[ssi.name].result
    fdd_result = setup.algorithms[fdd.name].result

    freqlim = _safe_freqlim(cfg)

    stab_png = out_dir / "stabilization.png"
    fig, ax = stab_plot(
        Fn=np.asarray(ssi_result.Fn_poles),
        Lab=np.asarray(ssi_result.Lab),
        step=int(ssi.run_params.step),
        ordmax=int(ssi.run_params.ordmax or ordmax),
        ordmin=int(ssi.run_params.ordmin),
        freqlim=freqlim,
        hide_poles=True,
        Fn_std=np.asarray(ssi_result.Fn_poles_std) if getattr(ssi_result, "Fn_poles_std", None) is not None else None,
        color_scheme="high_contrast",
    )
    fig.tight_layout()
    fig.savefig(stab_png, dpi=160)
    plt.close(fig)

    # Export raw SSI poles so the Java scatter panel can render them interactively
    poles_csv = out_dir / "stabilization_poles.csv"
    try:
        Fn_poles = np.asarray(ssi_result.Fn_poles)
        Lab = np.asarray(ssi_result.Lab)
        Xi_poles_raw = getattr(ssi_result, "Xi_poles", None)
        Xi_poles = np.asarray(Xi_poles_raw) if Xi_poles_raw is not None else None
        _step = int(getattr(ssi.run_params, "step", 1))
        _ordmin = int(getattr(ssi.run_params, "ordmin", 0))
        def _is_stable(lab) -> int:
            try:
                return 1 if int(float(str(lab))) > 0 else 0
            except (ValueError, TypeError):
                return 1 if str(lab).strip().lower() not in {"n", "u", "0", ""} else 0
        pole_rows = []
        for o_idx in range(Fn_poles.shape[0]):
            _order = _ordmin + (o_idx + 1) * _step
            for p_idx in range(Fn_poles.shape[1]):
                fn_val = float(Fn_poles[o_idx, p_idx])
                if not np.isfinite(fn_val) or fn_val <= 0:
                    continue
                lab_val = str(Lab[o_idx, p_idx]) if o_idx < Lab.shape[0] and p_idx < Lab.shape[1] else "0"
                xi_val = float("nan")
                if Xi_poles is not None and o_idx < Xi_poles.shape[0] and p_idx < Xi_poles.shape[1]:
                    xi_val = float(Xi_poles[o_idx, p_idx])
                    if not np.isfinite(xi_val):
                        xi_val = float("nan")
                stable_flag = _is_stable(lab_val)
                pole_rows.append({"order": _order, "frequency_hz": round(fn_val, 6), "damping_ratio": round(xi_val, 6) if np.isfinite(xi_val) else "", "label": lab_val, "stable": stable_flag})
        import pandas as _pd_poles
        _pd_poles.DataFrame(pole_rows).to_csv(poles_csv, index=False)
    except Exception:
        poles_csv = None

    cmif_png = out_dir / "frequency_response_cmif.png"
    nsv_req: int | str
    if cfg.cmif_nsv <= 0:
        nsv_req = "all"
    else:
        max_nsv = int(np.asarray(fdd_result.S_val).shape[1])
        if int(cfg.cmif_nsv) >= max_nsv:
            nsv_req = "all"
        else:
            nsv_req = int(cfg.cmif_nsv)
    fig, ax = CMIF_plot(
        S_val=np.asarray(fdd_result.S_val),
        freq=np.asarray(fdd_result.freq),
        freqlim=freqlim,
        nSv=nsv_req,
    )
    fig.tight_layout()
    fig.savefig(cmif_png, dpi=160)
    plt.close(fig)

    phi: np.ndarray
    fn: np.ndarray
    xi: np.ndarray
    if getattr(ssi_result, "Phi", None) is not None and getattr(ssi_result, "Fn", None) is not None:
        phi = np.asarray(ssi_result.Phi)
        fn = np.asarray(ssi_result.Fn)
        xi = np.asarray(ssi_result.Xi) if getattr(ssi_result, "Xi", None) is not None else np.full((phi.shape[1],), np.nan)
    else:
        from scipy.signal import find_peaks

        freq = np.asarray(fdd_result.freq, dtype=float)
        sv_mat = np.asarray(fdd_result.S_val, dtype=float)
        sv = sv_mat[0, 0, :]
        if freqlim is not None:
            lo, hi = freqlim
            mask = (freq >= lo) & (freq <= hi)
        else:
            mask = np.ones_like(freq, dtype=bool)

        sv_masked = np.where(mask, sv, np.nan)
        sv_clean = np.nan_to_num(sv_masked, nan=float(np.nanmin(sv_masked[np.isfinite(sv_masked)]) if np.isfinite(sv_masked).any() else 0.0))
        peaks, props = find_peaks(sv_clean, prominence=np.nanmax(sv_clean) * 0.02 if float(np.nanmax(sv_clean)) > 0 else None)
        if peaks.size == 0:
            peaks, props = find_peaks(sv_clean)

        if peaks.size == 0:
            phi = np.zeros((len(channel_labels), 0), dtype=complex)
            fn = np.zeros((0,), dtype=float)
            xi = np.zeros((0,), dtype=float)
        else:
            peak_heights = sv_clean[peaks]
            order = np.argsort(-peak_heights)
            peaks = peaks[order]
            peaks = peaks[: int(max(1, cfg.max_modes_export))]
            peaks = np.sort(peaks)

            V = np.asarray(fdd_result.S_vec)
            modes = []
            fn_list = []
            xi_list = []
            for idx in peaks:
                fn_list.append(float(freq[idx]))
                modes.append(V[:, 0, idx])

                peak_amp = float(sv[idx])
                if peak_amp <= 0 or not np.isfinite(peak_amp):
                    xi_list.append(float("nan"))
                    continue
                thr = peak_amp / math.sqrt(2.0)

                left = idx
                while left > 0 and sv[left] > thr:
                    left -= 1
                right = idx
                while right < sv.size - 1 and sv[right] > thr:
                    right += 1
                if left == 0 or right == sv.size - 1:
                    xi_list.append(float("nan"))
                else:
                    f1 = float(freq[left])
                    f2 = float(freq[right])
                    fn0 = float(freq[idx])
                    xi_list.append((f2 - f1) / (2.0 * fn0) if fn0 > 0 else float("nan"))

            phi = np.stack(modes, axis=1) if modes else np.zeros((len(channel_labels), 0), dtype=complex)
            fn = np.asarray(fn_list, dtype=float)
            xi = np.asarray(xi_list, dtype=float)

    if phi.ndim != 2:
        phi = np.asarray(phi).reshape((len(channel_labels), -1))

    n_modes = int(min(cfg.max_modes_export, phi.shape[1]))
    n_modes = max(0, n_modes)

    mode_png = out_dir / "mode_shapes.png"
    if n_modes == 0:
        fig = plt.figure(figsize=(10, 3))
        plt.text(0.5, 0.5, "No modes extracted", ha="center", va="center")
        plt.axis("off")
        fig.savefig(mode_png, dpi=160)
        plt.close(fig)
    else:
        rows = int(math.ceil(n_modes / 2))
        cols = 2 if n_modes > 1 else 1
        fig, axes = plt.subplots(rows, cols, figsize=(10, 3.4 * rows), squeeze=False)
        for i in range(n_modes):
            r = i // cols
            c = i % cols
            ax = axes[r][c]
            v = phi[:, i]
            v = np.asarray(v)
            mag = np.abs(v)
            denom = float(np.max(mag)) if float(np.max(mag)) > 0 else 1.0
            mag = mag / denom
            ax.bar(range(len(channel_labels)), mag)
            ax.set_xticks(range(len(channel_labels)))
            ax.set_xticklabels(channel_labels, rotation=25, ha="right")
            f_i = float(fn[i]) if i < fn.size else float("nan")
            xi_i = float(xi[i]) if i < xi.size else float("nan")
            ax.set_title(f"Mode {i+1}  f={f_i:.3f} Hz  damping={xi_i:.4f}")
            ax.set_ylim(0.0, 1.05)
        for j in range(n_modes, rows * cols):
            axes[j // cols][j % cols].axis("off")
        fig.tight_layout()
        fig.savefig(mode_png, dpi=160)
        plt.close(fig)

    mac_png = out_dir / "mac_matrix.png"
    mac = MAC(phi, phi) if phi.size and phi.shape[1] >= 2 else np.zeros((0, 0), dtype=float)
    if mac.size == 0:
        fig = plt.figure(figsize=(6, 3))
        msg = "No MAC matrix (need at least 2 modes)"
        plt.text(0.5, 0.5, msg, ha="center", va="center")
        plt.axis("off")
        fig.savefig(mac_png, dpi=160)
        plt.close(fig)
    else:
        fig, ax = plot_mac_matrix(phi, phi)
        fig.tight_layout()
        fig.savefig(mac_png, dpi=160)
        plt.close(fig)

    modes_csv = out_dir / "modal_properties.csv"
    rows_out: list[dict[str, float | int | str]] = []
    for i in range(n_modes):
        v = phi[:, i]
        row = {
            "mode_index": i + 1,
            "frequency_hz": float(fn[i]) if i < fn.size else float("nan"),
            "damping_ratio": float(xi[i]) if i < xi.size else float("nan"),
            "mpc": float(MPC(v)),
            "mpd": float(MPD(v)),
        }
        for j, name in enumerate(channel_labels):
            row[f"phi_{name}"] = float(np.real(v[j]))
        rows_out.append(row)

    import pandas as pd

    pd.DataFrame(rows_out).to_csv(modes_csv, index=False)

    summary_json = out_dir / "summary.json"
    summary = {
        "input_csv": str(source_csv) if source_csv is not None else None,
        "fs_hz": float(fs_hz),
        "channels": list(channel_labels),
        "ssi": {
            "ordmax": ordmax,
            "br": br,
            "modes_exported": n_modes,
        },
        "files": {
            "raw_csv": str(raw_csv) if raw_csv is not None else None,
            "stabilization_png": str(stab_png),
            "stabilization_poles_csv": str(poles_csv) if poles_csv is not None else None,
            "cmif_png": str(cmif_png),
            "mode_shapes_png": str(mode_png),
            "mac_png": str(mac_png),
            "modal_properties_csv": str(modes_csv),
        },
    }
    summary_json.write_text(json.dumps(summary, indent=2), encoding="utf-8")

    props_path = out_dir / "summary.properties"

    def _fp(p) -> str:
        """Return a path string safe for Java Properties.load() — forward slashes, no backslash escaping issues."""
        return str(p).replace("\\", "/") if p is not None else ""

    props_lines = [
        "status=ok",
        f"input_csv={_fp(source_csv)}",
        f"fs_hz={fs_hz}",
        f"raw_csv={_fp(raw_csv)}",
        f"stabilization_png={_fp(stab_png)}",
        f"stabilization_poles_csv={_fp(poles_csv)}",
        f"cmif_png={_fp(cmif_png)}",
        f"mode_shapes_png={_fp(mode_png)}",
        f"mac_png={_fp(mac_png)}",
        f"modal_properties_csv={_fp(modes_csv)}",
        f"summary_json={_fp(summary_json)}",
    ]
    props_path.write_text("\n".join(props_lines) + "\n", encoding="utf-8")

    return {
        "raw_csv": str(raw_csv) if raw_csv is not None else "",
        "stabilization_png": str(stab_png),
        "stabilization_poles_csv": str(poles_csv) if poles_csv is not None else "",
        "cmif_png": str(cmif_png),
        "mode_shapes_png": str(mode_png),
        "mac_png": str(mac_png),
        "modal_properties_csv": str(modes_csv),
        "summary_json": str(summary_json),
        "summary_properties": str(props_path),
    }


def run_from_csv(csv_path: Path, out_dir: Path, fs_hz: float | None, cfg: OmaRunConfig) -> dict[str, str]:
    data, fs, labels = load_sensor_csv(csv_path, fs_hz=fs_hz)
    return run_pyoma2(data, fs, labels, out_dir, cfg, source_csv=csv_path)


def _cli() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("--csv", required=True)
    p.add_argument("--out", required=True)
    p.add_argument("--fs", type=float, default=None)
    p.add_argument("--ssi-ordmax", type=int, default=20)
    p.add_argument("--ssi-br", type=int, default=10)
    p.add_argument("--max-modes", type=int, default=8)
    p.add_argument("--cmif-nsv", type=int, default=4)
    p.add_argument("--freqlim-min", type=float, default=None)
    p.add_argument("--freqlim-max", type=float, default=None)
    args = p.parse_args()

    csv_path = Path(args.csv)
    out_dir = Path(args.out)
    cfg = OmaRunConfig(
        ssi_ordmax=int(args.ssi_ordmax),
        ssi_br=int(args.ssi_br),
        max_modes_export=int(args.max_modes),
        cmif_nsv=int(args.cmif_nsv),
        freqlim_min_hz=args.freqlim_min,
        freqlim_max_hz=args.freqlim_max,
    )

    try:
        _ensure_dir(out_dir)
        run_from_csv(csv_path, out_dir, fs_hz=args.fs, cfg=cfg)
        return 0
    except Exception as ex:
        _ensure_dir(out_dir)
        (out_dir / "summary.properties").write_text("status=error\nmessage=" + str(ex).replace("\n", " ") + "\n", encoding="utf-8")
        print(str(ex))
        return 2


if __name__ == "__main__":
    raise SystemExit(_cli())

