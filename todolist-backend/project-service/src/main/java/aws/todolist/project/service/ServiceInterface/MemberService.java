package aws.todolist.project.service.ServiceInterface;

import aws.todolist.project.dto.member.MemberCreateRequestDTO;
import aws.todolist.project.dto.member.MemberResponseDTO;
import aws.todolist.project.dto.member.MemberUpdateRoleRequestDTO;
import aws.todolist.project.dto.member.MemberUpdateStatusRequestDTO;

import java.util.List;

public interface MemberService {

    List<MemberResponseDTO> getAllMember(String idProject);

    MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO);

    MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRoleRequestDTO requestDTO);

    MemberResponseDTO deleteMember(String idMember);

    MemberResponseDTO responseRequestMember(String idProject, MemberUpdateStatusRequestDTO requestDTO);
}
