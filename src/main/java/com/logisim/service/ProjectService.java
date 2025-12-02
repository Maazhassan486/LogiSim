package com.logisim.service;

import com.logisim.domain.Circuit;
import com.logisim.domain.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Service class for managing projects.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class ProjectService {
    private static final Logger logger = LogManager.getLogger(ProjectService.class);
    private Project currentProject;

    /**
     * Default constructor.
     */
    public ProjectService() {
        this.currentProject = null;
    }

    /**
     * Creates a new project.
     * 
     * @param name The project name
     * @return The created project
     * @throws IllegalArgumentException if name is null or empty
     */
    public Project createProject(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }

        Project project = new Project(name);
        this.currentProject = project;
        logger.info("Created new project: {}", name);
        return project;
    }

    /**
     * Gets the current project.
     * 
     * @return The current project, or null if none
     */
    public Project getCurrentProject() {
        return currentProject;
    }

    /**
     * Sets the current project.
     * 
     * @param project The project to set as current
     */
    public void setCurrentProject(Project project) {
        this.currentProject = project;
        if (project != null) {
            logger.info("Set current project: {}", project.getName());
        }
    }

    /**
     * Adds a circuit to the current project.
     * 
     * @param circuit The circuit to add
     * @throws IllegalStateException if no project is current
     */
    public void addCircuit(Circuit circuit) {
        if (currentProject == null) {
            throw new IllegalStateException("No current project");
        }
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }

        currentProject.addCircuit(circuit);
        logger.debug("Added circuit {} to project {}", circuit.getName(), currentProject.getName());
    }

    /**
     * Removes a circuit from the current project.
     * 
     * @param circuit The circuit to remove
     * @return true if removed, false otherwise
     */
    public boolean removeCircuit(Circuit circuit) {
        if (currentProject == null || circuit == null) {
            return false;
        }

        boolean removed = currentProject.removeCircuit(circuit);
        if (removed) {
            logger.debug("Removed circuit {} from project {}", circuit.getName(), currentProject.getName());
        }
        return removed;
    }

    /**
     * Creates a new circuit in the current project.
     * 
     * @param name The circuit name
     * @return The created circuit
     * @throws IllegalStateException if no project is current
     * @throws IllegalArgumentException if name is null or empty
     */
    public Circuit createCircuit(String name) {
        if (currentProject == null) {
            throw new IllegalStateException("No current project");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Circuit name cannot be null or empty");
        }

        Circuit circuit = new Circuit(name);
        currentProject.addCircuit(circuit);
        logger.info("Created new circuit: {} in project {}", name, currentProject.getName());
        return circuit;
    }
}



