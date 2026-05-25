package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.task.TaskResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.task.TaskService;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
@Tag(name = "Task API", description = "Workflow task assignment and status endpoints")
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/my")
    public ApiResponse<List<TaskResponse>> findMyTasks() {
        return new ApiResponse<>("SUCCESS", taskService.findMyTasks());
    }

    @GetMapping
    public ApiResponse<List<TaskResponse>> findAll() {
        return new ApiResponse<>("SUCCESS", taskService.findAll());
    }

    @PatchMapping("/{id}/start")
    public ApiResponse<TaskResponse> start(@PathVariable Long id) {
        return new ApiResponse<>("SUCCESS", taskService.start(id));
    }

    @PatchMapping("/{id}/complete")
    public ApiResponse<TaskResponse> complete(@PathVariable Long id) {
        return new ApiResponse<>("SUCCESS", taskService.complete(id));
    }

    @PatchMapping("/{id}/cancel")
    public ApiResponse<TaskResponse> cancel(@PathVariable Long id) {
        return new ApiResponse<>("SUCCESS", taskService.cancel(id));
    }
}
