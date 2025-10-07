package aws.todolist.taskflow.service;

import aws.todolist.taskflow.dto.member.MemberCreateRequestDTO;
import aws.todolist.taskflow.dto.member.MemberResponseDTO;
import aws.todolist.taskflow.dto.member.MemberUpdateRequestDTO;
import aws.todolist.taskflow.entity.Account;
import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.entity.Project;
import aws.todolist.taskflow.enums.Role;
import aws.todolist.taskflow.exceptions.ProjectException.BadRequestException;
import aws.todolist.taskflow.mapper.MemberMapper;
import aws.todolist.taskflow.repository.AccountRepository;
import aws.todolist.taskflow.repository.MemberRepository;
import aws.todolist.taskflow.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Override
    public List<MemberResponseDTO> getAllMember(String idProject) {
        List<Member> members = memberRepository.findAllByProjectId(idProject);

        return memberMapper.ResponseDTOList(members);
    }

    @Transactional
    @Override
    public MemberResponseDTO addNewMember(String idProject, MemberCreateRequestDTO requestDTO) {

        // Kiểm tra xem member của account và project đã được tạo chưa
        Optional<Member> OptMember = memberRepository.findFirstByAccountIdAndProjectIdAndIsDeletedFalse(requestDTO.getIdAccount(), idProject);

        if (OptMember.isPresent()) {
            throw new BadRequestException("This account has already been added to this project.");
        }

        // Lấy thông tin chi tiết của project và account

        Optional<Project> OptProject = projectRepository.findByIdAndIsDeletedFalse(idProject);

        Optional<Account> OptAccount = accountRepository.findByIdAndIsDeletedFalse(requestDTO.getIdAccount());

        Project project;

        Account account;

        if (OptProject.isPresent()) {
            project = OptProject.get();
        } else {
            throw new BadRequestException("Project doesn't exist.");
        }

        if (OptAccount.isPresent()) {
            account = OptAccount.get();
        } else {
            throw new BadRequestException("Account doesn't exist.");
        }

        // Kiểm tra người dùng có phân quyền làm owner không

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role to a member.");
        }

        // Kiểm tra có dùng 3 quyền là ADMIN, MEMBER, VIEWER không

        try {
            Role role = Role.valueOf(String.valueOf(requestDTO.getRole()));
            // role hợp lệ
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + requestDTO.getRole());
        }

        Member member = Member.builder().account(account).project(project).role(requestDTO.getRole()).build();

        Member member_saved = memberRepository.saveAndFlush(member);

        return memberMapper.ResponseDTO(member_saved);
    }

    @Override
    public MemberResponseDTO updateRoleMember(String idMember, MemberUpdateRequestDTO requestDTO) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new BadRequestException("This member doesn't exist");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException("Cannot change role: this member is an OWNER.");
        }

        if (requestDTO.getRole() == Role.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role to a member.");
        }

        member.setRole(requestDTO.getRole());

        Member member_saved = memberRepository.saveAndFlush(member);

        return memberMapper.ResponseDTO(member_saved);
    }

    @Override
    public MemberResponseDTO deleteMember(String idMember) {
        Optional<Member> OptMember = memberRepository.findFirstByIdAndIsDeletedFalse(idMember);

        Member member;

        if (OptMember.isPresent()) {
            member = OptMember.get();
        } else {
            throw new BadRequestException("This member doesn't exist");
        }

        if (member.getRole() == Role.OWNER) {
            throw new BadRequestException("Cannot delete member: this member is an OWNER.");
        }

        member.softDelete();

        Member member_saved = memberRepository.saveAndFlush(member);

        return memberMapper.ResponseDTO(member_saved);
    }
}
