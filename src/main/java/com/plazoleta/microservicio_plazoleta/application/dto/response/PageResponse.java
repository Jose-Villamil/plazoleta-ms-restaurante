package com.plazoleta.microservicio_plazoleta.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PageResponse<T> {
    private java.util.List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
