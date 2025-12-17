package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Member;
import aws.todolist.taskflow.enums.StatusMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String>, JpaSpecificationExecutor<Member> {
    @Query("""
                SELECT DISTINCT m
                FROM Member m
                JOIN FETCH m.project p
                WHERE p.id = :projectId
                  AND (m.isDeleted = false OR m.isDeleted IS NULL)
                ORDER BY p.updatedAt DESC
            """)
    List<Member> findAllByProjectId(@Param("projectId") String projectId);

    Optional<Member> findFirstByAccountIdAndProjectIdAndIsDeletedFalse(
            String accountId,
            String projectId
    );

    Optional<Member> findFirstByAccountIdAndProjectIdAndStatusAndIsDeletedFalse(
            String accountId,
            String projectId,
            StatusMember status
    );


    Optional<Member> findFirstByIdAndIsDeletedFalse(
            String Id
    );

    @Query("""
                SELECT m.account.email
                FROM Member m
                WHERE m.project.id = :projectId
                  AND (m.isDeleted = false OR m.isDeleted IS NULL)
                  AND status = ACCEPTED
            """)
    List<String> findAccountIdsByProjectId(@Param("projectId") String projectId);


}
