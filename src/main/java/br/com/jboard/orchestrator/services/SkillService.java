package br.com.jboard.orchestrator.services;

import br.com.jboard.orchestrator.clients.SkillClient;
import br.com.jboard.orchestrator.models.Skill;
import br.com.jboard.orchestrator.models.dtos.MetaDTO;
import br.com.jboard.orchestrator.models.dtos.SkillDTO;
import br.com.jboard.orchestrator.models.dtos.SkillResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
public class SkillService {
    private final SkillClient skillClient;

    public SkillService(SkillClient skillClient) {
        this.skillClient = skillClient;
    }

    private SkillResponseDTO buildSkillResponseDTO(List<Skill> skillList) {
        List<String> skills = skillList.stream().map(Skill::getSkill).toList();
        return new SkillResponseDTO(skills, buildMeta(skillList));
    }

    private MetaDTO buildMeta(List<Skill> skillList) {
        return new MetaDTO(skillList.size());
    }

    private String normalizeSkill(String skill) {
        if (skill == null || skill.trim().isEmpty()) {
            throw new IllegalArgumentException("Skill cannot be null or empty");
        }
        return skill.trim().toLowerCase();
    }

    private void logSkillOperation(String operation, String skill, String username) {
        log.info("[{}] {} skill {} for user {}", operation, operation.toLowerCase(), skill, username);
    }

    private void executeSkillOperation(String operation, Runnable clientOperation) {
        try {
            clientOperation.run();
            log.info("[{}] Operation completed successfully", operation);
        } catch (Exception ex) {
            log.error("[{}] Error: {}", operation, ex.getMessage());
            throw ex;
        }
    }

    public SkillResponseDTO getAllSkills(String username) {
        log.info("[getAllSkills] Getting all skills for user {}", username);
        try {
            List<Skill> skillList = skillClient.getAllSkills(username);
            log.info("[getAllSkills] Found {} skills for user {}", skillList.size(), username);
            return buildSkillResponseDTO(skillList);
        } catch (HttpClientErrorException.NotFound ex) {
            log.info("[getAllSkills] No skills found for user {}, returning empty list", username);
            return new SkillResponseDTO(List.of(), new MetaDTO(0));
        } catch (Exception ex) {
            log.error("[getAllSkills] Error: {}", ex.getMessage());
            throw ex;
        }
    }

    public void addSkill(SkillDTO skillDTO, String username) {
        String normalizedSkill = normalizeSkill(skillDTO.getSkill());
        logSkillOperation("addSkill", normalizedSkill, username);

        Skill skill = new Skill(username, normalizedSkill);
        executeSkillOperation("addSkill", () -> skillClient.addSkill(skill));
    }

    public void removeSkill(SkillDTO skillDTO, String username) {
        String normalizedSkill = normalizeSkill(skillDTO.getSkill());
        logSkillOperation("removeSkill", normalizedSkill, username);

        Skill skill = new Skill(username, normalizedSkill);
        executeSkillOperation("removeSkill", () -> skillClient.removeSkill(skill));
    }

    public void deleteAllSkills(String username) {
        log.info("[deleteAllSkills] Deleting all skills for user {}", username);
        executeSkillOperation("deleteAllSkills", () -> skillClient.deleteAllSkills(username));
    }
}
