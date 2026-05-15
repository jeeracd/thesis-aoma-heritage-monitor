import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public final class DesktopEmailService implements EmailService {
    @Override
    public boolean send(String to, String subject, String body) {
        try {
            if (to == null || to.isBlank()) {
                return false;
            }
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            Desktop d = Desktop.getDesktop();
            if (!d.isSupported(Desktop.Action.MAIL)) {
                return false;
            }

            String s = subject == null ? "" : subject;
            String b = body == null ? "" : body;
            String uri = "mailto:" + encode(to.trim())
                    + "?subject=" + encode(s)
                    + "&body=" + encode(b);
            d.mail(new URI(uri));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String encode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}

