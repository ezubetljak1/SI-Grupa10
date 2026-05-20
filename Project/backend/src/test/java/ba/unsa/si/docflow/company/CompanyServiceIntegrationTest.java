package ba.unsa.si.docflow.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.service.company.CompanyService;
import ba.unsa.si.docflow.service.company.CompanyValidation;
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
class CompanyServiceIntegrationTest {

    private static final String KEYCLOAK_USER_ID = "kc-company-service-test";

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    @Autowired private CompanyService companyService;

    @Autowired private CompanyValidation companyValidation;

    private Long companyId;

    @BeforeEach
    void setUp() {
        CompanyEntity company = new CompanyEntity();
        company.setName("ABC d.o.o.");
        company.setAddress("Zmaja od Bosne bb");
        company.setEmail("info-" + System.nanoTime() + "@abc.ba");
        company.setRegistrationDate(LocalDateTime.now());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setKeycloakGroupId("test-group-id");

        companyId = companyDAO.persist(company).getId();

        UserEntity user = new UserEntity();
        user.setCompanyId(companyId);
        user.setRoleId(roleService.getAdminRole().getId());
        user.setKeycloakUserId(KEYCLOAK_USER_ID);
        user.setFirstName("Service");
        user.setLastName("Test");
        user.setEmail("service-test-" + System.nanoTime() + "@abc.ba");
        user.setAccountStatus(AccountStatus.ACTIVE);
        userDAO.persist(user);
        userDAO.flush();

        authenticate();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void findByIdReturnsCompany() {
        CompanyEntity company = companyDAO.findByPK(companyId);

        var response = companyService.findById(companyId);

        assertEquals("OK", response.getCode());
        assertEquals("ABC d.o.o.", response.getPayload().getName());
        assertEquals(company.getEmail(), response.getPayload().getEmail());
        assertEquals("ACTIVE", response.getPayload().getStatus());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        assertThrows(ApiNotFoundException.class, () -> companyService.findById(99999L));
    }

    @Test
    void updateCompanyChangesFields() {
        CompanyUpdateRequest request = new CompanyUpdateRequest();
        request.setId(companyId);
        request.setName("ABC Updated");
        request.setAddress("Nova adresa 1");

        var response = companyService.update(request);

        assertEquals("ABC Updated", response.getPayload().getName());
        assertEquals("Nova adresa 1", response.getPayload().getAddress());
    }

    @Test
    void validateRegisterRejectsDuplicateCompanyEmail() {
        CompanyEntity existing = companyDAO.findByPK(companyId);

        CompanyRegisterRequest request = new CompanyRegisterRequest();
        request.setCompanyName("Nova firma");
        request.setCompanyAddress("Adresa");
        request.setCompanyEmail(existing.getEmail());
        request.setAdminFirstName("Emina");
        request.setAdminLastName("Test");
        request.setAdminEmail("admin@abc.ba");

        assertThrows(ApiValidationException.class, () -> companyValidation.validateRegister(request));
    }

    @Test
    void validateRegisterRejectsSameCompanyAndAdminEmail() {
        CompanyRegisterRequest request = new CompanyRegisterRequest();
        request.setCompanyName("Nova firma");
        request.setCompanyAddress("Adresa");
        request.setCompanyEmail("same@abc.ba");
        request.setAdminFirstName("Emina");
        request.setAdminLastName("Test");
        request.setAdminEmail("same@abc.ba");

        assertThrows(ApiValidationException.class, () -> companyValidation.validateRegister(request));
    }

    private void authenticate() {
        Jwt jwt =
                Jwt.withTokenValue("company-service-test-token")
                        .header("alg", "none")
                        .subject(KEYCLOAK_USER_ID)
                        .claim("email", "service-test@abc.ba")
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(3600))
                        .build();

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new JwtAuthenticationToken(
                                jwt, List.of(new SimpleGrantedAuthority("ROLE_USER"))));
    }
}
