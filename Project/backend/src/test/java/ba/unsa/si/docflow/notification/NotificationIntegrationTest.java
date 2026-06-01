package ba.unsa.si.docflow.notification;

import static org.junit.jupiter.api.Assertions.*;

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
import ba.unsa.si.docflow.service.notification.NotificationService;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

    private static final String KEYCLOAK_USER_ID = "kc-notif-user-test";

    @Autowired private NotificationService notificationService;
    @Autowired private NotificationDAO notificationDAO;
    @Autowired private CompanyDAO companyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private RoleService roleService;

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
        Long companyId = companyDAO.persist(company).getId();

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
                "/action"
        );

        List<NotificationResponse> myNotifs = notificationService.getMyNotifications();
        assertEquals(1, myNotifs.size());
        assertEquals("Test Title", myNotifs.get(0).getTitle());
        assertEquals("Test Text", myNotifs.get(0).getText());
        assertFalse(myNotifs.get(0).isRead());

        UnreadCountResponse countResponse = notificationService.getMyUnreadCount();
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
                "/action"
        );

        List<NotificationResponse> myNotifs = notificationService.getMyNotifications();
        Long notifId = myNotifs.get(0).getId();

        NotificationResponse readNotif = notificationService.markOneRead(notifId);
        assertTrue(readNotif.isRead());
        assertNotNull(readNotif.getReadAt());

        UnreadCountResponse countResponse = notificationService.getMyUnreadCount();
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
                "/action"
        );
        notificationService.createNotification(
                currentUserEntity.getId(),
                null,
                null,
                NotificationType.DOCUMENT_ASSIGNED,
                "Test Title 2",
                "Test Text 2",
                "/action"
        );

        UnreadCountResponse countResponse1 = notificationService.getMyUnreadCount();
        assertEquals(2, countResponse1.getUnreadCount());

        notificationService.markAllRead();

        UnreadCountResponse countResponse2 = notificationService.getMyUnreadCount();
        assertEquals(0, countResponse2.getUnreadCount());
    }

    @Test
    void testMarkReadNotFoundThrowsException() {
        assertThrows(ApiNotFoundException.class, () -> {
            notificationService.markOneRead(999999L);
        });
    }

    private void authenticate(Jwt jwt) {
        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
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
