# Changelog

## Unreleased

- Replaced CSV import file browsing from Swing `JFileChooser` to native OS file picker (`FileDialog`) for a more modern experience with system recents/favorites/search.
- Added inline status messaging and toast notifications for CSV selection flows.
- Added CSV validation helper and tests to cover success, cancellation, and error cases (missing file, non-file path, size limit, wrong extension).
- Added spectrogram rendering to OMA Analysis Result screens using the last uploaded CSV file as input.
- Aligned the spectrogram + spectral table layout using responsive Swing sizing and a two-row control bar to avoid wrapping/misalignment.
- Added CSV-driven modal parameter inference and a “Parameters…” viewer for the spectral table.
- Added “Select all” support and improved table control bar layout to prevent control overlap.
- Added multi-format export for the spectral table (CSV, Excel XML, PDF) plus time/frequency filtering.
- Added FDD-style singular value plot (SVD curves) and user-configurable frequency range controls.
- Added auto-refresh support to re-run analysis when the source CSV changes on disk.
- Filled the Damping Ratios and Mode Shapes panels using peak picking, damping estimates, and per-channel mode-shape visualization.
- Replaced the static risk/status block with an interpretation panel driven by selected peaks.
- Standardized top navigation across the app using a role-based menu bar (`RoleMenuBar`) and removed the workflow-heavy top-left tab/dropdown navigation in dashboards.
- Updated login flow to route directly to the role dashboard after successful authentication.
