package com.resumebuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class that integrates Spring Boot with JavaFX
 */
@SpringBootApplication
public class JavaResumeBuilderApplication extends Application {

    private ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(JavaResumeBuilderApplication.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML and inject Spring beans into controller
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ResumeEditor.fxml"));
        loader.setControllerFactory(springContext::getBean);
        
        Parent root = loader.load();
        
        primaryStage.setTitle("Java Resume Builder");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    @Override
    public void stop() {
        springContext.close();
    }
}
