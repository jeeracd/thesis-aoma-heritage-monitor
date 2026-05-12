import java.awt.image.BufferedImage;

public final class SpectrogramColorMappingTest {
    public static void main(String[] args) {
        testClampingAndGradient();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testClampingAndGradient() {
        int bins = 2;
        int frames = 4;
        double vmin = -100.0;
        double vmax = 0.0;
        double mid = (vmin + vmax) / 2.0;

        double[][] db = new double[bins][frames];
        db[0][0] = vmin;
        db[0][1] = mid;
        db[0][2] = vmax;
        db[0][3] = vmin - 50.0;

        db[1][0] = vmax;
        db[1][1] = mid;
        db[1][2] = vmin;
        db[1][3] = vmax + 50.0;

        SpectrogramData data = new SpectrogramData(db, bins, frames, 4, 4, 2, 100.0, 1.0, vmin, vmax);
        BufferedImage img = SpectrogramGenerator.renderImage(data);

        int yForK0 = bins - 1 - 0;
        int yForK1 = bins - 1 - 1;

        assertEquals(img.getRGB(0, yForK0) & 0xFFFFFF, ColorMap.rgb(Math.pow(0.0, 0.55)) & 0xFFFFFF, "v=vmin maps to low color");
        assertEquals(img.getRGB(2, yForK0) & 0xFFFFFF, ColorMap.rgb(Math.pow(1.0, 0.55)) & 0xFFFFFF, "v=vmax maps to high color");

        int expectedMid = ColorMap.rgb(Math.pow(0.5, 0.55)) & 0xFFFFFF;
        assertEquals(img.getRGB(1, yForK0) & 0xFFFFFF, expectedMid, "v=mid maps to mid color");

        int expectedClampLow = ColorMap.rgb(Math.pow(0.0, 0.55)) & 0xFFFFFF;
        assertEquals(img.getRGB(3, yForK0) & 0xFFFFFF, expectedClampLow, "v below vmin clamps to low color");

        int expectedClampHigh = ColorMap.rgb(Math.pow(1.0, 0.55)) & 0xFFFFFF;
        assertEquals(img.getRGB(3, yForK1) & 0xFFFFFF, expectedClampHigh, "v above vmax clamps to high color");
    }

    private static void assertEquals(int a, int b, String msg) {
        if (a != b) {
            throw new AssertionError(msg + " (got 0x" + Integer.toHexString(a) + " expected 0x" + Integer.toHexString(b) + ")");
        }
    }
}

