package co.edu.unicauca.piedraazul.agenda.appointments.internal.application.dto;
public class ChangeAppointmentStatusRequest {

    private String status;
    private String notes;

    public ChangeAppointmentStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
