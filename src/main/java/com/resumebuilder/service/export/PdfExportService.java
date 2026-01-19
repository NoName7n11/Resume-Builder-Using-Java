package com.resumebuilder.service.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

import com.resumebuilder.model.Education;
import com.resumebuilder.model.PersonalInfo;
import com.resumebuilder.model.Project;
import com.resumebuilder.model.Resume;
import com.resumebuilder.model.Skill;
import com.resumebuilder.model.WorkExperience;

/**
 * Service for exporting resumes to PDF format using Apache PDFBox
 */
@Service
public class PdfExportService {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE_TITLE = 24;
    private static final float FONT_SIZE_HEADING = 14;
    private static final float FONT_SIZE_SUBHEADING = 12;
    private static final float FONT_SIZE_NORMAL = 10;
    private static final float LINE_HEIGHT = 15;

    public byte[] exportToPdf(Resume resume) throws IOException {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            // Choose template and render - using switch expression
            String templateName = resume.getTemplateName() != null ? resume.getTemplateName() : "professional";
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                switch (templateName) {
                    case "modern" -> renderModernTemplate(document, contentStream, page, resume, 
                                                         page.getMediaBox().getHeight() - MARGIN);
                    case "creative" -> renderCreativeTemplate(document, contentStream, page, resume, 
                                                              page.getMediaBox().getHeight() - MARGIN);
                    default -> renderProfessionalTemplate(document, contentStream, page, resume, 
                                                          page.getMediaBox().getHeight() - MARGIN);
                }
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }

    /**
     * Renders resume content using the professional template.
     * Note: document parameter is currently unused but kept for future multi-page pagination support.
     * 
     * @param document the PDF document being created
     * @param contentStream the content stream for writing to the page
     * @param page the current page being rendered
     * @param resume the resume data to render
     * @param yPosition the starting Y position
     * @return the final Y position after rendering
     */
    @SuppressWarnings("unused") // document will be used when multi-page support is implemented
    private float renderProfessionalTemplate(PDDocument document, PDPageContentStream contentStream, 
                                            PDPage page, Resume resume, 
                                            float yPosition) throws IOException {
        PDFont boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
        PDFont regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont italicFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);

        // Personal Information
        if (resume.getPersonalInfo() != null) {
            PersonalInfo pi = resume.getPersonalInfo();
            
            // Name
            contentStream.beginText();
            contentStream.setFont(boldFont, FONT_SIZE_TITLE);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(pi.getFullName());
            contentStream.endText();
            yPosition -= FONT_SIZE_TITLE + 5;

            // Contact Info
            contentStream.beginText();
            contentStream.setFont(regularFont, FONT_SIZE_NORMAL);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            String contactLine = String.format("%s | %s", pi.getEmail(), pi.getPhone() != null ? pi.getPhone() : "");
            contentStream.showText(contactLine);
            contentStream.endText();
            yPosition -= LINE_HEIGHT;

            // Links
            if (pi.getLinkedinUrl() != null || pi.getGithubUrl() != null || pi.getPortfolioUrl() != null) {
                contentStream.beginText();
                contentStream.setFont(regularFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                List<String> links = new ArrayList<>();
                if (pi.getLinkedinUrl() != null) links.add("LinkedIn: " + pi.getLinkedinUrl());
                if (pi.getGithubUrl() != null) links.add("GitHub: " + pi.getGithubUrl());
                if (pi.getPortfolioUrl() != null) links.add("Portfolio: " + pi.getPortfolioUrl());
                contentStream.showText(String.join(" | ", links));
                contentStream.endText();
                yPosition -= LINE_HEIGHT + 10;
            } else {
                yPosition -= 10;
            }

            // Separator line
            contentStream.setLineWidth(1f);
            contentStream.moveTo(MARGIN, yPosition);
            contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, yPosition);
            contentStream.stroke();
            yPosition -= 15;
        }

        // Professional Summary
        if (resume.getProfessionalSummary() != null && !resume.getProfessionalSummary().isEmpty()) {
            yPosition = addSection(contentStream, page, "PROFESSIONAL SUMMARY", resume.getProfessionalSummary(), 
                                  boldFont, regularFont, yPosition);
        }

