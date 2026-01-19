package com.resumebuilder.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.resumebuilder.model.PersonalInfo;
import com.resumebuilder.model.Resume;
import com.resumebuilder.model.WorkExperience;
import com.resumebuilder.service.ResumeService;
import com.resumebuilder.service.export.DocxExportService;
import com.resumebuilder.service.export.PdfExportService;

/**
 * REST API controller for resume operations
 */
@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;
    private final PdfExportService pdfExportService;
    private final DocxExportService docxExportService;

    // @Autowired is unnecessary on constructor when there's only one constructor (Spring 4.3+)
    public ResumeController(ResumeService resumeService, 
                           PdfExportService pdfExportService,
                           DocxExportService docxExportService) {
        this.resumeService = resumeService;
        this.pdfExportService = pdfExportService;
        this.docxExportService = docxExportService;
    }

    @PostMapping
    public ResponseEntity<Resume> createResume(
            @RequestParam @NonNull Long userId,
            @RequestParam @NonNull String title,
            @RequestParam(required = false) String description) {
        Resume resume = resumeService.createResume(
            Objects.requireNonNull(userId, "User ID is required"),
            Objects.requireNonNull(title, "Title is required"),
            description
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resume);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(@PathVariable @NonNull Long id) {
        return resumeService.findByIdWithAllDetails(Objects.requireNonNull(id, "ID is required"))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Resume>> getUserResumes(@PathVariable @NonNull Long userId) {
        List<Resume> resumes = resumeService.findByUserId(Objects.requireNonNull(userId, "User ID is required"));
        return ResponseEntity.ok(resumes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resume> updateResume(
            @PathVariable @NonNull Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String professionalSummary,
            @RequestParam(required = false) String templateName) {
        Resume updated = resumeService.updateResume(
            Objects.requireNonNull(id, "ID is required"),
            title, description, professionalSummary, templateName
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResume(@PathVariable @NonNull Long id) {
        resumeService.deleteResume(Objects.requireNonNull(id, "ID is required"));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/share")
    public ResponseEntity<String> generateShareLink(@PathVariable @NonNull Long id) {
        String token = resumeService.generateShareableLink(Objects.requireNonNull(id, "ID is required"));
        return ResponseEntity.ok(token);
    }

    @PostMapping("/{id}/share/disable")
    public ResponseEntity<Void> disableSharing(@PathVariable @NonNull Long id) {
        resumeService.disableSharing(Objects.requireNonNull(id, "ID is required"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/shared/{token}")
    public ResponseEntity<Resume> getSharedResume(@PathVariable String token) {
        return resumeService.findByShareToken(token)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable @NonNull Long id) {
        try {
            Resume resume = resumeService.findByIdWithAllDetails(Objects.requireNonNull(id, "ID is required"))
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

            byte[] pdfBytes = pdfExportService.exportToPdf(resume);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "resume.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/export/docx")
    public ResponseEntity<byte[]> exportToDocx(@PathVariable @NonNull Long id) {
        try {
            Resume resume = resumeService.findByIdWithAllDetails(Objects.requireNonNull(id, "ID is required"))
                    .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

            byte[] docxBytes = docxExportService.exportToDocx(resume);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "resume.docx");

            return new ResponseEntity<>(docxBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/export/txt")
    public ResponseEntity<String> exportToText(@PathVariable @NonNull Long id) {
        Resume resume = resumeService.findByIdWithAllDetails(Objects.requireNonNull(id, "ID is required"))
                .orElseThrow(() -> new IllegalArgumentException("Resume not found"));

        StringBuilder text = new StringBuilder();
        
        // Add personal info
        PersonalInfo pi = resume.getPersonalInfo();
        if (pi != null) {
            String fullName = pi.getFirstName() + " " + pi.getLastName();
            text.append(fullName).append("\n");
            text.append(pi.getEmail()).append("\n");
            if (pi.getPhone() != null) text.append(pi.getPhone()).append("\n");
            text.append("\n");
        }

        // Add summary
        String summary = resume.getProfessionalSummary();
        if (summary != null && !summary.isEmpty()) {
            text.append("PROFESSIONAL SUMMARY\n");
            text.append(summary).append("\n\n");
        }

        // Add work experience
        List<WorkExperience> workExperiences = resume.getWorkExperiences();
        if (workExperiences != null && !workExperiences.isEmpty()) {
            text.append("WORK EXPERIENCE\n");
            workExperiences.forEach(we -> {
                text.append(we.getJobTitle()).append(" - ").append(we.getCompany()).append("\n");
                if (we.getStartDate() != null) {
                    text.append(we.getStartDate().toString());
                    if (we.getEndDate() != null) {
                        text.append(" - ").append(we.getEndDate().toString());
                    } else if (we.isCurrent()) {
                        text.append(" - Present");
                    }
                    text.append("\n");
                }
                if (we.getResponsibilities() != null) {
                    text.append(we.getResponsibilities()).append("\n");
                }
                text.append("\n");
            });
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "resume.txt");

        return new ResponseEntity<>(text.toString(), headers, HttpStatus.OK);
    }
}
