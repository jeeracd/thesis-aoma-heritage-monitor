import tempfile
from pathlib import Path

import numpy as np

from pyoma2_oma_results import OmaRunConfig, load_sensor_csv, run_pyoma2


def _read_props(path: Path) -> dict[str, str]:
    out: dict[str, str] = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        if not line.strip() or "=" not in line:
            continue
        k, v = line.split("=", 1)
        out[k.strip()] = v.strip()
    return out


def test_run_pyoma2_generates_outputs() -> None:
    fs = 100.0
    n = 4096
    t = np.arange(n) / fs
    x1 = 0.8 * np.sin(2 * np.pi * 3.5 * t) + 0.2 * np.sin(2 * np.pi * 9.0 * t)
    x2 = 0.6 * np.sin(2 * np.pi * 3.5 * t + 0.4) + 0.25 * np.sin(2 * np.pi * 9.0 * t + 0.2)
    x3 = 0.4 * np.sin(2 * np.pi * 3.5 * t + 0.9) + 0.15 * np.sin(2 * np.pi * 9.0 * t + 0.7)
    data = np.stack([x1, x2, x3], axis=1) + 0.02 * np.random.RandomState(0).randn(n, 3)

    with tempfile.TemporaryDirectory() as tmp:
        out_dir = Path(tmp) / "out"
        files = run_pyoma2(data, fs, ["accelX", "accelY", "accelZ"], out_dir, OmaRunConfig(max_modes_export=4))
        for key in [
            "stabilization_png",
            "cmif_png",
            "mode_shapes_png",
            "mac_png",
            "modal_properties_csv",
            "summary_json",
            "summary_properties",
        ]:
            assert key in files
            assert Path(files[key]).exists()

        props = _read_props(Path(files["summary_properties"]))
        assert props.get("status") == "ok"
        assert float(props.get("fs_hz", "0")) == fs

        csv_text = Path(files["modal_properties_csv"]).read_text(encoding="utf-8")
        assert "frequency_hz" in csv_text


def test_block_delimited_requires_fs() -> None:
    block = """timestamp
accelX
accelY
accelZ
########
0.1
0.2
0.3
########
0.2
0.1
0.0
"""
    with tempfile.TemporaryDirectory() as tmp:
        p = Path(tmp) / "block.csv"
        p.write_text(block, encoding="utf-8")
        ok = False
        try:
            load_sensor_csv(p, fs_hz=None)
        except Exception:
            ok = True
        assert ok


if __name__ == "__main__":
    test_run_pyoma2_generates_outputs()
    test_block_delimited_requires_fs()
    print("ok")

