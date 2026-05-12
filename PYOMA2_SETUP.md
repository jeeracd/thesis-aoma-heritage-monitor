# PyOMA2 (pyOMA-2) installation notes

## Environment used

- OS: Windows
- Python: 3.13.x (system install)
- Installation target: user site-packages (because system site-packages was not writeable)

## Install

```powershell
python -m pip install -U "pyOMA-2"
```

## Verify installation

Version check:

```powershell
python -c "import importlib.metadata as md; print(md.version('pyOMA-2'))"
```

Dependency resolution check:

```powershell
python -m pip check
```

## Run the smoke test

This repo includes a minimal non-interactive OMA workflow test:

```powershell
python pyoma2_smoketest.py
```

The script generates a built-in example dataset, runs a SingleSetup analysis with FDD + SSI (data-driven), extracts modal parameters, and prints estimated frequencies/damping.

## Notes / system requirements

- GUI/interactive plots: pyOMA-2 supports interactive plots. On Windows, ensure your Python installation includes Tk (tkinter) if you want to use matplotlib with Tk-based backends, and ensure a working GUI environment for PyQt-based tools.
- 3D visualization: pyOMA-2 depends on VTK/pyvista for 3D features. This typically requires an OpenGL-capable environment. On headless/remote sessions, avoid 3D/interactive plotting or configure off-screen rendering.
- PATH warning (optional): pip may warn that the user Scripts folder is not on PATH. Using `python -m pip ...` avoids relying on PATH, so it is safe to ignore unless you want to run `pip` directly.

## Compatibility with this repository

This repository is primarily a Java Swing codebase and does not define a Python dependency lockfile. The pyOMA-2 installation was verified in the active Python environment using `pip check` with no broken requirements reported.

