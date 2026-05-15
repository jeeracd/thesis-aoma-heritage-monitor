public final class EngineerUiNames {
    public static final String ENGINEER_ACCOUNT_WINDOW_TITLE = "AOMA-Heritage Monitor - Engineer Account";

    public static final String TAB_PROJECTS = "Projects";
    public static final String TAB_VIEW = "View";
    public static final String TAB_HELP = "Help";

    public static final String MENU_NEW_PROJECT = "New Project";
    public static final String MENU_OPEN_PROJECT = "Open Project";
    public static final String MENU_IMPORT_SENSOR_DATA = "Import Sensor Data (.csv)";
    public static final String MENU_EXPORT_REPORT_PDF = "Export Report (PDF)";
    public static final String MENU_EXIT = "Exit";
    public static final String TITLE_EXIT_CONFIRMATION = "Exit Confirmation";

    public static final String MENU_DASHBOARD_VIEW = "Dashboard View";
    public static final String MENU_SETUP_CONNECTION = "Setup & Connection";
    public static final String MENU_CONFIGURE_SENSOR = "Configure Sensor";
    public static final String MENU_ESP32_STATUS = "ESP32 Status";
    public static final String MENU_VIBRATION_DATA = "Vibration Data";
    public static final String MENU_OMA_ANALYSIS_RESULT = "OMA Analysis Result";
    public static final String MENU_VIEW_REPORT = "View Report";
    public static final String MENU_SYSTEM_LOGS = "System Logs";

    public static final String MENU_SENSOR_SETUP_GUIDE = "Sensor Setup Guide";
    public static final String MENU_USER_DOCUMENTATION = "User Documentation";
    public static final String MENU_ABOUT = "About AOMA-Heritage Monitor";
    public static final String MENU_CONTACT_SUPPORT = "Contact Support";

    public static final String MENU_USER_SETTINGS = "User Settings";
    public static final String MENU_LOGOUT = "Logout";
    public static final String TITLE_LOGOUT_CONFIRMATION = "Logout Confirmation";

    public static final String USER_SETTINGS_HEADER = "User Settings";
    public static final String USER_SETTINGS_TAB_NAME_PHOTO = "Name & Photo";
    public static final String USER_SETTINGS_TAB_EMAIL_PASSWORD = "Email & Password";
    public static final String USER_SETTINGS_TAB_NOTIFICATIONS = "Notifications";

    public static final String ACTION_SAVE_CHANGES_EXIT = "Save Changes & Exit";
    public static final String DIALOG_SUCCESS_TITLE = "Success";
    public static final String DIALOG_CHANGES_SAVED = "Changes saved successfully!";

    private EngineerUiNames() {}

    public static String windowTitle(String pageTitle) {
        if (pageTitle == null || pageTitle.isBlank()) {
            return ENGINEER_ACCOUNT_WINDOW_TITLE;
        }
        return ENGINEER_ACCOUNT_WINDOW_TITLE + " - " + pageTitle.trim();
    }
}

