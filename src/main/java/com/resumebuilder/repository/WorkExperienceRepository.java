package com.resumebuilder.repository;

import com.resumebuilder.model.WorkExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
    List<WorkExperience> findByResumeIdOrderByStartDateDesc(Long resumeId);
}
