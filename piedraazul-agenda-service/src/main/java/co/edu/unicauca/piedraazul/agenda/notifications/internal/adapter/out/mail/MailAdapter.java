package co.edu.unicauca.piedraazul.agenda.notifications.internal.adapter.out.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.SendEmailPort;

@Component
public class MailAdapter implements SendEmailPort {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${notification.mail.from:no-reply@piedraazul.com}")
    private String sender;

    public MailAdapter(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(
            String recipient,
            String subject,
            String body) {

        if (!mailEnabled) {
            return;
        }

        if (recipient == null || recipient.isBlank()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}