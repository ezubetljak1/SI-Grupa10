package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.user.UserResponse;
import ba.unsa.si.docflow.entity.RoleEntity;
import ba.unsa.si.docflow.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "entity.id", target = "id")
    @Mapping(source = "entity.companyId", target = "companyId")
    @Mapping(source = "entity.firstName", target = "firstName")
    @Mapping(source = "entity.lastName", target = "lastName")
    @Mapping(source = "entity.email", target = "email")
    @Mapping(source = "entity.createdAt", target = "createdAt")
    @Mapping(source = "entity.updatedAt", target = "updatedAt")
    @Mapping(target = "role", expression = "java(role.getName().name())")
    @Mapping(target = "accountStatus", expression = "java(entity.getAccountStatus().name())")
    UserResponse entityToDto(UserEntity entity, RoleEntity role);
}
