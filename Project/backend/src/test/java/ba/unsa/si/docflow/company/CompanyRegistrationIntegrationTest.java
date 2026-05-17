package ba.unsa.si.docflow.company;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.service.role.RoleService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CompanyRegistrationIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Autowired private CompanyDAO companyDAO;

    @Autowired private UserDAO userDAO;

    @Autowired private RoleService roleService;

    @Test
    void registerCompanyCreatesCompanyAndAdmin() throws Exception {
        CompanyRegisterRequest request = buildValidRequest();

        mockMvc.perform(
                        post("/api/public/companies/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("OK")))
                .andExpect(jsonPath("$.payload.companyId", notNullValue()))
                .andExpect(jsonPath("$.payload.companyName", equalTo("Test Company d.o.o.")));

        CompanyEntity company = companyDAO.findByEmail(request.getCompanyEmail());
        assertNotNull(company);
        assertNotNull(company.getKeycloakGroupId());

        UserEntity admin =
                userDAO.findByKeycloakUserId("kc-user-" + request.getAdminEmail().hashCode());
        assertNotNull(admin);
        assertEquals(company.getId(), admin.getCompanyId());
        assertEquals(AccountStatus.PENDING_PASSWORD_CHANGE, admin.getAccountStatus());
        assertEquals(roleService.getAdminRole().getId(), admin.getRoleId());
    }

    @Test
    void registerCompanyDoesNotRequireAuthentication() throws Exception {
        CompanyRegisterRequest request = buildValidRequest();
        request.setCompanyEmail("public-" + System.nanoTime() + "@test.ba");
        request.setAdminEmail("admin-public-" + System.nanoTime() + "@test.ba");

        mockMvc.perform(
                        post("/api/public/companies/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registerCompanyRejectsDuplicateCompanyEmail() throws Exception {
        CompanyRegisterRequest request = buildValidRequest();
        String email = "duplicate-company-" + System.nanoTime() + "@test.ba";
        request.setCompanyEmail(email);
        request.setAdminEmail("admin1-" + System.nanoTime() + "@test.ba");

        mockMvc.perform(
                        post("/api/public/companies/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        request.setAdminEmail("admin2-" + System.nanoTime() + "@test.ba");

        mockMvc.perform(
                        post("/api/public/companies/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private CompanyRegisterRequest buildValidRequest() {
        long suffix = System.nanoTime();

        CompanyRegisterRequest request = new CompanyRegisterRequest();
        request.setCompanyName("Test Company d.o.o.");
        request.setCompanyAddress("Zmaja od Bosne bb, Sarajevo");
        request.setCompanyEmail("company-" + suffix + "@test.ba");
        request.setAdminFirstName("Emina");
        request.setAdminLastName("Zubetljak");
        request.setAdminEmail("admin-" + suffix + "@test.ba");
        return request;
    }
}
