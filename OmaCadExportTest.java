import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class OmaCadExportTest {
    public static void main(String[] args) throws Exception {
        Path outDir = Path.of("pyoma2_out_test");
        OmaResultsModel model = OmaResultsModel.loadFromDir(outDir);
        if (model.modes().isEmpty()) {
            throw new IllegalStateException("Expected modes.");
        }

        Path tmp = Files.createTempDirectory("oma_export_test");

        File csv = tmp.resolve("oma.csv").toFile();
        File xls = tmp.resolve("oma.xml").toFile();
        File pdf = tmp.resolve("oma.pdf").toFile();
        File dxf = tmp.resolve("oma.dxf").toFile();

        OmaResultsExport.writeCsv(csv, model.modes());
        OmaResultsExport.writeExcelXml(xls, model.modes(), "OmaResults");
        OmaResultsExport.writePdfSummary(pdf, model, model.modes());
        OmaResultsExport.writeDxfPlot(dxf, model, CadViewportPanel.ViewType.FREQUENCY, List.of());

        if (!csv.exists() || !xls.exists() || !pdf.exists() || !dxf.exists()) {
            throw new IllegalStateException("Expected export files to exist.");
        }
    }
}

