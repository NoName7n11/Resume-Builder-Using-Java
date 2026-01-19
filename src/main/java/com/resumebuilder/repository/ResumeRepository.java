package com.resumebuilder.repository;

import com.resumebuilder.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Resume entity
 */
@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    
    List<Resume> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    List<Resume> findByUserIdAndActiveTrue(Long userId);
    
    @Query("SELECT r FROM Resume r WHERE r.user.id = :userId AND r.active = true ORDER BY r.updatedAt DESC")
    List<Resume> findActiveResumesByUser(@Param("userId") Long userId);
    
    Optional<Resume> findByShareableToken(String shareableToken);
    
    long countByUserId(Long userId);
    
    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.personalInfo WHERE r.id = :id")
    Optional<Resume> findByIdWithPersonalInfo(@Param("id") Long id);
    
    @Query("SELECT r FROM Resume r " +
           "LEFT JOIN FETCH r.personalInfo " +
           "LEFT JOIN FETCH r.educations " +
           "LEFT JOIN FETCH r.workExperiences " +
           "LEFT JOIN FETCH r.skills " +
           "LEFT JOIN FETCH r.projects " +
           "LEFT JOIN FETCH r.customSections " +
           "WHERE r.id = :id")
    Optional<Resume> findByIdWithAllDetails(@Param("id") Long id);
}
