package com.logisim.service;

import com.logisim.domain.Circuit;
import com.logisim.domain.Component;
import com.logisim.domain.Connector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing circuits.
 * Handles circuit operations and simulation.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class CircuitService {
    private static final Logger logger = LogManager.getLogger(CircuitService.class);
    private List<SimulationObserver> observers;

    /**
     * Default constructor.
     */
    public CircuitService() {
        this.observers = new ArrayList<>();
    }

    /**
     * Adds a simulation observer.
     * 
     * @param observer The observer to add
     */
    public void addObserver(SimulationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            logger.debug("Added simulation observer: {}", observer.getClass().getSimpleName());
        }
    }

    /**
     * Removes a simulation observer.
     * 
     * @param observer The observer to remove
     */
    public void removeObserver(SimulationObserver observer) {
        observers.remove(observer);
        logger.debug("Removed simulation observer: {}", observer.getClass().getSimpleName());
    }

    /**
     * Notifies all observers of simulation updates.
     * 
     * @param outputs The simulation outputs
     */
    private void notifyObservers(Map<String, Boolean> outputs) {
        for (SimulationObserver observer : observers) {
            try {
                observer.onSimulationUpdate(new HashMap<>(outputs));
            } catch (Exception e) {
                logger.error("Error notifying observer", e);
            }
        }
    }

    /**
     * Runs a simulation on the given circuit with input values.
     * 
     * @param circuit The circuit to simulate
     * @param inputs Map of component names to input values
     * @return Map of component names to output values
     * @throws IllegalArgumentException if circuit is null
     */
    public Map<String, Boolean> runSimulation(Circuit circuit, Map<String, Boolean> inputs) {
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }

        logger.info("Running simulation on circuit: {}", circuit.getName());
        
        try {
            Map<String, Boolean> outputs = circuit.simulate(inputs != null ? inputs : new HashMap<>());
            notifyObservers(outputs);
            logger.info("Simulation completed successfully");
            return outputs;
        } catch (Exception e) {
            logger.error("Error during simulation", e);
            throw new RuntimeException("Simulation failed", e);
        }
    }

    /**
     * Analyzes the circuit and generates a truth table.
     * 
     * @param circuit The circuit to analyze
     * @return List of truth table rows
     * @throws IllegalArgumentException if circuit is null
     */
    public List<Map<String, Boolean>> analyzeCircuit(Circuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }

        logger.info("Analyzing circuit: {}", circuit.getName());
        
        try {
            List<Map<String, Boolean>> truthTable = circuit.analyze();
            logger.info("Analysis completed. Generated {} truth table rows", truthTable.size());
            return truthTable;
        } catch (Exception e) {
            logger.error("Error during circuit analysis", e);
            throw new RuntimeException("Analysis failed", e);
        }
    }

    /**
     * Analyzes the circuit and generates both truth table and boolean expressions.
     * 
     * @param circuit The circuit to analyze
     * @return AnalysisResult containing truth table and boolean expressions
     * @throws IllegalArgumentException if circuit is null
     */
    public AnalysisResult analyzeCircuitWithExpressions(Circuit circuit) {
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }

        logger.info("Analyzing circuit with expressions: {}", circuit.getName());
        
        try {
            List<Map<String, Boolean>> truthTable = analyzeCircuit(circuit);
            
            // Extract input and output variable names
            List<String> inputVariables = new ArrayList<>();
            List<String> outputVariables = new ArrayList<>();
            
            if (!truthTable.isEmpty()) {
                Map<String, Boolean> firstRow = truthTable.get(0);
                for (String key : firstRow.keySet()) {
                    if (key.startsWith("Input_")) {
                        inputVariables.add(key);
                    } else if (key.startsWith("Output_")) {
                        outputVariables.add(key);
                    }
                }
            }

            // Generate boolean expressions
            BooleanExpressionGenerator generator = new BooleanExpressionGenerator();
            Map<String, String> expressions = generator.generateAllExpressions(truthTable, inputVariables);

            AnalysisResult result = new AnalysisResult(truthTable, expressions, inputVariables, outputVariables);
            logger.info("Analysis with expressions completed");
            return result;
        } catch (Exception e) {
            logger.error("Error during circuit analysis with expressions", e);
            throw new RuntimeException("Analysis failed", e);
        }
    }

    /**
     * Result class containing truth table and boolean expressions.
     */
    public static class AnalysisResult {
        private final List<Map<String, Boolean>> truthTable;
        private final Map<String, String> booleanExpressions;
        private final List<String> inputVariables;
        private final List<String> outputVariables;

        public AnalysisResult(List<Map<String, Boolean>> truthTable,
                            Map<String, String> booleanExpressions,
                            List<String> inputVariables,
                            List<String> outputVariables) {
            this.truthTable = truthTable;
            this.booleanExpressions = booleanExpressions;
            this.inputVariables = inputVariables;
            this.outputVariables = outputVariables;
        }

        public List<Map<String, Boolean>> getTruthTable() {
            return truthTable;
        }

        public Map<String, String> getBooleanExpressions() {
            return booleanExpressions;
        }

        public List<String> getInputVariables() {
            return inputVariables;
        }

        public List<String> getOutputVariables() {
            return outputVariables;
        }
    }

    /**
     * Adds a component to a circuit.
     * 
     * @param circuit The circuit
     * @param component The component to add
     * @throws IllegalArgumentException if circuit or component is null
     */
    public void addComponent(Circuit circuit, Component component) {
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }

        circuit.addComponent(component);
        logger.debug("Added component {} to circuit {}", component.getName(), circuit.getName());
    }

    /**
     * Removes a component from a circuit.
     * 
     * @param circuit The circuit
     * @param component The component to remove
     * @return true if removed, false otherwise
     */
    public boolean removeComponent(Circuit circuit, Component component) {
        if (circuit == null || component == null) {
            return false;
        }

        boolean removed = circuit.removeComponent(component);
        if (removed) {
            logger.debug("Removed component {} from circuit {}", component.getName(), circuit.getName());
        }
        return removed;
    }

    /**
     * Adds a connector to a circuit.
     * 
     * @param circuit The circuit
     * @param connector The connector to add
     * @throws IllegalArgumentException if circuit or connector is null
     */
    public void addConnector(Circuit circuit, Connector connector) {
        if (circuit == null) {
            throw new IllegalArgumentException("Circuit cannot be null");
        }
        if (connector == null) {
            throw new IllegalArgumentException("Connector cannot be null");
        }

        circuit.addConnector(connector);
        logger.debug("Added connector to circuit {}", circuit.getName());
    }

    /**
     * Removes a connector from a circuit.
     * 
     * @param circuit The circuit
     * @param connector The connector to remove
     * @return true if removed, false otherwise
     */
    public boolean removeConnector(Circuit circuit, Connector connector) {
        if (circuit == null || connector == null) {
            return false;
        }

        boolean removed = circuit.removeConnector(connector);
        if (removed) {
            logger.debug("Removed connector from circuit {}", circuit.getName());
        }
        return removed;
    }
}

