package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateStatusRequestDTO;
import aws.todolist.taskflow.entity.Account;

import java.util.List;

public interface MemberService {

    List<MemberResponseDTO> getAllMember(String idProject);

    MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO, String actorId);

    MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRoleRequestDTO requestDTO, String actorId);

    MemberResponseDTO deleteMember(String idMember);

    MemberResponseDTO responseRequestMember(String idProject, MemberUpdateStatusRequestDTO requestDTO, Account account);
}
