package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.user.*;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.service.user.UserCompanyManagementService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/users")
@AllArgsConstructor
@Tag(name = "User Management API", description = "Admin user management endpoints")
public class UserController {

    private final UserCompanyManagementService userCompanyManagementService;

    @GetMapping
    public PagedResponse<UserResponse> findAll(@ModelAttribute UserFilterRequest filter) {
        return userCompanyManagementService.findAll(filter);
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> currentUserProfile() {
        return new ApiResponse<>("OK", userCompanyManagementService.currentUserProfile());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findById(@PathVariable Long id) {
        return new ApiResponse<>("OK", userCompanyManagementService.findById(id));
    }

    @PostMapping
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateApiRequest request) {
        return new ApiResponse<>("OK", userCompanyManagementService.createUser(request));
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return new ApiResponse<>("OK", userCompanyManagementService.update(id, request));
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<UserResponse> changeRole(
            @PathVariable Long id, @Valid @RequestBody UserRoleChangeRequest request) {
        return new ApiResponse<>("OK", userCompanyManagementService.changeRole(id, request));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<UserResponse> changeStatus(
            @PathVariable Long id, @Valid @RequestBody UserStatusChangeRequest request) {
        return new ApiResponse<>("OK", userCompanyManagementService.changeStatus(id, request));
    }

    @PostMapping("/{id}/reset-password")
    public ApiResponse<String> resetPassword(@PathVariable Long id) {
        return userCompanyManagementService.resetPassword(id);
    }
}
