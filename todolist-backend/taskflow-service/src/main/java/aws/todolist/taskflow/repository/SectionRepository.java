package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String>, JpaSpecificationExecutor<Section> {
    List<Section> findByIsDeletedFalseOrderByUpdatedAtDesc();
}
