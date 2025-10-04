package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.ChangeRoleDTO;
import br.com.jboard.orchestrator.services.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }

    @PutMapping("/change-user-role")
    public ResponseEntity updateRole(@RequestBody @Valid ChangeRoleDTO data, HttpServletRequest request) {
        roleService.updateRole(data, request.getHeader("Authorization"));

        return ResponseEntity.ok().build();
    }
}
