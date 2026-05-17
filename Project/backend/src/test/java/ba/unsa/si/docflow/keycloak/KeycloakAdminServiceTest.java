package ba.unsa.si.docflow.keycloak;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ba.unsa.si.docflow.config.KeycloakTestConfiguration;
import ba.unsa.si.docflow.service.keycloak.KeycloakAdminService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(KeycloakTestConfiguration.class)
class KeycloakAdminServiceTest {

    @Autowired private KeycloakAdminService keycloakAdminService;

    @Test
    void mockCreatesCompanyGroupId() {
        String groupId = keycloakAdminService.createCompanyGroup("ABC d.o.o.");

        assertNotNull(groupId);
        assertTrue(groupId.startsWith("kc-group-"));
    }

    @Test
    void mockCreatesUserId() {
        String userId =
                keycloakAdminService.createUser(
                        "admin@abc.ba",
                        "Emina",
                        "Zubetljak",
                        "kc-group-test",
                        true);

        assertNotNull(userId);
        assertTrue(userId.startsWith("kc-user-"));
    }

    @Test
    void rollbackMethodsDoNotThrow() {
        keycloakAdminService.deleteUser("missing-user");
        keycloakAdminService.deleteCompanyGroup("missing-group");
    }
}
