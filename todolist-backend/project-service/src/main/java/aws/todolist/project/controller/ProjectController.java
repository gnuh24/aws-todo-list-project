package aws.todolist.project.controller;


import aws.todolist.project.annotation.RequireProjectRole;
import aws.todolist.project.api.ApiResponse;
import aws.todolist.project.dto.project.ProjectCreateRequestDTO;
import aws.todolist.project.dto.project.ProjectDetailResponseDTO;
import aws.todolist.project.dto.project.ProjectResponseDTO;
import aws.todolist.project.dto.project.ProjectUpdateRequestDTO;
import aws.todolist.project.enums.Role;
import aws.todolist.project.service.ProjectServiceImpl;
import aws.todolist.project.service.ServiceInterface.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@Tag(name = "Project API", description = "CRUD của project")
public class ProjectController {

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    private AccountService accountService;


    @Operation(summary = "Lấy danh sách các project", description = "Lấy danh sách các project theo id của người dùng")
    @GetMapping("/projects")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> GetListProject() {

        List<ProjectResponseDTO> listProject = projectService.getAllProject();

        ApiResponse<List<ProjectResponseDTO>> response = new ApiResponse<>(200, "Danh sách dự án được lấy thành công", listProject);

        return ResponseEntity.ok(response);

    }


    @Operation(summary = "Lấy thông tin chi tiết của project", description = "Lấy toàn bộ thông tin chi tiết của project")
    @GetMapping("/projects/{id}")
    @RequireProjectRole({Role.OWNER, Role.ADMIN, Role.MEMBER, Role.VIEWER})
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> GetProjectByID(@PathVariable("id") String projectID) {

        ProjectDetailResponseDTO project = projectService.getProjectById(projectID);

        ApiResponse<ProjectDetailResponseDTO> response = new ApiResponse<>(200, "Chi tiết dự án lấy thành công", project);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Tạo một project mới", description = "Thực hiện khởi tạo một project mới")
    @PostMapping("/projects")
    // @Valid là annotation dùng để kích hoạt validation trên các đối tượng (DTO, entity…) khi được truyền vào controller.
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> addNewProject(@RequestBody @Valid ProjectCreateRequestDTO request) {
        ProjectResponseDTO project = projectService.addProject(request);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "Dự án mới được tạo thành công", project);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Chỉnh sửa project", description = "Chỉnh sửa thông tin project bao gồm name và isArchived")
    @PatchMapping("/projects/{id}")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable("id") String projectID, @RequestBody ProjectUpdateRequestDTO request) {
        ProjectResponseDTO project = projectService.updateProject(projectID, request);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "Dự án mới được cập nhật thành công", project);

        return ResponseEntity.ok(response);

    }

    @Operation(summary = "Xóa project", description = "Chuyển trạng thái project về đã xóa")
    @DeleteMapping("/projects/{projectId}")
    @RequireProjectRole({Role.OWNER})
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> removeProject(@PathVariable("projectId") String projectId) {


        ProjectResponseDTO project = projectService.removeProject(projectId);

        ApiResponse<ProjectResponseDTO> response = new ApiResponse<>(200, "Dự án cập nhật trạng thái đã xóa", project);

        return ResponseEntity.ok(response);

    }


}