        // Work Experience
        if (resume.getWorkExperiences() != null && !resume.getWorkExperiences().isEmpty()) {
            yPosition = addSectionHeader(contentStream, "WORK EXPERIENCE", boldFont, yPosition);
            
            for (WorkExperience we : resume.getWorkExperiences()) {
                yPosition = checkNewPage(yPosition, 100);
                
                // Job title and company
                contentStream.beginText();
                contentStream.setFont(boldFont, FONT_SIZE_SUBHEADING);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(we.getJobTitle() + " - " + we.getCompany());
                contentStream.endText();
                yPosition -= LINE_HEIGHT;

                // Date and location
                contentStream.beginText();
                contentStream.setFont(italicFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(we.getDateRange() + " | " + (we.getLocation() != null ? we.getLocation() : ""));
                contentStream.endText();
                yPosition -= LINE_HEIGHT + 3;

                // Responsibilities
                if (we.getResponsibilities() != null && !we.getResponsibilities().isEmpty()) {
                    for (String bullet : we.getResponsibilityBullets()) {
                        yPosition = addBulletPoint(contentStream, page, bullet, regularFont, yPosition);
                    }
                }
                yPosition -= 10;
            }
        }

        // Education
        if (resume.getEducations() != null && !resume.getEducations().isEmpty()) {
            yPosition = addSectionHeader(contentStream, "EDUCATION", boldFont, yPosition);
            
            for (Education edu : resume.getEducations()) {
                yPosition = checkNewPage(yPosition, 80);
                
                // Degree
                contentStream.beginText();
                contentStream.setFont(boldFont, FONT_SIZE_SUBHEADING);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(edu.getDegree() + (edu.getFieldOfStudy() != null ? " in " + edu.getFieldOfStudy() : ""));
                contentStream.endText();
                yPosition -= LINE_HEIGHT;

                // Institution
                contentStream.beginText();
                contentStream.setFont(regularFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(edu.getInstitution());
                contentStream.endText();
                yPosition -= LINE_HEIGHT;

                // Date and GPA
                contentStream.beginText();
                contentStream.setFont(italicFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                String eduInfo = edu.getDateRange();
                if (edu.getGpa() != null) {
                    eduInfo += " | GPA: " + edu.getFormattedGpa();
                }
                contentStream.showText(eduInfo);
                contentStream.endText();
                yPosition -= LINE_HEIGHT + 10;
            }
        }

        // Skills
        if (resume.getSkills() != null && !resume.getSkills().isEmpty()) {
            yPosition = addSectionHeader(contentStream, "SKILLS", boldFont, yPosition);
            yPosition = checkNewPage(yPosition, 60);
            
            // Group skills by category
            var skillsByCategory = resume.getSkills().stream()
                .collect(java.util.stream.Collectors.groupingBy(Skill::getCategory));
            
            for (var entry : skillsByCategory.entrySet()) {
                contentStream.beginText();
                contentStream.setFont(boldFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(entry.getKey() + ": ");
                contentStream.endText();

                String skillsList = entry.getValue().stream()
                    .map(Skill::getName)
                    .collect(java.util.stream.Collectors.joining(", "));
                
                contentStream.beginText();
                contentStream.setFont(regularFont, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN + 100, yPosition);
                contentStream.showText(skillsList);
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
            }
            yPosition -= 10;
        }

        // Projects
        if (resume.getProjects() != null && !resume.getProjects().isEmpty()) {
            yPosition = addSectionHeader(contentStream, "PROJECTS", boldFont, yPosition);
            
            for (Project project : resume.getProjects()) {
                yPosition = checkNewPage(yPosition, 80);
                
                // Project name
                contentStream.beginText();
                contentStream.setFont(boldFont, FONT_SIZE_SUBHEADING);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(project.getName());
                contentStream.endText();
                yPosition -= LINE_HEIGHT;

                // Technologies
                if (project.getTechnologies() != null && !project.getTechnologies().isEmpty()) {
                    contentStream.beginText();
                    contentStream.setFont(italicFont, FONT_SIZE_NORMAL);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    contentStream.showText("Technologies: " + project.getTechnologies());
                    contentStream.endText();
                    yPosition -= LINE_HEIGHT;
                }

                // Description
                if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                    yPosition = addWrappedText(contentStream, page, project.getDescription(), regularFont, yPosition);
                }

                // Links
                if (project.getProjectUrl() != null || project.getGithubUrl() != null) {
                    contentStream.beginText();
                    contentStream.setFont(regularFont, FONT_SIZE_NORMAL - 1);
                    contentStream.newLineAtOffset(MARGIN, yPosition);
                    String links = "";
                    if (project.getProjectUrl() != null) links += "URL: " + project.getProjectUrl();
                    if (project.getGithubUrl() != null) {
                        if (!links.isEmpty()) links += " | ";
                        links += "GitHub: " + project.getGithubUrl();
                    }
                    contentStream.showText(links);
                    contentStream.endText();
                    yPosition -= LINE_HEIGHT;
                }
                yPosition -= 10;
            }
        }

        return yPosition;
    }

    private float renderModernTemplate(PDDocument document, PDPageContentStream contentStream, 
                                      PDPage page, Resume resume, 
                                      float yPosition) throws IOException {
        // Modern template with accent colors and clean design
        // Similar structure to professional but with color accents
        return renderProfessionalTemplate(document, contentStream, page, resume, yPosition);
    }

    private float renderCreativeTemplate(PDDocument document, PDPageContentStream contentStream, 
                                        PDPage page, Resume resume, 
                                        float yPosition) throws IOException {
        // Creative template with more visual elements
        // Similar structure to professional for now
        return renderProfessionalTemplate(document, contentStream, page, resume, yPosition);
    }

    private float addSectionHeader(PDPageContentStream contentStream, String title, 
                                   PDFont font, float yPosition) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE_HEADING);
        contentStream.newLineAtOffset(MARGIN, yPosition);
        contentStream.showText(title);
        contentStream.endText();
        return yPosition - FONT_SIZE_HEADING - 10;
    }

