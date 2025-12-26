package aws.todolist.taskflow.service.ServiceInterface;


import aws.todolist.taskflow.dto.section.*;

import java.util.List;

public interface SectionService {

    List<SectionResponseDTO> getAllSection(String idProject);

    SectionResponseDTO addSection(String idProject, SectionCreateRequestDTO requestDTO);

    SectionResponseDTO updatePositionSection(String idSection, SectionUpdateRequestDTO requestDTO);

    SectionResponseDTO updateNameSection(String idSection, SectionUpdateNameDTO requestDTO);

    SectionResponseDTO removeSection(String idSection);

    SectionResponseDTO removeSectionAndMigrate(SectionDeleteAndMigrateDTO requestDTO);
}
