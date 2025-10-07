package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRequestDTO;

import java.util.List;

public interface MemberService {

    List<MemberResponseDTO> getAllMember(String idProject);

    MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO);

    MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRequestDTO requestDTO);

    MemberResponseDTO deleteMember(String idMember);
}
