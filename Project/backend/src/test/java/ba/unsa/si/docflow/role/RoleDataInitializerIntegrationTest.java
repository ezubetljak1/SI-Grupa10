package ba.unsa.si.docflow.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ba.unsa.si.docflow.dao.RoleDAO;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ba.unsa.si.docflow.config.KeycloakTestConfiguration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(KeycloakTestConfiguration.class)
class RoleDataInitializerIntegrationTest {

    @Autowired private RoleDAO roleDAO;

    @Autowired private RoleService roleService;

    @Test
    void seedsAllSystemRolesOnStartup() {
        for (RoleName roleName : RoleName.values()) {
            assertNotNull(roleDAO.findByName(roleName), "Missing seeded role: " + roleName);
        }

        assertEquals(4, roleService.findAll().size());
    }

    @Test
    void getAdminRoleReturnsAdmin() {
        assertEquals(RoleName.ADMIN, roleService.getAdminRole().getName());
    }
}
