package br.com.jboard.orchestrator.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnalysisDTO {
    @NotEmpty
    @NotNull
    private String position;
}
