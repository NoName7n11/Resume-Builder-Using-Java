package com.resumebuilder.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resume customization settings
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeSettings {

    @Column(name = "primary_color")
    @Builder.Default
    private String primaryColor = "#2c3e50";

    @Column(name = "secondary_color")
    @Builder.Default
    private String secondaryColor = "#3498db";

    @Column(name = "font_family")
    @Builder.Default
    private String fontFamily = "Arial";

    @Column(name = "font_size")
    @Builder.Default
    private Integer fontSize = 11;

    @Column(name = "line_spacing")
    @Builder.Default
    private Double lineSpacing = 1.15;

    @Column(name = "margin_top")
    @Builder.Default
    private Integer marginTop = 20;

    @Column(name = "margin_bottom")
    @Builder.Default
    private Integer marginBottom = 20;

    @Column(name = "margin_left")
    @Builder.Default
    private Integer marginLeft = 20;

    @Column(name = "margin_right")
    @Builder.Default
    private Integer marginRight = 20;

    @Column(name = "show_profile_photo")
    @Builder.Default
    private boolean showProfilePhoto = false;

    @Column(name = "section_order", length = 500)
    @Builder.Default
    private String sectionOrder = "personal,summary,experience,education,skills,projects,custom";

    public String[] getSectionOrderArray() {
        if (sectionOrder == null || sectionOrder.isEmpty()) {
            return new String[]{"personal", "summary", "experience", "education", "skills", "projects", "custom"};
        }
        return sectionOrder.split(",");
    }

    public void setSectionOrderArray(String[] sections) {
        this.sectionOrder = String.join(",", sections);
    }
}
