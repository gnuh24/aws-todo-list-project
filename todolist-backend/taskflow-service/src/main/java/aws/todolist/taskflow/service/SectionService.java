package aws.todolist.taskflow.service;


import aws.todolist.taskflow.dto.section.SectionCreateRequestDTO;
import aws.todolist.taskflow.dto.section.SectionDeleteAndMigrateDTO;
import aws.todolist.taskflow.dto.section.SectionResponseDTO;
import aws.todolist.taskflow.dto.section.SectionUpdateRequestDTO;

import java.util.List;

public interface SectionService {

    List<SectionResponseDTO> getAllSection(String idProject);

    SectionResponseDTO addSection(String idProject, SectionCreateRequestDTO requestDTO);

    SectionResponseDTO updateSection(String idSection, SectionUpdateRequestDTO requestDTO);

    SectionResponseDTO removeSection(String idSection);

    SectionResponseDTO removeSectionAndMigrate(SectionDeleteAndMigrateDTO requestDTO);
}
