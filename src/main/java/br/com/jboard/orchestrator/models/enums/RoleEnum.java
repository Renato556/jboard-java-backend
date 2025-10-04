package br.com.jboard.orchestrator.models.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    FREE("free"),
    PREMIUM("premium");

    private final String role;

    RoleEnum(String role) {
        this.role = role;
    }
}
