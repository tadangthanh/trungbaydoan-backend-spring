
package vnua.edu.xdptpm09.service;

public interface IEmailService {
    void sendMailVerification(String to, String code);

    void sendSimpleMessage(String to, String subject, String text);
}
