public final class ColorMapTest {
    public static void main(String[] args) {
        testEndpoints();
        testMidpoints();
        System.out.println("ALL TESTS PASSED");
    }

    private static void testEndpoints() {
        int blue = ColorMap.rgb(0.0);
        assertRgb(blue, 0, 0, 255, "t=0 must be blue");

        int red = ColorMap.rgb(1.0);
        assertRgb(red, 255, 0, 0, "t=1 must be red");
    }

    private static void testMidpoints() {
        int cyan = ColorMap.rgb(0.25);
        assertTrue(g(cyan) >= 170 && b(cyan) == 255, "t=0.25 should be cyan-ish");

        int green = ColorMap.rgb(0.50);
        assertRgb(green, 0, 255, 0, "t=0.5 must be green");

        int yellow = ColorMap.rgb(0.75);
        assertRgb(yellow, 255, 255, 0, "t=0.75 must be yellow");
    }

    private static int r(int rgb) { return (rgb >> 16) & 0xFF; }
    private static int g(int rgb) { return (rgb >> 8) & 0xFF; }
    private static int b(int rgb) { return rgb & 0xFF; }

    private static void assertRgb(int rgb, int r, int g, int b, String msg) {
        if (r(rgb) != r || g(rgb) != g || b(rgb) != b) {
            throw new AssertionError(msg + " (got " + r(rgb) + "," + g(rgb) + "," + b(rgb) + ")");
        }
    }

    private static void assertTrue(boolean ok, String msg) {
        if (!ok) {
            throw new AssertionError(msg);
        }
    }
}

