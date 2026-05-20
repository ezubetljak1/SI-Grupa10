package ba.unsa.si.docflow.mapper;

import ba.unsa.si.docflow.dto.company.CompanyResponse;
import ba.unsa.si.docflow.dto.company.CompanyUpdateRequest;
import ba.unsa.si.docflow.entity.CompanyEntity;
import ba.unsa.si.docflow.entity.enums.CompanyStatus;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", imports = CompanyStatus.class)
public interface CompanyMapper {

    @Mapping(target = "status", expression = "java(entity.getStatus().name())")
    CompanyResponse entityToDto(CompanyEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "keycloakGroupId", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateEntityFromRequest(CompanyUpdateRequest request, @MappingTarget CompanyEntity entity);
}
