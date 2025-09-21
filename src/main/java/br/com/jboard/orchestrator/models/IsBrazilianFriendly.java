package br.com.jboard.orchestrator.models;

import lombok.Data;

@Data
public class IsBrazilianFriendly {
    private boolean isFriendly;
    private String reason;
}
