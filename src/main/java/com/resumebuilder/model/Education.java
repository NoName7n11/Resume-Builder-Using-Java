package com.resumebuilder.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Education entry in a resume
 */
@Entity
@Table(name = "education")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false)
    private String degree;

    @Column(name = "field_of_study")
    private String fieldOfStudy;

    @Column(nullable = false)
    private String institution;

    private String location;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_current")
    @Builder.Default
    private boolean current = false;

    private Double gpa;

    @Column(name = "gpa_scale")
    @Builder.Default
    private Double gpaScale = 4.0;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
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

    public String getFormattedGpa() {
        if (gpa == null) {
            return "";
        }
        // Use primitive types after null check to avoid unnecessary boxing/unboxing
        double gpaValue = gpa;
        double scaleValue = (gpaScale != null) ? gpaScale : 4.0;
        return String.format("%.2f / %.1f", gpaValue, scaleValue);
    }
}
