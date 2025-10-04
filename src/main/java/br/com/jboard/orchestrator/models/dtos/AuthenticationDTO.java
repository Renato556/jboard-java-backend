package br.com.jboard.orchestrator.models.dtos;

import lombok.Data;

@Data
public class AuthenticationDTO {
    private String username;
    private String password;
}
