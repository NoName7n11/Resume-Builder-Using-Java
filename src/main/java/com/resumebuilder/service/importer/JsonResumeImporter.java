package com.resumebuilder.service.importer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumebuilder.model.Education;
import com.resumebuilder.model.PersonalInfo;
import com.resumebuilder.model.Project;
import com.resumebuilder.model.Resume;
import com.resumebuilder.model.ResumeSettings;
import com.resumebuilder.model.Skill;
import com.resumebuilder.model.WorkExperience;

/**
 * Service for importing resume data from JSON Resume format
 * Specification: https://jsonresume.org/schema/
 */
@Service
public class JsonResumeImporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Resume importFromJson(String jsonContent) throws IOException {
        JsonNode root = objectMapper.readTree(jsonContent);

        Resume resume = Resume.builder()
                .title("Imported Resume")
                .templateName("professional")
                .active(true)
                .settings(ResumeSettings.builder().build())
                .build();

        // Import basics (personal info)
        if (root.has("basics")) {
            PersonalInfo pi = importBasics(root.get("basics"));
            resume.setPersonalInfo(pi);
        }

        // Import summary
        if (root.has("summary")) {
            resume.setProfessionalSummary(root.get("summary").asText());
        }

        // Import work experience
        if (root.has("work")) {
            List<WorkExperience> experiences = importWork(root.get("work"));
            experiences.forEach(resume::addWorkExperience);
        }

        // Import education
        if (root.has("education")) {
            List<Education> educations = importEducation(root.get("education"));
            educations.forEach(resume::addEducation);
        }

        // Import skills
        if (root.has("skills")) {
            List<Skill> skills = importSkills(root.get("skills"));
            skills.forEach(resume::addSkill);
        }

        // Import projects
        if (root.has("projects")) {
            List<Project> projects = importProjects(root.get("projects"));
            projects.forEach(resume::addProject);
        }

