package co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out;

public interface SendEmailPort {

    void send(
            String recipient,
            String subject,
            String body);
}