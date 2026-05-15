import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordHasher {
    private static final String ALG = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 120_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RAND = new SecureRandom();

    private PasswordHasher() {}

    public static String hash(char[] password) {
        if (password == null) {
            password = new char[0];
        }
        byte[] salt = new byte[SALT_BYTES];
        RAND.nextBytes(salt);
        byte[] dk = pbkdf2(password, salt, ITERATIONS, KEY_BITS);
        return ITERATIONS + ":" + b64(salt) + ":" + b64(dk);
    }

    public static boolean verify(char[] password, String stored) {
        if (stored == null || stored.isBlank()) {
            return false;
        }
        try {
            String[] parts = stored.split(":");
            if (parts.length != 3) {
                return false;
            }
            int it = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expected = Base64.getDecoder().decode(parts[2]);
            byte[] actual = pbkdf2(password == null ? new char[0] : password, salt, it, expected.length * 8);
            return constantTimeEquals(expected, actual);
        } catch (Exception ex) {
            return false;
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyBits) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyBits);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(ALG);
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("Password hashing is unavailable.");
        }
    }

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        int diff = 0;
        for (int i = 0; i < a.length; i++) {
            diff |= (a[i] ^ b[i]);
        }
        return diff == 0;
    }

    private static String b64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes == null ? new byte[0] : bytes);
    }
}

