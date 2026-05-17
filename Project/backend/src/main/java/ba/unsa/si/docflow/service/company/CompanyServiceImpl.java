package ba.unsa.si.docflow.service.company;

import ba.unsa.si.docflow.dao.CompanyDAO;
import ba.unsa.si.docflow.dto.company.CompanyResponse;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;
import ba.unsa.si.docflow.mapper.CompanyMapper;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.security.CurrentUserService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDAO companyDAO;
    private final CompanyMapper companyMapper;
    private final CompanyValidation companyValidation;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CompanyResponse> findById(Long id) {
        CompanyEntity entity =
                companyValidation.validateTenantAccess(id, currentUserService.getCurrentCompanyId());
        return new ApiResponse<>("OK", companyMapper.entityToDto(entity));
    }

    @Override
    public ApiResponse<CompanyResponse> update(CompanyUpdateRequest request) {
        CompanyEntity entity =
                companyValidation.validateTenantAccess(
                        request.getId(), currentUserService.getCurrentCompanyId());
        companyValidation.validateUpdate(request);

        companyMapper.updateEntityFromRequest(request, entity);

        if (StringUtils.hasText(request.getEmail())) {
            entity.setEmail(request.getEmail().trim());
        }

        if (StringUtils.hasText(request.getStatus())) {
            entity.setStatus(CompanyStatus.valueOf(request.getStatus().toUpperCase()));
        }

        CompanyEntity saved = companyDAO.merge(entity);

        return new ApiResponse<>("OK", companyMapper.entityToDto(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyEntity getEntityById(Long id) {
        return companyValidation.validateExists(id);
    }
}
