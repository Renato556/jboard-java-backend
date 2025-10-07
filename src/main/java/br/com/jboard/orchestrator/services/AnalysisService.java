package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.AnalysisClient;
import br.com.jboard.orchestrator.models.AnalysisResponseDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisDTO;
import br.com.jboard.orchestrator.models.dtos.AnalysisRequestDTO;
import br.com.jboard.orchestrator.models.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AnalysisService {
    private final AnalysisClient analysisClient;
    private final SkillService skillService;

    public AnalysisService(AnalysisClient analysisClient, SkillService skillService){
        this.analysisClient = analysisClient;
        this.skillService = skillService;
    }

    public AnalysisResponseDTO analyseMatch(AnalysisDTO data, String username) {
        var skills = skillService.getAllSkills(username).getSkills();

        if (skills.isEmpty()) throw new BadRequestException( "No skills found for user " + username);

        var request = new AnalysisRequestDTO(data.getPosition(), skills);
        return analysisClient.analyseMatch(request);
    }
}
