# OMA Analysis Results (CAD Workspace) Guide

## Overview
The OMA Analysis Results window is redesigned as a CAD-style workspace:
- Dockable panels (Show, Data, PyOMA2 Images) with float/dock/hide controls
- Split viewport with pan/zoom/select/annotate tools
- A command line for power users
- Persistent per-role workspace layout across sessions

The window loads the latest PyOMA2 run output from:
`%USERPROFILE%\.aoma-heritage-monitor\pyoma2-results\<timestamp>\`

## Workspace Layout
### Docking / Floating
Each dockable panel includes:
- Float: detach into a floating window (multi-monitor supported)
- Dock: return to the workspace
- Hide: remove the panel from the workspace

The Workspace ribbon provides quick repositioning:
- Show->Left / Show->Right
- Data->Left / Data->Right
- Show Images toggle

### Persistence (Per Role)
The following are persisted per role (Engineer/Head):
- Window bounds (multi-monitor safe restore)
- Splitter divider positions
- Split-view on/off
- Dock positions for Show/Data
- Layer visibility + alpha settings

## Viewport (CAD Navigation)
### Tools (Ribbon: View)
- Select: click a plotted mode point to select the corresponding row in the Data table
- Pan: click-drag to pan the view
- Annotate: click to add a drafting-style callout at the clicked world coordinate

### Zoom
- Mouse wheel zooms around the cursor position
- ESC resets the view

### Split View
- Toggle Split enables/disables the second viewport
- Each viewport can switch between Frequency and Damping plots

## Show Panel (Visibility Control)
The Show panel provides hierarchical visibility toggles:
- Natural Frequencies
  - Points
  - Labels
- OMA Results
  - Validation Flags
- Overlays
  - Annotations

### Search
Use the Search box to highlight/filter visible categories quickly.

### Presets
Buttons apply one-click visibility presets:
- Basic: points + validation
- Advanced: points + labels + validation
- Full: points + labels + validation

### Layer Alpha
The Alpha slider adjusts transparency for:
- Points (when selecting Points)
- Labels (when selecting Labels)

## Data Panel (Filterable + Linked)
The Data panel is a filterable table synced with the viewport:
- Selecting a row highlights the mode in both viewports
- Selecting a point in the viewport selects the matching row
- Details shows raw values and their source (modal_properties.csv / summary.properties)

## Validation Indicators
Each mode row is validated and assigned a severity:
- OK (green)
- WARNING (amber)
- CRITICAL (red)

Validation rules include basic bounds checking on frequency, damping, and metrics.

## Exports
The Export ribbon supports:
- CSV: modal rows export
- Excel: SpreadsheetML XML export (openable in Excel)
- PDF: text summary export
- DXF (2D): exports the plotted modes and annotations for CAD workflows

## Command Line
Supported commands:
- help
- load latest
- split on|off
- preset basic|advanced|full
- tool select|pan|annotate
- export dxf|csv|excel|pdf
- reset view

