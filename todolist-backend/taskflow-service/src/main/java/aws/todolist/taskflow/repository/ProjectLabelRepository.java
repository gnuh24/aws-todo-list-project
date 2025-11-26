package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.ProjectLabel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProjectLabelRepository extends JpaRepository<ProjectLabel, String> {

    	List<ProjectLabel> findAllByProjectIdAndIsDeletedFalse(String projectId);
	
	    @Query("SELECT p FROM ProjectLabel p WHERE p.project.id = :projectId AND p.name = :name AND p.isDeleted = false")
	ProjectLabel findByProjectIdAndName(@Param("projectId") String projectId, @Param("name") String name);
	
}
