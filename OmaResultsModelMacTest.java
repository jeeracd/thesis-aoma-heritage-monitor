import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OmaResultsModelMacTest {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("oma_mac_test_");
        Files.writeString(dir.resolve("summary.properties"), "status=ok\n", StandardCharsets.UTF_8);
        String csv = String.join(
                "\n",
                "mode_index,frequency_hz,damping_ratio,mpc,mpd,phi_accelX,phi_accelY,phi_accelZ",
                "1,1.0,0.01,1.0,0.0,1.0,0.0,0.0",
                "2,2.0,0.02,1.0,0.0,0.0,1.0,0.0",
                ""
        );
        Files.writeString(dir.resolve("modal_properties.csv"), csv, StandardCharsets.UTF_8);

        OmaResultsModel m = OmaResultsModel.loadFromDir(dir);
        double[][] mac = m.macMatrix();

        if (mac.length != 2 || mac[0].length != 2) {
            throw new IllegalStateException("Expected 2x2 MAC matrix");
        }
        assertClose(mac[0][0], 1.0, "mac(1,1)");
        assertClose(mac[1][1], 1.0, "mac(2,2)");
        assertClose(mac[0][1], 0.0, "mac(1,2)");
        assertClose(mac[1][0], 0.0, "mac(2,1)");

        System.out.println("OmaResultsModelMacTest OK");
    }

    private static void assertClose(double a, double b, String name) {
        if (!(Double.isFinite(a) && Math.abs(a - b) < 1e-9)) {
            throw new IllegalStateException("Mismatch " + name + ": " + a + " vs " + b);
        }
    }
}

