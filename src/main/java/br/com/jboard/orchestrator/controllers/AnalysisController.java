package br.com.jboard.orchestrator.controllers;

import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisDTO;
import br.com.jboard.orchestrator.services.AnalysisService;
import br.com.jboard.orchestrator.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    private final AnalysisService analysisService;
    private final AuthUtils authUtils;

    public AnalysisController(AnalysisService analysisService, AuthUtils authUtils){
        this.analysisService = analysisService;
        this.authUtils = authUtils;
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> analyze(@RequestBody @Valid AnalysisDTO data, HttpServletRequest request) {
        var username = authUtils.getUsernameFromRequest(request);

        return ResponseEntity.ok(analysisService.analyseMatch(data, username));
    }
}
