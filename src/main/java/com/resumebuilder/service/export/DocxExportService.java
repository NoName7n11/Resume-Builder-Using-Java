package com.resumebuilder.service.export;

import com.resumebuilder.model.*;
import org.apache.poi.xwpf.usermodel.*;

import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Service for exporting resumes to Microsoft Word (.docx) format using Apache POI
 */
@Service
public class DocxExportService {

    private static final String FONT_FAMILY = "Calibri";
    private static final int FONT_SIZE_TITLE = 24;
    private static final int FONT_SIZE_HEADING = 16;
    private static final int FONT_SIZE_SUBHEADING = 12;
    private static final int FONT_SIZE_NORMAL = 11;

    public byte[] exportToDocx(Resume resume) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            
            // Personal Information
            if (resume.getPersonalInfo() != null) {
                addPersonalInfo(document, resume.getPersonalInfo());
            }

            // Professional Summary
            if (resume.getProfessionalSummary() != null && !resume.getProfessionalSummary().isEmpty()) {
                addSectionHeading(document, "PROFESSIONAL SUMMARY");
                addParagraph(document, resume.getProfessionalSummary(), false);
                addEmptyLine(document);
            }

            // Work Experience
            if (resume.getWorkExperiences() != null && !resume.getWorkExperiences().isEmpty()) {
                addSectionHeading(document, "WORK EXPERIENCE");
                for (WorkExperience we : resume.getWorkExperiences()) {
                    addWorkExperience(document, we);
                }
                addEmptyLine(document);
            }

