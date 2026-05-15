# Engineer Dashboard Test Report

## Automated Tests Added

Name synchronization:
- EngineerNameConsistencyTest

Viewport smoke:
- EngineerDashboardViewportSmokeTest

Cross-component integration:
- EngineerNotificationsIntegrationTest

Credential synchronization:
- EngineerCredentialSyncTest

Load test (real-time notifications feed):
- EngineerNotificationLoadTest

## How Tests Were Run

Compilation (includes jSerialComm dependency):
- javac -cp .:jSerialComm-2.11.4.jar *.java

Execution:
- java -cp .:jSerialComm-2.11.4.jar EngineerNameConsistencyTest
- java -cp .:jSerialComm-2.11.4.jar EngineerDashboardViewportSmokeTest
- java -cp .:jSerialComm-2.11.4.jar EngineerCredentialSyncTest
- java -cp .:jSerialComm-2.11.4.jar EngineerNotificationsIntegrationTest
- java -cp .:jSerialComm-2.11.4.jar EngineerNotificationLoadTest

## Expected Output

Each test prints:
- ALL TESTS PASSED

Load test additionally prints:
- PUSHED=<N> TIME_MS=<value>

## Manual Verification Checklist

Desktop viewport:
- Open EngineerStartingPage and confirm tab labels match expected
- Navigate to User Settings and confirm all tab labels are consistent
- Edit first/last name and confirm auto-save updates status without requiring manual save
- Use Reset Preferences and confirm the settings window restarts with defaults
- Open Notifications tab and confirm new notifications appear (test button + simulated alerts)
- Click a notification and confirm it is marked read and details dialog is shown
- Toggle in-app/email routing and confirm persistence after reopening User Settings
- Open Email & Password tab:
-  - Change email and confirm it persists after reopening
  - Change password with strength meter enforcing complexity

Mobile viewport (simulated):
- Resize EngineerStartingPage and User Settings to ~360x640 and confirm no crashes or rendering failures
