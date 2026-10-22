package za.ac.cput.domain;

import jakarta.persistence.*;
import za.ac.cput.domain.user.*;
import za.ac.cput.domain.enums.NotificationStatus;
import za.ac.cput.domain.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int notificationId;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    private NotificationStatus notificationStatus;

    private String notificationMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinic_staff_id")
    private ClinicStaff clinicStaff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private PatientTicket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    private LocalDateTime notificationDate;

    // NEW — snapshot of the stage this notification is about, recorded at
    // creation so an old notification keeps its meaning after the ticket moves
    // on. Plain VARCHAR (not an enum column) so ddl-auto=update can add it to
    // existing databases without a manual ALTER.
    @Column(length = 40)
    private String relatedStatus;

    // NEW — true once the notification was also delivered by email.
    private Boolean emailSent;

    protected Notification() {
        // Required by JPA
    }

    private Notification(Builder builder) {
        this.notificationId = builder.notificationId;
        this.notificationType = builder.notificationType;
        this.notificationStatus = builder.notificationStatus;
        this.notificationMessage = builder.notificationMessage;
        this.notificationDate = builder.notificationDate;
        this.relatedStatus = builder.relatedStatus;
        this.emailSent = builder.emailSent;
        this.appointment = builder.appointment;
        this.patient = builder.patient;
        this.doctor = builder.doctor;
        this.clinicStaff = builder.clinicStaff;
        this.ticket = builder.ticket;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public Patient getPatient() {
        return patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public ClinicStaff getClinicStaff() {
        return clinicStaff;
    }

    public PatientTicket getTicket() {
        return ticket;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public String getRelatedStatus() {
        return relatedStatus;
    }

    // Boolean (not boolean) so rows created before this column existed read as null.
    public boolean isEmailSent() {
        return Boolean.TRUE.equals(emailSent);
    }

    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }

    @Transient
    public User getRecipient() {
        if (patient != null) return patient;
        if (doctor != null) return doctor;
        if (clinicStaff != null) return clinicStaff;
        return null;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", notificationType=" + notificationType +
                ", notificationStatus=" + notificationStatus +
                ", notificationMessage='" + notificationMessage + '\'' +
                ", patient=" + patient +
                ", doctor=" + doctor +
                ", clinicStaff=" + clinicStaff +
                ", ticket=" + ticket +
                ", appointment=" + appointment +
                ", notificationDate=" + notificationDate +
                '}';
    }

    public static class Builder {

        private int notificationId;
        private NotificationType notificationType;
        private NotificationStatus notificationStatus;
        private String notificationMessage;

        private Patient patient;
        private Doctor doctor;
        private ClinicStaff clinicStaff;

        private PatientTicket ticket;
        private Appointment appointment;

        private LocalDateTime notificationDate;
        private String relatedStatus;
        private Boolean emailSent;

        public Builder setNotificationId(int notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public Builder setNotificationType(NotificationType notificationType) {
            this.notificationType = notificationType;
            return this;
        }

        public Builder setNotificationStatus(NotificationStatus notificationStatus) {
            this.notificationStatus = notificationStatus;
            return this;
        }

        public Builder setNotificationMessage(String notificationMessage) {
            this.notificationMessage = notificationMessage;
            return this;
        }

        public Builder setPatient(Patient patient) {
            this.patient = patient;
            return this;
        }

        public Builder setDoctor(Doctor doctor) {
            this.doctor = doctor;
            return this;
        }

        public Builder setClinicStaff(ClinicStaff clinicStaff) {
            this.clinicStaff = clinicStaff;
            return this;
        }

        public Builder setTicket(PatientTicket ticket) {
            this.ticket = ticket;
            return this;
        }

        public Builder setAppointment(Appointment appointment) {
            this.appointment = appointment;
            return this;
        }

        // Stage of the appointment/ticket/payment this notification is about,
        // recorded when it is created (e.g. TICKET_OPEN, PAYMENT_DUE).
        public Builder setRelatedStatus(String relatedStatus) {
            this.relatedStatus = relatedStatus;
            return this;
        }

        public Builder setEmailSent(Boolean emailSent) {
            this.emailSent = emailSent;
            return this;
        }

        public Builder setNotificationDate(LocalDateTime notificationDate) {
            this.notificationDate = notificationDate;
            return this;
        }

        public Builder copy(Notification notification) {
            this.notificationId = notification.notificationId;
            this.notificationType = notification.notificationType;
            this.notificationStatus = notification.notificationStatus;
            this.notificationMessage = notification.notificationMessage;
            this.patient = notification.patient;
            this.doctor = notification.doctor;
            this.clinicStaff = notification.clinicStaff;
            this.ticket = notification.ticket;
            this.appointment = notification.appointment;
            this.notificationDate = notification.notificationDate;
            this.relatedStatus = notification.relatedStatus;
            this.emailSent = notification.emailSent;
            return this;
        }

        public Notification build() {
            return new Notification(this);
        }
    }
}

