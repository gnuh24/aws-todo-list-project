package aws.todolist.taskflow.controller;


import aws.todolist.taskflow.annotation.RequireProjectRole;
import aws.todolist.taskflow.api.ApiResponse;
import aws.todolist.taskflow.dto.section.*;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.service.ServiceInterface.AccountService;
import aws.todolist.taskflow.service.ServiceInterface.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects")
@Tag(name = "Section API", description = "CRUD của section")
// TODO: OWNER, MEMBER quyền post, patch, delete
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @Autowired
    private AccountService accountService;

    @Operation(summary = "Lấy danh sách section của project", description = "Dùng id client cung cấp để lấy danh sách section")
    @GetMapping("/{idProject}/sections")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<List<SectionResponseDTO>>> getSectionByIdProject(@PathVariable("idProject") String id) {
        List<SectionResponseDTO> sections = sectionService.getAllSection(id);

        ApiResponse<List<SectionResponseDTO>> response = new ApiResponse<>(200, "Danh sách section đã được lấy thành công.", sections);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Thêm mới một section cho project", description = "Tạo mới một section trong project của user")
    @PostMapping("/{idProject}/sections")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<SectionResponseDTO>> addNewSection(@PathVariable("idProject") String id, @RequestBody @Valid SectionCreateRequestDTO requestDTO) {
        SectionResponseDTO section = sectionService.addSection(id, requestDTO);

        ApiResponse<SectionResponseDTO> response = new ApiResponse<>(200, "Section mới đã được tạo thành công.", section);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật vị trí section", description = "Đổi vị trí khác cho section")
    @PatchMapping("/{idProject}/sections/{idSection}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<SectionResponseDTO>> updateSection(@PathVariable("idProject") String idProject, @PathVariable("idSection") String id, @RequestBody @Valid SectionUpdateRequestDTO requestDTO) {
        SectionResponseDTO section = sectionService.updatePositionSection(id, requestDTO);

        ApiResponse<SectionResponseDTO> response = new ApiResponse<>(200, "Section đã được cập nhật thành công.", section);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cập nhật tên section", description = "Đổi tên khác cho section")
    @PatchMapping("/{idProject}/sections/{idSection}/update-name")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<SectionResponseDTO>> updateNameSection(@PathVariable("idProject") String idProject, @PathVariable("idSection") String id, @RequestBody @Valid SectionUpdateNameDTO requestDTO) {
        SectionResponseDTO section = sectionService.updateNameSection(id, requestDTO);

        ApiResponse<SectionResponseDTO> response = new ApiResponse<>(200, "Section đã được cập nhật thành công.", section);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Xóa section", description = "Xóa section và các task trong section")
    @DeleteMapping("/{idProject}/sections/{idSection}")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<SectionResponseDTO>> deleteSection(@PathVariable("idProject") String idProject, @PathVariable("idSection") String id) {
        SectionResponseDTO section = sectionService.removeSection(id);

        ApiResponse<SectionResponseDTO> response = new ApiResponse<>(200, "Section đã được xóa thành công.", section);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Xóa section và di cư task", description = "Thực hiện xóa section đồng thời, di cư task qua section khác")
    @DeleteMapping("/{idProject}/sections")
    @RequireProjectRole({Role.OWNER, Role.MEMBER})
    public ResponseEntity<ApiResponse<SectionResponseDTO>> deleteSectionAndMigrateTask(@PathVariable("idProject") String idProject, @RequestBody @Valid SectionDeleteAndMigrateDTO requestDTO) {
        SectionResponseDTO section = sectionService.removeSectionAndMigrate(requestDTO);

        ApiResponse<SectionResponseDTO> response = new ApiResponse<>(200, "Section đã được xóa và các task đã được di cư thành công.", section);

        return ResponseEntity.ok(response);
    }

}
