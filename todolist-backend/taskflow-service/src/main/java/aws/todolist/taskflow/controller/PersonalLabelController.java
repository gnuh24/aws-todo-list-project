package aws.todolist.taskflow.controller;

import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.personalLabel.PersonalLabelRequestDTO;
import aws.todolist.taskflow.dto.personalLabel.PersonalLabelResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/personal-labels")
@RequiredArgsConstructor
public class PersonalLabelController {
	
	private final LabelService labelService;
	
	// ================= CREATE PERSONAL LABEL =================
	@Operation(summary = "Tạo nhãn cá nhân", description = "Tạo nhãn label cá nhân cho người dùng hiện tại")
	@PostMapping()
	public ResponseEntity<ApiResponse<PersonalLabelResponseDTO>> createPersonalLabel(
	    @Valid @RequestBody PersonalLabelRequestDTO req
	) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (!(principal instanceof Account account)) {
			return ResponseEntity.status(401).body(new ApiResponse<>(401, "User not authenticated", null));
		}
		PersonalLabelResponseDTO label = labelService.createPersonalLabel(req, account);
		
		return ResponseEntity.ok(
		    new ApiResponse<>(200, "Tạo nhãn cá nhân thành công", label)
		);
	}

//    // ================= GET ALL PERSONAL LABELS =================
//    @Operation(summary = "Lấy toàn bộ nhãn cá nhân", description = "Lấy danh sách nhãn cá nhân theo user đang đăng nhập")
//    @GetMapping("/v1/personal-labels")
//    public ResponseEntity<ApiResponse<?>> getMyPersonalLabels(
//            @AuthenticationPrincipal Account user
//    ) {
//        return ResponseEntity.ok(
//                new ApiResponse<>(200, "Lấy danh sách nhãn cá nhân thành công",
//                        labelService.getPersonalLabelsByUser(user.getId()))
//        );
//    }
//
//    // ================= DELETE PERSONAL LABEL =================
//    @Operation(summary = "Xóa nhãn cá nhân", description = "Xóa nhãn cá nhân theo ID (soft delete)")
//    @DeleteMapping("/v1/personal-labels/{labelId}")
//    public ResponseEntity<ApiResponse<Void>> deletePersonalLabel(
//            @PathVariable("labelId") String labelId,
//            @AuthenticationPrincipal Account user
//    ) {
//        labelService.deletePersonalLabel(labelId, user);
//        return ResponseEntity.ok(
//                new ApiResponse<>(200, "Xóa nhãn cá nhân thành công")
//        );
//    }
}
