package aws.todolist.notification.controller;


import aws.todolist.notification.api.ApiResponse;
import aws.todolist.notification.dto.activity.ActivityRequest;
import aws.todolist.notification.dto.activity.ActivityResponse;
import aws.todolist.notification.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ActivityController {


    @Autowired
    private ActivityService activityService;

    @PostMapping("/activity")
    public ResponseEntity<ApiResponse<Map<LocalDate, List<ActivityResponse>>>> getActivity(@RequestBody ActivityRequest request) {

        Map<LocalDate, List<ActivityResponse>> map  = activityService.getActivityByCondition(request);

        ApiResponse<Map<LocalDate, List<ActivityResponse>>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Lấy được danh sách activity thành công.",
                map
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
