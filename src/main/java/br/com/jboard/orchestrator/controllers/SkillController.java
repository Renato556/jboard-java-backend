package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.dtos.SkillDTO;
import br.com.jboard.orchestrator.models.dtos.SkillResponseDTO;
import br.com.jboard.orchestrator.services.SkillService;
import br.com.jboard.orchestrator.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
public class SkillController {
    private final SkillService skillService;
    private final AuthUtils authUtils;

    public SkillController(SkillService skillService, AuthUtils authUtils) {
        this.skillService = skillService;
        this.authUtils = authUtils;
    }

    private String extractUsername(HttpServletRequest request) {
        return authUtils.getUsernameFromRequest(request);
    }

    @GetMapping
    public ResponseEntity<SkillResponseDTO> getAllSkills(HttpServletRequest request) {
        return ResponseEntity.ok(skillService.getAllSkills(extractUsername(request)));
    }

    @PostMapping
    public ResponseEntity<Void> addSkill(@Valid @RequestBody SkillDTO skillDTO, HttpServletRequest request) {
        skillService.addSkill(skillDTO, extractUsername(request));
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> removeSkill(@Valid @RequestBody SkillDTO skillDTO, HttpServletRequest request) {
        skillService.removeSkill(skillDTO, extractUsername(request));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSkills(HttpServletRequest request) {
        skillService.deleteAllSkills(extractUsername(request));
        return ResponseEntity.ok().build();
    }
}
