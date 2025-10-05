package br.com.jboard.orchestrator.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillDTO implements Serializable {
    @NotBlank(message = "Skill cannot be empty")
    @Size(min = 2, max = 50, message = "Skill must be between 2 and 50 characters")
    private String skill;
}
