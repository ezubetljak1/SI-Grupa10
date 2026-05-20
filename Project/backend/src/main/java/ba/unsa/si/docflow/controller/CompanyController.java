package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.company.CompanyResponse;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.company.CompanyService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@AllArgsConstructor
@Tag(name = "Company API", description = "Company profile endpoints")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> findById(@PathVariable Long id) {
        return companyService.findById(id);
    }

    @PatchMapping("/{id}")
    public ApiResponse<CompanyResponse> update(
            @PathVariable Long id, @Valid @RequestBody CompanyUpdateRequest request) {
        request.setId(id);
        return companyService.update(request);
    }
}
