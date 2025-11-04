package aws.todolist.taskflow.mapper;

import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberMapper {

    public MemberResponseDTO ResponseDTO(Member member) {
        return MemberResponseDTO.builder()
                .id(member.getId())
                .accountId(member.getAccount().getId())
                .displayName(member.getAccount().getDisplayName())
                .avatar(member.getAccount().getAvatar())
                .role(member.getRole())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public List<MemberResponseDTO> ResponseDTOList(List<Member> members) {
        return members.stream().map(this::ResponseDTO).toList();
    }
}
