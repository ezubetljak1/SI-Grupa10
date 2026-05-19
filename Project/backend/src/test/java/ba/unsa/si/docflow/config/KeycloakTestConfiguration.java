package ba.unsa.si.docflow.config;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ba.unsa.si.docflow.service.keycloak.KeycloakAdminService;
import ba.unsa.si.docflow.service.keycloak.KeycloakUserCreationResult;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class KeycloakTestConfiguration {

    @Bean
    @Primary
    public KeycloakAdminService keycloakAdminService() {
        KeycloakAdminService service = mock(KeycloakAdminService.class);

        when(service.createCompanyGroup(anyString()))
                .thenAnswer(invocation -> "kc-group-" + invocation.getArgument(0).hashCode());

        when(service.createUser(anyString(), anyString(), anyString(), anyString(), anyBoolean()))
                .thenAnswer(
                        invocation ->
                                new KeycloakUserCreationResult(
                                        "kc-user-"
                                                + invocation.getArgument(0).toString().hashCode(),
                                        "TempPass123!"));

        org.mockito.Mockito.doNothing().when(service).setUserEnabled(anyString(), anyBoolean());
        when(service.resetUserPassword(anyString())).thenReturn("ResetTemp123!");

        return service;
    }
}
