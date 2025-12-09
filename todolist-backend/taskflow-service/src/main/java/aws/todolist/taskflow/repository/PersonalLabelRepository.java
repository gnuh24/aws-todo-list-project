package aws.todolist.taskflow.repository;

import aws.todolist.taskflow.entity.PersonalLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface PersonalLabelRepository extends JpaRepository<PersonalLabel, String> {

    List<PersonalLabel> findAllByAccountIdAndIsDeletedFalse(String accountId);
}
