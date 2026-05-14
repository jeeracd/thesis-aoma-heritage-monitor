# Navigation Audit

## Summary

The application uses Java Swing `JFrame` windows for each role (Engineer, Head, Officer). Many windows previously relied on a top-left `JTabbedPane` (“Projects / View / Help”) plus overlay dropdown buttons to drive navigation.

To improve workflow consistency, role navigation is now provided by a standard Swing `JMenuBar` installed per-window using `RoleMenuBar.install(frame, role)`.

## Role Menu Bar

- File: `RoleMenuBar.java`
- Menus: `Projects`, `View`, `Help`, plus a role-labeled account menu.
- Navigation behavior: selecting a menu item opens the destination window and disposes the current window.

## Windows Updated

All windows that previously created the top-left navigation `JTabbedPane` now install `RoleMenuBar`.

Categories covered:

- Dashboards and dashboards-adjacent flows: starting pages, view details, edit structural details.
- Operational screens: import/export sensor data, setup/connection, configure sensor(s), ESP32 status, vibration data, OMA analysis result, report viewing, system logs.
- Informational screens: sensor setup guide, user documentation, about, contact support.

User Settings screens also install `RoleMenuBar` to keep navigation consistent, while retaining their internal settings tabs.

## Testing Notes

- Compilation check was run for all `*.java` files excluding `*ESP32Connect.java` (those screens require the external `jSerialComm` dependency).
- Manual smoke testing should confirm:
  - Menu items open the expected windows.
  - Opening multiple windows and resizing does not break layout.
  - Dashboard functionality remains unchanged.
