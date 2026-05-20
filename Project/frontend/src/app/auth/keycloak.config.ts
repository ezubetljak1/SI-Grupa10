export interface KeycloakClientConfig {
  url: string;
  realm: string;
  clientId: string;
}

export const keycloakConfig: KeycloakClientConfig = {
  url: 'http://localhost:8081',
  realm: 'docflow',
  clientId: 'docflow-frontend',
};
