package aws.todolist.taskflow.mapper;

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
    MemberResponseDTO toResponse(Member member);

    List<MemberResponseDTO> toResponseList(List<Member> members);
}
