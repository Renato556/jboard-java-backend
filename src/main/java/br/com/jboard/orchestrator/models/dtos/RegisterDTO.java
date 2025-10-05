package br.com.jboard.orchestrator.models.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
}
