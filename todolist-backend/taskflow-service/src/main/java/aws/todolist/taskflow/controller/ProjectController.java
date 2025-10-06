package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.project.ProjectDetailResponseDTO;
import aws.todolist.taskflow.dto.project.ProjectResponseDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.service.ProjectServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/projects/{id-project}")
    public ResponseEntity<ApiResponse<ProjectDetailResponseDTO>> GetProjectByID(@PathVariable("id-project") String projectID) {

        ProjectDetailResponseDTO project = projectService.getProjectById(projectID);

        ApiResponse<ProjectDetailResponseDTO> response = new ApiResponse<>(200, "project has getted successfully", project);

        return ResponseEntity.ok(response);
    }
}
