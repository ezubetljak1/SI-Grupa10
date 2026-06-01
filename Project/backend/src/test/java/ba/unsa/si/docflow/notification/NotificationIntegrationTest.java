package ba.unsa.si.docflow.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.NotificationDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.dto.notification.UnreadCountResponse;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.NotificationEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.enums.NotificationType;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.service.notification.NotificationReminderScheduler;
import ba.unsa.si.docflow.service.notification.NotificationService;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

    private static final String KEYCLOAK_USER_ID = "kc-notif-user-test";

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationDAO notificationDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotificationReminderScheduler notificationReminderScheduler;

    @MockitoBean
    private JavaMailSender mailSender;

    private Long companyId;
    private UserEntity currentUserEntity;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("Notification Test Company");
        company.setAddress("Address");
        company.setEmail("notif-company-" + System.nanoTime() + "@test.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("group-test-notif");

        companyId = companyDAO.persist(company).getId();

        currentUserEntity = new UserEntity();
        currentUserEntity.setCompanyId(companyId);
        currentUserEntity.setRoleId(roleService.getAdminRole().getId());
        currentUserEntity.setKeycloakUserId(KEYCLOAK_USER_ID);
        currentUserEntity.setFirstName("Notif");
        currentUserEntity.setLastName("User");
        currentUserEntity.setEmail("notif-user-" + System.nanoTime() + "@test.ba");
        currentUserEntity.setAccountStatus(AccountStatus.ACTIVE);

        userDAO.persist(currentUserEntity);
        userDAO.flush();

        authenticate(buildJwt(KEYCLOAK_USER_ID, currentUserEntity.getEmail()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateAndFetchNotifications() {
        notificationService.createNotification(
                currentUserEntity.getId(),
                null,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Test Title",
                "Test Text",
                "/action");

        List<NotificationResponse> myNotifications =
                notificationService.getMyNotifications();

        assertEquals(1, myNotifications.size());
        assertEquals("Test Title", myNotifications.get(0).getTitle());
        assertEquals("Test Text", myNotifications.get(0).getText());
        assertFalse(myNotifications.get(0).isRead());

        UnreadCountResponse countResponse =
                notificationService.getMyUnreadCount();

        assertEquals(1, countResponse.getUnreadCount());
    }

    @Test
    void testMarkRead() {
        notificationService.createNotification(
                currentUserEntity.getId(),
                null,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Test Title",
                "Test Text",
                "/action");

        List<NotificationResponse> myNotifications =
                notificationService.getMyNotifications();

        Long notificationId = myNotifications.get(0).getId();

        NotificationResponse readNotification =
                notificationService.markOneRead(notificationId);

        assertTrue(readNotification.isRead());
        assertNotNull(readNotification.getReadAt());

        UnreadCountResponse countResponse =
                notificationService.getMyUnreadCount();

        assertEquals(0, countResponse.getUnreadCount());
    }

    @Test
    void testMarkAllRead() {
        notificationService.createNotification(
                currentUserEntity.getId(),
                null,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Test Title 1",
                "Test Text 1",
                "/action");

        notificationService.createNotification(
                currentUserEntity.getId(),
                null,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Test Title 2",
                "Test Text 2",
                "/action");

        UnreadCountResponse countBefore =
                notificationService.getMyUnreadCount();

        assertEquals(2, countBefore.getUnreadCount());

        notificationService.markAllRead();

        UnreadCountResponse countAfter =
                notificationService.getMyUnreadCount();

        assertEquals(0, countAfter.getUnreadCount());
    }

    @Test
    void testMarkReadNotFoundThrowsException() {
        assertThrows(
                ApiNotFoundException.class,
                () -> notificationService.markOneRead(999999L));
    }

    @Test
    void getMyNotificationsThenReturnsOnlyCurrentUsersNotifications() {
        UserEntity secondUser =
                createSecondUser("kc-second-notif-user");

        notificationService.createNotification(
                currentUserEntity.getId(),
                10L,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "My notification",
                "Visible to current user",
                "/documents/10");

        notificationService.createNotification(
                secondUser.getId(),
                20L,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Other user's notification",
                "Must not be visible",
                "/documents/20");

        List<NotificationResponse> notifications =
                notificationService.getMyNotifications();

        assertEquals(1, notifications.size());
        assertEquals("My notification", notifications.get(0).getTitle());
        assertEquals(currentUserEntity.getId(), notifications.get(0).getUserId());
    }

    @Test
    void markOneReadForAnotherUsersNotificationThenThrowsNotFound() {
        UserEntity secondUser =
                createSecondUser("kc-second-notif-owner");

        NotificationEntity notification =
                persistNotification(
                        secondUser.getId(),
                        "Other user's notification",
                        Instant.now());

        assertThrows(
                ApiNotFoundException.class,
                () -> notificationService.markOneRead(notification.getId()));

        NotificationEntity stored =
                notificationDAO.findByIdAndUserId(
                        notification.getId(),
                        secondUser.getId());

        assertNotNull(stored);
        assertFalse(stored.isRead());
        assertNull(stored.getReadAt());
    }

    @Test
    void markAllReadThenChangesOnlyCurrentUsersNotifications() {
        UserEntity secondUser =
                createSecondUser("kc-second-notif-mark-all");

        NotificationEntity myNotification =
                persistNotification(
                        currentUserEntity.getId(),
                        "My unread notification",
                        Instant.now());

        NotificationEntity otherNotification =
                persistNotification(
                        secondUser.getId(),
                        "Other user's unread notification",
                        Instant.now());

        notificationService.markAllRead();

        NotificationEntity storedMine =
                notificationDAO.findByIdAndUserId(
                        myNotification.getId(),
                        currentUserEntity.getId());

        NotificationEntity storedOther =
                notificationDAO.findByIdAndUserId(
                        otherNotification.getId(),
                        secondUser.getId());

        assertNotNull(storedMine);
        assertTrue(storedMine.isRead());
        assertNotNull(storedMine.getReadAt());

        assertNotNull(storedOther);
        assertFalse(storedOther.isRead());
        assertNull(storedOther.getReadAt());
    }

    @Test
    void emailReminderSchedulerThenSendsOneDigestAndDoesNotSendDuplicate() {
        ReflectionTestUtils.setField(
                notificationReminderScheduler,
                "emailReminderEnabled",
                true);

        ReflectionTestUtils.setField(
                notificationReminderScheduler,
                "emailReminderAfterHours",
                1);

        ReflectionTestUtils.setField(
                notificationReminderScheduler,
                "frontendBaseUrl",
                "http://localhost:4200");

        ReflectionTestUtils.setField(
                notificationReminderScheduler,
                "mailFrom",
                "noreply@test.ba");

        ReflectionTestUtils.setField(
                notificationReminderScheduler,
                "mailFromName",
                "Docflow");

        NotificationEntity first =
                persistNotification(
                        currentUserEntity.getId(),
                        "First old notification",
                        Instant.now().minus(Duration.ofHours(2)));

        NotificationEntity second =
                persistNotification(
                        currentUserEntity.getId(),
                        "Second old notification",
                        Instant.now().minus(Duration.ofHours(3)));

        notificationReminderScheduler.sendEmailReminders();

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));

        NotificationEntity storedFirst =
                notificationDAO.findByIdAndUserId(
                        first.getId(),
                        currentUserEntity.getId());

        NotificationEntity storedSecond =
                notificationDAO.findByIdAndUserId(
                        second.getId(),
                        currentUserEntity.getId());

        assertNotNull(storedFirst);
        assertNotNull(storedSecond);
        assertNotNull(storedFirst.getEmailSentAt());
        assertNotNull(storedSecond.getEmailSentAt());

        notificationReminderScheduler.sendEmailReminders();

        verify(mailSender, times(1))
                .send(any(SimpleMailMessage.class));
    }

    private UserEntity createSecondUser(String keycloakUserId) {
        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getAdminRole().getId());
        user.setKeycloakUserId(keycloakUserId);
        user.setFirstName("Second");
        user.setLastName("User");
        user.setEmail("second-notif-user-" + System.nanoTime() + "@test.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);

        UserEntity saved = userDAO.persist(user);
        userDAO.flush();

        return saved;
    }

    private NotificationEntity persistNotification(
            Long userId,
            String title,
            Instant createdAt) {

        NotificationEntity notification =
                NotificationEntity.builder()
                        .userId(userId)
                        .type(NotificationType.DOCUMENT_ASSIGNED)
                        .title(title)
                        .text("Notification test text")
                        .actionUrl("/documents/1")
                        .read(false)
                        .createdAt(createdAt)
                        .build();

        NotificationEntity saved =
                notificationDAO.persist(notification);

        notificationDAO.flush();

        return saved;
    }

    private void authenticate(Jwt jwt) {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_USER"))));
    }

    private Jwt buildJwt(String subject, String email) {
        return Jwt.withTokenValue("test-token")
                .header("alg", "none")
                .subject(subject)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}