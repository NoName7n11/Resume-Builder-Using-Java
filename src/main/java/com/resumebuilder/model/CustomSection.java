package com.resumebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom section for additional resume content (e.g., Certifications, Publications, Volunteer Work)
 */
@Entity
@Table(name = "custom_sections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "section_title", nullable = false)
    private String sectionTitle;

    @Column(length = 10000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type")
    @Builder.Default
    private ContentType contentType = ContentType.TEXT;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private boolean visible = true;

    public enum ContentType {
        TEXT,           // Free-form text
        BULLET_LIST,    // Bulleted list
        TABLE,          // Tabular data
        CUSTOM          // Custom HTML/formatted content
    }

    public String[] getContentAsList() {
        if (content == null || content.isEmpty()) {
            return new String[0];
        }
        return content.split("\n");
    }
}
