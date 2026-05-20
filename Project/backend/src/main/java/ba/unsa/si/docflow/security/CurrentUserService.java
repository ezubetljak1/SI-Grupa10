package ba.unsa.si.docflow.security;

import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.entity.enums.AccountStatus;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.service.role.RoleService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserDAO userDAO;
    private final RoleService roleService;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public CurrentUser getCurrentUser() {
        Jwt jwt = getCurrentJwt();
        String keycloakUserId = jwt.getSubject();

        UserEntity user = userDAO.findByKeycloakUserId(keycloakUserId);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    messageSource.getMessage(
                            "user.security.not_registered", null, Locale.getDefault()));
        }

        if (user.getAccountStatus() == AccountStatus.INACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    messageSource.getMessage(
                            "user.security.inactive", null, Locale.getDefault()));
        }

        RoleEntity role = roleService.getById(user.getRoleId());

        return new CurrentUser(
                user.getId(),
                keycloakUserId,
                user.getCompanyId(),
                resolveEmail(jwt, user),
                role.getName().name());
    }

    @Transactional(readOnly = true)
    public Long getCurrentUserId() {
        return getCurrentUser().userId();
    }

    @Transactional(readOnly = true)
    public Long getCurrentCompanyId() {
        return getCurrentUser().companyId();
    }

    @Transactional(readOnly = true)
    public String getCurrentRole() {
        return getCurrentUser().role();
    }

    public String getCurrentKeycloakUserId() {
        return getCurrentJwt().getSubject();
    }

    public String getCurrentEmail() {
        return getCurrentJwt().getClaimAsString("email");
    }

    public void requireAdmin() {
        if (!RoleName.ADMIN.name().equals(getCurrentRole())) {
            throw new AccessDeniedException(
                    messageSource.getMessage(
                            "user.security.admin_required", null, Locale.getDefault()));
        }
    }

    public void requireAnyRole(RoleName... allowedRoles) {
        String currentRole = getCurrentRole();

        for (RoleName role : allowedRoles) {
            if (role.name().equals(currentRole)) {
                return;
            }
        }

        throw new AccessDeniedException(
                messageSource.getMessage(
                        "user.security.role_required", null, Locale.getDefault()));
    }

    private String resolveEmail(Jwt jwt, UserEntity user) {
        String tokenEmail = jwt.getClaimAsString("email");

        if (tokenEmail != null && !tokenEmail.isBlank()) {
            return tokenEmail;
        }

        return user.getEmail();
    }

    private Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    messageSource.getMessage(
                            "user.security.authentication_required", null, Locale.getDefault()));
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            return jwtAuthenticationToken.getToken();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                messageSource.getMessage(
                        "user.security.invalid_token", null, Locale.getDefault()));
    }
}
