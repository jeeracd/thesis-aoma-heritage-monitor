# Engineer Dashboard Enhancements

## Scope

Enhanced components:
- EngineerDashboardUserSettings
- EngineerDashboardNotifications
- EngineerDashboardEmailPassword

Synchronized with:
- EngineerStartingPage

## Naming Synchronization Architecture

Centralized naming source:
- EngineerUiNames

Updated modules reference EngineerUiNames for:
- Window titles
- Core page titles and navigation labels (EngineerStartingPage tabs + User Settings tabs)
- Shared action labels (e.g., Save Changes & Exit) and common success dialog strings

Consistency verification:
- EngineerNameConsistencyTest validates key labels/titles across EngineerStartingPage, EngineerDashboardUserSettings, EngineerDashboardNotifications, and EngineerDashboardEmailPassword.

## Centralized State & Preferences

Centralized persisted preferences:
- EngineerPreferences (java.util.prefs.Preferences)

Centralized credential store:
- EngineerCredentialStore (in-memory + persisted to EngineerPreferences; password stored as hash only)

Stored settings include:
- Profile: first name, last name, photo path
- Access level: VIEWER / EDITOR / OWNER
- Notifications: in-app/email routing, frequency, thresholds
- Authentication: email, password hash

## EngineerDashboardUserSettings Enhancements

Role-based access control:
- Uses EngineerPreferences.AccessLevel
- VIEWER access disables editing controls

Live name synchronization:
- EngineerProfileStore provides a single source of truth for first/last name
- Updates are reflected immediately in EngineerStartingPage and User Settings when both are open

Profile validation:
- Validates first/last name with basic format rules (letters + common separators)
- Save is disabled when invalid

Auto-save with debouncing:
- Profile changes auto-save via SwingDebouncer to avoid excessive writes

Preferences reset:
- “Reset Preferences” clears EngineerPreferences and restarts the settings window

## EngineerDashboardNotifications Enhancements

Real-time push notifications:
- EngineerNotificationCenter provides an in-memory feed with listener subscriptions
- Feed updates live inside the Notifications tab

Customizable thresholds:
- Vibration RMS threshold
- Frequency shift threshold

Read status tracking:
- Selecting a notification marks it as read
- “Mark All Read” supported

Multi-channel routing:
- In-app routing toggle
- Email routing toggle
- Email frequency options: Right away / Once a day / Never
- Digest support exposed via “Send Digest Now”

## EngineerDashboardEmailPassword Enhancements

Secure password hashing:
- PasswordHasher uses PBKDF2WithHmacSHA256
- Password is stored as a PBKDF2 hash string in EngineerPreferences

Email update:
- Change email updates the stored email value (no verification workflow)

Credential synchronization:
- EngineerDashboardEmailPassword reads/writes credentials via EngineerCredentialStore
- UsersLoginOptions and CreateAccount also update EngineerCredentialStore, so changes propagate live when both UIs are open

Password strength meter:
- Strength meter shown during password change workflow
- Enforces “Good” or better score before allowing updates
