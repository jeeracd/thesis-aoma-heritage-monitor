public final class ColorMap {
    private ColorMap() {}

    public static int rgb(double t) {
        double tt = clamp01(t);
        int r;
        int g;
        int b;
        if (tt < 0.25) {
            double u = tt / 0.25;
            r = 0;
            g = (int) (u * 180);
            b = 255;
        } else if (tt < 0.5) {
            double u = (tt - 0.25) / 0.25;
            r = 0;
            g = 180 + (int) (u * 75);
            b = 255 - (int) (u * 255);
        } else if (tt < 0.75) {
            double u = (tt - 0.5) / 0.25;
            r = (int) (u * 255);
            g = 255;
            b = 0;
        } else {
            double u = (tt - 0.75) / 0.25;
            r = 255;
            g = 255 - (int) (u * 255);
            b = 0;
        }
        return (r << 16) | (g << 8) | b;
    }

    private static double clamp01(double v) {
        return Math.max(0, Math.min(1, v));
    }
}

