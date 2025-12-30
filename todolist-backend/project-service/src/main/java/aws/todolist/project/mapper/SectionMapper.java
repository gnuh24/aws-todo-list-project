package aws.todolist.project.mapper;

import aws.todolist.project.dto.event.ActorDto;
import aws.todolist.project.dto.event.dto.SectionEventDto;
import aws.todolist.project.dto.event.payload.SectionPayload;
import aws.todolist.project.dto.section.SectionResponseDTO;
import aws.todolist.project.entity.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SectionMapper {

    // Map Section → SectionResponseDTO full (có tasks)
    @Mapping(target = "tasks", source = "tasks")
    SectionResponseDTO toResponse(Section section);

    // Map Section → SectionResponseDTO chỉ id + name + position
    @Mapping(target = "tasks", ignore = true)
    SectionResponseDTO toResponseNameAndId(Section section);

    // Map list Section → list SectionResponseDTO full, filter isDeleted
    @Named("full")
    default List<SectionResponseDTO> toResponseList(List<Section> sections) {
        if (sections == null || sections.isEmpty()) return List.of();
        return sections.stream()
                .filter(section -> section.getIsDeleted() == null || !section.getIsDeleted())
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Map list Section → list SectionResponseDTO (name + id only), filter isDeleted
    @Named("nameAndId")
    default List<SectionResponseDTO> toResponseNameAndIdList(List<Section> sections) {
        if (sections == null || sections.isEmpty()) return List.of();
        return sections.stream()
                .filter(section -> section.getIsDeleted() == null || !section.getIsDeleted())
                .map(this::toResponseNameAndId)
                .collect(Collectors.toList());
    }

    SectionEventDto toEventDto(Section section);

    default SectionPayload toPayload(
            Section section,
            ActorDto actor,
            List<String> receivers
    ) {
        SectionPayload payload = new SectionPayload();

        // rất quan trọng để route WS đúng project
        payload.setProjectId(section.getProject().getId());

        payload.setActor(actor);
        payload.setReceivers(receivers);
        payload.setSection(toEventDto(section));

        return payload;
    }
}
