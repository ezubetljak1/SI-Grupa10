package ba.unsa.si.docflow.service.company;

import ba.unsa.si.docflow.dto.company.CompanyResponse;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.response.ApiResponse;

public interface CompanyService {

    ApiResponse<CompanyResponse> findById(Long id);

    ApiResponse<CompanyResponse> update(CompanyUpdateRequest request);

    CompanyEntity getEntityById(Long id);
}
