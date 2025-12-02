package com.logisim.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a circuit containing components and connectors.
 * A circuit can simulate execution and analyze its behavior.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Circuit {
    private String name;
    private List<Component> components;
    private List<Connector> connectors;

    /**
     * Default constructor.
     */
    public Circuit() {
        this.components = new ArrayList<>();
        this.connectors = new ArrayList<>();
    }

    /**
     * Constructor with name.
     * 
     * @param name The circuit name
     */
    public Circuit(String name) {
        this.name = name;
        this.components = new ArrayList<>();
        this.connectors = new ArrayList<>();
    }

    /**
     * Gets the circuit name.
     * 
     * @return The circuit name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the circuit name.
     * 
     * @param name The circuit name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of components.
     * 
     * @return List of components
     */
    public List<Component> getComponents() {
        return components;
    }

    /**
     * Sets the list of components.
     * 
     * @param components List of components
     */
    public void setComponents(List<Component> components) {
        this.components = components;
    }

    /**
     * Gets the list of connectors.
     * 
     * @return List of connectors
     */
    public List<Connector> getConnectors() {
        return connectors;
    }

    /**
     * Sets the list of connectors.
     * 
     * @param connectors List of connectors
     */
    public void setConnectors(List<Connector> connectors) {
        this.connectors = connectors;
    }

    /**
     * Adds a component to the circuit.
     * 
     * @param component The component to add
     */
    public void addComponent(Component component) {
        if (component != null && !components.contains(component)) {
            components.add(component);
        }
    }

    /**
     * Removes a component from the circuit.
     * Also removes all connectors associated with this component.
     * 
     * @param component The component to remove
     * @return true if removed, false otherwise
     */
    public boolean removeComponent(Component component) {
        if (components.remove(component)) {
            // Remove all connectors connected to this component
            connectors.removeIf(connector -> 
                connector.getSource() == component || connector.getSink() == component);
            return true;
        }
        return false;
    }

    /**
     * Adds a connector to the circuit.
     * 
     * @param connector The connector to add
     */
    public void addConnector(Connector connector) {
        if (connector != null && !connectors.contains(connector)) {
            connectors.add(connector);
        }
    }

    /**
     * Removes a connector from the circuit.
     * 
     * @param connector The connector to remove
     * @return true if removed, false otherwise
     */
    public boolean removeConnector(Connector connector) {
        return connectors.remove(connector);
    }

    /**
     * Simulates the circuit execution with given input values.
     * 
     * @param inputs Map of component names to their input values
     * @return Map of component names to their output values
     */
    public Map<String, Boolean> simulate(Map<String, Boolean> inputs) {
        Map<String, Boolean> outputs = new HashMap<>();
        Map<Component, Boolean> componentOutputs = new HashMap<>();

        // Set input values for components
        for (Map.Entry<String, Boolean> entry : inputs.entrySet()) {
            Component component = findComponentByName(entry.getKey());
            if (component != null && component.getInputs().size() > 0) {
                component.setInput(0, entry.getValue());
            }
        }

        // Execute all components
        for (Component component : components) {
            component.execute();
            componentOutputs.put(component, component.getOutput(0));
        }

        // Process connectors to propagate signals
        boolean changed = true;
        int iterations = 0;
        final int MAX_ITERATIONS = 100; // Prevent infinite loops

        while (changed && iterations < MAX_ITERATIONS) {
            changed = false;
            for (Connector connector : connectors) {
                Component source = connector.getSource();
                Component sink = connector.getSink();
                
                if (source != null && sink != null) {
                    Boolean sourceOutput = componentOutputs.get(source);
                    if (sourceOutput != null) {
                        // Find the input index for this connector
                        int inputIndex = findInputIndex(sink, connector);
                        if (inputIndex >= 0) {
                            Boolean oldInput = sink.getInputs().get(inputIndex);
                            sink.setInput(inputIndex, sourceOutput);
                            if (!Objects.equals(oldInput, sourceOutput)) {
                                changed = true;
                                sink.execute();
                                componentOutputs.put(sink, sink.getOutput(0));
                            }
                        }
                    }
                }
            }
            iterations++;
        }

        // Collect outputs
        for (Component component : components) {
            if (component.getOutputs().size() > 0) {
                outputs.put(component.getName(), componentOutputs.getOrDefault(component, false));
            }
        }

        return outputs;
    }

    /**
     * Analyzes the circuit and generates a truth table.
     * 
     * @return List of truth table rows
     */
    public List<Map<String, Boolean>> analyze() {
        List<Map<String, Boolean>> truthTable = new ArrayList<>();

        // Prefer using Switch components as primary inputs (this matches the UI model)
        List<Component> switchInputs = new ArrayList<>();
        for (Component component : components) {
            if (component instanceof com.logisim.domain.components.Switch) {
                switchInputs.add(component);
            }
        }

        if (!switchInputs.isEmpty()) {
            // Generate combinations based on switches
            int numInputs = switchInputs.size();
            int numCombinations = (int) Math.pow(2, numInputs);

            for (int i = 0; i < numCombinations; i++) {
                Map<String, Boolean> row = new HashMap<>();

                // Set switch states according to current combination
                for (int j = 0; j < numInputs; j++) {
                    boolean value = (i & (1 << (numInputs - 1 - j))) != 0;
                    Component comp = switchInputs.get(j);
                    com.logisim.domain.components.Switch sw =
                            (com.logisim.domain.components.Switch) comp;
                    sw.setState(value);
                    row.put("Input_" + sw.getName(), value);
                }

                // Simulate with the switch states already applied
                Map<String, Boolean> outputs = simulate(new HashMap<>());

                // Add outputs to row
                for (Map.Entry<String, Boolean> output : outputs.entrySet()) {
                    row.put("Output_" + output.getKey(), output.getValue());
                }

                truthTable.add(row);
            }

            return truthTable;
        }

        // Fallback: treat components with inputs but no incoming connectors as abstract inputs
        List<Component> inputComponents = new ArrayList<>();
        for (Component component : components) {
            boolean hasInput = connectors.stream()
                    .anyMatch(c -> c.getSink() == component);
            if (!hasInput && component.getInputs().size() > 0) {
                inputComponents.add(component);
            }
        }

        if (inputComponents.isEmpty()) {
            return truthTable;
        }

        int numInputs = inputComponents.size();
        int numCombinations = (int) Math.pow(2, numInputs);

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> row = new HashMap<>();
            Map<String, Boolean> inputs = new HashMap<>();

            // Set input values based on binary representation
            for (int j = 0; j < numInputs; j++) {
                boolean value = (i & (1 << (numInputs - 1 - j))) != 0;
                Component inputComp = inputComponents.get(j);
                inputs.put(inputComp.getName(), value);
                row.put("Input_" + inputComp.getName(), value);
            }

            // Simulate with these inputs
            Map<String, Boolean> outputs = simulate(inputs);

            // Add outputs to row
            for (Map.Entry<String, Boolean> output : outputs.entrySet()) {
                row.put("Output_" + output.getKey(), output.getValue());
            }

            truthTable.add(row);
        }

        return truthTable;
    }

    /**
     * Finds a component by name.
     * 
     * @param name The component name
     * @return The component if found, null otherwise
     */
    public Component findComponentByName(String name) {
        return components.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Finds the input index for a connector in a sink component.
     * 
     * @param sink The sink component
     * @param connector The connector
     * @return The input index, or -1 if not found
     */
    private int findInputIndex(Component sink, Connector connector) {
        // This is a simplified implementation
        // In a real system, you might need to track which input port the connector connects to
        List<Component> sources = new ArrayList<>();
        for (Connector c : connectors) {
            if (c.getSink() == sink) {
                sources.add(c.getSource());
            }
        }
        return sources.indexOf(connector.getSource());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Circuit circuit = (Circuit) o;
        return Objects.equals(name, circuit.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Circuit{" +
                "name='" + name + '\'' +
                ", components=" + components.size() +
                ", connectors=" + connectors.size() +
                '}';
    }
}


