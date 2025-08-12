package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.User;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.UserFeignResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IUserFeignMapper {
    User toUser(UserFeignResponseDto userFeignResponseDto);
}
