package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.company.CompanyRegisterRequest;
import ba.unsa.si.docflow.dto.company.CompanyRegisterResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.company.CompanyRegistrationService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/companies")
@AllArgsConstructor
@Tag(name = "Public Company API", description = "Public company registration endpoints")
public class PublicCompanyController {

    private final CompanyRegistrationService companyRegistrationService;

    @PostMapping("/register")
    public ApiResponse<CompanyRegisterResponse> register(
            @Valid @RequestBody CompanyRegisterRequest request) {
        return new ApiResponse<>("OK", companyRegistrationService.register(request));
    }
}
