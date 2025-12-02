package com.logisim.repository;

import com.logisim.domain.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.embed.swing.SwingFXUtils;

/**
 * Repository class for persisting and loading projects.
 * Implements the Repository design pattern.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class ProjectRepository {
    private static final Logger logger = LogManager.getLogger(ProjectRepository.class);
    private final Gson gson;

    /**
     * Default constructor.
     */
    public ProjectRepository() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    /**
     * Saves a project to a file.
     * 
     * @param project The project to save
     * @param filePath The file path
     * @throws IOException if saving fails
     * @throws IllegalArgumentException if project or filePath is null
     */
    public void save(Project project, String filePath) throws IOException {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        logger.info("Saving project {} to {}", project.getName(), filePath);

        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(file)) {
            project.setPath(filePath);
            gson.toJson(project, writer);
            logger.info("Project saved successfully");
        } catch (IOException e) {
            logger.error("Error saving project", e);
            throw new IOException("Failed to save project: " + e.getMessage(), e);
        }
    }

    /**
     * Loads a project from a file.
     * 
     * @param filePath The file path
     * @return The loaded project
     * @throws IOException if loading fails
     * @throws IllegalArgumentException if filePath is null
     */
    public Project load(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        logger.info("Loading project from {}", filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }

        try (FileReader reader = new FileReader(file)) {
            Project project = gson.fromJson(reader, Project.class);
            if (project != null) {
                project.setPath(filePath);
                logger.info("Project loaded successfully: {}", project.getName());
            }
            return project;
        } catch (IOException e) {
            logger.error("Error loading project", e);
            throw new IOException("Failed to load project: " + e.getMessage(), e);
        }
    }

    /**
     * Exports a project diagram to a file.
     * Note: This method requires a Canvas snapshot to be passed from the UI layer.
     * 
     * @param project The project to export
     * @param filePath The export file path
     * @param imageSnapshot The image snapshot from the canvas
     * @throws IOException if export fails
     */
    public void export(Project project, String filePath, javafx.scene.image.WritableImage imageSnapshot) throws IOException {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        if (imageSnapshot == null) {
            throw new IllegalArgumentException("Image snapshot cannot be null");
        }

        logger.info("Exporting project {} to {}", project.getName(), filePath);

        try {
            // Determine format from file extension
            String lowerPath = filePath.toLowerCase();
            String format;
            if (lowerPath.endsWith(".png")) {
                format = "png";
            } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                format = "jpg";
            } else {
                // Default to PNG
                format = "png";
                if (!filePath.endsWith(".png")) {
                    filePath += ".png";
                }
            }

            // Convert JavaFX image to BufferedImage
            java.awt.image.BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageSnapshot, null);
            
            if (bufferedImage == null) {
                throw new IOException("Failed to convert JavaFX image to BufferedImage");
            }

            // Save the image
            File outputFile = new File(filePath);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            boolean success = javax.imageio.ImageIO.write(bufferedImage, format, outputFile);
            if (!success) {
                throw new IOException("Failed to write image file. Format: " + format);
            }

            logger.info("Project exported successfully to {}", filePath);
        } catch (Exception e) {
            logger.error("Error exporting project", e);
            throw new IOException("Failed to export project: " + e.getMessage(), e);
        }
    }
}

