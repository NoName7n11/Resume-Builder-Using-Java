# Quick Start Guide - Java Resume Builder

## Prerequisites Check

Before running the project, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   ```powershell
   java -version
   ```
   Should show version 17.x.x or higher

2. **Apache Maven 3.6+**
   ```powershell
   mvn -version
   ```

## Installation Steps

### 1. Navigate to Project Directory
```powershell
cd C:\Users\novan\Desktop\Java_Project
```

### 2. Clean and Build the Project
```powershell
mvn clean install
```

This will:
- Download all dependencies
- Compile the code
- Run tests
- Create an executable JAR file

### 3. Run the Application

#### Option A: Run with Maven (Recommended for Development)
```powershell
mvn spring-boot:run
```

#### Option B: Run the JAR directly
```powershell
java -jar target/java-resume-builder-1.0.0.jar
```

#### Option C: Run JavaFX Desktop UI
```powershell
mvn javafx:run
```

### 4. Access the Application

- **Web Interface**: Open browser to `http://localhost:8080`
- **H2 Console**: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:file:./data/resumebuilder`
  - Username: `sa`
  - Password: (leave empty)
- **REST API**: Available at `http://localhost:8080/api/`

## Quick Test

### Test PDF Export via API

1. First, create a user (in production, use proper authentication):
```powershell
# Using curl (if available)
curl -X POST "http://localhost:8080/api/resumes?userId=1&title=My Resume"
```

2. Export to PDF:
```powershell
# This will download resume.pdf
curl -o resume.pdf "http://localhost:8080/api/resumes/1/export/pdf"
```

### Test with Browser

1. Open `http://localhost:8080/h2-console`
2. Connect to the database
3. Run SQL to create test data:
```sql
-- Insert a test user
INSERT INTO users (id, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (1, 'test@example.com', 'password', 'John', 'Doe', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert user role
INSERT INTO user_roles (user_id, role) VALUES (1, 'USER');

-- Create a resume
INSERT INTO resumes (id, user_id, title, template_name, active, created_at, updated_at)
VALUES (1, 1, 'Software Engineer Resume', 'professional', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

4. Test export: Visit `http://localhost:8080/api/resumes/1/export/pdf`

## Common Issues

### Issue: "Port 8080 is already in use"
**Solution**: Change the port in `application.properties`:
```properties
server.port=8081
```

### Issue: Lombok errors in IDE
**Solution**:
1. Install Lombok plugin for your IDE
2. Enable annotation processing in IDE settings
3. Rebuild the project

### Issue: JavaFX not found
**Solution**: Ensure you're using Java 17+ and Maven has downloaded JavaFX dependencies:
```powershell
mvn dependency:tree | Select-String "javafx"
```

### Issue: Database locked
**Solution**: Stop all running instances of the application and delete the database files:
```powershell
Remove-Item -Recurse -Force ./data
```

## Development Workflow

### 1. Run in Development Mode
```powershell
# Terminal 1: Run the application with auto-reload
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.devtools.restart.enabled=true"
```

### 2. Make Code Changes
- Edit files in `src/main/java`
- Save changes
- Application will auto-restart

### 3. Build for Production
```powershell
mvn clean package -Dspring.profiles.active=prod
```

This creates: `target/java-resume-builder-1.0.0.jar`

### 4. Run Production Build
```powershell
java -jar target/java-resume-builder-1.0.0.jar --spring.profiles.active=prod
```

## Next Steps

1. **Customize Templates**: Edit PDF generation in `PdfExportService.java`
2. **Add Authentication**: Implement login/registration in `SecurityConfig.java`
3. **Create UI**: Build JavaFX forms in `src/main/resources/fxml/`
4. **Add Features**: Extend models and services as needed

## Testing

### Run Unit Tests
```powershell
mvn test
```

### Run Integration Tests
```powershell
mvn verify
```

### Test Coverage
```powershell
mvn jacoco:report
```
Report will be in `target/site/jacoco/index.html`

## Deployment

### Deploy to Production Server

1. **Build the JAR**:
```powershell
mvn clean package -Dspring.profiles.active=prod
```

2. **Transfer to server** (using SCP, FTP, etc.)

3. **Run on server**:
```bash
nohup java -jar java-resume-builder-1.0.0.jar &
```

4. **Use systemd service** (Linux):
Create `/etc/systemd/system/resumebuilder.service`:
```ini
[Unit]
Description=Java Resume Builder
After=network.target

[Service]
Type=simple
User=appuser
ExecStart=/usr/bin/java -jar /opt/resumebuilder/java-resume-builder-1.0.0.jar
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

Enable and start:
```bash
sudo systemctl enable resumebuilder
sudo systemctl start resumebuilder
```

## Environment Variables

For production, set these environment variables:

```powershell
# Windows PowerShell
$env:JWT_SECRET="your-256-bit-secret-key-here"
$env:DB_URL="jdbc:postgresql://localhost:5432/resumebuilder"
$env:DB_USERNAME="resumebuilder_user"
$env:DB_PASSWORD="your-secure-password"
$env:SPRING_PROFILES_ACTIVE="prod"
```

## Monitoring

### Check Application Health
```powershell
curl http://localhost:8080/actuator/health
```

### View Logs
```powershell
# Real-time logs
tail -f logs/application.log

# In Windows
Get-Content logs/application.log -Wait
```

## Backup Database

### H2 Database
```powershell
# Create backup
Copy-Item ./data ./data-backup-$(Get-Date -Format 'yyyyMMdd') -Recurse
```

### PostgreSQL
```powershell
pg_dump resumebuilder > backup-$(date +%Y%m%d).sql
```

## Performance Tuning

### Increase JVM Memory
```powershell
java -Xms512m -Xmx2048m -jar java-resume-builder-1.0.0.jar
```

### Enable JMX Monitoring
```powershell
java -Dcom.sun.management.jmxremote `
     -Dcom.sun.management.jmxremote.port=9010 `
     -Dcom.sun.management.jmxremote.authenticate=false `
     -Dcom.sun.management.jmxremote.ssl=false `
     -jar java-resume-builder-1.0.0.jar
```

## Support & Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **JavaFX Docs**: https://openjfx.io/
- **Apache PDFBox**: https://pdfbox.apache.org/
- **JSON Resume**: https://jsonresume.org/

## License

Educational project - Free to use and modify

---

**Happy Resume Building! 🚀**
