package com.plazoleta.microservicio_plazoleta.domain.spi;

import com.plazoleta.microservicio_plazoleta.domain.model.Tracelog;

import java.util.List;


public interface ITraceLogOutPort {
    void recordTrace(Tracelog trace);
    List<Tracelog> findClientTrace(Long orderId, Long clientId);
}

