package com.resumebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resume entity representing a complete resume document
 */
@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "template_name", nullable = false)
    @Builder.Default
    private String templateName = "professional";

    @Column(name = "is_active")
    @Builder.Default
    private boolean active = true;

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private PersonalInfo personalInfo;

    @Column(name = "professional_summary", length = 2000)
    private String professionalSummary;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate DESC")
    @Builder.Default
    private List<Education> educations = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate DESC")
    @Builder.Default
    private List<WorkExperience> workExperiences = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("startDate DESC")
    @Builder.Default
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<CustomSection> customSections = new ArrayList<>();

    @Embedded
    private ResumeSettings settings;

    @Column(name = "shareable_token", unique = true)
    private String shareableToken;

    @Column(name = "share_enabled")
    @Builder.Default
    private boolean shareEnabled = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void setPersonalInfo(PersonalInfo personalInfo) {
        if (personalInfo == null) {
            if (this.personalInfo != null) {
                this.personalInfo.setResume(null);
            }
        } else {
            personalInfo.setResume(this);
        }
        this.personalInfo = personalInfo;
    }

    public void addEducation(Education education) {
        educations.add(education);
        education.setResume(this);
    }

    public void removeEducation(Education education) {
        educations.remove(education);
        education.setResume(null);
    }

    public void addWorkExperience(WorkExperience workExperience) {
        workExperiences.add(workExperience);
        workExperience.setResume(this);
    }

    public void removeWorkExperience(WorkExperience workExperience) {
        workExperiences.remove(workExperience);
        workExperience.setResume(null);
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
        skill.setResume(this);
    }

    public void removeSkill(Skill skill) {
        skills.remove(skill);
        skill.setResume(null);
    }

    public void addProject(Project project) {
        projects.add(project);
        project.setResume(this);
    }

    public void removeProject(Project project) {
        projects.remove(project);
        project.setResume(null);
    }

    public void addCustomSection(CustomSection customSection) {
        customSections.add(customSection);
        customSection.setResume(this);
    }

    public void removeCustomSection(CustomSection customSection) {
        customSections.remove(customSection);
        customSection.setResume(null);
    }
}
