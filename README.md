# Java Resume Builder

A full-featured resume builder application with JavaFX desktop UI and Spring Boot REST API. Create, customize, and export professional resumes in multiple formats (PDF, DOCX, TXT).

![Java Resume Builder Interface](./src/image.png)

## ✨ Features

### Core Features
- 📝 **Resume Data Entry**: Comprehensive forms for all resume sections
  - Personal information, professional summary, education, work experience, skills, and projects
- 🎨 **3 Professional Templates**: Choose from Modern, Professional, or Creative designs
- 👁️ **Real-time Preview**: See changes instantly as you type
- 📥 **JSON Resume Import**: Support for standard JSON Resume format
- 💾 **Multiple Versions**: Save and manage unlimited resume versions
- 🔒 **User Authentication**: Secure accounts with email/password

### Customization & Export
- 🎯 **Template Customization**: Adjust colors, fonts, sizes, margins, and section order
- 📄 **Multiple Export Formats**: PDF, DOCX (Word), and TXT
- 📋 **Custom Sections**: Add certifications, publications, volunteer work, etc.
- 🔗 **Shareable Links**: Generate private URLs for resume sharing
- 🌐 **REST API**: Full API for programmatic access

## 🛠️ Technology Stack

- **Backend**: Java 17, Spring Boot 3.1.5, Spring Data JPA, Spring Security
- **Frontend**: JavaFX 21 (Desktop UI)
- **Database**: H2 (dev) / PostgreSQL (production)
- **Document Generation**: Apache PDFBox, Apache POI
- **Build Tool**: Maven
- **Authentication**: JWT, BCrypt

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Installation

```powershell
# Clone and navigate to project
cd Resume_Builder

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📂 Project Structure

```
├── src/main/java/com/resumebuilder/
│   ├── model/                 # Domain entities (User, Resume, Education, etc.)
│   ├── repository/            # Data access layer
│   ├── service/               # Business logic
│   │   ├── export/            # PDF and DOCX export services
│   │   └── importer/          # JSON Resume import
│   ├── controller/            # REST API endpoints
│   ├── ui/controller/         # JavaFX UI controllers
│   └── config/                # Configuration (Security, etc.)
├── src/main/resources/
│   └── fxml/                  # JavaFX UI definitions
└── pom.xml                    # Maven configuration
│   │   ├── CustomSection.java                # Custom resume sections
│   │   └── ResumeSettings.java               # Customization settings
│   │
│   ├── repository/                            # Data Access Layer (JPA)
│   │   ├── UserRepository.java
│   │   ├── ResumeRepository.java
│   │   ├── EducationRepository.java
│   │   ├── WorkExperienceRepository.java
│   │   ├── SkillRepository.java
│   │   ├── ProjectRepository.java
│   │   └── CustomSectionRepository.java
│   │
│   ├── service/                               # Business Logic
│   │   ├── UserService.java                  # User management
│   │   ├── ResumeService.java                # Resume CRUD operations
│   │   ├── export/
│   │   │   ├── PdfExportService.java         # PDF generation
│   │   │   └── DocxExportService.java        # Word document export
│   │   └── importer/
│   │       └── JsonResumeImporter.java       # JSON Resume import
│   │
│   ├── controller/                            # REST API
│   │   └── ResumeController.java             # Resume endpoints
│   │
│   ├── config/                                # Configuration
│   │   └── SecurityConfig.java               # Security setup
│   │
│   └── ui/controller/                         # JavaFX UI
│       └── ResumeEditorController.java       # Main UI controller
│
├── src/main/resources/
│   ├── application.properties                 # App configuration
│   └── application-prod.properties            # Production settings
│
├── pom.xml                                    # Maven dependencies
├── README.md                                  # Full documentation
├── QUICKSTART.md                              # Quick start guide
└── .gitignore                                 # Git ignore rules
```

## 🔧 Technologies Used

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 17 | Main programming language |
| **Framework** | Spring Boot | 3.1.5 | Web framework & DI |
| **UI** | JavaFX | 21.0.1 | Desktop interface |
| **Database** | H2 / PostgreSQL | Latest | Data persistence |
| **ORM** | Spring Data JPA | 3.1.5 | Database access |
| **Security** | Spring Security | 3.1.5 | Authentication |
| **PDF** | Apache PDFBox | 3.0.0 | PDF generation |
| **Word** | Apache POI | 5.2.5 | DOCX export |
| **JSON** | Jackson | Latest | JSON processing |
| **Auth** | JWT (jjwt) | 0.12.3 | Token authentication |
| **Build** | Maven | 3.6+ | Build automation |

## 🚀 Getting Started

### 1. Prerequisites
- Java 17 or higher
- Maven 3.6+
- (Optional) PostgreSQL for production

### 2. Build the Project
```powershell
cd C:\Users\novan\Desktop\Java_Project
mvn clean install
```

### 3. Run the Application
```powershell
# Run Spring Boot backend
mvn spring-boot:run

