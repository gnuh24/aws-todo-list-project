package aws.todolist.taskflow.mapper;


import aws.todolist.taskflow.context.RequestContext;
import aws.todolist.taskflow.dto.event.ActorDto;
import aws.todolist.taskflow.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = RequestContext.class)
public interface ActorMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "displayName", target = "name")
    @Mapping(
            target = "clientId",
            expression = "java(RequestContext.getClientId())"
    )
    ActorDto toActorDto(Account account);
}