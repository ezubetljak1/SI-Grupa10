package ba.unsa.si.docflow.service.keycloak;

import ba.unsa.si.docflow.config.KeycloakProperties;
import ba.unsa.si.docflow.exception.KeycloakIntegrationException;

import jakarta.ws.rs.core.Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String PASSWORD_ALPHABET =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";

    private static final String UPDATE_PASSWORD_ACTION = "UPDATE_PASSWORD";

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    /**
     * Creates a Keycloak group representing one registered company (tenant).
     *
     * @return Keycloak group id
     */
    public String createCompanyGroup(String companyName) {
        GroupRepresentation group = new GroupRepresentation();
        group.setName(buildGroupName(companyName));

        try (Response response = realm().groups().add(group)) {
            assertSuccessfulResponse(response, "create company group");
            return CreatedResponseUtil.getCreatedId(response);
        } catch (KeycloakIntegrationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new KeycloakIntegrationException(
                    "Failed to create company group in Keycloak.", ex);
        }
    }

    /**
     * Creates a realm user and assigns them to the company group.
     *
     * @return Keycloak user id (subject)
     */
    public KeycloakUserCreationResult createUser(
            String email,
            String firstName,
            String lastName,
            String keycloakGroupId,
            boolean requirePasswordUpdate) {
        String normalizedEmail = email.trim().toLowerCase();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(normalizedEmail);
        user.setEmail(normalizedEmail);
        user.setFirstName(firstName.trim());
        user.setLastName(lastName.trim());
        user.setEmailVerified(true);

        if (requirePasswordUpdate) {
            user.setRequiredActions(List.of(UPDATE_PASSWORD_ACTION));
        }

        String userId = null;

        try (Response response = realm().users().create(user)) {
            assertSuccessfulResponse(response, "create user");

            userId = CreatedResponseUtil.getCreatedId(response);
            joinGroup(userId, keycloakGroupId);

            return new KeycloakUserCreationResult(userId);
        } catch (KeycloakIntegrationException ex) {
            deleteUser(userId);
            throw ex;
        } catch (Exception ex) {
            deleteUser(userId);
            throw new KeycloakIntegrationException("Failed to create user in Keycloak.", ex);
        }
    }

    public void sendPasswordSetupEmail(String keycloakUserId) {
        if (!StringUtils.hasText(keycloakUserId)) {
            throw new KeycloakIntegrationException(
                    "Keycloak user id is required for password setup email.");
        }

        try {
            ensureRequiredAction(keycloakUserId, UPDATE_PASSWORD_ACTION);

            realm().users()
                    .get(keycloakUserId)
                    .executeActionsEmail(
                            keycloakProperties.getFrontendClientId(),
                            keycloakProperties.getFrontendRedirectUri(),
                            keycloakProperties.getPasswordSetupLinkLifespanSeconds(),
                            List.of(UPDATE_PASSWORD_ACTION));
        } catch (KeycloakIntegrationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new KeycloakIntegrationException("Failed to send password setup email.", ex);
        }
    }

    public void deleteCompanyGroup(String keycloakGroupId) {
        if (!StringUtils.hasText(keycloakGroupId)) {
            return;
        }

        try {
            realm().groups().group(keycloakGroupId).remove();
        } catch (Exception ex) {
            log.warn("Failed to delete Keycloak group {} during rollback", keycloakGroupId, ex);
        }
    }

    public void deleteUser(String keycloakUserId) {
        if (!StringUtils.hasText(keycloakUserId)) {
            return;
        }

        try {
            realm().users().get(keycloakUserId).remove();
        } catch (Exception ex) {
            log.warn("Failed to delete Keycloak user {} during rollback", keycloakUserId, ex);
        }
    }

    public void setUserEnabled(String keycloakUserId, boolean enabled) {
        if (!StringUtils.hasText(keycloakUserId)) {
            return;
        }

        try {
            UserRepresentation user = realm().users().get(keycloakUserId).toRepresentation();
            user.setEnabled(enabled);
            realm().users().get(keycloakUserId).update(user);

            if (enabled) {
                realm().attackDetection().clearBruteForceForUser(keycloakUserId);
            }

        } catch (Exception ex) {
            throw new KeycloakIntegrationException("Failed to update user status in Keycloak.", ex);
        }
    }

    public boolean isPasswordUpdateRequired(String keycloakUserId) {
        if (!StringUtils.hasText(keycloakUserId)) {
            return false;
        }

        try {
            UserRepresentation user = realm().users().get(keycloakUserId).toRepresentation();
            List<String> actions = user.getRequiredActions();
            return actions != null && actions.contains("UPDATE_PASSWORD");
        } catch (Exception ex) {
            log.warn("Failed to read Keycloak user required actions for {}", keycloakUserId, ex);
            return false;
        }
    }

    protected RealmResource realm() {
        return keycloak.realm(keycloakProperties.getRealm());
    }

    private void joinGroup(String userId, String keycloakGroupId) {
        try {
            realm().users().get(userId).joinGroup(keycloakGroupId);
        } catch (Exception ex) {
            throw new KeycloakIntegrationException(
                    "Failed to assign user to company group in Keycloak.", ex);
        }
    }

    private void assertSuccessfulResponse(Response response, String operation) {
        int status = response.getStatus();

        if (status >= 200 && status < 300) {
            return;
        }

        if (status == 409 && "create user".equals(operation)) {
            throw new KeycloakIntegrationException(
                    "A user with this email already exists in the identity provider.");
        }

        throw new KeycloakIntegrationException(
                "Keycloak operation failed. Please try again or contact support.");
    }

    private String buildGroupName(String companyName) {
        String slug =
                companyName
                        .trim()
                        .toLowerCase()
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("^-|-$", "");

        if (!StringUtils.hasText(slug)) {
            slug = "company";
        }

        if (slug.length() > 40) {
            slug = slug.substring(0, 40);
        }

        return slug + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void ensureRequiredAction(String keycloakUserId, String requiredAction) {
        try {
            UserRepresentation user = realm().users().get(keycloakUserId).toRepresentation();

            List<String> requiredActions =
                    user.getRequiredActions() == null
                            ? new ArrayList<>()
                            : new ArrayList<>(user.getRequiredActions());

            if (!requiredActions.contains(requiredAction)) {
                requiredActions.add(requiredAction);
                user.setRequiredActions(requiredActions);
                realm().users().get(keycloakUserId).update(user);
            }
        } catch (Exception ex) {
            throw new KeycloakIntegrationException(
                    "Failed to set required password update action in Keycloak.", ex);
        }
    }
}
