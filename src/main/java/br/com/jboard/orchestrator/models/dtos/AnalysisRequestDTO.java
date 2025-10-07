package br.com.jboard.orchestrator.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AnalysisRequestDTO {
    private String position;
    private List<String> skills;
}
