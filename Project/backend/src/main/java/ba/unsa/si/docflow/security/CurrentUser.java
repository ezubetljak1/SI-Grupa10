package ba.unsa.si.docflow.security;

public record CurrentUser(
        Long userId, String keycloakUserId, Long companyId, String email, String role) {}
