package ba.unsa.si.docflow.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrentUserService {

    public CurrentUser getCurrentUser() {
        Jwt jwt = getCurrentJwt();

        return new CurrentUser(
                null, // TODO: map to local User.id when User module is implemented
                jwt.getSubject(), // Keycloak user id
                null, // TODO: map to local User.companyId when Company/User module is implemented
                jwt.getClaimAsString("email"),
                null // TODO: map to local Role when Role module is implemented
                );
    }

    public String getCurrentKeycloakUserId() {
        return getCurrentJwt().getSubject();
    }

    public String getCurrentEmail() {
        return getCurrentJwt().getClaimAsString("email");
    }

    private Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Authentication is required.");
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getToken();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication token.");
    }
}
