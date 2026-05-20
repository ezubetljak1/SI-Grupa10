package ba.unsa.si.docflow.service.user;

import ba.unsa.si.docflow.dao.UserDAO;
import ba.unsa.si.docflow.dto.user.UserCreateRequest;
import ba.unsa.si.docflow.entity.UserEntity;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;
import ba.unsa.si.docflow.service.company.CompanyValidation;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserValidation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PERSON_NAME_PATTERN =
            Pattern.compile("^[A-Za-zČĆŽŠĐčćžšđ]+([ '-][A-Za-zČĆŽŠĐčćžšđ]+)*$");

    private final UserDAO userDAO;
    private final CompanyValidation companyValidation;
    private final MessageSource messageSource;

    public UserEntity validateExists(Long id) {
        UserEntity entity = userDAO.findByPK(id);

        if (entity == null) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "user.validation.not_found", null, Locale.getDefault()));
        }

        return entity;
    }

    public UserEntity validateExistsInCompany(Long id, Long companyId) {
        UserEntity entity = userDAO.findByIdAndCompanyId(id, companyId);

        if (entity == null) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "user.validation.not_found", null, Locale.getDefault()));
        }

        return entity;
    }

    public void validateCreate(UserCreateRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (request.getCompanyId() == null) {
            addError(errors, "USER_COMPANY_REQUIRED", "user.validation.company.required");
        } else {
            companyValidation.validateExists(request.getCompanyId());
        }

        if (request.getRole() == null) {
            addError(errors, "USER_ROLE_REQUIRED", "user.validation.role.required");
        }

        if (!StringUtils.hasText(request.getFirstName())) {
            addError(errors, "USER_FIRST_NAME_REQUIRED", "user.validation.first_name.required");
        }

        if (!StringUtils.hasText(request.getLastName())) {
            addError(errors, "USER_LAST_NAME_REQUIRED", "user.validation.last_name.required");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            addError(errors, "USER_EMAIL_REQUIRED", "user.validation.email.required");
        } else if (!EMAIL_PATTERN.matcher(request.getEmail().trim()).matches()) {
            addError(errors, "USER_EMAIL_INVALID", "user.validation.email.invalid");
        } else if (request.getCompanyId() != null
                && userDAO.existsByEmailAndCompanyId(
                        request.getEmail(), request.getCompanyId(), null)) {
            addError(errors, "USER_EMAIL_EXISTS", "user.validation.email.exists");
        }

        if (!StringUtils.hasText(request.getKeycloakUserId())) {
            addError(
                    errors,
                    "USER_KEYCLOAK_ID_REQUIRED",
                    "user.validation.keycloak_user_id.required");
        } else if (userDAO.findByKeycloakUserId(request.getKeycloakUserId()) != null) {
            addError(errors, "USER_KEYCLOAK_ID_EXISTS", "user.validation.keycloak_user_id.exists");
        }

        validatePersonName(
                errors,
                request.getFirstName(),
                "COMPANY_ADMIN_FIRST_NAME_INVALID",
                "company.validation.admin_first_name.invalid");

        validatePersonName(
                errors,
                request.getLastName(),
                "COMPANY_ADMIN_LAST_NAME_INVALID",
                "company.validation.admin_last_name.invalid");

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    private void validatePersonName(
            ValidationErrors errors, String value, String code, String messageKey) {
        if (StringUtils.hasText(value) && !PERSON_NAME_PATTERN.matcher(value.trim()).matches()) {
            addError(errors, code, messageKey);
        }
    }

    private void addError(ValidationErrors errors, String code, String messageKey) {
        errors.add(code, messageSource.getMessage(messageKey, null, Locale.getDefault()));
    }
}
