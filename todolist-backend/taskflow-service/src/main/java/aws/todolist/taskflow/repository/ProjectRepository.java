package aws.todolist.taskflow.repository;


import aws.todolist.taskflow.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {
    @Query("""
                SELECT DISTINCT p
                FROM Project p
                JOIN FETCH p.members m
                WHERE m.account.id = :accountId
                  AND m.status = ACCEPTED
                  AND (m.isDeleted = false OR m.isDeleted IS NULL)
                  AND (p.isDeleted = false OR p.isDeleted IS NULL)
                ORDER BY p.updatedAt DESC
            """)
    List<Project> findAllByAccountId(@Param("accountId") String accountId);


    Optional<Project> findByIdAndIsDeletedFalse(String id);


    @Query("""
                SELECT DISTINCT p 
                FROM Project p 
                JOIN FETCH p.members m 
                WHERE m.account.id = :accountId
                  AND (p.isDeleted = false OR p.isDeleted IS NULL)
                  AND p.isDefault = true
                  AND m.status = ACCEPTED  
            """)
    Project findProjectIsDefault(@Param("accountId") String accountId);


}
