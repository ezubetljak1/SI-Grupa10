package ba.unsa.si.docflow.security;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    void protectedApiWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code", equalTo("UNAUTHORIZED")))
                .andExpect(
                        jsonPath(
                                "$.payload",
                                equalTo("Authentication is required to access this resource.")));
    }

    @Test
    void swaggerUiIsPublic() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void apiDocsArePublic() throws Exception {
        mockMvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
    }

    @Test
    void protectedApiWithJwtIsNotRejectedBySecurityLayer() throws Exception {
        mockMvc.perform(
                        get("/api/documents")
                                .with(
                                        SecurityMockMvcRequestPostProcessors.jwt()
                                                .jwt(
                                                        jwt ->
                                                                jwt.subject("keycloak-test-user-id")
                                                                        .claim(
                                                                                "email",
                                                                                "test@docflow.local"))))
                .andExpect(
                        result -> {
                            int status = result.getResponse().getStatus();

                            if (status == 401) {
                                throw new AssertionError(
                                        "Request with JWT should not be rejected as unauthorized.");
                            }
                        });
    }
}
