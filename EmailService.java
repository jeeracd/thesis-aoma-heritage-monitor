public interface EmailService {
    boolean send(String to, String subject, String body);
}

