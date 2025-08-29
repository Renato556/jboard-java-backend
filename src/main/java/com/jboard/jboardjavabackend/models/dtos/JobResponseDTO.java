package com.jboard.jboardjavabackend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobResponseDTO {
    private List<JobDTO> data;
    private MetaDTO meta;
}
