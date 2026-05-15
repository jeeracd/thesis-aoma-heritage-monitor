# Deployment Checklist (Engineer Dashboard)

## Pre-deploy

- Compile with dependencies available (includes jSerialComm jar on classpath)
- Run name synchronization checks (EngineerNameConsistencyTest)
- Run viewport smoke tests (EngineerDashboardViewportSmokeTest)
- Run integration tests (EngineerNotificationsIntegrationTest)
- Run load test (EngineerNotificationLoadTest)

## Security

- Confirm passwords are stored only as hashes (PasswordHasher, EngineerPreferences.auth.passwordHash)
- Confirm no plaintext passwords are printed or logged
- Confirm credential sync logs do not include sensitive values (EngineerCredentialStore uses event-only logging)

## Rollout

- Validate engineer account navigation still routes correctly back to EngineerStartingPage
- Verify User Settings auto-save does not block the UI during typing
- Verify Notifications feed stays responsive under sustained incoming notifications

## Post-deploy

- Validate email routing behavior on target environment (Desktop mail client support varies)
