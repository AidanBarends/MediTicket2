package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.repository.NotificationRepository;
import za.ac.cput.service.impl.INotificationService;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.enums.NotificationStatus;
import za.ac.cput.domain.enums.NotificationType;
import za.ac.cput.domain.user.ClinicStaff;
import za.ac.cput.domain.user.Doctor;
import za.ac.cput.domain.user.Patient;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.PatientTicket;
import za.ac.cput.domain.user.User;
import za.ac.cput.factory.NotificationFactory;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmailService emailService){
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    // ===== Flow notifications =====
    // One entry point per recipient type, used by AppointmentService,
    // PatientTicketService and PaymentService.
    //   - status starts as SENT: an in-app notification is delivered the moment
    //     it is saved; it becomes READ when the user opens it.
    //   - stage records what the notification is about (TICKET_OPEN, PAYMENT_DUE...).
    //   - patients and doctors also get an email copy; staff are in-app only,
    //     otherwise every booking would email every nurse and admin.

    public Notification notifyPatient(Patient patient, String message, String stage,
                                      PatientTicket ticket, Appointment appointment, String emailSubject) {
        Notification saved = save(message, stage, patient, null, null, ticket, appointment);
        email(saved, patient, emailSubject, message);
        return saved;
    }

    public Notification notifyDoctor(Doctor doctor, String message, String stage,
                                     PatientTicket ticket, Appointment appointment, String emailSubject) {
        Notification saved = save(message, stage, null, doctor, null, ticket, appointment);
        email(saved, doctor, emailSubject, message);
        return saved;
    }

    public Notification notifyStaff(ClinicStaff staff, String message, String stage,
                                    PatientTicket ticket, Appointment appointment) {
        return save(message, stage, null, null, staff, ticket, appointment);
    }

    public boolean markAsRead(int notificationId) {
        return notificationRepository.markAsRead(notificationId) > 0;
    }

    private Notification save(String message, String stage, Patient patient, Doctor doctor,
                              ClinicStaff staff, PatientTicket ticket, Appointment appointment) {
        Notification built = NotificationFactory.createNotification(
                0, NotificationType.EMAIL, NotificationStatus.SENT, message,
                patient, doctor, staff, ticket, appointment, LocalDateTime.now());
        if (built == null) return null;
        Notification withStage = new Notification.Builder()
                .copy(built)
                .setRelatedStatus(stage)
                .setEmailSent(false)
                .build();
        return notificationRepository.save(withStage);
    }

    private void email(Notification saved, User recipient, String subject, String message) {
        if (saved == null || recipient == null || subject == null) return;
        String firstName = recipient.getName() != null ? recipient.getName().getFirstName() : null;
        emailService.sendNotificationEmail(saved.getNotificationId(), recipient.getEmail(),
                firstName, subject, message);
    }

    @Override
    public Notification create(Notification notification){
        return notificationRepository.save(notification);
    }

    @Override
    public Notification read(Integer id){
        return notificationRepository.findById(id).orElse(null);
    }

    @Override
    public Notification update(Notification notification){
        return notificationRepository.save(notification);
    }

    @Override
    public void delete(Integer id){
        notificationRepository.deleteById(id);
    }

    @Override
    public List<Notification> getAll(){
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> findByPatient(Patient patient){
        return notificationRepository.findByPatient(patient);
    }

    @Override
    public List<Notification> findByDoctor(Doctor doctor){
        return notificationRepository.findByDoctor(doctor);
    }

    @Override
    public List<Notification> findByClinicStaff(ClinicStaff clinicStaff){
        return notificationRepository.findByClinicStaff(clinicStaff);
    }
    @Override
    public List<Notification> findByNotificationType(NotificationType notificationType){
        return notificationRepository.findByNotificationType(notificationType);
    }

    @Override
    public List<Notification> findByNotificationStatus(NotificationStatus notificationStatus){
        return notificationRepository.findByNotificationStatus(notificationStatus);
    }


}