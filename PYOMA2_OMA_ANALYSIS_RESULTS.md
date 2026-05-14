# PyOMA2 Integration (OMA Analysis Results)

## Where PyOMA2 Is Used

The application’s OMA results are presented in the Swing windows:

- [EngineerOMAAnalysisResult.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/EngineerOMAAnalysisResult.java)
- [HeadOMAAnalysisResult.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/HeadOMAAnalysisResult.java)

These windows now include a **PyOMA2 OMA Results** section which runs a Python pipeline and displays the generated plots.

## Auto-Run After Import

After a successful CSV upload (Import Sensor Data), the application stores the CSV in `AppSession` and automatically triggers PyOMA2 processing the next time the OMA Analysis Results window is opened (or immediately if it is already open). The manual **Run PyOMA2** button remains available as a fallback.

## Pipeline Script

- Python runner: `pyoma2_oma_results.py`
- Inputs:
  - `--csv <path>` raw sensor CSV (same file used by the analysis UI)
  - `--out <dir>` output folder
  - `--fs <Hz>` optional; required for block-delimited sensor CSV
- Outputs (written into `--out`):
  - `stabilization.png` (SSI stabilization diagram)
  - `frequency_response_cmif.png` (CMIF plot)
  - `mode_shapes.png` (mode shapes)
  - `mac_matrix.png` (MAC matrix)
  - `modal_properties.csv` (frequencies, damping estimate, MPC/MPD, mode shape components)
  - `summary.json` and `summary.properties`

## Modal Properties + Validation

The pipeline uses PyOMA2 algorithms:

- `SSI` to compute stabilization poles and render the stabilization diagram.
- `FDD` to compute singular values/vectors and derive:
  - CMIF curves
  - auto peak-picked natural frequencies
  - mode shape estimates from singular vectors
  - damping estimates via half-power bandwidth on the first singular value curve

Validation utilities used:

- `MPC`, `MPD`, `MAC` from `pyoma2.functions.gen`

## Java Integration

- Runner: `PyOma2Runner.java` executes the Python script and reads `summary.properties`.
- UI: `PyOma2ResultsPanel.java` provides Run controls, status feedback, and renders the generated images.

## Tests

- `pyoma2_oma_results_test.py` exercises the Python pipeline on synthetic multi-sine data and asserts that outputs are generated.
