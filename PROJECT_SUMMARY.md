# Java Resume Builder

A comprehensive resume builder application built with Java, featuring both a JavaFX desktop interface and a Spring Boot REST API backend. Create, customize, and export professional resumes in multiple formats (PDF, DOCX, TXT).

## 🚀 Features

### Core Features (MVP)
- ✅ **User Data Entry**: Intuitive forms for all resume sections
  - Personal Information (Name, Contact, Links)
  - Professional Summary/Objective
  - Education (Degree, University, GPA, Dates)
  - Work Experience (Company, Role, Responsibilities, Achievements)
  - Skills (Categorized by type)
  - Projects (with technologies and highlights)
  
- ✅ **Template Selection**: Choose from 3 professional templates
  - Professional (Clean and traditional)
  - Modern (Contemporary with accent colors)
  - Creative (Visually engaging)
  
- ✅ **Real-time Preview**: See changes as you type
  
- ✅ **PDF Export**: High-quality PDF generation using Apache PDFBox

### Usability & Customization
- ✅ **User Accounts**: Secure authentication with email/password
- ✅ **Multiple Resume Versions**: Save and manage multiple resumes
- ✅ **Dynamic Section Management**:
  - Add custom sections (Certifications, Publications, Volunteer Work)
  - Reorder sections via drag-and-drop
  - Toggle section visibility
  
- ✅ **Template Customization**:
  - Change primary/secondary colors
  - Adjust font family and size
  - Customize margins and spacing
  - Control section order

### Advanced Features
- ✅ **Import from JSON Resume**: Standard JSON Resume format support
- ✅ **Multiple Export Formats**: PDF, DOCX (Word), and TXT
- ✅ **Shareable Links**: Generate private URLs for resume sharing
- ✅ **Database Persistence**: Save all resume data with JPA/Hibernate

## 🛠️ Technology Stack

### Backend
- **Java 17**: Modern Java with latest features
- **Spring Boot 3.1.5**: Web framework and dependency injection
- **Spring Data JPA**: Database access and ORM
- **Spring Security**: Authentication and authorization
- **H2 Database**: Development database (easily switch to PostgreSQL)
- **Lombok**: Reduce boilerplate code

### Frontend
- **JavaFX 21**: Rich desktop UI
- **FXML**: Declarative UI design

### Document Generation
- **Apache PDFBox 3.0**: PDF creation
- **Apache POI 5.2**: Microsoft Word document generation

### Additional Libraries
- **Jackson**: JSON processing for import/export
- **JWT (jjwt)**: Token-based authentication
- **Maven**: Build and dependency management

## 📁 Project Structure

```
Java_Project/
├── src/main/java/com/resumebuilder/
│   ├── JavaResumeBuilderApplication.java      # Main application entry point
│   ├── model/                                   # Domain entities
│   │   ├── User.java                           # User account
│   │   ├── Resume.java                         # Resume document
│   │   ├── PersonalInfo.java                   # Contact information
│   │   ├── Education.java                      # Education entry
│   │   ├── WorkExperience.java                 # Work history
│   │   ├── Skill.java                          # Skills
│   │   ├── Project.java                        # Projects
│   │   ├── CustomSection.java                  # Custom sections
│   │   └── ResumeSettings.java                 # Customization settings
│   ├── repository/                              # Data access layer
│   │   ├── UserRepository.java
│   │   ├── ResumeRepository.java
│   │   └── ... (other repositories)
│   ├── service/                                 # Business logic
│   │   ├── UserService.java
│   │   ├── ResumeService.java
│   │   ├── export/
│   │   │   ├── PdfExportService.java           # PDF generation
│   │   │   └── DocxExportService.java          # Word document generation
│   │   └── importer/
│   │       └── JsonResumeImporter.java         # JSON Resume import
│   ├── controller/                              # REST API endpoints
│   │   └── ResumeController.java
│   └── config/                                  # Configuration classes
│       └── SecurityConfig.java
├── src/main/resources/
│   ├── application.properties                   # Application configuration
│   └── application-prod.properties              # Production settings
├── pom.xml                                      # Maven dependencies
└── README.md                                    # This file
```

## 🚦 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- (Optional) PostgreSQL for production

### Installation

1. **Clone the repository**
```powershell
cd Desktop\Java_Project
```

2. **Build the project**
```powershell
mvn clean install
```

3. **Run the application**
```powershell
# Run Spring Boot backend
mvn spring-boot:run

# Or run JavaFX desktop application
mvn javafx:run
```

The application will start on `http://localhost:8080`

### Database Configuration

