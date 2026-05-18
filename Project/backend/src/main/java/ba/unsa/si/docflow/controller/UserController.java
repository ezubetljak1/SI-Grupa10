package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.user.UserFilterRequest;
import ba.unsa.si.docflow.dto.user.UserResponse;
import ba.unsa.si.docflow.dto.user.UserUpdateRequest;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.response.PagedResponse;
import ba.unsa.si.docflow.security.CurrentUserService;
import ba.unsa.si.docflow.service.user.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company/users")
@AllArgsConstructor
@Tag(name = "User Management API", description = "Admin user management endpoints")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    @GetMapping
    public PagedResponse<UserResponse> findAll(@ModelAttribute UserFilterRequest filter) {
        Long companyId = currentUserService.getCurrentCompanyId();
        return userService.findAll(filter, companyId);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findById(@PathVariable Long id) {
        Long companyId = currentUserService.getCurrentCompanyId();
        return new ApiResponse<>("OK", userService.findByIdAndCompanyId(id, companyId));
    }

    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        Long companyId = currentUserService.getCurrentCompanyId();
        return new ApiResponse<>("OK", userService.update(id, request, companyId));
    }
}