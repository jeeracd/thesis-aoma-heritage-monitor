import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SpectrogramGeneratorTest {
    public static void main(String[] args) throws Exception {
        testDataShapeAndFinite();
        testRendersWithGradient();
        testSelectsNonConstantSignalColumn();
        testRemovesStrongDC();
        testRejectsTooSmall();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testDataShapeAndFinite() throws Exception {
        Path dir = Files.createTempDirectory("specgen-");
        Path f = dir.resolve("sig.csv");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2048; i++) {
            double t = i / 200.0;
            double x = Math.sin(2 * Math.PI * 5 * t) + 0.5 * Math.sin(2 * Math.PI * 20 * t);
            sb.append(t).append(",").append(x).append("\n");
        }
        Files.writeString(f, sb.toString());

        SpectrogramData data = SpectrogramGenerator.generateDataFromCsv(f.toFile());
        assertTrue(data.frequencyBins() == 256, "expected 256 frequency bins");
        assertTrue(data.timeFrames() == 13, "expected 13 time frames");
        assertTrue(data.db().length == data.frequencyBins(), "db rows match bins");
        assertTrue(data.db()[0].length == data.timeFrames(), "db cols match frames");
        for (int k = 0; k < data.frequencyBins(); k++) {
            for (int t = 0; t < data.timeFrames(); t++) {
                double v = data.db()[k][t];
                assertTrue(Double.isFinite(v), "db contains finite values");
            }
        }
        assertTrue(data.vmaxDb() > data.vminDb(), "vmin/vmax must have range");
    }

    private static void testRendersWithGradient() throws Exception {
        Path dir = Files.createTempDirectory("specgen-");
        Path f = dir.resolve("sig.csv");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4096; i++) {
            double t = i / 200.0;
            double x = Math.sin(2 * Math.PI * 5 * t) + 0.5 * Math.sin(2 * Math.PI * 20 * t);
            sb.append(t).append(",").append(x).append("\n");
        }
        Files.writeString(f, sb.toString());

        SpectrogramData data = SpectrogramGenerator.generateDataFromCsv(f.toFile());
        assertTrue(data.frequencyBins() == 256, "expected 256 frequency bins");
        assertTrue(data.timeFrames() == 29, "expected 29 time frames");
        BufferedImage img = SpectrogramGenerator.renderImage(data);
        int unique = countUniqueColors(img);
        assertTrue(unique > 50, "rendered image should have a gradient (unique colors > 50)");
        BufferedImage legacy = SpectrogramGenerator.renderImageLegacyMinMax(data);
        int legacyUnique = countUniqueColors(legacy);
        assertTrue(legacyUnique > 10, "legacy render should be non-empty");
    }

    private static void testRejectsTooSmall() throws Exception {
        Path dir = Files.createTempDirectory("specgen-");
        Path f = dir.resolve("small.csv");
        Files.writeString(f, "0,1\n1,2\n2,3\n");
        try {
            SpectrogramGenerator.generateFromCsv(f.toFile());
        } catch (Exception e) {
            return;
        }
        throw new AssertionError("expected generateFromCsv to fail for too few samples");
    }

    private static void testSelectsNonConstantSignalColumn() throws Exception {
        Path dir = Files.createTempDirectory("specgen-");
        Path f = dir.resolve("multi.csv");
        StringBuilder sb = new StringBuilder();
        double fs = 100.0;
        for (int i = 0; i < 4096; i++) {
            double t = i / fs;
            double constant = 0.0;
            double sig = Math.sin(2 * Math.PI * 8 * t);
            sb.append(t).append(",").append(constant).append(",").append(sig).append("\n");
        }
        Files.writeString(f, sb.toString());

        SpectrogramData data = SpectrogramGenerator.generateDataFromCsv(f.toFile());
        BufferedImage img = SpectrogramGenerator.renderImage(data);
        int unique = countUniqueColors(img);
        assertTrue(unique > 50, "auto column selection should not render as a solid color");
    }

    private static void testRemovesStrongDC() throws Exception {
        int n = 4096;
        double fs = 200.0;
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            double t = i / fs;
            x[i] = 9.8 + 0.1 * Math.sin(2 * Math.PI * 12 * t);
        }
        SpectrogramData data = SpectrogramGenerator.generateDataFromSignal(x, fs);
        BufferedImage img = SpectrogramGenerator.renderImage(data);
        int unique = countUniqueColors(img);
        assertTrue(unique > 30, "DC removal should preserve visible spectral content");
    }

    private static int countUniqueColors(BufferedImage img) {
        java.util.HashSet<Integer> set = new java.util.HashSet<>();
        int w = img.getWidth();
        int h = img.getHeight();
        int stepX = Math.max(1, w / 300);
        int stepY = Math.max(1, h / 300);
        for (int y = 0; y < h; y += stepY) {
            for (int x = 0; x < w; x += stepX) {
                set.add(img.getRGB(x, y));
            }
        }
        return set.size();
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

