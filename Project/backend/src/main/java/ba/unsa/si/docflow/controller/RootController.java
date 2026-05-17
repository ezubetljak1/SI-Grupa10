package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.response.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ApiResponse<Map<String, String>> root() {
        return new ApiResponse<>(
                "OK",
                Map.of(
                        "application", "docflow-backend",
                        "swaggerUi", "/swagger-ui.html",
                        "apiDocs", "/v3/api-docs"));
    }
}
