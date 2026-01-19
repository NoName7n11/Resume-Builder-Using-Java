package com.resumebuilder.repository;

import com.resumebuilder.model.CustomSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomSectionRepository extends JpaRepository<CustomSection, Long> {
    List<CustomSection> findByResumeIdOrderByDisplayOrderAsc(Long resumeId);
    List<CustomSection> findByResumeIdAndVisibleTrue(Long resumeId);
}
