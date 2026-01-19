package com.resumebuilder.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.resumebuilder.model.Education;
import com.resumebuilder.model.PersonalInfo;
import com.resumebuilder.model.Project;
import com.resumebuilder.model.Resume;
import com.resumebuilder.model.ResumeSettings;
import com.resumebuilder.model.Skill;
import com.resumebuilder.model.User;
import com.resumebuilder.model.WorkExperience;
import com.resumebuilder.service.ResumeService;
import com.resumebuilder.service.UserService;
import com.resumebuilder.service.export.DocxExportService;
import com.resumebuilder.service.export.PdfExportService;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import lombok.RequiredArgsConstructor;

/**
 * Main JavaFX controller for the Resume Builder UI
 * 
 * IMPORTANT: This class uses JavaFX FXML injection. Fields and methods annotated
 * with @FXML are accessed by the JavaFX runtime through reflection when loading
 * the corresponding .fxml file. IDEs may incorrectly report these as "unused"
 * because the usage is not visible in Java source code.
 * 
 * @FXML fields are injected by FXMLLoader at initialization time
 * @FXML methods are invoked as event handlers defined in the .fxml file
 */
@Component
@RequiredArgsConstructor
public class ResumeEditorController {

    private final ResumeService resumeService;
    private final UserService userService;
    private final PdfExportService pdfExportService;
    private final DocxExportService docxExportService;

    private Resume currentResume;
    private User currentUser;

    // Personal Info Fields
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipField;
    @FXML private TextField linkedInField;
    @FXML private TextField githubField;
    @FXML private TextField portfolioField;

    // Professional Summary
    @FXML private TextArea professionalSummaryArea;

    // Template Selection
    @FXML private ComboBox<String> templateComboBox;

    // Lists
    @FXML private ListView<Education> educationListView;
    @FXML private ListView<WorkExperience> workExperienceListView;
    @FXML private ListView<Skill> skillsListView;
    @FXML private ListView<Project> projectsListView;

    // Buttons - Injected by FXML runtime via reflection
    // IDE may incorrectly report these as "unused" - they are referenced in the .fxml file
    @FXML private Button saveButton;              // Bound to onAction="#handleSave"
    @FXML private Button exportPdfButton;         // Bound to onAction="#handleExportPdf"
    @FXML private Button exportDocxButton;        // Bound to onAction="#handleExportDocx"
    @FXML private Button addEducationButton;      // Bound to onAction="#handleAddEducation"
    @FXML private Button addExperienceButton;     // Bound to onAction="#handleAddExperience"
    @FXML private Button addSkillButton;          // Bound to onAction="#handleAddSkill"
    @FXML private Button addProjectButton;        // Bound to onAction="#handleAddProject"

    // Preview
    @FXML private WebView previewWebView;

    @FXML
    public void initialize() {
        setupTemplateComboBox();
        setupListeners();
        
        // Load or create a default resume
        loadDefaultResume();
    }

    private void setupTemplateComboBox() {
        templateComboBox.getItems().addAll("professional", "modern", "creative");
        templateComboBox.setValue("professional");
        templateComboBox.setOnAction(e -> updateTemplate());
    }

