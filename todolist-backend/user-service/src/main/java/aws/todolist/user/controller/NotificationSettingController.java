package aws.todolist.user.controller;

import aws.todolist.user.api.ApiResponse;
import aws.todolist.user.context.RequestContext;
import aws.todolist.user.dto.notificationSetting.NotificationSettingBulkUpdateRequest;
import aws.todolist.user.dto.notificationSetting.NotificationSettingResponse;
import aws.todolist.user.entity.Account;
import aws.todolist.user.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    @Autowired
    private NotificationSettingService notificationSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationSettingResponse>>> getMySettings() {

        Account account = RequestContext.getAccount();

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Lấy danh sách thành công", notificationSettingService.getByAccountId(account.getId()))
        );
    }

    @PutMapping("/bulk")
    public ResponseEntity<ApiResponse<List<NotificationSettingResponse>>> bulkUpdate(@RequestBody NotificationSettingBulkUpdateRequest request) {

        Account account = RequestContext.getAccount();

        List<NotificationSettingResponse> list = notificationSettingService.bulkUpdate(account.getId(), request);

        return ResponseEntity.ok(
                new ApiResponse<>(200, "Cập nhật danh sách thành công",list)
        );
    }
}
