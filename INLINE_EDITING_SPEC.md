# Inline editing in View Details

## Scope

Inline editing is implemented directly in the existing Swing `View Details` screens for both roles:

- `EngineerViewDetails`
- `HeadViewDetails`

The flow no longer navigates to a separate edit screen when the user clicks “Edit Project Details”.

## Important note about “API / browsers”

This repository is a Java Swing desktop application. It does not include an HTTP frontend, browser targets, or a backend API layer. The persistence “backend” in this codebase is `ProjectRepository`, which saves to a local on-disk store.

## Layout note (avoid overlap)

The screens are built with absolute positioning (`null` layouts). To prevent overlap between the “Description” editor and the “Safety Assessment Report” area, the report panels are positioned relative to the bottom of the “View Details” container rather than hard-coded y coordinates.

## UX behavior

- Default state is read-only.
- Clicking “Edit Project Details” switches to edit mode:
  - Enables form inputs in-place (no window transitions).
  - Enables the date picker button.
  - Changes primary button label to “Save Changes”.
  - Changes “Manage Access” button into “Cancel” (discard edits).
- Saving:
  - Runs validation before attempting to persist.
  - Shows an indeterminate progress dialog while saving.
  - Shows a success dialog on save completion.
- Save failure:
  - Reverts the UI back to the last persisted values.
  - Shows an error dialog.
- Unsaved changes:
  - If the user tries to navigate away (menus or window close) with unsaved changes, a discard confirmation is shown.

## Validation rules

- Project Name: required (non-empty)
- Building Name: required (non-empty)
- Date Constructed: required, must match `YYYY-MM-DD`

## Persistence integration

- Save uses `ProjectRepository.updateProject(...)`.
- The update is asynchronous using `SwingWorker` so the UI remains responsive.

## Tests

Because the repository does not use a build system or JUnit, tests are implemented as standalone runners:

- `ProjectDetailsControllerTest` verifies edit-state transitions, dirty tracking, validation, and revert semantics.

Run:

```powershell
javac -cp ".;jSerialComm-2.11.4.jar" *.java
java -ea -cp ".;jSerialComm-2.11.4.jar" ProjectDetailsControllerTest
```
