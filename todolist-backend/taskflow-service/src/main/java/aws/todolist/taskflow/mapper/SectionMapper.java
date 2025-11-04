package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.section.SectionResponseDTO;
import aws.todolist.taskflow.entity.Section;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionMapper {

    @Autowired
    private TaskMapper taskMapper;

    public SectionResponseDTO ResponseDTO(Section section) {
        return SectionResponseDTO.builder()
                .id(section.getId())
                .name(section.getName())
                .isArchived(section.getIsArchived())
                .position(section.getPosition())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .tasks(taskMapper.ResponseDTOList(section.getTasks()))
                .build();
    }

    public List<SectionResponseDTO> ResponseDTOList(List<Section> sections) {
        return sections.stream()
                .filter(section -> !section.getIsDeleted())
                .map(this::ResponseDTO)
                .toList();
    }
}
