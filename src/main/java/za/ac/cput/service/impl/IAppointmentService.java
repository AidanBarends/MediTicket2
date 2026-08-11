package za.ac.cput.service.impl;

import za.ac.cput.domain.Appointment;
import za.ac.cput.domain.enums.ConfirmationStatus;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService extends IService<Appointment, Integer> {

    List<Appointment> findByDoctorUserId(int doctorId);

    List<Appointment> findByStaffUserId(int staffId);

    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findByConfirmationStatus(ConfirmationStatus confirmationStatus);

    List<Appointment> findByDoctorUserIdAndAppointmentDate(int doctorId, LocalDate appointmentDate);

    // NEW
    List<Appointment> findByPatientUserId(int patientId);

    // NEW — the orchestration methods. Each does everything the workflow PDF
    // describes as one business transaction, not a raw field update.
    Appointment approveAppointment(int appointmentId, int doctorId, int staffId);

    Appointment rejectAppointment(int appointmentId, int staffId, String reason);

    Appointment completeAppointment(int appointmentId);
}