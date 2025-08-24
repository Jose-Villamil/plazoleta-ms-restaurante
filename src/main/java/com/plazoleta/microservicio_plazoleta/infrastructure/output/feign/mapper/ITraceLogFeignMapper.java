package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper;

import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.TraceLogFeignResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ITraceLogFeignMapper {
    Tracelog toOrderTrace(TraceLogFeignResponseDto dto);
    List<Tracelog> toListOrderTrace(List<TraceLogFeignResponseDto> list);
}
