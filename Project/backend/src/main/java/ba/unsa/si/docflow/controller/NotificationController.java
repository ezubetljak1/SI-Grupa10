package ba.unsa.si.docflow.controller;

import ba.unsa.si.docflow.dto.notification.NotificationResponse;
import ba.unsa.si.docflow.dto.notification.UnreadCountResponse;
import ba.unsa.si.docflow.response.ApiResponse;
import ba.unsa.si.docflow.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ApiResponse<List<NotificationResponse>> getMyNotifications() {
        List<NotificationResponse> notifications = notificationService.getMyNotifications();
        return new ApiResponse<>("OK", notifications);
    }

    @GetMapping("/my/unread-count")
    public ApiResponse<UnreadCountResponse> getMyUnreadCount() {
        UnreadCountResponse countResponse = notificationService.getMyUnreadCount();
        return new ApiResponse<>("OK", countResponse);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markOneRead(@PathVariable Long id) {
        NotificationResponse notificationResponse = notificationService.markOneRead(id);
        return new ApiResponse<>("OK", notificationResponse);
    }

    @PatchMapping("/read-all")
    public ApiResponse<Void> markAllRead() {
        notificationService.markAllRead();
        return new ApiResponse<>("OK", null);
    }
}
