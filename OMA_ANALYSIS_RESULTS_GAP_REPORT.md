# OMA Analysis Results Window – Gap Analysis and Improvements

This project’s OMA Analysis Results window is a **Java Swing desktop UI**. Web-specific concepts (DOM/CSS selectors, ARIA attributes, Lighthouse scores) do not apply directly.

## Reference (Industry Features)

Commercial and widely used OMA solutions typically support:

- **Frequency-domain OMA (FDD/EFDD/CFDD)**: singular values of spectral density matrices, peak picking, damping estimation, and mode shape extraction/animation.
- **Time-domain OMA (SSI family)**: stabilization diagrams, model order selection, mode validation and clustering.
- **Validation tools**: MAC tables/plots, coherence/complexity indicators, deterministic/harmonic suppression, stability diagrams.
- **Workflow support**: interactive peak picking, cursor readouts, reporting/export, and result comparison across estimators.

Sources (overview/product pages):

- ARTeMIS / Structural Vibration Solutions OMA overview (FDD/EFDD/CFDD + SSI, stabilization diagrams).
  - https://www.svibs.com/operational-modal-analysis/
- Dewesoft ARTeMIS OMA article (FDD workflow: spectral density matrices → SVD → peak picking; damping for extended estimators).
  - https://dewesoft.com/blog/dewesoft-artemis-oma
- HBK BK Connect OMA product page (MAC, stabilization diagrams, deterministic suppression, batch processing).
  - https://www.hbkworld.com/en/products/software/daq/bk-connect-pulse/structural/operational-modal-analysis/operational-modal-analysis-8760-8761-8762

## Current Window – Identified Gaps

Before this change set, the window had:

- A time–frequency spectrogram and a spectral data table (good for exploration).
- A Natural Frequencies panel that previously lacked a full FDD workflow.
- **Empty placeholders** for “Damping Ratios (%)” and “Mode Shapes”.
- A static “Risk Level / STATUS / ACTION” block that was not derived from analysis outputs.

Key gaps vs. typical OMA workflow:

- No peak picking + linking across plots/tables.
- No damping estimation.
- No mode shape visualization.
- Limited interpretation guidance inside the window.
- No validation metrics (MAC/coherence/stabilization diagrams) – still not implemented.

## Implemented Improvements

### 1) Peak Picking and Interactive Selection

- Automatically detects peaks in **SVD line 1**.
- Shows vertical peak markers in the FDD plot; click-to-select.
- Cursor readout on the plot (“Cursor: … Hz, … dB”).

Code:

- [FddPeakAnalysis.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPeakAnalysis.java)
- [FddPlotPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotPanel.java)
- [FddPlotViewer.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotViewer.java)

### 2) Damping Ratios Panel (Filled)

- Populates “Damping Ratios (%)” with a peak table.
- Estimates damping using a **half-power bandwidth** approximation (≈ -3 dB).
- Adds “Export peaks…” to write peak/damping results to CSV.

Code:

- [FddDampingPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddDampingPanel.java)

### 3) Mode Shapes Panel (Filled)

- Displays a normalized per-channel “mode shape” bar chart at the selected peak.
- Derived from the dominant eigenvector of the spectral density magnitude matrix.

Code:

- [FddModeShapePanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddModeShapePanel.java)
- [FddGenerator.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddGenerator.java)
- [FddResult.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddResult.java)

### 4) Removed/Aligned: Static “Risk Level / STATUS / ACTION” Block

- Removed the non-analysis-derived status block.
- Replaced with an OMA-focused interpretation panel that updates with the selected peak.

Code:

- [OmaInterpretationPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/OmaInterpretationPanel.java)

### 5) Consistent Layout and Scaling

- Standardized control fonts and heights so UI rows visually align.

Code:

- [UiControlMetrics.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/UiControlMetrics.java)

## Backward Compatibility Notes

- Existing spectrogram/table functionality remains intact.
- FDD results now carry extra metadata (channel labels + mode-shape vectors) via `FddResult`.

## Tests

- Existing FDD tests still pass.
- FDD plot viewer range test still passes.

Files:

- [FddGeneratorTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddGeneratorTest.java)
- [FddPlotViewerTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotViewerTest.java)

## Remaining Gaps (Future Work)

High-value next additions aligned with industry practice:

- SSI estimators + stabilization diagram.
- MAC/coherence/complexity plots for validation.
- EFDD-style damping refinement (time-domain decay from bandpass around peak) and uncertainty reporting.
- Mode shape animation with geometry import (requires node layout / sensor mapping data).
- Report generator with embedded plots (would benefit from a real PDF library).
