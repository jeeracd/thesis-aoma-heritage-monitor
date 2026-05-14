# Changelog

## Unreleased

- Replaced CSV import file browsing from Swing `JFileChooser` to native OS file picker (`FileDialog`) for a more modern experience with system recents/favorites/search.
- Added inline status messaging and toast notifications for CSV selection flows.
- Added CSV validation helper and tests to cover success, cancellation, and error cases (missing file, non-file path, size limit, wrong extension).
- Added spectrogram rendering to OMA Analysis Result screens using the last uploaded CSV file as input.
- Aligned the spectrogram + spectral table layout using responsive Swing sizing and a two-row control bar to avoid wrapping/misalignment.
