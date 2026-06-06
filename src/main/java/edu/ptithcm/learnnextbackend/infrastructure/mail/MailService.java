package edu.ptithcm.learnnextbackend.infrastructure.mail;

public interface MailService {
    void sendActivationCode(String to, String fullName, String courseTitle, String activationCode);

    default void sendActivationCode(String to, String fullName, String courseTitle, String activationCode, String activationLink) {
        sendActivationCode(to, fullName, courseTitle, activationCode);
    }
}
