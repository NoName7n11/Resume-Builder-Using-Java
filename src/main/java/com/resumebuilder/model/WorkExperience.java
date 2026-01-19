package com.resumebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Work experience entry in a resume
 */
@Entity
@Table(name = "work_experience")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    @Column(nullable = false)
    private String company;

    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private boolean current = false;

    @Column(length = 5000)
    private String description;

    @Column(length = 5000)
    private String responsibilities;

    @Column(length = 5000)
    private String achievements;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    public String getDateRange() {
        if (startDate == null) {
            return "Present";
        }
        String start = startDate.getMonthValue() + "/" + startDate.getYear();
        String end = current ? "Present" : (endDate != null ? endDate.getMonthValue() + "/" + endDate.getYear() : "");
        return start + " - " + end;
    }

    public String[] getResponsibilityBullets() {
        if (responsibilities == null || responsibilities.isEmpty()) {
            return new String[0];
        }
        return responsibilities.split("\n");
    }

    public String[] getAchievementBullets() {
        if (achievements == null || achievements.isEmpty()) {
            return new String[0];
        }
        return achievements.split("\n");
    }
}