    private float addSection(PDPageContentStream contentStream, PDPage page, String title, 
                            String content, PDFont boldFont, PDFont regularFont, 
                            float yPosition) throws IOException {
        yPosition = addSectionHeader(contentStream, title, boldFont, yPosition);
        yPosition = addWrappedText(contentStream, page, content, regularFont, yPosition);
        return yPosition - 10;
    }

    private float addWrappedText(PDPageContentStream contentStream, PDPage page, 
                                 String text, PDFont font, float yPosition) throws IOException {
        float maxWidth = page.getMediaBox().getWidth() - 2 * MARGIN;
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * FONT_SIZE_NORMAL;
            
            if (textWidth > maxWidth && line.length() > 0) {
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                contentStream.showText(line.toString());
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE_NORMAL);
            contentStream.newLineAtOffset(MARGIN, yPosition);
            contentStream.showText(line.toString());
            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }

    private float addBulletPoint(PDPageContentStream contentStream, PDPage page, 
                                 String text, PDFont font, float yPosition) throws IOException {
        float bulletX = MARGIN + 10;
        float textX = MARGIN + 20;
        float maxWidth = page.getMediaBox().getWidth() - textX - MARGIN;

        // Add bullet
        contentStream.beginText();
        contentStream.setFont(font, FONT_SIZE_NORMAL);
        contentStream.newLineAtOffset(bulletX, yPosition);
        contentStream.showText("•");
        contentStream.endText();

        // Add text with wrapping
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float textWidth = font.getStringWidth(testLine) / 1000 * FONT_SIZE_NORMAL;
            
            if (textWidth > maxWidth && line.length() > 0) {
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE_NORMAL);
                contentStream.newLineAtOffset(textX, yPosition);
                contentStream.showText(line.toString());
                contentStream.endText();
                yPosition -= LINE_HEIGHT;
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(testLine);
            }
        }
        
        if (line.length() > 0) {
            contentStream.beginText();
            contentStream.setFont(font, FONT_SIZE_NORMAL);
            contentStream.newLineAtOffset(textX, yPosition);
            contentStream.showText(line.toString());
            contentStream.endText();
            yPosition -= LINE_HEIGHT;
        }
        
        return yPosition;
    }

    /**
     * Checks if remaining space is sufficient for the required content.
     * Note: Current implementation does not support multi-page documents.
     * This method is kept for future enhancement to support pagination.
     * 
     * @param yPosition current Y position on the page
     * @param requiredSpace minimum space required for content
     * @return the current yPosition (unchanged in current implementation)
     */
    @SuppressWarnings("unused") // requiredSpace will be used when multi-page support is implemented
    private float checkNewPage(float yPosition, float requiredSpace) {
        // Multi-page support not yet implemented
        // Current behavior: continue rendering even if space is limited (content may overflow)
        // Future implementation will create a new page when: yPosition - requiredSpace < MARGIN
        return yPosition;
    }
}
