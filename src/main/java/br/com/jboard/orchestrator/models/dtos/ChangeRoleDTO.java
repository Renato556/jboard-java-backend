package br.com.jboard.orchestrator.models.dtos;

import br.com.jboard.orchestrator.models.enums.RoleEnum;
import lombok.Data;

@Data
public class ChangeRoleDTO {
    private String username;
    private RoleEnum role;
}
