package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String>, JpaSpecificationExecutor<Section> {
    List<Section> findByProjectIdAndIsDeletedFalseOrderByPositionAsc(String idProject);

    Section findByIdAndIsDeletedFalse(String id);

    @Query("SELECT MAX(s.position) FROM Section s WHERE s.project.id = :projectId AND s.isDeleted = false")
    Integer findMaxPositionByProjectId(@Param("projectId") String projectId);

    @Modifying
    @Query("""
                UPDATE Section s 
                SET s.position = s.position + 1 
                WHERE s.project.id = :projectId 
                  AND s.position >= :newPosition 
                  AND s.position < :oldPosition
            """)
    void shiftPositionsUp(@Param("projectId") String projectId,
                          @Param("newPosition") int newPosition,
                          @Param("oldPosition") int oldPosition);

    @Modifying
    @Query("""
                UPDATE Section s 
                SET s.position = s.position - 1 
                WHERE s.project.id = :projectId 
                  AND s.position <= :newPosition 
                  AND s.position > :oldPosition
            """)
    void shiftPositionsDown(@Param("projectId") String projectId,
                            @Param("newPosition") int newPosition,
                            @Param("oldPosition") int oldPosition);

    @Modifying
    @Query("""
                UPDATE Section s 
                SET s.position = s.position - 1 
                WHERE s.project.id = :projectId 
                  AND s.position > :deletedPosition
            """)
    void shiftPositionsAfterDelete(@Param("projectId") String projectId,
                                   @Param("deletedPosition") int deletedPosition);


    Section findTopByProjectIdAndIsDeletedFalseOrderByPositionAsc(String idProject);


}
