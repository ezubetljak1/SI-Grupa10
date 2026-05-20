package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.role.RoleResponse;
import ba.unsa.si.docflow.entity.RoleEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "name", expression = "java(entity.getName().name())")
    RoleResponse entityToDto(RoleEntity entity);

    List<RoleResponse> entitiesToDtos(List<RoleEntity> entities);
}
