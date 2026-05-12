# Project persistence fix (Engineer / Head dashboards)

## Problem statement

When a user creates a new project (Project Name, Building Name, Date Constructed, etc.) and later returns to the dashboard, the dashboard UI is reconstructed and previously entered project data disappears.

## Root cause

The application was treating “created projects” as temporary Swing UI rows only:

- The dashboards recreate `projectsContainer` every time the dashboard screen opens.
- The project creation form was only calling `addNewProjectRow(...)` to add a row to the current dashboard instance, and did not store the full project anywhere.
- When the user navigated away and re-opened the dashboard, there was no model/repository to reload from.
- “View Details” did not receive a selected project reference, so fields were not populated.

This is a desktop Java Swing application; there are no backend API endpoints, database transactions, or HTTP session mechanics in this repository.

## Solution overview

1. Introduced a persistent project model and repository:
   - `Project` holds the full set of project detail fields.
   - `ProjectRepository` stores projects in memory and persists them to disk using Java serialization.
   - Storage location defaults to `%USERPROFILE%\.aoma-heritage-monitor\projects.ser`.

2. Dashboard now reloads projects from the repository on every open:
   - The dashboard clears and repopulates `projectsContainer` from `ProjectRepository.getAll()`.
   - “View Details” is wired to the selected project via its persisted `UUID`.

3. Added validation and error handling:
   - Required fields: Project Name, Building Name, Date Constructed.
   - Validation errors show as modal error dialogs and block saving.
   - Persistence errors show as modal error dialogs and do not report success.
   - Repository write failures roll back in-memory changes to avoid “phantom saved” projects.

## Monitoring / alerting

- `AppMonitoring` configures file logging to `%USERPROFILE%\.aoma-heritage-monitor\logs\app.log`.
- `ProjectRepository` logs load failures and continues with an empty in-memory cache to keep the UI usable.
- Persistence failures are surfaced to the user via modal dialogs in the form submit handlers.

## Automated tests

Because the project does not use Maven/Gradle or ship a JUnit dependency, tests are implemented as a standalone Java test runner:

- `ProjectRepositoryTest` verifies:
  - create + reload from disk persists fields
  - validation failures block creation
  - persistence failures roll back the in-memory cache
  - concurrent creates remain consistent and persist correctly

Run:

```powershell
javac *.java
java -ea ProjectRepositoryTest
```

## Follow-ups / future hardening

- Add per-user isolation once the app has real login/session state (e.g., store projects per account).
- Replace Java serialization with a versioned, human-readable format (JSON) once a JSON library/build system is introduced.
- Add UI affordances for editing/deleting projects and for handling project-not-found states more gracefully.