# Or run JavaFX desktop UI
mvn javafx:run
```

## 📡 API Endpoints

### Resume Management
```
POST   /api/resumes                    # Create resume
GET    /api/resumes/{id}               # Get resume
GET    /api/resumes/user/{userId}      # Get user's resumes
PUT    /api/resumes/{id}               # Update resume
DELETE /api/resumes/{id}               # Delete resume
```

### Export Functions
```
GET /api/resumes/{id}/export/pdf       # Export as PDF
GET /api/resumes/{id}/export/docx      # Export as DOCX
GET /api/resumes/{id}/export/txt       # Export as text
```

### Sharing
```
POST /api/resumes/{id}/share           # Generate share link
POST /api/resumes/{id}/share/disable   # Disable sharing
GET  /api/resumes/shared/{token}       # View shared resume
```

## 🎨 Resume Templates

### 1. Professional Template
- Clean, traditional layout
- Black and white
- Perfect for corporate jobs
- Emphasis on content

### 2. Modern Template
- Contemporary design
- Customizable colors
- Good for tech industry
- Balanced visual appeal

### 3. Creative Template
- Unique layout
- Bold typography
- Color accents
- Ideal for creative fields

## 💾 Database Schema

**Core Tables:**
- `users` - User accounts
- `user_roles` - User role assignments
- `resumes` - Resume documents
- `personal_info` - Contact information
- `education` - Education entries
- `work_experience` - Work history
- `skills` - Skill entries
- `projects` - Project entries
- `custom_sections` - Custom resume sections

**Relationships:**
- One User → Many Resumes
- One Resume → One PersonalInfo
- One Resume → Many Education/WorkExperience/Skills/Projects/CustomSections

## 🔐 Security Features

✅ Password encryption (BCrypt)
✅ JWT token authentication
✅ Role-based access control
✅ CORS configuration
✅ SQL injection protection (JPA)
✅ XSS protection headers
✅ OAuth2 support ready (Google, GitHub, LinkedIn)

## 📊 Key Features by Priority

### Must-Have (Implemented)
- [x] User data entry forms
- [x] Template selection
- [x] PDF export
- [x] Database persistence
- [x] User accounts

### Nice-to-Have (Implemented)
- [x] Multiple resume versions
- [x] Template customization
- [x] DOCX export
- [x] JSON import
- [x] Shareable links
- [x] REST API

### Future Enhancements (Not Implemented)
- [ ] AI-powered content suggestions
- [ ] Keyword analysis for job descriptions
- [ ] Real LinkedIn API integration
- [ ] ATS optimization scoring
- [ ] Email resume feature
- [ ] Mobile apps
- [ ] Cloud storage integration
- [ ] Collaboration features

## 🧪 Testing

The project is set up for testing with:
- JUnit 5 for unit tests
- Spring Boot Test for integration tests
- Test coverage with JaCoCo

Run tests:
```powershell
mvn test
```

## 📦 Deployment

### Development
```powershell
mvn spring-boot:run
```

### Production
```powershell
# Build
mvn clean package -Dspring.profiles.active=prod

# Run
java -jar target/java-resume-builder-1.0.0.jar
```

## 🐛 Known Issues & Solutions

### Lombok Errors in IDE
**Issue**: IDE shows compilation errors for @Builder, @Data, etc.
**Solution**: Install Lombok plugin and enable annotation processing

### Database Locked
**Issue**: "Database is locked" error
**Solution**: Stop all running instances and delete ./data directory

### Port Already in Use
**Issue**: Port 8080 is occupied
**Solution**: Change port in application.properties: `server.port=8081`

## 📚 Documentation Files

1. **README.md** - Complete project documentation
2. **QUICKSTART.md** - Quick start guide for developers
3. **PROJECT_SUMMARY.md** (this file) - Project overview

## 🔮 Future Development Ideas

1. **AI Integration**: GPT-powered content suggestions
2. **ATS Optimization**: Score resumes for ATS compatibility
3. **LinkedIn Sync**: Real-time LinkedIn profile import
4. **Analytics**: Track resume views and downloads
5. **Collaboration**: Share and review with mentors
6. **Version Control**: Git-like version history
7. **Templates**: Add more industry-specific templates
8. **Multi-language**: i18n support
9. **Cloud Sync**: Store resumes in cloud
10. **Mobile App**: React Native or Flutter companion

## 📝 Code Quality

The codebase follows:
- Java naming conventions
- SOLID principles
- Clean code practices
- Proper exception handling
- Comprehensive documentation
- Consistent formatting

## 🤝 Contributing

This is an educational project. To enhance it:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

Educational project - Free to use and modify