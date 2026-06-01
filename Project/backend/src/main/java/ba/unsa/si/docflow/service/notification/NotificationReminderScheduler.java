package ba.unsa.si.docflow.service.notification;

import ba.unsa.si.docflow.dao.NotificationDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.NotificationEntity;
import ba.unsa.si.docflow.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationReminderScheduler {

    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;
    private final JavaMailSender mailSender;

    @Value("${docflow.notifications.email-reminder-enabled:true}")
    private boolean emailReminderEnabled;

    @Value("${docflow.notifications.email-reminder-after-hours:24}")
    private int emailReminderAfterHours;

    @Value("${docflow.notifications.email-reminder-cron}")
    private String emailReminderCron;

    @Value("${docflow.frontend.base-url}")
    private String frontendBaseUrl;

    @Value("${docflow.mail.from}")
    private String mailFrom;

    @Value("${docflow.mail.from-name}")
    private String mailFromName;

    @Scheduled(cron = "${docflow.notifications.email-reminder-cron}")
    public void sendEmailReminders() {
        if (!emailReminderEnabled) {
            log.info("Email reminders are disabled.");
            return;
        }

        log.info("Starting email reminder scheduler...");
        Instant threshold = Instant.now().minus(Duration.ofHours(emailReminderAfterHours));
        Map<Long, List<NotificationEntity>> unreadGrouped = notificationDAO.findUnreadOlderThanWithNoEmail(threshold);

        if (unreadGrouped.isEmpty()) {
            log.info("No unread notifications to process for email reminder.");
            return;
        }

        for (Map.Entry<Long, List<NotificationEntity>> entry : unreadGrouped.entrySet()) {
            Long userId = entry.getKey();
            List<NotificationEntity> notifications = entry.getValue();

            try {
                sendDigestForUser(userId, notifications);
            } catch (Exception e) {
                log.error("Failed to send email reminder digest for user: {}", userId, e);
            }
        }
    }

    private void sendDigestForUser(Long userId, List<NotificationEntity> notifications) {
        UserEntity user = userDAO.findByPK(userId);
        if (user == null) {
            log.warn("User with ID {} not found. Skipping email reminder.", userId);
            return;
        }

        String email = user.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("User with ID {} does not have an email address. Skipping email reminder.", userId);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(mailFromName + " <" + mailFrom + ">");
        message.setSubject("Docflow - Pending notifications reminder");

        StringBuilder body = new StringBuilder();

        body.append("Hello ")
                .append(user.getFirstName())
                .append(",\n\n");

        body.append("You have unread notifications in the Docflow application:\n\n");

        for (NotificationEntity notification : notifications) {
            body.append("- ")
                    .append(notification.getTitle())
                    .append("\n");

            body.append("  ")
                    .append(notification.getText())
                    .append("\n");

            if (notification.getActionUrl() != null
                    && !notification.getActionUrl().isBlank()) {

                body.append("  Open: ")
                        .append(frontendBaseUrl)
                        .append(notification.getActionUrl())
                        .append("\n");
            }

            body.append("\n");
        }

        body.append("Kind regards,\n")
                .append(mailFromName);

        message.setText(body.toString());

        mailSender.send(message);

        Instant now = Instant.now();
        for (NotificationEntity notification : notifications) {
            notification.setEmailSentAt(now);
            notificationDAO.merge(notification);
        }
        log.info("Sent email reminder digest with {} notifications to user {}", notifications.size(), userId);
    }
}
