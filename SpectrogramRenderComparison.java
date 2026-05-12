import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SpectrogramRenderComparison {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("spectrogram-repro-");
        Path csv = dir.resolve("synthetic.csv");
        Files.writeString(csv, syntheticCsv());

        SpectrogramData data = SpectrogramGenerator.generateDataFromCsv(csv.toFile());
        BufferedImage before = SpectrogramGenerator.renderImageLegacyMinMax(data);
        BufferedImage after = SpectrogramGenerator.renderImage(data);

        File outBefore = new File("spectrogram_before.png");
        File outAfter = new File("spectrogram_after.png");
        ImageIO.write(before, "png", outBefore);
        ImageIO.write(after, "png", outAfter);

        System.out.println("Wrote: " + outBefore.getAbsolutePath());
        System.out.println("Wrote: " + outAfter.getAbsolutePath());
        System.out.println("Shape (freq_bins, time_frames) = (" + data.frequencyBins() + ", " + data.timeFrames() + ")");
        System.out.println("vminDb=" + data.vminDb() + " vmaxDb=" + data.vmaxDb());
        System.out.println("fsHz=" + data.fsHz() + " durationSec=" + data.durationSec());

        SpectrogramData blueData = SpectrogramGenerator.generateDataFromSignal(constantSignal(30000), 100.0);
        BufferedImage blue = SpectrogramGenerator.renderImage(blueData);
        File outBlue = new File("spectrogram_blue_before.png");
        ImageIO.write(blue, "png", outBlue);
        System.out.println("Wrote: " + outBlue.getAbsolutePath());

        SpectrogramData artifact = constantDbArtifact();
        BufferedImage artifactImg = SpectrogramGenerator.renderImage(artifact);
        File outArtifact = new File("spectrogram_blue_artifact_before.png");
        ImageIO.write(artifactImg, "png", outArtifact);
        System.out.println("Wrote: " + outArtifact.getAbsolutePath());

        SpectrogramData sampleFormat = SpectrogramGenerator.generateDataFromCsv(sampleFormatCsv(dir).toFile());
        BufferedImage sampleImg = SpectrogramGenerator.renderImage(sampleFormat);
        File outSample = new File("spectrogram_sample_format.png");
        ImageIO.write(sampleImg, "png", outSample);
        System.out.println("Wrote: " + outSample.getAbsolutePath());
    }

    private static String syntheticCsv() {
        StringBuilder sb = new StringBuilder();
        double fs = 200.0;
        for (int i = 0; i < 8192; i++) {
            double t = i / fs;
            double constant = 0.0;
            double x = Math.sin(2 * Math.PI * 5 * t)
                    + 0.5 * Math.sin(2 * Math.PI * 20 * t)
                    + 0.1 * Math.sin(2 * Math.PI * 60 * t);
            sb.append(t).append(",").append(constant).append(",").append(x).append("\n");
        }
        return sb.toString();
    }

    private static double[] constantSignal(int n) {
        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = 0.0;
        }
        return x;
    }

    private static SpectrogramData constantDbArtifact() {
        int bins = 64;
        int frames = 200;
        double[][] db = new double[bins][frames];
        for (int k = 0; k < bins; k++) {
            for (int f = 0; f < frames; f++) {
                db[k][f] = -240.0;
            }
        }
        return new SpectrogramData(db, bins, frames, 128, 128, 32, 100.0, 10.0, -120.0, 0.0);
    }

    private static Path sampleFormatCsv(Path dir) throws Exception {
        Path p = dir.resolve("sample_data_sensor.csv");
        StringBuilder sb = new StringBuilder();
        sb.append("timestamp\naccelX\naccelY\naccelZ\n");
        for (int i = 0; i < 300; i++) {
            double t = i / 100.0;
            double ax = 0.12 * Math.sin(2 * Math.PI * 6 * t);
            double ay = 0.08 * Math.cos(2 * Math.PI * 6 * t);
            double az = 9.8 + 0.05 * Math.sin(2 * Math.PI * 18 * t);
            sb.append("########\n");
            sb.append(ax).append("\n");
            sb.append(ay).append("\n");
            sb.append(az).append("\n");
        }
        Files.writeString(p, sb.toString());
        return p;
    }
}

