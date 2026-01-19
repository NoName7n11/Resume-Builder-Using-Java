package com.resumebuilder.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resumebuilder.model.CustomSection;
import com.resumebuilder.model.Education;
import com.resumebuilder.model.PersonalInfo;
import com.resumebuilder.model.Project;
import com.resumebuilder.model.Resume;
import com.resumebuilder.model.ResumeSettings;
import com.resumebuilder.model.Skill;
import com.resumebuilder.model.User;
import com.resumebuilder.model.WorkExperience;
import com.resumebuilder.repository.ResumeRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service for resume management operations
 * 
 * Note: Some IDE null-safety warnings may appear due to Eclipse JDT's strict null analysis.
 * These are false positives as Spring Data JPA guarantees @NonNull returns from repository methods.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserService userService;

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume createResume(@NonNull Long userId, @NonNull String title, String description) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Resume resume = Resume.builder()
                .user(user)
                .title(title)
                .description(description)
                .templateName("professional")
                .active(true)
                .settings(ResumeSettings.builder().build())
                .build();

        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume createResumeFromTemplate(@NonNull Long userId, @NonNull String title, @NonNull Resume templateResume) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Resume newResume = Resume.builder()
                .user(user)
                .title(title)
                .description("Copy of " + templateResume.getTitle())
                .templateName(templateResume.getTemplateName())
                .professionalSummary(templateResume.getProfessionalSummary())
                .settings(templateResume.getSettings())
                .active(true)
                .build();

        Resume saved = resumeRepository.save(newResume);

        // Copy personal info
        if (templateResume.getPersonalInfo() != null) {
            PersonalInfo pi = templateResume.getPersonalInfo();
            PersonalInfo newPi = PersonalInfo.builder()
                    .resume(saved)
                    .firstName(pi.getFirstName())
                    .lastName(pi.getLastName())
                    .email(pi.getEmail())
                    .phone(pi.getPhone())
                    .address(pi.getAddress())
                    .city(pi.getCity())
                    .state(pi.getState())
                    .zipCode(pi.getZipCode())
                    .country(pi.getCountry())
                    .linkedinUrl(pi.getLinkedinUrl())
                    .githubUrl(pi.getGithubUrl())
                    .portfolioUrl(pi.getPortfolioUrl())
                    .websiteUrl(pi.getWebsiteUrl())
                    .build();
            saved.setPersonalInfo(newPi);
        }

        return resumeRepository.save(saved);
    }

    @Transactional(readOnly = true)
    public Optional<Resume> findById(@NonNull Long id) {
        return resumeRepository.findById(Objects.requireNonNull(id, "ID must not be null"));
    }

    @Transactional(readOnly = true)
    public Optional<Resume> findByIdWithAllDetails(@NonNull Long id) {
        return resumeRepository.findByIdWithAllDetails(Objects.requireNonNull(id, "ID must not be null"));
    }

    @Transactional(readOnly = true)
    public List<Resume> findByUserId(@NonNull Long userId) {
        return resumeRepository.findByUserIdOrderByUpdatedAtDesc(Objects.requireNonNull(userId, "User ID must not be null"));
    }

    @Transactional(readOnly = true)
    public List<Resume> findActiveResumesByUserId(@NonNull Long userId) {
        return resumeRepository.findByUserIdAndActiveTrue(Objects.requireNonNull(userId, "User ID must not be null"));
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume updateResume(@NonNull Long resumeId, String title, String description,
                              String professionalSummary, String templateName) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        if (title != null) resume.setTitle(title);
        if (description != null) resume.setDescription(description);
        if (professionalSummary != null) resume.setProfessionalSummary(professionalSummary);
        if (templateName != null) resume.setTemplateName(templateName);

        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume updateResumeSettings(@NonNull Long resumeId, ResumeSettings settings) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.setSettings(settings);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume toggleResumeActive(@NonNull Long resumeId) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.setActive(!resume.isActive());
        return resumeRepository.save(resume);
    }

    @NonNull
    public String generateShareableLink(@NonNull Long resumeId) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        String token = UUID.randomUUID().toString();
        resume.setShareableToken(token);
        resume.setShareEnabled(true);
        resumeRepository.save(resume);

        return Objects.requireNonNull(token, "Token generation failed");
    }

    public void disableSharing(@NonNull Long resumeId) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));        resume.setShareEnabled(false);
        resumeRepository.save(resume);
    }

    @Transactional(readOnly = true)
    public Optional<Resume> findByShareToken(String token) {
        return resumeRepository.findByShareableToken(token);
    }

    public void deleteResume(@NonNull Long resumeId) {
        resumeRepository.deleteById(Objects.requireNonNull(resumeId, "Resume ID must not be null"));
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume setPersonalInfo(@NonNull Long resumeId, PersonalInfo personalInfo) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.setPersonalInfo(personalInfo);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume addEducation(@NonNull Long resumeId, Education education) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.addEducation(education);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume addWorkExperience(@NonNull Long resumeId, WorkExperience workExperience) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.addWorkExperience(workExperience);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume addSkill(@NonNull Long resumeId, Skill skill) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.addSkill(skill);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume addProject(@NonNull Long resumeId, Project project) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.addProject(project);
        return resumeRepository.save(resume);
    }

    @NonNull
    @SuppressWarnings("null") // Spring Data JPA guarantees @NonNull return from save()
    public Resume addCustomSection(@NonNull Long resumeId, CustomSection customSection) {
        Resume resume = resumeRepository.findById(Objects.requireNonNull(resumeId, "Resume ID must not be null"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        resume.addCustomSection(customSection);
        return resumeRepository.save(resume);
    }
}
