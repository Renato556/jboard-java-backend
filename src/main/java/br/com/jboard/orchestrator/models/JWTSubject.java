package br.com.jboard.orchestrator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWTSubject {
    private String username;
    private String role;
}