            // Education
            if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
                addSectionHeading(document, "EDUCATION");
                for (Education edu : resume.getEducations()) {
                    addEducation(document, edu);
                }
                addEmptyLine(document);
            }

            // Skills
            if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
                addSectionHeading(document, "SKILLS");
                addSkills(document, resume.getSkills());
                addEmptyLine(document);
            }

            // Projects
            if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
                addSectionHeading(document, "PROJECTS");
                for (Project project : resume.getProjects()) {
                    addProject(document, project);
                }
                addEmptyLine(document);
            }

            // Custom Sections
            if (resume.getCustomSections() != null && !resume.getCustomSections().isEmpty()) {
                for (CustomSection section : resume.getCustomSections()) {
                    if (section.isVisible()) {
                        addCustomSection(document, section);
                    }
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.write(baos);
            return baos.toByteArray();
        }
    }

    private void addPersonalInfo(XWPFDocument document, PersonalInfo pi) {
        // Name
        XWPFParagraph namePara = document.createParagraph();
        namePara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun nameRun = namePara.createRun();
        nameRun.setText(pi.getFullName());
        nameRun.setFontFamily(FONT_FAMILY);
        nameRun.setFontSize(FONT_SIZE_TITLE);
        nameRun.setBold(true);

        // Contact Info
        XWPFParagraph contactPara = document.createParagraph();
        contactPara.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun contactRun = contactPara.createRun();
        StringBuilder contact = new StringBuilder();
        if (pi.getEmail() != null) contact.append(pi.getEmail());
        if (pi.getPhone() != null) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append(pi.getPhone());
        }
        String fullAddress = pi.getFullAddress();
        if (!fullAddress.isEmpty()) {
            if (contact.length() > 0) contact.append(" | ");
            contact.append(fullAddress);
        }
        contactRun.setText(contact.toString());
        contactRun.setFontFamily(FONT_FAMILY);
        contactRun.setFontSize(FONT_SIZE_NORMAL);

        // Links
        if (pi.getLinkedinUrl() != null || pi.getGithubUrl() != null || pi.getPortfolioUrl() != null) {
            XWPFParagraph linksPara = document.createParagraph();
            linksPara.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun linksRun = linksPara.createRun();
            StringBuilder links = new StringBuilder();
            if (pi.getLinkedinUrl() != null) links.append("LinkedIn: ").append(pi.getLinkedinUrl());
            if (pi.getGithubUrl() != null) {
                if (links.length() > 0) links.append(" | ");
                links.append("GitHub: ").append(pi.getGithubUrl());
            }
            if (pi.getPortfolioUrl() != null) {
                if (links.length() > 0) links.append(" | ");
                links.append("Portfolio: ").append(pi.getPortfolioUrl());
            }
            linksRun.setText(links.toString());
            linksRun.setFontFamily(FONT_FAMILY);
            linksRun.setFontSize(FONT_SIZE_NORMAL - 1);
        }

        addEmptyLine(document);
    }

    private void addSectionHeading(XWPFDocument document, String heading) {
        XWPFParagraph para = document.createParagraph();
        para.setStyle("Heading1");
        XWPFRun run = para.createRun();
        run.setText(heading);
        run.setFontFamily(FONT_FAMILY);
        run.setFontSize(FONT_SIZE_HEADING);
        run.setBold(true);
        run.setUnderline(UnderlinePatterns.SINGLE);
    }

    private void addWorkExperience(XWPFDocument document, WorkExperience we) {
        // Job Title and Company
        XWPFParagraph titlePara = document.createParagraph();
        XWPFRun titleRun = titlePara.createRun();
        titleRun.setText(we.getJobTitle() + " - " + we.getCompany());
        titleRun.setFontFamily(FONT_FAMILY);
        titleRun.setFontSize(FONT_SIZE_SUBHEADING);
        titleRun.setBold(true);

        // Date and Location
        XWPFParagraph datePara = document.createParagraph();
        XWPFRun dateRun = datePara.createRun();
        String dateLocation = we.getDateRange();
        if (we.getLocation() != null && !we.getLocation().isEmpty()) {
            dateLocation += " | " + we.getLocation();
        }
        dateRun.setText(dateLocation);
        dateRun.setFontFamily(FONT_FAMILY);
        dateRun.setFontSize(FONT_SIZE_NORMAL);
        dateRun.setItalic(true);

        // Responsibilities
        if (we.getResponsibilities() != null && !we.getResponsibilities().isEmpty()) {
            for (String bullet : we.getResponsibilityBullets()) {
                addBulletPoint(document, bullet);
            }
        }

        // Achievements
        if (we.getAchievements() != null && !we.getAchievements().isEmpty()) {
            for (String bullet : we.getAchievementBullets()) {
                addBulletPoint(document, bullet);
            }
        }

        addEmptyLine(document);
    }

    private void addEducation(XWPFDocument document, Education edu) {
        // Degree
        XWPFParagraph degreePara = document.createParagraph();
        XWPFRun degreeRun = degreePara.createRun();
        String degreeText = edu.getDegree();
        if (edu.getFieldOfStudy() != null) {
            degreeText += " in " + edu.getFieldOfStudy();
        }
        degreeRun.setText(degreeText);
        degreeRun.setFontFamily(FONT_FAMILY);
        degreeRun.setFontSize(FONT_SIZE_SUBHEADING);
        degreeRun.setBold(true);

        // Institution
        XWPFParagraph instPara = document.createParagraph();
        XWPFRun instRun = instPara.createRun();
        instRun.setText(edu.getInstitution());
        instRun.setFontFamily(FONT_FAMILY);
        instRun.setFontSize(FONT_SIZE_NORMAL);

        // Date and GPA
        XWPFParagraph datePara = document.createParagraph();
        XWPFRun dateRun = datePara.createRun();
        String eduInfo = edu.getDateRange();
        if (edu.getGpa() != null) {
            eduInfo += " | GPA: " + edu.getFormattedGpa();
        }
        dateRun.setText(eduInfo);
        dateRun.setFontFamily(FONT_FAMILY);
        dateRun.setFontSize(FONT_SIZE_NORMAL);
        dateRun.setItalic(true);

        // Description/Achievements
        if (edu.getAchievements() != null && !edu.getAchievements().isEmpty()) {
            addParagraph(document, edu.getAchievements(), false);
        }

        addEmptyLine(document);
    }

    private void addSkills(XWPFDocument document, List<Skill> skills) {
        var skillsByCategory = skills.stream()
            .collect(java.util.stream.Collectors.groupingBy(Skill::getCategory));

        for (var entry : skillsByCategory.entrySet()) {
            XWPFParagraph para = document.createParagraph();
            
            XWPFRun categoryRun = para.createRun();
            categoryRun.setText(entry.getKey() + ": ");
            categoryRun.setFontFamily(FONT_FAMILY);
            categoryRun.setFontSize(FONT_SIZE_NORMAL);
            categoryRun.setBold(true);

            String skillsList = entry.getValue().stream()
                .map(Skill::getName)
                .collect(java.util.stream.Collectors.joining(", "));

            XWPFRun skillsRun = para.createRun();
            skillsRun.setText(skillsList);
            skillsRun.setFontFamily(FONT_FAMILY);
            skillsRun.setFontSize(FONT_SIZE_NORMAL);
        }
    }

    private void addProject(XWPFDocument document, Project project) {
        // Project Name
        XWPFParagraph namePara = document.createParagraph();
        XWPFRun nameRun = namePara.createRun();
        nameRun.setText(project.getName());
        nameRun.setFontFamily(FONT_FAMILY);
        nameRun.setFontSize(FONT_SIZE_SUBHEADING);
        nameRun.setBold(true);

        // Technologies
        if (project.getTechnologies() != null && !project.getTechnologies().isEmpty()) {
            XWPFParagraph techPara = document.createParagraph();
            XWPFRun techRun = techPara.createRun();
            techRun.setText("Technologies: " + project.getTechnologies());
            techRun.setFontFamily(FONT_FAMILY);
            techRun.setFontSize(FONT_SIZE_NORMAL);
            techRun.setItalic(true);
        }

        // Description
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            addParagraph(document, project.getDescription(), false);
        }

        // Highlights
        if (project.getHighlights() != null && !project.getHighlights().isEmpty()) {
            for (String highlight : project.getHighlightBullets()) {
                addBulletPoint(document, highlight);
            }
        }

        // Links
        if (project.getProjectUrl() != null || project.getGithubUrl() != null) {
            XWPFParagraph linkPara = document.createParagraph();
            XWPFRun linkRun = linkPara.createRun();
            StringBuilder links = new StringBuilder();
            if (project.getProjectUrl() != null) {
                links.append("URL: ").append(project.getProjectUrl());
            }
            if (project.getGithubUrl() != null) {
                if (links.length() > 0) links.append(" | ");
                links.append("GitHub: ").append(project.getGithubUrl());
            }
            linkRun.setText(links.toString());
            linkRun.setFontFamily(FONT_FAMILY);
            linkRun.setFontSize(FONT_SIZE_NORMAL - 1);
        }

        addEmptyLine(document);
    }

    private void addCustomSection(XWPFDocument document, CustomSection section) {
        addSectionHeading(document, section.getSectionTitle().toUpperCase());
        
        if (section.getContentType() == CustomSection.ContentType.BULLET_LIST) {
            for (String item : section.getContentAsList()) {
                if (!item.trim().isEmpty()) {
                    addBulletPoint(document, item);
                }
            }
        } else {
            addParagraph(document, section.getContent(), false);
        }
        
        addEmptyLine(document);
    }

    private void addParagraph(XWPFDocument document, String text, boolean bold) {
        XWPFParagraph para = document.createParagraph();
        XWPFRun run = para.createRun();
        run.setText(text);
        run.setFontFamily(FONT_FAMILY);
        run.setFontSize(FONT_SIZE_NORMAL);
        if (bold) run.setBold(true);
    }

    private void addBulletPoint(XWPFDocument document, String text) {
        XWPFParagraph para = document.createParagraph();
        para.setIndentationLeft(720); // 0.5 inch
        para.setIndentationFirstLine(-360); // hanging indent
        
        XWPFRun run = para.createRun();
        run.setText("• " + text);
        run.setFontFamily(FONT_FAMILY);
        run.setFontSize(FONT_SIZE_NORMAL);
    }

    private void addEmptyLine(XWPFDocument document) {
        document.createParagraph();
    }
}
