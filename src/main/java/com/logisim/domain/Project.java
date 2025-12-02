package com.logisim.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a project containing multiple circuits.
 * A project can be saved, loaded, and exported.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Project {
    private String name;
    private String path;
    private List<Circuit> circuits;

    /**
     * Default constructor.
     */
    public Project() {
        this.circuits = new ArrayList<>();
    }

    /**
     * Constructor with name.
     * 
     * @param name The name of the project
     */
    public Project(String name) {
        this.name = name;
        this.circuits = new ArrayList<>();
    }

    /**
     * Gets the project name.
     * 
     * @return The project name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the project name.
     * 
     * @param name The project name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the project file path.
     * 
     * @return The file path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the project file path.
     * 
     * @param path The file path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the list of circuits in this project.
     * 
     * @return List of circuits
     */
    public List<Circuit> getCircuits() {
        return circuits;
    }

    /**
     * Sets the list of circuits.
     * 
     * @param circuits List of circuits
     */
    public void setCircuits(List<Circuit> circuits) {
        this.circuits = circuits;
    }

    /**
     * Adds a circuit to the project.
     * 
     * @param circuit The circuit to add
     */
    public void addCircuit(Circuit circuit) {
        if (circuit != null && !circuits.contains(circuit)) {
            circuits.add(circuit);
        }
    }

    /**
     * Removes a circuit from the project.
     * 
     * @param circuit The circuit to remove
     * @return true if removed, false otherwise
     */
    public boolean removeCircuit(Circuit circuit) {
        return circuits.remove(circuit);
    }

    /**
     * Finds a circuit by name.
     * 
     * @param name The circuit name
     * @return The circuit if found, null otherwise
     */
    public Circuit findCircuitByName(String name) {
        return circuits.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(name, project.name) && Objects.equals(path, project.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }

    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", circuits=" + circuits.size() +
                '}';
    }
}



