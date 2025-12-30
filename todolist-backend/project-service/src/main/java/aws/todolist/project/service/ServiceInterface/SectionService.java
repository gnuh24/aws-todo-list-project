package aws.todolist.project.service.ServiceInterface;


import aws.todolist.project.dto.section.*;

import java.util.List;

public interface SectionService {

    List<SectionResponseDTO> getAllSection(String idProject);

    SectionResponseDTO addSection(String idProject, SectionCreateRequestDTO requestDTO);

    SectionResponseDTO updatePositionSection(String idSection, SectionUpdateRequestDTO requestDTO);

    SectionResponseDTO updateNameSection(String idSection, SectionUpdateNameDTO requestDTO);

    SectionResponseDTO removeSection(String idSection);
}
