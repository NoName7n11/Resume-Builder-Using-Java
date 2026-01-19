package com.resumebuilder.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Skill entry in a resume
 */
@Entity
@Table(name = "skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level")
    private ProficiencyLevel proficiencyLevel;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Builder.Default
    private boolean visible = true;

    public enum ProficiencyLevel {
        BEGINNER("Beginner"),
        INTERMEDIATE("Intermediate"),
        ADVANCED("Advanced"),
        EXPERT("Expert");

        private final String displayName;

        ProficiencyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
