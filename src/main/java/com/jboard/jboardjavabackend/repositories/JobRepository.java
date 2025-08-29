package com.jboard.jboardjavabackend.repositories;

import com.jboard.jboardjavabackend.models.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends CrudRepository<Job, String> {
    Page<Job> findAll(Pageable pageable);

    Page<Job> findAllByCompany(String company, Pageable pageable);

    Page<Job> findAllByField(String field, Pageable pageable);

    Page<Job> findAllBySeniorityLevel(String seniorityLevel, Pageable pageable);

    Page<Job> findAllByCompanyAndField(String company, String field, Pageable pageable);

    Page<Job> findAllByWorkplaceType(String workplaceType, Pageable pageable);
}
