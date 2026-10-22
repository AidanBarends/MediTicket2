package za.ac.cput;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // background notification emails (EmailService.sendNotificationEmail)
public class MediTicket02Application {

    public static void main(String[] args) {
        SpringApplication.run(MediTicket02Application.class, args);
    }

}