        return resume;
    }

    private PersonalInfo importBasics(JsonNode basics) {
        PersonalInfo.PersonalInfoBuilder builder = PersonalInfo.builder();

        if (basics.has("name")) {
            String[] nameParts = basics.get("name").asText().split(" ", 2);
            builder.firstName(nameParts[0]);
            if (nameParts.length > 1) {
                builder.lastName(nameParts[1]);
            }
        }

        if (basics.has("email")) builder.email(basics.get("email").asText());
        if (basics.has("phone")) builder.phone(basics.get("phone").asText());
        if (basics.has("url")) builder.websiteUrl(basics.get("url").asText());

        if (basics.has("location")) {
            JsonNode location = basics.get("location");
            if (location.has("address")) builder.address(location.get("address").asText());
            if (location.has("city")) builder.city(location.get("city").asText());
            if (location.has("region")) builder.state(location.get("region").asText());
            if (location.has("postalCode")) builder.zipCode(location.get("postalCode").asText());
            if (location.has("countryCode")) builder.country(location.get("countryCode").asText());
        }

        if (basics.has("profiles")) {
            for (JsonNode profile : basics.get("profiles")) {
                String network = profile.has("network") ? profile.get("network").asText().toLowerCase() : "";
                String url = profile.has("url") ? profile.get("url").asText() : "";

                switch (network) {
                    case "linkedin" -> builder.linkedinUrl(url);
                    case "github" -> builder.githubUrl(url);
                    default -> {} // No-op for other networks
                }
            }
        }

        return builder.build();
    }

    private List<WorkExperience> importWork(JsonNode workArray) {
        List<WorkExperience> experiences = new ArrayList<>();

        for (JsonNode work : workArray) {
            WorkExperience.WorkExperienceBuilder builder = WorkExperience.builder();

            if (work.has("position")) builder.jobTitle(work.get("position").asText());
            if (work.has("name")) builder.company(work.get("name").asText());
            if (work.has("location")) builder.location(work.get("location").asText());
            if (work.has("startDate")) builder.startDate(LocalDate.parse(work.get("startDate").asText()));
            if (work.has("endDate")) {
                String endDate = work.get("endDate").asText();
                if (!endDate.isEmpty() && !endDate.equalsIgnoreCase("present")) {
                    builder.endDate(LocalDate.parse(endDate));
                    builder.current(false);
                } else {
                    builder.current(true);
                }
            }
            if (work.has("summary")) builder.description(work.get("summary").asText());
            if (work.has("highlights")) {
                StringBuilder highlights = new StringBuilder();
                for (JsonNode highlight : work.get("highlights")) {
                    highlights.append(highlight.asText()).append("\n");
                }
                builder.responsibilities(highlights.toString());
            }

            experiences.add(builder.build());
        }

        return experiences;
    }

    private List<Education> importEducation(JsonNode educationArray) {
        List<Education> educations = new ArrayList<>();

        for (JsonNode edu : educationArray) {
            Education.EducationBuilder builder = Education.builder();

            if (edu.has("studyType")) builder.degree(edu.get("studyType").asText());
            if (edu.has("area")) builder.fieldOfStudy(edu.get("area").asText());
            if (edu.has("institution")) builder.institution(edu.get("institution").asText());
            if (edu.has("startDate")) builder.startDate(LocalDate.parse(edu.get("startDate").asText()));
            if (edu.has("endDate")) {
                String endDate = edu.get("endDate").asText();
                if (!endDate.isEmpty() && !endDate.equalsIgnoreCase("present")) {
                    builder.endDate(LocalDate.parse(endDate));
                    builder.current(false);
                } else {
                    builder.current(true);
                }
            }
            if (edu.has("score")) {
                try {
                    builder.gpa(Double.valueOf(edu.get("score").asText()));
                } catch (NumberFormatException e) {
                    // Ignore invalid GPA
                }
            }

            educations.add(builder.build());
        }

        return educations;
    }

    private List<Skill> importSkills(JsonNode skillsArray) {
        List<Skill> skills = new ArrayList<>();
        int displayOrder = 0;

        for (JsonNode skillGroup : skillsArray) {
            String category = skillGroup.has("name") ? skillGroup.get("name").asText() : "Other";

            if (skillGroup.has("keywords")) {
                for (JsonNode keyword : skillGroup.get("keywords")) {
                    Skill skill = Skill.builder()
                            .name(keyword.asText())
                            .category(category)
                            .displayOrder(displayOrder++)
                            .visible(true)
                            .build();
                    skills.add(skill);
                }
            }
        }

        return skills;
    }

    private List<Project> importProjects(JsonNode projectsArray) {
        List<Project> projects = new ArrayList<>();

        for (JsonNode proj : projectsArray) {
            Project.ProjectBuilder builder = Project.builder();

            if (proj.has("name")) builder.name(proj.get("name").asText());
            if (proj.has("description")) builder.description(proj.get("description").asText());
            if (proj.has("url")) builder.projectUrl(proj.get("url").asText());
            if (proj.has("startDate")) builder.startDate(LocalDate.parse(proj.get("startDate").asText()));
            if (proj.has("endDate")) {
                String endDate = proj.get("endDate").asText();
                if (!endDate.isEmpty() && !endDate.equalsIgnoreCase("present")) {
                    builder.endDate(LocalDate.parse(endDate));
                    builder.current(false);
                } else {
                    builder.current(true);
                }
            }
            if (proj.has("keywords")) {
                StringBuilder tech = new StringBuilder();
                for (JsonNode keyword : proj.get("keywords")) {
                    if (tech.length() > 0) tech.append(", ");
                    tech.append(keyword.asText());
                }
                builder.technologies(tech.toString());
            }
            if (proj.has("highlights")) {
                StringBuilder highlights = new StringBuilder();
                for (JsonNode highlight : proj.get("highlights")) {
                    highlights.append(highlight.asText()).append("\n");
                }
                builder.highlights(highlights.toString());
            }

            projects.add(builder.build());
        }

        return projects;
    }
}
