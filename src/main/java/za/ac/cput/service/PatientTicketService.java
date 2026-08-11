package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.PatientTicket;
import za.ac.cput.domain.enums.StatusType;
import za.ac.cput.repository.PatientTicketRepository;
import za.ac.cput.service.impl.IPatientTicketService;

import java.util.List;

@Service
public class PatientTicketService implements IPatientTicketService {

    @Autowired
    private PatientTicketRepository repository;

    @Override
    public PatientTicket create(PatientTicket ticket) {
        if (ticket == null) return null;

        PatientTicket saved = repository.save(ticket);
        // A ticket must always start with a status — otherwise currentStatus
        // stays null and it becomes invisible to any status-based query
        // (findByCurrentStatus, and the admin/nurse Tickets page filters).
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

        ticket.addStatus(newStatus, notes);

        return repository.save(ticket);
    }

    @Override
    public PatientTicket findByAppointment(Appointment appointment) {
        if (appointment == null) return null;
        return repository.findByAppointment(appointment);
    }
}
