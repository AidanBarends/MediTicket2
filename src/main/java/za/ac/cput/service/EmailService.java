package za.ac.cput.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.enums.UserType;
import za.ac.cput.repository.NotificationRepository;
import za.ac.cput.service.impl.IEmailService;

@Service
public class EmailService implements IEmailService {

    @Autowired
    private final JavaMailSender mailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Used only to flag a notification as emailed once the send succeeds.
    @Autowired
    private NotificationRepository notificationRepository;

    @Value("${app.verification.base-url}")
    private String verificationBaseUrl;

    @Value("${app.employee-invite.base-url}")
    private String employeeInviteBaseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String firstName, String token, UserType userType) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your Mediticket account");

        if (userType == UserType.DOCTOR) {
            // Doctor gets a short code, matching the invite-code UX already
            // used elsewhere in employee signup — typed into the app rather
            // than clicked.
            message.setText(
                    "Hi " + firstName + ",\n\n" +
                            "Thanks for completing your MediTicket doctor signup. Your verification code is:\n\n" +
                            token + "\n\n" +
                            "Enter this code in the app to activate your account. This code will expire in 24 hours.\n\n" +
                            "If you did not create this account, please ignore this email."
            );
        } else {
            // Patient gets a clickable link — the token here is a UUID, long
            // enough that typing it in would be impractical.
            String verificationLink = verificationBaseUrl + "?token=" + token;
            message.setText(
                    "Hi " + firstName + ",\n\n" +
                            "Thanks for signing up to Mediticket. Please verify your email by clicking the link below:\n\n" +
                            verificationLink + "\n\n" +
                            "This link will expire in 24 hours.\n\n" +
                            "If you did not create this account, please ignore this email."
            );
        }

        mailSender.send(message);
    }

    @Override
    public void sendEmployeeInviteEmail(String toEmail, UserType userType, String token) {
        String role = userType == UserType.DOCTOR ? "doctor" : "clinic staff";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("You're invited to join MediTicket as a " + role);
        message.setText(
                "Hello,\n\n" +
                        "You have been invited to register as a " + role + " on MediTicket.\n\n" +
                        "To complete your signup, open the MediTicket desktop app, choose " +
                        "\"Already have an invite code?\" on the access request screen, and paste in the code below:\n\n" +
                        token + "\n\n" +
                        "This code will expire in 48 hours.\n\n" +
                        "If you did not expect this invitation, please ignore this email."
        );

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("MediTicket Password Reset");
        message.setText(
                "Hello,\n\n" +
                        "Your password reset code is:\n\n" +
                        code + "\n\n" +
                        "This code will expire in 10 minutes.\n\n" +
                        "If you did not request a password reset, you can safely ignore this email."
        );
        mailSender.send(message);
    }

    // NEW — email copy of an in-app notification. Runs on a background thread
    // (@Async, enabled in MediTicket02Application) so the HTTP request that
    // triggered it returns immediately instead of waiting 2-5 s for SMTP.
    // A failed send is logged and never breaks the action that caused it.
    @Async
    public void sendNotificationEmail(int notificationId, String toEmail, String firstName,
                                      String subject, String body) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("MediTicket: " + subject);
            message.setText(
                    "Hi " + (firstName != null ? firstName : "there") + ",\n\n" +
                            body + "\n\n" +
                            "You can see the details in the MediTicket app under Notifications.\n\n" +
                            "MediTicket Clinic"
            );
            mailSender.send(message);
            notificationRepository.markEmailSent(notificationId);
        } catch (Exception e) {
            logger.warn("Notification email to {} failed: {}", toEmail, e.getMessage());
        }
    }
}
