package com.jboard.jboardjavabackend.models;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "jobs")
public class Job {
    @Id
    private String _id;
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

