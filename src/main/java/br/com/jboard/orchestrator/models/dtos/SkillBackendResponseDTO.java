package br.com.jboard.orchestrator.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillBackendResponseDTO implements Serializable {
    private String username;
    private List<String> skills;
}
