-- =====================================================================
-- MediTicket: demo data reset
-- Run in MySQL Workbench against mediticket_db with the backend STOPPED.
--
-- KEEPS   every account and its login: patient, doctor, clinic_staff
--         (admins + nurses), refresh_token, verification_token.
-- CLEARS  all test activity: appointments, tickets + their status history,
--         payments, notifications. IDs restart at 1 (first ticket is TK-001).
--
-- Step 1 makes backup copies inside the same database, so nothing is lost:
-- if anything looks wrong afterwards, run the RESTORE block at the bottom.
-- =====================================================================

USE mediticket_db;

-- ---------------------------------------------------------------------
-- STEP 1  Backup copies (tables named backup_<table>)
-- ---------------------------------------------------------------------
DROP TABLE IF EXISTS backup_appointment, backup_patient_ticket, backup_ticket_status,
                     backup_payment, backup_notification;

CREATE TABLE backup_appointment    AS SELECT * FROM appointment;
CREATE TABLE backup_patient_ticket AS SELECT * FROM patient_ticket;
CREATE TABLE backup_ticket_status  AS SELECT * FROM ticket_status;
CREATE TABLE backup_payment        AS SELECT * FROM payment;
CREATE TABLE backup_notification   AS SELECT * FROM notification;

-- ---------------------------------------------------------------------
-- STEP 2  Clear the test activity (children first; TRUNCATE resets IDs)
-- ---------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE notification;
TRUNCATE TABLE payment;
TRUNCATE TABLE ticket_status;
TRUNCATE TABLE patient_ticket;
TRUNCATE TABLE appointment;

-- Optional: leftover test requests. Remove the "-- " to clear them too.
-- TRUNCATE TABLE employee_access_request;
-- TRUNCATE TABLE password_reset_request;

SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- STEP 3  Make the notification columns plain text
-- If these columns were created as MySQL ENUMs, any status the database
-- didn't know about (e.g. READ) failed with "Data truncated". VARCHAR
-- accepts every value the Java enums define, now and later.
-- ---------------------------------------------------------------------
ALTER TABLE notification MODIFY notification_status VARCHAR(20) NOT NULL;
ALTER TABLE notification MODIFY notification_type   VARCHAR(20) NOT NULL;

-- ---------------------------------------------------------------------
-- STEP 4  Check the result: first five should be 0, the accounts unchanged
-- ---------------------------------------------------------------------
SELECT 'appointment'    AS table_name, COUNT(*) AS row_count FROM appointment
UNION ALL SELECT 'patient_ticket', COUNT(*) FROM patient_ticket
UNION ALL SELECT 'ticket_status',  COUNT(*) FROM ticket_status
UNION ALL SELECT 'payment',        COUNT(*) FROM payment
UNION ALL SELECT 'notification',   COUNT(*) FROM notification
UNION ALL SELECT 'patient',        COUNT(*) FROM patient
UNION ALL SELECT 'doctor',         COUNT(*) FROM doctor
UNION ALL SELECT 'clinic_staff',   COUNT(*) FROM clinic_staff;

-- =====================================================================
-- RESTORE (only if needed): puts the backed-up rows back.
-- Select the block below and run it.
-- =====================================================================
-- SET FOREIGN_KEY_CHECKS = 0;
-- INSERT INTO appointment    SELECT * FROM backup_appointment;
-- INSERT INTO patient_ticket SELECT * FROM backup_patient_ticket;
-- INSERT INTO ticket_status  SELECT * FROM backup_ticket_status;
-- INSERT INTO payment        SELECT * FROM backup_payment;
-- INSERT INTO notification   (notification_id, notification_type, notification_status, notification_message,
--                             patient_id, doctor_id, clinic_staff_id, ticket_id, appointment_id, notification_date)
--                     SELECT notification_id, notification_type, notification_status, notification_message,
--                            patient_id, doctor_id, clinic_staff_id, ticket_id, appointment_id, notification_date
--                     FROM backup_notification;
-- SET FOREIGN_KEY_CHECKS = 1;

-- Once the demo is over and everything is fine, the backups can go:
-- DROP TABLE backup_appointment, backup_patient_ticket, backup_ticket_status, backup_payment, backup_notification;
