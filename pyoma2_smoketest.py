import importlib.metadata as md
import numpy as np

from pyoma2.algorithms.fdd import FDD
from pyoma2.algorithms.ssi import SSI
from pyoma2.functions.gen import example_data
from pyoma2.setup.single import SingleSetup


def _unpack_example():
    out = example_data()
    if not isinstance(out, tuple):
        raise TypeError(f"example_data() returned {type(out)}")
    if len(out) == 2:
        data, ground_truth = out
        excitation = None
    elif len(out) == 3:
        data, excitation, ground_truth = out
    else:
        raise ValueError(f"example_data() returned {len(out)} values, expected 2 or 3")
    return data, excitation, ground_truth


def _unpack_ground_truth(ground_truth):
    if not isinstance(ground_truth, tuple) or len(ground_truth) != 3:
        raise ValueError("ground_truth is expected to be a tuple of length 3")

    fn = np.asarray(ground_truth[0], dtype=float).ravel()
    second = ground_truth[1]
    third = ground_truth[2]

    if np.isscalar(second):
        xi = float(second)
        phi = np.asarray(third, dtype=float)
    else:
        phi = np.asarray(second, dtype=float)
        xi = float(third) if np.isscalar(third) else float(np.asarray(third).ravel()[0])

    return fn, xi, phi


def main():
    print("pyOMA2 smoke test")
    print(f"Python package distribution: pyOMA-2 {md.version('pyOMA-2')}")

    data, _, ground_truth = _unpack_example()
    fn_true, xi_true, _ = _unpack_ground_truth(ground_truth)

    setup = SingleSetup(data, fs=100)
    setup.decimate_data(q=2)

    fdd = FDD(name="FDD", nxseg=2**11, method_SD="per")
    ssidat = SSI(name="SSIdat", method="dat", br=30, ordmax=50, step=2)

    setup.add_algorithms(fdd, ssidat)
    setup.run_all()

    sel_freq = [float(x) for x in fn_true[: min(5, len(fn_true))]]
    setup.mpe("SSIdat", sel_freq=sel_freq, order_in=30)

    res = dict(ssidat.result)
    fn_est = np.asarray(res.get("Fn", []), dtype=float).ravel()
    xi_est = np.asarray(res.get("Xi", []), dtype=float).ravel()

    print(f"Ground truth Fn (Hz): {np.round(fn_true, 3)}")
    print(f"Ground truth Xi: {xi_true}")
    print(f"Estimated Fn (Hz): {np.round(fn_est, 3)}")
    if xi_est.size:
        print(f"Estimated Xi: {np.round(xi_est, 4)}")

    if fn_est.size:
        n = min(fn_est.size, fn_true.size)
        err_hz = fn_est[:n] - fn_true[:n]
        print(f"Fn error (Hz): {np.round(err_hz, 4)}")

    print("OK")


if __name__ == "__main__":
    main()

