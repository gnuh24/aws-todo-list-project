package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.project.ProjectCreateRequestDTO;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.ProjectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Project API", description = "CRUD của project")
public class ProjectController {

    @Autowired
    private ProjectServiceImpl projectService;


    @Operation(summary = "Lấy danh sách các project", description = "Lấy danh sách các project theo id của người dùng")
    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> GetListProject(@AuthenticationPrincipal Account account) {

        List<ProjectResponseDTO> listProject = projectService.getAllProject(account.getId());

        ApiResponse<List<ProjectResponseDTO>> response = new ApiResponse<>(200, "list projects has getted successfullys", listProject);

        return ResponseEntity.ok(response);

    }


    @Operation(summary = "Lấy thông tin chi tiết của project", description = "Lấy toàn bộ thông tin chi tiết của project")
    @GetMapping("/projects/{id}")
    @RequireProjectRole({Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER})
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> GetProjectByID(@PathVariable("id") String projectID) {

        ProjectDetailResponseDTO project = projectService.getProjectById(projectID);

        ApiResponse<ProjectDetailResponseDTO> response = new ApiResponse<>(200, "project has getted successfully", project);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tạo một project mới", description = "Thực hiện khởi tạo một project mới")
    @PostMapping("/projects")
    // @Valid là annotation dùng để kích hoạt validation trên các đối tượng (DTO, entity…) khi được truyền vào controller.
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addNewProject(@RequestBody @Valid ProjectCreateRequestDTO request, @AuthenticationPrincipal Account account) {
        ProjectResponseDTO project = projectService.addProject(request, account);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "new project was created completely", project);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa project", description = "Chỉnh sửa thông tin project bao gồm name và isArchived")
    @PatchMapping("/projects/{id}")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable("id") String projectID, @RequestBody ProjectUpdateRequestDTO request) {
        ProjectResponseDTO project = projectService.updateProject(projectID, request);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "project was updated completely", project);

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Xóa project", description = "Chuyển trạng thái project về đã xóa")
    @DeleteMapping("/projects/{projectId}")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeProject(@PathVariable("projectId") String projectId, @AuthenticationPrincipal Account account) {
        ProjectResponseDTO project = projectService.removeProject(projectId, account);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "project was updated to is_Deleted completely", project);

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Phục hồi project đã xóa", description = "Chuyển trạng thái project đã xóa về như cũ")
    @PatchMapping("/projects/{id}/restore")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> restoreProject(@PathVariable("id") String projectID) {
        ProjectResponseDTO project = projectService.restoreProject(projectID);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "project was updated to is_Deleted completely", project);

        return ResponseEntity.ok(response);

    }

}
