package com.pofo.backend.domain.user.join.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    /** ✅ 인증 코드 이메일 발송 */
    public void sendVerificationEmail(String toEmail, String code) {
        String subject = "이메일 인증 코드";
        String content = "<h3>안녕하세요!</h3>" +
                "<p>아래의 인증 코드를 입력하여 이메일을 인증하세요:</p>" +
                "<h2>" + code + "</h2>";

        sendHtmlEmail(toEmail, subject, content);
    }

    /** ✅ HTML 이메일 전송 */
    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }
}
