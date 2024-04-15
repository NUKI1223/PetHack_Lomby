package org.pethack.lorby.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendConfirmationCode(String to, int confirmationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(to);
        message.setSubject("Подтверждение регистрации");
        message.setText("Ваш код подтверждения: " + confirmationCode);
        emailSender.send(message);
    }
}
