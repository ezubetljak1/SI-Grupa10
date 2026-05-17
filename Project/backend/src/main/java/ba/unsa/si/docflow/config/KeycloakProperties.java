package ba.unsa.si.docflow.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "docflow.keycloak")
public class KeycloakProperties {

    private String realm = "docflow";

    private String serverUrl = "http://localhost:8081";

    private String frontendClientId = "docflow-frontend";

    private String backendAdminClientId = "docflow-backend-admin";

    private String backendAdminClientSecret = "change_me_later";
}
