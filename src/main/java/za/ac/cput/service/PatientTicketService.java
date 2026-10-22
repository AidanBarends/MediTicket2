package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.PatientTicket;
import za.ac.cput.domain.enums.ConfirmationStatus;
import za.ac.cput.domain.enums.StatusType;
import za.ac.cput.repository.AppointmentRepository;
import za.ac.cput.repository.PatientTicketRepository;
import za.ac.cput.service.impl.IPatientTicketService;

import java.util.List;

@Service
public class PatientTicketService implements IPatientTicketService {

    @Autowired
    private PatientTicketRepository repository;

    // NEW — raw repository, not AppointmentService, deliberately. AppointmentService
    // already depends on PatientTicketService (to auto-create the ticket at approval
    // time), so depending back on AppointmentService here would create a circular
    // bean dependency. Same pattern PaymentService already uses to reach across into
    // PatientTicket's table without going through its service layer.
    @Autowired
    private AppointmentRepository appointmentRepository;

    // NotificationService only depends on its repository, so injecting it
    // here creates no circular dependency.
    @Autowired
    private NotificationService notificationService;

    @Override
    public PatientTicket create(PatientTicket ticket) {
        if (ticket == null) return null;

        PatientTicket saved = repository.save(ticket);
        saved.addStatus(StatusType.OPEN);
        return repository.save(saved);
    }

    @Override
    public PatientTicket read(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public PatientTicket update(PatientTicket ticket) {
        if (ticket == null) return null;
        return repository.save(ticket);
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<PatientTicket> getAll() {
        return repository.findAll();
    }

    @Override
    public List<PatientTicket> findByCurrentStatus(StatusType status) {
        return repository.findByCurrentStatus(status);
    }

    @Override
    public List<PatientTicket> findByPatientUserId(int patientId) {
        return repository.findByPatientUserId(patientId);
    }

    @Override
    public PatientTicket progressStatus(int ticketId, StatusType newStatus, String notes) {
        PatientTicket ticket = read(ticketId);
        if (ticket == null) return null;

        boolean wasResolved = ticket.getCurrentStatus() == StatusType.RESOLVED;

        ticket.addStatus(newStatus, notes);
        PatientTicket saved = repository.save(ticket);

        completeAppointmentIfResolved(saved);

        // Only on the transition into RESOLVED, so a repeat call doesn't
        // send the nurse a second "ready for payment" notification.
        if (newStatus == StatusType.RESOLVED && !wasResolved) {
            notifyStaffConsultationComplete(saved);
        }

        return saved;
    }

    @Override
    public PatientTicket findByAppointment(Appointment appointment) {
        if (appointment == null) return null;
        return repository.findByAppointment(appointment);
    }

    // The nurse/admin who approved the appointment generates the payment,
    // so they are the one told the consultation has ended.
    private void notifyStaffConsultationComplete(PatientTicket ticket) {
        Appointment appointment = ticket.getAppointment();
        if (appointment == null || appointment.getStaff() == null) return;

        String patient = ticket.getPatient() != null && ticket.getPatient().getName() != null
                ? ticket.getPatient().getName().getFirstName() + " " + ticket.getPatient().getName().getLastName()
                : "the patient";
        String message = "Consultation complete for " + patient + " (ticket TK-"
                + String.format("%03d", ticket.getTicketId())
                + "). The ticket is ready for a payment request.";

        notificationService.notifyStaff(appointment.getStaff(), message, "TICKET_RESOLVED", ticket, appointment);
    }

    // NEW — when the doctor resolves the ticket, the linked appointment is
    // automatically marked COMPLETED. Mirrors PaymentService's
    // closeTicketIfPaid() structure for consistency.
    private void completeAppointmentIfResolved(PatientTicket ticket) {
        if (ticket.getCurrentStatus() != StatusType.RESOLVED || ticket.getAppointment() == null) {
            return;
        }

        // BUG FIX — ticket.getAppointment() is a lazy Hibernate proxy. Appointment.Builder.copy()
        // used to read fields directly, and a proxy's own fields are empty, so the copy was blank:
        // save() inserted a new empty appointment row and the real one stayed CONFIRMED.
        // copy() now uses getters; reloading by id here is a second safeguard.
        Appointment appointment = appointmentRepository
                .findById(ticket.getAppointment().getAppointmentId())
                .orElse(null);
        if (appointment == null) return;
        if (appointment.getConfirmationStatus() == ConfirmationStatus.COMPLETED) {
            return; // already completed — nothing to do
        }

        Appointment updated = new Appointment.Builder()
                .copy(appointment)
                .setConfirmationStatus(ConfirmationStatus.COMPLETED)
                .build();

        appointmentRepository.save(updated);
    }
}