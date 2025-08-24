package com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.adapter;

import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;
import com.plazoleta.microservicio_plazoleta.domain.spi.ITraceLogOutPort;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.client.ITraceLogFeignClient;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.dto.TraceCreateLogFeignRequestDto;
import com.plazoleta.microservicio_plazoleta.infrastructure.output.feign.mapper.ITraceLogFeignMapper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TraceLogFeignAdapter implements ITraceLogOutPort {
    private final ITraceLogFeignClient traceFeignClient;
    private final ITraceLogFeignMapper traceFeignMapper;

    @Override
    public void recordTrace(Tracelog trace) {
        TraceCreateLogFeignRequestDto dto = new TraceCreateLogFeignRequestDto(
                trace.getOrderId(),
                trace.getClientId(),
                trace.getClientEmail(),
                trace.getEmployeeId(),
                trace.getEmployeeEmail(),
                trace.getOldStatus(),
                trace.getNewStatus(),
                trace.getAt()
        );
        traceFeignClient.create(dto);
    }

    @Override
    public List<Tracelog> findClientTrace(Long orderId, Long clientId) {
        var list = traceFeignClient.getClientTrace(orderId, clientId);
        return traceFeignMapper.toListOrderTrace(list);
    }
}
