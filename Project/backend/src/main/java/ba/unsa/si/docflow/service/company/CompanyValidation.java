package ba.unsa.si.docflow.service.company;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.exception.ApiNotFoundException;
import ba.unsa.si.docflow.exception.ApiValidationException;
import ba.unsa.si.docflow.response.ValidationErrors;

import lombok.AllArgsConstructor;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class CompanyValidation {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PERSON_NAME_PATTERN =
            Pattern.compile("^[A-Za-zČĆŽŠĐčćžšđ]+([ '-][A-Za-zČĆŽŠĐčćžšđ]+)*$");
    private static final Pattern ADDRESS_HAS_NUMBER_OR_BB_PATTERN =
            Pattern.compile(".*(\\d+|\\bb\\.?\\s*b\\.?\\b).*", Pattern.CASE_INSENSITIVE);
    private final CompanyDAO companyDAO;
    private final MessageSource messageSource;

    public CompanyEntity validateExists(Long id) {
        CompanyEntity entity = companyDAO.findByPK(id);

        if (entity == null) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "company.validation.not_found", null, Locale.getDefault()));
        }

        return entity;
    }

    /**
     * Ensures the authenticated user may access the given company. Cross-tenant access returns 404.
     */
    public CompanyEntity validateTenantAccess(Long companyId, Long currentUserCompanyId) {
        CompanyEntity entity = validateExists(companyId);

        if (currentUserCompanyId == null || !entity.getId().equals(currentUserCompanyId)) {
            throw new ApiNotFoundException(
                    messageSource.getMessage(
                            "company.validation.not_found", null, Locale.getDefault()));
        }

        return entity;
    }

    public void validateUpdate(CompanyUpdateRequest request) {
        ValidationErrors errors = new ValidationErrors();

        if (StringUtils.hasText(request.getName()) && request.getName().length() > 255) {
            addError(errors, "COMPANY_NAME_TOO_LONG", "company.validation.name.max_length");
        }

        if (StringUtils.hasText(request.getAddress()) && request.getAddress().length() > 500) {
            addError(errors, "COMPANY_ADDRESS_TOO_LONG", "company.validation.address.max_length");
        }

        if (StringUtils.hasText(request.getEmail())) {
            validateEmailFormat(errors, request.getEmail(), "COMPANY_EMAIL_INVALID");

            if (companyDAO.existsByEmail(request.getEmail(), request.getId())) {
                addError(errors, "COMPANY_EMAIL_EXISTS", "company.validation.email.exists");
            }
        }

        if (StringUtils.hasText(request.getAddress())
                && !ADDRESS_HAS_NUMBER_OR_BB_PATTERN
                        .matcher(request.getAddress().trim())
                        .matches()) {
            addError(
                    errors,
                    "COMPANY_ADDRESS_NUMBER_REQUIRED",
                    "company.validation.address.number_required");
        }

        if (StringUtils.hasText(request.getStatus()) && !isValidStatus(request.getStatus())) {
            addError(errors, "COMPANY_STATUS_INVALID", "company.validation.status.invalid");
        }

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    public void validateRegister(CompanyRegisterRequest request) {
        ValidationErrors errors = new ValidationErrors();

        validateRequiredText(errors, request.getCompanyName(), "COMPANY_NAME_REQUIRED");
        validateRequiredText(errors, request.getCompanyAddress(), "COMPANY_ADDRESS_REQUIRED");
        validateRequiredText(errors, request.getCompanyEmail(), "COMPANY_EMAIL_REQUIRED");
        validateRequiredText(
                errors, request.getAdminFirstName(), "COMPANY_ADMIN_FIRST_NAME_REQUIRED");
        validateRequiredText(
                errors, request.getAdminLastName(), "COMPANY_ADMIN_LAST_NAME_REQUIRED");
        validateRequiredText(errors, request.getAdminEmail(), "COMPANY_ADMIN_EMAIL_REQUIRED");

        if (StringUtils.hasText(request.getCompanyEmail())) {
            validateEmailFormat(errors, request.getCompanyEmail(), "COMPANY_EMAIL_INVALID");

            if (companyDAO.existsByEmail(request.getCompanyEmail(), null)) {
                addError(errors, "COMPANY_EMAIL_EXISTS", "company.validation.email.exists");
            }
        }

        if (StringUtils.hasText(request.getAdminEmail())) {
            validateEmailFormat(errors, request.getAdminEmail(), "COMPANY_ADMIN_EMAIL_INVALID");
        }

        if (StringUtils.hasText(request.getCompanyEmail())
                && StringUtils.hasText(request.getAdminEmail())
                && request.getCompanyEmail().equalsIgnoreCase(request.getAdminEmail())) {
            addError(
                    errors,
                    "COMPANY_ADMIN_EMAIL_SAME_AS_COMPANY",
                    "company.validation.admin_email.same_as_company");
        }

        if (StringUtils.hasText(request.getCompanyAddress())
                && !ADDRESS_HAS_NUMBER_OR_BB_PATTERN
                        .matcher(request.getCompanyAddress().trim())
                        .matches()) {
            addError(
                    errors,
                    "COMPANY_ADDRESS_NUMBER_REQUIRED",
                    "company.validation.address.number_required");
        }

        validatePersonName(
                errors,
                request.getAdminFirstName(),
                "COMPANY_ADMIN_FIRST_NAME_INVALID",
                "company.validation.admin_first_name.invalid");

        validatePersonName(
                errors,
                request.getAdminLastName(),
                "COMPANY_ADMIN_LAST_NAME_INVALID",
                "company.validation.admin_last_name.invalid");

        if (errors.hasErrors()) {
            throw new ApiValidationException(errors);
        }
    }

    private void validateRequiredText(ValidationErrors errors, String value, String code) {
        if (!StringUtils.hasText(value)) {
            addError(errors, code, "company.validation." + code.toLowerCase());
        }
    }

    private void validateEmailFormat(ValidationErrors errors, String email, String code) {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            addError(errors, code, "company.validation.email.invalid");
        }
    }

    private boolean isValidStatus(String status) {
        try {
            CompanyStatus.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private void addError(ValidationErrors errors, String code, String messageKey) {
        errors.add(code, messageSource.getMessage(messageKey, null, Locale.getDefault()));
    }

    private void validatePersonName(
            ValidationErrors errors, String value, String code, String messageKey) {
        if (StringUtils.hasText(value) && !PERSON_NAME_PATTERN.matcher(value.trim()).matches()) {
            addError(errors, code, messageKey);
        }
    }
}
