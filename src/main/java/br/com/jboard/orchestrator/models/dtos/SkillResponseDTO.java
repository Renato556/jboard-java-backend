package br.com.jboard.orchestrator.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponseDTO implements Serializable {
    private List<String> skills;
    private MetaDTO meta;
}
