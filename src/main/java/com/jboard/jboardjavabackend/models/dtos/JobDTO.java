package com.jboard.jboardjavabackend.models.dtos;

import com.jboard.jboardjavabackend.models.Job;
import lombok.Data;

import java.io.Serializable;

@Data
public class JobDTO implements Serializable {
    private String title;
    private String updatedAt;
    private String employmentType;
    private String publishedDate;
    private String applicationDeadline;
    private String compensationTierSummary;
    private String workplaceType;
    private String officeLocation;
    private String company;
    private String url;
    private String seniorityLevel;
    private String field;

    public JobDTO(Job job) {
        this.title = job.getTitle();
        this.updatedAt = job.getUpdatedAt();
        this.employmentType = job.getEmploymentType();
        this.publishedDate = job.getPublishedDate();
        this.applicationDeadline = job.getApplicationDeadline();
        this.compensationTierSummary = job.getCompensationTierSummary();
        this.workplaceType = job.getWorkplaceType();
        this.officeLocation = job.getOfficeLocation();
        this.company = job.getCompany();
        this.url = job.getUrl();
        this.seniorityLevel = job.getSeniorityLevel();
    }
}
