package br.com.jboard.orchestrator.models;

import lombok.Getter;

@Getter
public class Job {
    private String id;
    private String title;
    private String updatedAt;
    private String employmentType;
    private String publishedDate;
    private String applicationDeadline;
    private String compensationTierSummary;
    private String workplaceType;
    private String officeLocation;
    private IsBrazilianFriendly isBrazilianFriendly;
    private String company;
    private String url;
    private String seniorityLevel;
    private String field;
}

