package com.logisim.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Main application class for LogiSim.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class LogiSimApplication extends Application {
    private static final Logger logger = LogManager.getLogger(LogiSimApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Starting LogiSim application");
        
        FXMLLoader fxmlLoader = new FXMLLoader(LogiSimApplication.class.getResource("/com/logisim/ui/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        stage.setTitle("LogiSim - Logic Circuit Simulator");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
        
        logger.info("Application started successfully");
    }

    public static void main(String[] args) {
        launch();
    }
}



