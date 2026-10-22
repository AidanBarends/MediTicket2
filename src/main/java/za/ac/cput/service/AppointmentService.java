package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.Notification;
import za.ac.cput.domain.PatientTicket;
import za.ac.cput.domain.enums.ConfirmationStatus;
import za.ac.cput.domain.enums.NotificationStatus;
import za.ac.cput.domain.enums.NotificationType;
import za.ac.cput.domain.enums.StatusType;
import za.ac.cput.domain.user.ClinicStaff;
import za.ac.cput.domain.user.Doctor;
import za.ac.cput.domain.user.Patient;
import za.ac.cput.factory.NotificationFactory;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.service.impl.IAppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository repository;
    private final DoctorService doctorService;
    private final ClinicStaffService clinicStaffService;
    private final PatientTicketService patientTicketService;
    private final NotificationService notificationService;
    private final za.ac.cput.repository.PatientRepository patientRepository;

    @Autowired
    public AppointmentService(AppointmentRepository repository,
                              DoctorService doctorService,
                              ClinicStaffService clinicStaffService,
                              PatientTicketService patientTicketService,
                              NotificationService notificationService,
                              za.ac.cput.repository.PatientRepository patientRepository) {
        this.repository = repository;
        this.doctorService = doctorService;
        this.clinicStaffService = clinicStaffService;
        this.patientTicketService = patientTicketService;
        this.notificationService = notificationService;
        this.patientRepository = patientRepository;
    }

    @Override
    public Appointment create(Appointment appointment) {
        if (appointment == null) return null;
        Appointment saved = repository.save(appointment);

        // A new PENDING request needs a nurse or admin to act on it.
        if (saved.getConfirmationStatus() == ConfirmationStatus.PENDING) {
            notifyStaffOfNewBooking(saved);
        }

        return saved;
    }

    @Override
    public Appointment read(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Appointment update(Appointment appointment) {
        if (appointment == null) return null;
        if (!repository.existsById(appointment.getAppointmentId())) return null;
        return repository.save(appointment);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Appointment> getAll() {
        return repository.findAll();
    }

    @Override
    public List<Appointment> findByDoctorUserId(int doctorId) {
        return repository.findByDoctor_UserId(doctorId);
    }

    @Override
    public List<Appointment> findByStaffUserId(int staffId) {
        return repository.findByStaff_UserId(staffId);
    }

    @Override
    public List<Appointment> findByAppointmentDate(LocalDate appointmentDate) {
        return repository.findByAppointmentDate(appointmentDate);
    }

    @Override
    public List<Appointment> findByConfirmationStatus(ConfirmationStatus confirmationStatus) {
        return repository.findByConfirmationStatus(confirmationStatus);
    }

    @Override
    public List<Appointment> findByDoctorUserIdAndAppointmentDate(int doctorId, LocalDate appointmentDate) {
        return repository.findByDoctor_UserIdAndAppointmentDate(doctorId, appointmentDate);
    }

    @Override
    public List<Appointment> findByPatientUserId(int patientId) {
        return repository.findByPatient_UserId(patientId);
    }

    // ===== Orchestration methods =====

    @Override
    @Transactional
    public Appointment approveAppointment(int appointmentId, int doctorId, int staffId) {
        Appointment existing = read(appointmentId);
        if (existing == null) return null;
        if (existing.getConfirmationStatus() != ConfirmationStatus.PENDING) return null;

        Doctor doctor = doctorService.read(doctorId);
        if (doctor == null) return null;

        ClinicStaff staff = clinicStaffService.read(staffId);
        if (staff == null) return null;

        Patient patient = existing.getPatient();
        if (patient == null) return null; // shouldn't happen post-migration, but don't NPE if it does

        // Step 1: confirm the appointment, assigning doctor + staff
        Appointment confirmed = new Appointment.Builder()
                .copy(existing)
                .setDoctor(doctor)
                .setStaff(staff)
                .setConfirmationStatus(ConfirmationStatus.CONFIRMED)
                .build();
        confirmed = repository.save(confirmed);

        // Step 2: create the ticket, opened
        PatientTicket ticket = new PatientTicket.Builder()
                .setTicketDescription(confirmed.getReason())
                .setTicketCreatedDate(LocalDateTime.now())
                .setPatient(patient)
                .setAppointment(confirmed)
                .build();
        // OPEN is added inside patientTicketService.create() — adding it here
        // too gave every ticket two OPEN entries in its history.
        ticket = patientTicketService.create(ticket);

        // Step 3: notify patient + doctor
        notifyAppointmentConfirmed(patient, doctor, ticket, confirmed);

        return confirmed;
    }

    @Override
    @Transactional
    public Appointment rejectAppointment(int appointmentId, int staffId, String reason) {
        Appointment existing = read(appointmentId);
        if (existing == null) return null;
        if (existing.getConfirmationStatus() != ConfirmationStatus.PENDING) return null;

        ClinicStaff staff = clinicStaffService.read(staffId);
        if (staff == null) return null;

        Patient patient = existing.getPatient();

        Appointment rejected = new Appointment.Builder()
                .copy(existing)
                .setStaff(staff)
                .setConfirmationStatus(ConfirmationStatus.REJECTED)
                .build();
        rejected = repository.save(rejected);

        if (patient != null) {
            notifyAppointmentRejected(patient, reason, rejected);
        }

        return rejected;
    }

    // ===== Private notification helpers =====
    // Messages name the people and the slot so they make sense on their own,
    // in the app and in the email copy.

    private static final java.time.format.DateTimeFormatter DAY =
            java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy");

    private static String slot(Appointment a) {
        if (a == null || a.getAppointmentDate() == null) return "your requested date";
        String day = a.getAppointmentDate().format(DAY);
        return a.getAppointmentTime() != null ? day + " at " + a.getAppointmentTime() : day;
    }

    private static String fullName(za.ac.cput.domain.user.User user) {
        if (user == null || user.getName() == null) return "the patient";
        String first = user.getName().getFirstName() != null ? user.getName().getFirstName() : "";
        String last = user.getName().getLastName() != null ? user.getName().getLastName() : "";
        return (first + " " + last).trim();
    }

    private void notifyStaffOfNewBooking(Appointment appointment) {
        // The request body only carries the patient's id, so load the patient for the name.
        Patient patient = appointment.getPatient();
        String who = patient != null ? fullName(patientName(patient.getUserId())) : "A patient";
        String message = who + " requested an appointment for " + slot(appointment)
                + ". It is waiting for approval.";

        for (ClinicStaff staff : clinicStaffService.getAll()) {
            notificationService.notifyStaff(staff, message, "APPOINTMENT_PENDING", null, appointment);
        }
    }

    private za.ac.cput.domain.user.User patientName(int patientId) {
        return patientRepository.findById(patientId).orElse(null);
    }

    private void notifyAppointmentConfirmed(Patient patient, Doctor doctor, PatientTicket ticket, Appointment appointment) {
        String ticketRef = ticket != null ? " Ticket TK-" + String.format("%03d", ticket.getTicketId()) + " is open." : "";

        notificationService.notifyPatient(patient,
                "Your appointment on " + slot(appointment) + " with Dr. " + doctor.getName().getLastName()
                        + " is confirmed." + ticketRef,
                "TICKET_OPEN", ticket, appointment, "Appointment confirmed");

        notificationService.notifyDoctor(doctor,
                "New appointment assigned: " + fullName(patient) + ", " + slot(appointment) + "." + ticketRef,
                "TICKET_OPEN", ticket, appointment, "New appointment assigned");
    }

    private void notifyAppointmentRejected(Patient patient, String reason, Appointment appointment) {
        String message = (reason == null || reason.isBlank())
                ? "Your appointment request for " + slot(appointment) + " could not be approved. Please contact the clinic for details."
                : "Your appointment request for " + slot(appointment) + " could not be approved: " + reason;

        notificationService.notifyPatient(patient, message, "APPOINTMENT_REJECTED",
                null, appointment, "Appointment request not approved");
    }
}
