package edu.ptithcm.learnnextbackend.infrastructure.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SimulatedMailService implements MailService {
    private static final Logger log = LoggerFactory.getLogger(SimulatedMailService.class);

    private final JavaMailSender mailSender;
    private final String mailFrom;

    public SimulatedMailService(JavaMailSender mailSender,
                                @Value("${mail.from:no-reply@learnext.local}") String mailFrom) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
    }

    @Override
    public void sendActivationCode(String to, String fullName, String courseTitle, String activationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(to);
        message.setSubject("Learnext activation code");
        message.setText("""
                Hi %s,

                Your payment for "%s" has been confirmed.
                Activation code: %s

                This code expires in 24 hours and can be used once.
                """.formatted(fullName, courseTitle, activationCode));

        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Simulated activation email could not be sent to {}. Code: {}", to, activationCode, ex);
        }
    }
}
