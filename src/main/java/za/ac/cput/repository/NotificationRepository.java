package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.PatientTicket;
import za.ac.cput.domain.enums.NotificationStatus;
import za.ac.cput.domain.enums.NotificationType;
import za.ac.cput.domain.user.ClinicStaff;
import za.ac.cput.domain.user.Doctor;
import za.ac.cput.domain.user.Patient;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    List<Notification> findByPatient(Patient patient);

    List<Notification> findByDoctor(Doctor doctor);

    List<Notification> findByClinicStaff(ClinicStaff clinicStaff);

    List<Notification> findByNotificationStatus(NotificationStatus notificationStatus);

    List<Notification> findByNotificationType(NotificationType notificationType);

    // NEW — a notification can reference an appointment or ticket without its
    // patient_id necessarily being set too (defensive), so patient cascade
    // delete gathers by all three to avoid leaving an orphaned FK reference.
    List<Notification> findByAppointmentIn(List<Appointment> appointments);

    List<Notification> findByTicketIn(List<PatientTicket> tickets);

    // NEW — single-column updates. Rebuilding the whole Notification through
    // its Builder (as markAsRead used to) risks dropping fields it forgets to copy.
    @Modifying
    @Transactional
    @Query("update Notification n set n.notificationStatus = za.ac.cput.domain.enums.NotificationStatus.READ where n.notificationId = :id")
    int markAsRead(@Param("id") int id);

    @Modifying
    @Transactional
    @Query("update Notification n set n.emailSent = true where n.notificationId = :id")
    int markEmailSent(@Param("id") int id);
}
