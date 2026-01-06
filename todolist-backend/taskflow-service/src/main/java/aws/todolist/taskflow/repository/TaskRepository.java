package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {

    Task findByIdAndIsDeletedFalse(String id);

    @Query("""
                select distinct t
                from Task t
                left join fetch t.taskLabels tl
                left join fetch tl.projectLabel pl
                where t.id = :id
                  and t.isDeleted = false
            """)
    Task findByIdWithLabels(@Param("id") String id);


    Task findByIdAndIsDeletedTrue(String id);

    @Query("SELECT t FROM Task t " +
            "JOIN t.section s " +
            "JOIN s.project p " +
            "JOIN p.members m " +
            "WHERE t.startTime >= :date AND t.isDeleted = false " +
            "AND m.account.id = :idAccount " +
            "ORDER BY t.startTime ASC")
    List<Task> findByTaskUpComingByAccount(@Param("date") LocalDateTime date, @Param("idAccount") String idAccount);

}
