# OMA Analysis Results Enhancements

This application is a Java Swing desktop UI. Concepts like browser compatibility and CSS do not apply; equivalent improvements are implemented via Swing layout managers, input validation, background workers (`SwingWorker`), and user feedback toasts.

## Control Row Consistency

Control bars across the table viewer, refresh controls, and FDD viewer share standardized font sizing and row/control heights to reduce visual jitter and improve scanability.

Relevant code:

- [UiControlMetrics.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/UiControlMetrics.java)

## Spectrogram + Table Viewer

### Filtering

- dB filter (`min` → `max`)
- Time range filter (seconds)
- Frequency range filter (Hz)
- Flag search (substring match)
- “Anomalies only” toggle

Filtering applies to the currently active spectrogram view window.

Relevant code:

- [SpectrogramDataTableViewer.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramDataTableViewer.java)
- [SpectrogramTableModel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramTableModel.java)

### Export

The spectral table supports exporting “selected” rows or the currently visible “window” rows to:

- CSV
- Excel-compatible SpreadsheetML (`.xml`)
- PDF (simple text report)

Exports include inferred modal-parameter metadata columns:

- `modal_title`
- `modal_encoding`
- `modal_delimiter`

Relevant code:

- [SpectrogramCsvExport.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramCsvExport.java)
- [SpectrogramExcelExport.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramExcelExport.java)
- [SpectrogramPdfExport.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramPdfExport.java)
- [SimplePdfWriter.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SimplePdfWriter.java)
- [CsvModalParametersGenerator.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/CsvModalParametersGenerator.java)

### Select All

The “Select all” checkbox selects/deselects all currently visible table rows and stays synchronized with manual selection changes.

## FDD Plot (Natural Frequencies)

The Natural Frequencies section now contains an FDD-style plot of singular value curves derived from the input CSV.

Enhancements:

- User-configurable frequency bounds (min/max Hz)
- Fixed magnitude axis range: `-100 dB .. 0 dB`
- Tick labels formatted with `dB` suffix
- In-plot legend with click-to-show/hide per curve and smart corner placement
- Peak picking (SVD line 1) with click-to-select and cursor readout

Relevant code:

- [FddGenerator.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddGenerator.java)
- [FddPlotPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotPanel.java)
- [FddPlotViewer.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotViewer.java)

## Damping Ratios + Mode Shapes

The Damping Ratios and Mode Shapes panels are populated from FDD results:

- Damping ratios are estimated using a simple half-power bandwidth approximation around detected peaks.
- Mode shapes are displayed as normalized per-channel participation derived from the dominant eigenvector of the spectral-density magnitude matrix.

Relevant code:

- [FddPeakAnalysis.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPeakAnalysis.java)
- [FddDampingPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddDampingPanel.java)
- [FddModeShapePanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddModeShapePanel.java)

## Interpretation

The previous static “Risk Level / STATUS / ACTION” box was replaced with an OMA-focused interpretation panel that updates based on the currently selected peak.

Relevant code:

- [OmaInterpretationPanel.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/OmaInterpretationPanel.java)

## Real-Time Update

OMA Analysis Results screens include a “Refresh” button and an optional “Auto refresh” toggle that detects on-disk changes to the loaded CSV file and re-runs the spectrogram + FDD computations.

Relevant code:

- [EngineerOMAAnalysisResult.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/EngineerOMAAnalysisResult.java)
- [HeadOMAAnalysisResult.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/HeadOMAAnalysisResult.java)

## Tests

The repository uses standalone `main`-based tests.

- [SpectrogramTableModelTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramTableModelTest.java)
- [SpectrogramExportFormatsTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/SpectrogramExportFormatsTest.java)
- [FddGeneratorTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddGeneratorTest.java)
- [FddPlotViewerTest.java](file:///d:/Downloads/thesis-aoma-heritage-monitor-main/thesis-aoma-heritage-monitor/FddPlotViewerTest.java)
