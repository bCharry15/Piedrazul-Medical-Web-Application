package co.edu.unicauca.piedraazul.agenda.notifications.internal.application.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import co.edu.unicauca.piedraazul.agenda.appointments.api.event.AppointmentCreatedEvent;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.NotificationLogPort;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.application.port.out.SendEmailPort;
import co.edu.unicauca.piedraazul.agenda.notifications.internal.domain.model.NotificationLog;

@Service
public class NotificationService {

    private final NotificationLogPort notificationLogPort;
    private final SendEmailPort sendEmailPort;

    @Value("${notification.mail.enabled:false}")
    private boolean mailEnabled;

    public NotificationService(
            NotificationLogPort notificationLogPort,
            SendEmailPort sendEmailPort) {
        this.notificationLogPort = notificationLogPort;
        this.sendEmailPort = sendEmailPort;
    }

    public void processAppointmentCreated(AppointmentCreatedEvent event) {

        if (event == null) {
            System.err.println(
                    "NOTIFICATIONS -> Solicitud de notificación vacía.");
            return;
        }

        try {
            if (mailEnabled) {
                sendConfirmationEmail(event);
                saveLog(event, "ENVIADO", null);

                System.out.println(
                        "NOTIFICATIONS -> Correo real enviado.");
                System.out.println(
                        "NOTIFICATIONS -> Appointment ID: "
                                + event.getAppointmentId());
                System.out.println(
                        "NOTIFICATIONS -> Patient: "
                                + event.getPatient());
                System.out.println(
                        "NOTIFICATIONS -> Email: "
                                + event.getPatientEmail());

            } else {
                saveLog(
                        event,
                        "SIMULADO",
                        "Envío real de email desactivado en ambiente local.");

                System.out.println(
                        "NOTIFICATIONS -> Envío de email desactivado. Modo SIMULADO.");
                System.out.println(
                        "NOTIFICATIONS -> Appointment ID: "
                                + event.getAppointmentId());
                System.out.println(
                        "NOTIFICATIONS -> Patient: "
                                + event.getPatient());
                System.out.println(
                        "NOTIFICATIONS -> Email: "
                                + event.getPatientEmail());
                System.out.println(
                        "NOTIFICATIONS -> Phone: "
                                + event.getPatientPhone());
                System.out.println(
                        "NOTIFICATIONS -> Doctor/Therapist: "
                                + event.getDoctor());
                System.out.println(
                        "NOTIFICATIONS -> Date: "
                                + event.getDate());
                System.out.println(
                        "NOTIFICATIONS -> Time: "
                                + event.getTime());
            }

        } catch (Exception exception) {
            saveLog(event, "FALLIDO", exception.getMessage());

            System.err.println(
                    "NOTIFICATIONS -> Error al procesar notificación.");
            System.err.println(
                    "NOTIFICATIONS -> Detalle: "
                            + exception.getMessage());
        }
    }

    private void sendConfirmationEmail(AppointmentCreatedEvent event) {

        if (event.getPatientEmail() == null
                || event.getPatientEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "El paciente no tiene correo registrado.");
        }

        String subject = "Confirmación de cita - Piedra Azul";

        String body =
                "Hola " + event.getPatient() + ",\n\n"
                        + "Tu cita fue agendada correctamente.\n\n"
                        + "Médico/Terapista: " + event.getDoctor() + "\n"
                        + "Fecha: " + event.getDate() + "\n"
                        + "Hora: " + event.getTime() + "\n\n"
                        + "Gracias por usar el sistema Piedra Azul.";

        sendEmailPort.send(
                event.getPatientEmail(),
                subject,
                body);
    }

    private void saveLog(
            AppointmentCreatedEvent event,
            String status,
            String errorDetail) {

        NotificationLog notificationLog = new NotificationLog();

        notificationLog.setAppointmentId(event.getAppointmentId());
        notificationLog.setPatient(event.getPatient());
        notificationLog.setDoctor(event.getDoctor());
        notificationLog.setPatientEmail(event.getPatientEmail());
        notificationLog.setPatientPhone(event.getPatientPhone());
        notificationLog.setStatus(status);
        notificationLog.setSentAt(LocalDateTime.now());

        notificationLogPort.save(notificationLog);

        System.out.println(
                "NOTIFICATIONS -> Notificación procesada con estado: "
                        + status);

        if (errorDetail != null && !errorDetail.isBlank()) {
            System.out.println(
                    "NOTIFICATIONS -> Detalle: " + errorDetail);
        }
    }

    public List<NotificationLog> listNotifications() {
        return notificationLogPort.findAll();
    }
}