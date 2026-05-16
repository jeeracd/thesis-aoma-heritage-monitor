import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public final class UserStore {

    public record AuthResult(boolean ok, RoleMenuBar.Role role, String message, long lockedUntilMs) {}
    public record UserRecord(String email, RoleMenuBar.Role role, long createdAtMs) {}

    static final int MAX_ATTEMPTS = 5;
    static final long LOCKOUT_MS = 5L * 60_000; // 5 minutes

    private static final Preferences ROOT =
            Preferences.userRoot().node("aoma-heritage-monitor/users");

    static {
        ensureDefaults();
    }

    private UserStore() {}

    public static synchronized AuthResult authenticate(String rawEmail, char[] password) {
        String email = normalize(rawEmail);
        if (email.isEmpty()) {
            return new AuthResult(false, null, "Email is required.", 0L);
        }
        String key = nodeKey(email);
        Preferences node;
        try {
            if (!ROOT.nodeExists(key)) {
                System.err.println("[AUTH-DEBUG] Login failed: no account found for email=" + email);
                return new AuthResult(false, null, "Invalid email or password.", 0L);
            }
            node = ROOT.node(key);
        } catch (BackingStoreException e) {
            System.err.println("[AUTH-DEBUG] Login failed: storage error for email=" + email + " – " + e.getMessage());
            return new AuthResult(false, null, "Storage error. Try again.", 0L);
        }

        long lockoutUntil = node.getLong("lockoutUntilMs", 0L);
        if (lockoutUntil > System.currentTimeMillis()) {
            System.err.println("[AUTH-DEBUG] Login failed: account locked until " + new java.util.Date(lockoutUntil) + " for email=" + email);
            return new AuthResult(false, null, "Account locked.", lockoutUntil);
        }

        String hash = node.get("passwordHash", "");
        if (!isPbkdf2Hash(hash)) {
            System.err.println("[AUTH-DEBUG] Login failed: stored hash is not valid PBKDF2 for email=" + email + " – hash prefix=" + (hash.length() > 10 ? hash.substring(0, 10) : hash));
            return new AuthResult(false, null, "Invalid email or password.", 0L);
        }
        boolean correct = PasswordHasher.verify(password, hash);
        if (!correct) {
            int attempts = node.getInt("failedAttempts", 0) + 1;
            System.err.println("[AUTH-DEBUG] Login failed: wrong password for email=" + email + " (attempt " + attempts + "/" + MAX_ATTEMPTS + ")");
            if (attempts >= MAX_ATTEMPTS) {
                long until = System.currentTimeMillis() + LOCKOUT_MS;
                node.putInt("failedAttempts", 0);
                node.putLong("lockoutUntilMs", until);
                return new AuthResult(false, null, "Account locked.", until);
            }
            node.putInt("failedAttempts", attempts);
            return new AuthResult(false, null, "Invalid email or password.", 0L);
        }

        node.putInt("failedAttempts", 0);
        node.putLong("lockoutUntilMs", 0L);
        RoleMenuBar.Role role = parseRole(node.get("role", "ENGINEER"));
        System.err.println("[AUTH-DEBUG] Login success for email=" + email + " role=" + role);
        return new AuthResult(true, role, "Login successful.", 0L);
    }

    public static synchronized boolean createUser(String rawEmail, RoleMenuBar.Role role, char[] password) {
        String email = normalize(rawEmail);
        if (email.isEmpty() || role == null || password == null || password.length == 0) {
            return false;
        }
        String key = nodeKey(email);
        try {
            if (ROOT.nodeExists(key)) {
                return false;
            }
        } catch (BackingStoreException e) {
            return false;
        }
        String hash = PasswordHasher.hash(password);
        Preferences node = ROOT.node(key);
        node.put("email", email);
        node.put("role", role.name());
        node.put("passwordHash", hash);
        node.putInt("failedAttempts", 0);
        node.putLong("lockoutUntilMs", 0L);
        node.putLong("createdAtMs", System.currentTimeMillis());
        return true;
    }

    public static synchronized boolean updatePassword(String rawEmail, char[] newPassword) {
        String email = normalize(rawEmail);
        String key = nodeKey(email);
        try {
            if (!ROOT.nodeExists(key)) return false;
        } catch (BackingStoreException e) {
            return false;
        }
        ROOT.node(key).put("passwordHash", PasswordHasher.hash(newPassword));
        return true;
    }

    public static synchronized boolean updateEmail(String rawOldEmail, String rawNewEmail) {
        String oldEmail = normalize(rawOldEmail);
        String newEmail = normalize(rawNewEmail);
        if (oldEmail.equals(newEmail)) return true;
        String oldKey = nodeKey(oldEmail);
        String newKey = nodeKey(newEmail);
        try {
            if (!ROOT.nodeExists(oldKey)) return false;
            if (ROOT.nodeExists(newKey)) return false;
            Preferences old = ROOT.node(oldKey);
            String role = old.get("role", "ENGINEER");
            String hash = old.get("passwordHash", "");
            long createdAt = old.getLong("createdAtMs", System.currentTimeMillis());
            old.removeNode();
            Preferences next = ROOT.node(newKey);
            next.put("email", newEmail);
            next.put("role", role);
            next.put("passwordHash", hash);
            next.putInt("failedAttempts", 0);
            next.putLong("lockoutUntilMs", 0L);
            next.putLong("createdAtMs", createdAt);
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    public static synchronized boolean deleteUser(String rawEmail) {
        String email = normalize(rawEmail);
        String key = nodeKey(email);
        try {
            if (!ROOT.nodeExists(key)) return false;
            ROOT.node(key).removeNode();
            return true;
        } catch (BackingStoreException e) {
            return false;
        }
    }

    public static synchronized List<UserRecord> getAll() {
        List<UserRecord> list = new ArrayList<>();
        try {
            for (String key : ROOT.childrenNames()) {
                Preferences node = ROOT.node(key);
                String email = node.get("email", "");
                if (email.isEmpty()) continue;
                RoleMenuBar.Role role = parseRole(node.get("role", "ENGINEER"));
                long createdAt = node.getLong("createdAtMs", 0L);
                list.add(new UserRecord(email, role, createdAt));
            }
        } catch (BackingStoreException ignored) {}
        list.sort((a, b) -> Long.compare(a.createdAtMs(), b.createdAtMs()));
        return list;
    }

    public static long remainingLockoutMs(String rawEmail) {
        String key = nodeKey(normalize(rawEmail));
        try {
            if (!ROOT.nodeExists(key)) return 0L;
            long until = ROOT.node(key).getLong("lockoutUntilMs", 0L);
            return Math.max(0L, until - System.currentTimeMillis());
        } catch (BackingStoreException e) {
            return 0L;
        }
    }

    static synchronized void ensureDefaults() {
        migrateEngineerPreferences();
        repairSeededAccounts();
        seedIfAbsent("juandelacruz@engr.com", RoleMenuBar.Role.ENGINEER, "dummypassword123".toCharArray());
        seedIfAbsent("juandelacruz2@officer.com", RoleMenuBar.Role.OFFICER, "dummy123".toCharArray());
        seedIfAbsent("juandelacruz3@head.com", RoleMenuBar.Role.HEAD, "dummy123".toCharArray());
        // Always clear lockouts for demo accounts so they remain accessible
        clearLockout("juandelacruz@engr.com");
        clearLockout("juandelacruz2@officer.com");
        clearLockout("juandelacruz3@head.com");
    }

    private static void migrateEngineerPreferences() {
        String engEmail = normalize(EngineerPreferences.getEmail());
        if (engEmail.isEmpty()) return;
        String key = nodeKey(engEmail);
        try {
            if (ROOT.nodeExists(key)) return;
            String hash = EngineerPreferences.getPasswordHash();
            // Only migrate if the stored hash is a valid PBKDF2 hash produced by PasswordHasher.
            // Old BCrypt or empty hashes must not be migrated — seedIfAbsent will create a fresh account.
            if (!isPbkdf2Hash(hash)) {
                System.err.println("[AUTH-DEBUG] Migration skipped for email=" + engEmail + ": stored hash is not valid PBKDF2.");
                return;
            }
            Preferences node = ROOT.node(key);
            node.put("email", engEmail);
            node.put("role", RoleMenuBar.Role.ENGINEER.name());
            node.put("passwordHash", hash);
            node.putInt("failedAttempts", 0);
            node.putLong("lockoutUntilMs", 0L);
            node.putLong("createdAtMs", System.currentTimeMillis());
        } catch (BackingStoreException ignored) {}
    }

    /** Removes any seeded demo account whose stored hash is not valid PBKDF2, so seedIfAbsent recreates it. */
    private static void repairSeededAccounts() {
        repairIfInvalidHash("juandelacruz@engr.com");
        repairIfInvalidHash("juandelacruz2@officer.com");
        repairIfInvalidHash("juandelacruz3@head.com");
    }

    private static void repairIfInvalidHash(String email) {
        String key = nodeKey(normalize(email));
        try {
            if (!ROOT.nodeExists(key)) return;
            Preferences node = ROOT.node(key);
            String hash = node.get("passwordHash", "");
            if (!isPbkdf2Hash(hash)) {
                System.err.println("[AUTH-DEBUG] Repairing account with invalid hash for email=" + email);
                node.removeNode();
            }
        } catch (BackingStoreException ignored) {}
    }

    /** Clears the failed-attempts counter and lockout timestamp for an account. */
    public static synchronized void clearLockout(String rawEmail) {
        String key = nodeKey(normalize(rawEmail));
        try {
            if (!ROOT.nodeExists(key)) return;
            Preferences node = ROOT.node(key);
            node.putInt("failedAttempts", 0);
            node.putLong("lockoutUntilMs", 0L);
        } catch (BackingStoreException ignored) {}
    }

    private static void seedIfAbsent(String email, RoleMenuBar.Role role, char[] password) {
        String key = nodeKey(normalize(email));
        try {
            if (!ROOT.nodeExists(key)) {
                String hash = PasswordHasher.hash(password);
                Preferences node = ROOT.node(key);
                node.put("email", normalize(email));
                node.put("role", role.name());
                node.put("passwordHash", hash);
                node.putInt("failedAttempts", 0);
                node.putLong("lockoutUntilMs", 0L);
                node.putLong("createdAtMs", System.currentTimeMillis());
            }
        } catch (BackingStoreException ignored) {}
    }

    /** Returns true if {@code hash} is in the PBKDF2 format produced by PasswordHasher: "iterations:base64salt:base64key". */
    private static boolean isPbkdf2Hash(String hash) {
        if (hash == null || hash.isBlank()) return false;
        String[] p = hash.split(":");
        if (p.length != 3) return false;
        try {
            Integer.parseInt(p[0]);
            java.util.Base64.getDecoder().decode(p[1]);
            java.util.Base64.getDecoder().decode(p[2]);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static String nodeKey(String normalizedEmail) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(normalizedEmail.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable");
        }
    }

    static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static RoleMenuBar.Role parseRole(String s) {
        try {
            return RoleMenuBar.Role.valueOf(s);
        } catch (Exception e) {
            return RoleMenuBar.Role.ENGINEER;
        }
    }
}
