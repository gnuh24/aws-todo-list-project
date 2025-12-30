package aws.todolist.project.mapper;


import aws.todolist.project.dto.event.ActorDto;
import aws.todolist.project.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ActorMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "displayName", target = "name")
    ActorDto toActorDto(Account account);
}