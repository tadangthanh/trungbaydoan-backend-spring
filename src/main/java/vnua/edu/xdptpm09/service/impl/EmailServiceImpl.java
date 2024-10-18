
package vnua.edu.xdptpm09.service.impl;


import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import vnua.edu.xdptpm09.exception.BadRequestException;
import vnua.edu.xdptpm09.service.IEmailService;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender javaMailSender;

    @Async
    public void sendMailVerification(String to, String code) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(to);
            helper.setSubject("Mã xác thực tài khoản");
            helper.setText("Mã xác thực tài khoản của bạn là: <h2>" + code + "</h2>", true);
            this.javaMailSender.send(message);
        } catch (Exception var6) {
            throw new BadRequestException("Gửi mail thất bại");
        }
    }

    @Async
    public void sendSimpleMessage(String to, String subject, String text) {
        MimeMessage message = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            this.javaMailSender.send(message);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }


}
