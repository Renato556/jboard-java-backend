package com.jboard.jboardjavabackend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MetaDTO {
    private long totalRecords;
    private int totalPages;
}
