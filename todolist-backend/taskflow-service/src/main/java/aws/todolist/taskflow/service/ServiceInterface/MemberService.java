package aws.todolist.taskflow.service.ServiceInterface;

import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateStatusRequestDTO;

import java.util.List;

public interface MemberService {

    List<MemberResponseDTO> getAllMember(String idProject);

    MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO);

    MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRoleRequestDTO requestDTO);

    MemberResponseDTO deleteMember(String idMember);

    MemberResponseDTO responseRequestMember(String idProject, MemberUpdateStatusRequestDTO requestDTO);
}
