public final class ProjectValidation {
    private ProjectValidation() {}

    public static void validateNewProject(String projectName, String buildingName, String dateConstructed) {
        validateProjectDetails(projectName, buildingName, dateConstructed);
    }

    public static void validateProjectDetails(String projectName, String buildingName, String dateConstructed) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project Name is required.");
        }
        if (buildingName == null || buildingName.trim().isEmpty()) {
            throw new IllegalArgumentException("Building Name is required.");
        }
        if (dateConstructed == null || dateConstructed.trim().isEmpty()) {
            throw new IllegalArgumentException("Date Constructed is required.");
        }
        String d = normalizeDateOrReturnOriginal(dateConstructed);
        try {
            java.time.LocalDate.parse(d, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException("Date Constructed must be in YYYY-MM-DD format.");
        }
    }

    public static String normalizeDateOrReturnOriginal(String date) {
        if (date == null) {
            return "";
        }
        String d = date.trim();
        if (d.isEmpty()) {
            return d;
        }
        d = d.replace('/', '-');
        if (d.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            try {
                java.time.LocalDate parsed = java.time.LocalDate.parse(
                        d,
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-M-d")
                );
                return parsed.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (java.time.format.DateTimeParseException ex) {
                return d;
            }
        }
        return d;
    }
}

