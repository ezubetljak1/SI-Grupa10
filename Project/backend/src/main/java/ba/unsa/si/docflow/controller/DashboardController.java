package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.dashboard.DashboardResponse;
import ba.unsa.si.docflow.response.ApiResponse;

import ba.unsa.si.docflow.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/company")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ApiResponse<DashboardResponse> getCompanyDashboard() {

        return new ApiResponse<>(
                "OK",
                dashboardService.getCompanyDashboard()
        );
    }

}