#### Development (H2 - Default)
The application uses H2 in-memory database by default. Access the H2 console at:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/resumebuilder`
- Username: `sa`
- Password: (empty)

#### Production (PostgreSQL)
1. Create a PostgreSQL database:
```sql
CREATE DATABASE resumebuilder;
CREATE USER resumebuilder_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE resumebuilder TO resumebuilder_user;
```

2. Update `application-prod.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/resumebuilder
spring.datasource.username=resumebuilder_user
spring.datasource.password=your_password
```

3. Run with production profile:
```powershell
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📖 API Documentation

### Resume Endpoints

#### Create Resume
```http
POST /api/resumes
?userId=1&title=My Resume&description=Software Engineer Resume
```

#### Get Resume
```http
GET /api/resumes/{id}
```

#### Get User's Resumes
```http
GET /api/resumes/user/{userId}
```

#### Update Resume
```http
PUT /api/resumes/{id}
?title=Updated Title&templateName=modern
```

#### Delete Resume
```http
DELETE /api/resumes/{id}
```

#### Export as PDF
```http
GET /api/resumes/{id}/export/pdf
```

#### Export as DOCX
```http
GET /api/resumes/{id}/export/docx
```

#### Export as Text
```http
GET /api/resumes/{id}/export/txt
```

#### Generate Share Link
```http
POST /api/resumes/{id}/share
```

#### View Shared Resume
```http
GET /api/resumes/shared/{token}
```

## 🎨 Resume Templates

### Professional Template
- Clean, traditional layout
- Black and white color scheme
- Perfect for corporate positions
- Emphasizes content over design

### Modern Template
- Contemporary design
- Customizable accent colors
- Balanced visual appeal
- Suitable for tech industry

### Creative Template
- Unique layout options
- Bold typography
- Color accents
- Ideal for creative fields

## 📥 Importing Resumes

### JSON Resume Format
Import resumes using the standard [JSON Resume](https://jsonresume.org/) format:

```json
{
  "basics": {
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "(555) 123-4567",
    "url": "https://johndoe.com",
    "location": {
      "city": "San Francisco",
      "region": "CA",
      "postalCode": "94102",
      "countryCode": "US"
    },
    "profiles": [
      {
        "network": "LinkedIn",
        "url": "https://linkedin.com/in/johndoe"
      },
      {
        "network": "GitHub",
        "url": "https://github.com/johndoe"
      }
    ]
  },
  "work": [...],
  "education": [...],
  "skills": [...],
  "projects": [...]
}
```

## 🔐 Security Features

- Password encryption using BCrypt
- JWT-based authentication
- CORS configuration for cross-origin requests
- SQL injection protection via JPA
- XSS protection headers

## 🧪 Testing

Run tests with:
```powershell
mvn test
```

## 📦 Building for Production

Create an executable JAR:
```powershell
mvn clean package
```

Run the JAR:
```powershell
java -jar target/java-resume-builder-1.0.0.jar
```

## 🔧 Configuration Options

Edit `application.properties` to customize:
- Database connection
- JWT secret and expiration
- File upload limits
- Export directory locations
- Template directories
- Maximum resume versions per user

## 🤝 Contributing

This is an educational project. Feel free to fork and enhance it with:
- Additional resume templates
- AI-powered content suggestions
- LinkedIn API integration
- ATS (Applicant Tracking System) optimization
- Multi-language support
- Cloud storage integration

## 📝 License

This project is created for educational purposes. Feel free to use and modify as needed.

## 🎯 Future Enhancements

- [ ] AI-powered bullet point suggestions
- [ ] Keyword analysis for job descriptions
- [ ] LinkedIn profile import
- [ ] ATS optimization scoring
- [ ] Collaboration features
- [ ] Version history and rollback
- [ ] Email resume directly from app
- [ ] Mobile app (Android/iOS)
- [ ] Cloud sync
- [ ] Resume analytics

## 💡 Tips for Use

1. **Multiple Versions**: Create separate resumes for different job types
2. **Template Selection**: Choose templates based on your industry
3. **Content**: Use action verbs and quantifiable achievements
4. **Skills**: Categorize skills (Technical, Soft Skills, Languages, etc.)
5. **Customization**: Adjust colors and fonts to match your personal brand
6. **Proofreading**: Export to PDF and review before sending

## 🐛 Troubleshooting

### Lombok Errors in IDE
If you see compilation errors related to Lombok:
1. Install Lombok plugin for your IDE
2. Enable annotation processing
3. Rebuild the project

### Database Connection Issues
- Check if H2 database file has proper permissions
- For PostgreSQL, verify connection credentials
- Check firewall settings

### JavaFX Not Loading
- Ensure JavaFX libraries are in your Maven dependencies
- Check Java version compatibility (Java 17+)

## 📧 Contact

For questions or suggestions about this project, please open an issue on the repository.

---

**Built with ❤️ using Java, Spring Boot, and JavaFX**