    private void setupListeners() {
        // Add listeners to update preview on text changes
        firstNameField.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());
        lastNameField.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());
        professionalSummaryArea.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());
    }

    private void loadDefaultResume() {
        // Try to load existing user and resume, or create demo data
        // In production with authentication, use the logged-in user
        currentUser = userService.findByEmail("demo@resumebuilder.com")
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email("demo@resumebuilder.com")
                            .firstName("Demo")
                            .lastName("User")
                            .build();
                    // In production, this would be saved: userService.save(newUser);
                    return newUser;
                });

        // Load the user's resumes or create a new one
        if (currentUser.getId() != null) {
            Long userId = currentUser.getId();
            if (userId != null) {
                resumeService.findByUserId(userId)
                        .stream()
                        .findFirst()
                        .ifPresentOrElse(
                                resume -> currentResume = resume,
                                this::createNewResume
                        );
            } else {
                createNewResume();
            }
        } else {
            createNewResume();
        }

        loadResumeData();
    }

    private void createNewResume() {
        currentResume = Resume.builder()
                .title("My Resume")
                .templateName("professional")
                .user(currentUser)
                .settings(ResumeSettings.builder().build())
                .build();
        // In production, this would be saved when user clicks save
    }

    private void loadResumeData() {
        if (currentResume == null) return;

        // Load personal info
        if (currentResume.getPersonalInfo() != null) {
            PersonalInfo pi = currentResume.getPersonalInfo();
            firstNameField.setText(pi.getFirstName());
            lastNameField.setText(pi.getLastName());
            emailField.setText(pi.getEmail());
            phoneField.setText(pi.getPhone());
            linkedInField.setText(pi.getLinkedinUrl());
            githubField.setText(pi.getGithubUrl());
            portfolioField.setText(pi.getPortfolioUrl());
        }

        // Load professional summary
        if (currentResume.getProfessionalSummary() != null) {
            professionalSummaryArea.setText(currentResume.getProfessionalSummary());
        }

        // Load template
        if (currentResume.getTemplateName() != null) {
            templateComboBox.setValue(currentResume.getTemplateName());
        }

        // Load lists
        educationListView.getItems().setAll(currentResume.getEducations());
        workExperienceListView.getItems().setAll(currentResume.getWorkExperiences());
        skillsListView.getItems().setAll(currentResume.getSkills());
        projectsListView.getItems().setAll(currentResume.getProjects());

        updatePreview();
    }

    /**
     * FXML Event Handler: Called when save button is clicked
     * Bound in .fxml file via onAction="#handleSave"
     */
    @FXML
    private void handleSave() {
        saveCurrentData();
        
        // Persist to database if resume has an ID (already saved) or currentUser has ID
        Long resumeId = currentResume.getId();
        Long userId = currentUser != null ? currentUser.getId() : null;
        
        if (resumeId != null) {
            try {
                String title = currentResume.getTitle();
                String description = currentResume.getDescription();
                String summary = currentResume.getProfessionalSummary();
                String template = currentResume.getTemplateName();
                
                if (title != null) {
                    resumeService.updateResume(resumeId, title, description, summary, template);
                    showAlert("Success", "Resume updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Resume title cannot be null", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to save resume: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else if (userId != null) {
            try {
                String title = currentResume.getTitle();
                String description = currentResume.getDescription();
                
                if (title != null) {
                    Resume saved = resumeService.createResume(userId, title, description);
                    currentResume.setId(saved.getId());
                    showAlert("Success", "Resume created successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Resume title cannot be null", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Error", "Failed to create resume: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            // Demo mode - no database persistence
            showAlert("Success", "Resume saved (demo mode - not persisted to database)", Alert.AlertType.INFORMATION);
        }
    }

    private void saveCurrentData() {
        // Update personal info
        PersonalInfo pi = PersonalInfo.builder()
                .firstName(firstNameField.getText())
                .lastName(lastNameField.getText())
                .email(emailField.getText())
                .phone(phoneField.getText())
                .address(addressField.getText())
                .city(cityField.getText())
                .state(stateField.getText())
                .zipCode(zipField.getText())
                .linkedinUrl(linkedInField.getText())
                .githubUrl(githubField.getText())
                .portfolioUrl(portfolioField.getText())
                .build();
        
        currentResume.setPersonalInfo(pi);
        
        // Update professional summary
        currentResume.setProfessionalSummary(professionalSummaryArea.getText());
        
        // Update template
        currentResume.setTemplateName(templateComboBox.getValue());
        
        // Update title using current user's name if available
        if (currentUser != null && currentUser.getFirstName() != null) {
            String title = currentUser.getFirstName() + " " + currentUser.getLastName() + "'s Resume";
            currentResume.setTitle(title);
        }

        // Note: Full persistence with personal info, education, experience etc.
        // would require calling resumeService methods for each entity
    }

    /**
     * FXML Event Handler: Called when export PDF button is clicked
     * Bound in .fxml file via onAction="#handleExportPdf"
     */
    @FXML
    private void handleExportPdf() {
        try {
            saveCurrentData();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Resume as PDF");
            fileChooser.setInitialFileName("resume.pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(exportPdfButton.getScene().getWindow());
            if (file != null) {
                byte[] pdfBytes = pdfExportService.exportToPdf(currentResume);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(pdfBytes);
                }
                showAlert("Success", "Resume exported to PDF successfully!", Alert.AlertType.INFORMATION);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to export PDF: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * FXML Event Handler: Called when export DOCX button is clicked
     * Bound in .fxml file via onAction="#handleExportDocx"
     */
    @FXML
    private void handleExportDocx() {
        try {
            saveCurrentData();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Resume as Word Document");
            fileChooser.setInitialFileName("resume.docx");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Word Documents", "*.docx")
            );
            
            File file = fileChooser.showSaveDialog(exportDocxButton.getScene().getWindow());
            if (file != null) {
                byte[] docxBytes = docxExportService.exportToDocx(currentResume);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(docxBytes);
                }
                showAlert("Success", "Resume exported to Word successfully!", Alert.AlertType.INFORMATION);
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to export Word document: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * FXML Event Handler: Called when add education button is clicked
     * Bound in .fxml file via onAction="#handleAddEducation"
     */
    @FXML
    private void handleAddEducation() {
        // Show dialog to add education
        EducationDialog dialog = new EducationDialog();
        Optional<Education> result = dialog.showAndWait();
        result.ifPresent(education -> {
            currentResume.addEducation(education);
            educationListView.getItems().add(education);
            updatePreview();
        });
    }

    /**
     * FXML Event Handler: Called when add experience button is clicked
     * Bound in .fxml file via onAction="#handleAddExperience"
     */
    @FXML
    private void handleAddExperience() {
        // Show dialog to add work experience
        WorkExperienceDialog dialog = new WorkExperienceDialog();
        Optional<WorkExperience> result = dialog.showAndWait();
        result.ifPresent(experience -> {
            currentResume.addWorkExperience(experience);
            workExperienceListView.getItems().add(experience);
            updatePreview();
        });
    }

    /**
     * FXML Event Handler: Called when add skill button is clicked
     * Bound in .fxml file via onAction="#handleAddSkill"
     */
    @FXML
    private void handleAddSkill() {
        // Show dialog to add skill
        SkillDialog dialog = new SkillDialog();
        Optional<Skill> result = dialog.showAndWait();
        result.ifPresent(skill -> {
            currentResume.addSkill(skill);
            skillsListView.getItems().add(skill);
            updatePreview();
        });
    }

    /**
     * FXML Event Handler: Called when add project button is clicked
     * Bound in .fxml file via onAction="#handleAddProject"
     */
    @FXML
    private void handleAddProject() {
        // Show dialog to add project
        ProjectDialog dialog = new ProjectDialog();
        Optional<Project> result = dialog.showAndWait();
        result.ifPresent(project -> {
            currentResume.addProject(project);
            projectsListView.getItems().add(project);
            updatePreview();
        });
    }

    private void updateTemplate() {
        if (currentResume != null) {
            currentResume.setTemplateName(templateComboBox.getValue());
            updatePreview();
        }
    }

    private void updatePreview() {
        // Generate HTML preview of the resume
        String html = generatePreviewHtml();
        if (previewWebView != null) {
            previewWebView.getEngine().loadContent(html);
        }
    }

    private String generatePreviewHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #2c3e50; border-bottom: 2px solid #3498db; }");
        html.append("h2 { color: #34495e; margin-top: 20px; }");
        html.append(".contact { color: #7f8c8d; }");
        html.append("</style></head><body>");

        // Personal Info
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        if (!firstName.isEmpty() || !lastName.isEmpty()) {
            html.append("<h1>").append(firstName).append(" ").append(lastName).append("</h1>");
        }

        String email = emailField.getText();
        String phone = phoneField.getText();
        if (!email.isEmpty() || !phone.isEmpty()) {
            html.append("<p class='contact'>").append(email);
            if (!phone.isEmpty()) {
                html.append(" | ").append(phone);
            }
            html.append("</p>");
        }

        // Professional Summary
        String summary = professionalSummaryArea.getText();
        if (!summary.isEmpty()) {
            html.append("<h2>Professional Summary</h2>");
            html.append("<p>").append(summary.replace("\n", "<br>")).append("</p>");
        }

        // Work Experience
        if (!workExperienceListView.getItems().isEmpty()) {
            html.append("<h2>Work Experience</h2>");
            for (WorkExperience we : workExperienceListView.getItems()) {
                html.append("<h3>").append(we.getJobTitle()).append(" - ").append(we.getCompany()).append("</h3>");
                html.append("<p><i>").append(we.getDateRange()).append("</i></p>");
            }
        }

        // Education
        if (!educationListView.getItems().isEmpty()) {
            html.append("<h2>Education</h2>");
            for (Education edu : educationListView.getItems()) {
                html.append("<h3>").append(edu.getDegree()).append("</h3>");
                html.append("<p>").append(edu.getInstitution()).append("</p>");
            }
        }

        // Skills
        if (!skillsListView.getItems().isEmpty()) {
            html.append("<h2>Skills</h2>");
            html.append("<p>");
            for (int i = 0; i < skillsListView.getItems().size(); i++) {
                if (i > 0) html.append(", ");
                html.append(skillsListView.getItems().get(i).getName());
            }
            html.append("</p>");
        }

        html.append("</body></html>");
        return html.toString();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner dialog classes for adding items
    private static class EducationDialog extends Dialog<Education> {
        public EducationDialog() {
            setTitle("Add Education");
            setHeaderText("Enter education details");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            VBox content = new VBox(10);
            TextField degreeField = new TextField();
            degreeField.setPromptText("Degree");
            TextField institutionField = new TextField();
            institutionField.setPromptText("Institution");
            TextField fieldField = new TextField();
            fieldField.setPromptText("Field of Study");

            content.getChildren().addAll(
                new Label("Degree:"), degreeField,
                new Label("Institution:"), institutionField,
                new Label("Field of Study:"), fieldField
            );

            getDialogPane().setContent(content);

            setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return Education.builder()
                            .degree(degreeField.getText())
                            .institution(institutionField.getText())
                            .fieldOfStudy(fieldField.getText())
                            .build();
                }
                return null;
            });
        }
    }

    private static class WorkExperienceDialog extends Dialog<WorkExperience> {
        public WorkExperienceDialog() {
            setTitle("Add Work Experience");
            setHeaderText("Enter work experience details");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            VBox content = new VBox(10);
            TextField jobTitleField = new TextField();
            jobTitleField.setPromptText("Job Title");
            TextField companyField = new TextField();
            companyField.setPromptText("Company");
            TextArea responsibilitiesArea = new TextArea();
            responsibilitiesArea.setPromptText("Responsibilities (one per line)");
            responsibilitiesArea.setPrefRowCount(5);

            content.getChildren().addAll(
                new Label("Job Title:"), jobTitleField,
                new Label("Company:"), companyField,
                new Label("Responsibilities:"), responsibilitiesArea
            );

            getDialogPane().setContent(content);

            setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return WorkExperience.builder()
                            .jobTitle(jobTitleField.getText())
                            .company(companyField.getText())
                            .responsibilities(responsibilitiesArea.getText())
                            .build();
                }
                return null;
            });
        }
    }

    private static class SkillDialog extends Dialog<Skill> {
        public SkillDialog() {
            setTitle("Add Skill");
            setHeaderText("Enter skill details");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            VBox content = new VBox(10);
            TextField nameField = new TextField();
            nameField.setPromptText("Skill Name");
            TextField categoryField = new TextField();
            categoryField.setPromptText("Category (e.g., Programming Languages)");

            content.getChildren().addAll(
                new Label("Skill Name:"), nameField,
                new Label("Category:"), categoryField
            );

            getDialogPane().setContent(content);

            setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return Skill.builder()
                            .name(nameField.getText())
                            .category(categoryField.getText())
                            .visible(true)
                            .build();
                }
                return null;
            });
        }
    }

    private static class ProjectDialog extends Dialog<Project> {
        public ProjectDialog() {
            setTitle("Add Project");
            setHeaderText("Enter project details");

            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            VBox content = new VBox(10);
            TextField nameField = new TextField();
            nameField.setPromptText("Project Name");
            TextArea descriptionArea = new TextArea();
            descriptionArea.setPromptText("Description");
            descriptionArea.setPrefRowCount(3);
            TextField techField = new TextField();
            techField.setPromptText("Technologies (comma-separated)");

            content.getChildren().addAll(
                new Label("Project Name:"), nameField,
                new Label("Description:"), descriptionArea,
                new Label("Technologies:"), techField
            );

            getDialogPane().setContent(content);

            setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return Project.builder()
                            .name(nameField.getText())
                            .description(descriptionArea.getText())
                            .technologies(techField.getText())
                            .build();
                }
                return null;
            });
        }
    }
}
