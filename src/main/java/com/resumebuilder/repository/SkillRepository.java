package com.resumebuilder.repository;

import com.resumebuilder.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findByResumeIdOrderByDisplayOrderAsc(Long resumeId);
    List<Skill> findByResumeIdAndCategory(Long resumeId, String category);
}
