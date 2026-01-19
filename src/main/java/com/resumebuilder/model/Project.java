package com.resumebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Project entry in a resume
 */
@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String technologies;

    @Column(name = "project_url")
    private String projectUrl;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private boolean current = false;

    @Column(length = 5000)
    private String highlights;

    private String role;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    public String getDateRange() {
        if (startDate == null) {
            return "";
        }
        String start = startDate.getMonthValue() + "/" + startDate.getYear();
        String end = current ? "Present" : (endDate != null ? endDate.getMonthValue() + "/" + endDate.getYear() : "");
        return start + " - " + end;
    }

    public String[] getHighlightBullets() {
        if (highlights == null || highlights.isEmpty()) {
            return new String[0];
        }
        return highlights.split("\n");
    }

    public String[] getTechnologiesArray() {
        if (technologies == null || technologies.isEmpty()) {
            return new String[0];
        }
        return technologies.split(",");
    }
}
