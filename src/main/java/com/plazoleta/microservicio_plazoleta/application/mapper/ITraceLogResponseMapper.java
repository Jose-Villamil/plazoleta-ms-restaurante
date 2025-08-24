package com.plazoleta.microservicio_plazoleta.application.mapper;

import com.plazoleta.microservicio_plazoleta.application.dto.response.TraceLogResponseDto;
import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ITraceLogResponseMapper {
    TraceLogResponseDto toDto(Tracelog log);
    List<TraceLogResponseDto> toDtoList(List<Tracelog> logs);
}
