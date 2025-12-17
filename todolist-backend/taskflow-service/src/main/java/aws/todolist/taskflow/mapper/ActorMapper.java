package aws.todolist.taskflow.mapper;


import aws.todolist.taskflow.dto.event.ActorDto;
import aws.todolist.taskflow.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActorMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "displayName", target = "name")
    ActorDto toActorDto(Account account);
}