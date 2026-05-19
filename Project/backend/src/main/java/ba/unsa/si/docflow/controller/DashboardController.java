package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.dashboard.DashboardResponse;
import ba.unsa.si.docflow.entity.enums.RoleName;
import ba.unsa.si.docflow.response.ApiResponse;

import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    private final CurrentUserService currentUserService;

    @GetMapping("/company")
    public ApiResponse<DashboardResponse> getCompanyDashboard() {
        currentUserService.requireAnyRole(RoleName.ADMIN, RoleName.MANAGER);

        return new ApiResponse<>(
                "OK",
                dashboardService.getCompanyDashboard()
        );
    }

}
