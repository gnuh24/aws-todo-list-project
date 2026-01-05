package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.event.ActorDto;
import aws.todolist.taskflow.dto.event.dto.MemberEventDto;
import aws.todolist.taskflow.dto.event.payload.MemberPayload;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "displayName", source = "account.displayName")
    @Mapping(target = "avatar", source = "account.avatar")
    @Mapping(target = "email", source = "account.email")
    MemberResponseDTO toResponse(Member member);

    List<MemberResponseDTO> toResponseList(List<Member> members);

    @Mapping(source = "account.id", target = "accountId")
    @Mapping(source = "account.displayName", target = "displayName")
    @Mapping(source = "account.avatar", target = "avatar")
    @Mapping(source = "account.email", target = "email")
    @Mapping(source = "project.name", target = "projectName")
    MemberEventDto toEventDto(Member member);

    default MemberPayload toPayload(
            Member member,
            ActorDto actor,
            List<String> receivers
    ) {
        MemberPayload payload = new MemberPayload();

        payload.setProjectId(member.getProject().getId()); // rất quan trọng để route WS
        payload.setActor(actor);
        payload.setReceivers(receivers);
        payload.setMember(toEventDto(member));

        return payload;
    }

}
