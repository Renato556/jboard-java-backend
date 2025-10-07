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

    public SkillResponseDTO getAllSkills(String username) {
        log.info("Pegando habilidades do usuário {}", username);
        try {
            List<Skill> skillList = skillClient.getAllSkills(username);
            log.info("{} habilidades encontradas para o usuário: {}", skillList.size(), username);
            return buildSkillResponseDTO(skillList);
        } catch (HttpClientErrorException.NotFound ex) {
            log.info("Nenhuma habilidade encontrada para o usuário: {}, retornando uma lista vazia", username);
            return new SkillResponseDTO(List.of(), new MetaDTO(0));
        } catch (Exception ex) {
            log.error("Erro ao pegar habilidades: {}", ex.getMessage());
            throw ex;
        }
    }

    public void addSkill(SkillDTO skillDTO, String username) {
        String normalizedSkill = normalizeSkill(skillDTO.getSkill());
        log.info("Adicionando habilidade {} para o usuário {}", normalizedSkill, username);

        Skill skill = new Skill(username, normalizedSkill);
        skillClient.addSkill(skill);
    }

    public void removeSkill(SkillDTO skillDTO, String username) {
        String normalizedSkill = normalizeSkill(skillDTO.getSkill());
        log.info("Removendo habilidade {} para o usuário {}", normalizedSkill, username);

        Skill skill = new Skill(username, normalizedSkill);
        skillClient.removeSkill(skill);
    }

    public void deleteAllSkills(String username) {
        log.info("Removendo todas as habilidades do usuário {}", username);
        skillClient.deleteAllSkills(username);
    }
}
