package com.logisim.domain.components;

import com.logisim.domain.Circuit;
import com.logisim.domain.Component;
import com.logisim.domain.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a sub-circuit component that encapsulates another circuit.
 * Allows circuits to be used as modules/components in other circuits.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class SubCircuit extends Component {
    private Circuit internalCircuit;
    private Map<String, Integer> inputMapping; // Maps internal input component names to input indices
    private Map<String, Integer> outputMapping; // Maps internal output component names to output indices

    /**
     * Default constructor.
     */
    public SubCircuit() {
        super("SUBCIRCUIT", 0, 0);
        this.inputMapping = new HashMap<>();
        this.outputMapping = new HashMap<>();
    }

    /**
     * Constructor with name and internal circuit.
     * 
     * @param name The component name
     * @param internalCircuit The circuit to encapsulate
     */
    public SubCircuit(String name, Circuit internalCircuit) {
        super(name, 0, 0);
        this.internalCircuit = internalCircuit;
        this.inputMapping = new HashMap<>();
        this.outputMapping = new HashMap<>();
        initializePorts();
    }

    /**
     * Initializes input and output ports based on the internal circuit.
     */
    private void initializePorts() {
        if (internalCircuit == null) {
            return;
        }

        // Find input components (components with no incoming connectors)
        List<com.logisim.domain.Component> inputComponents = internalCircuit.getComponents().stream()
                .filter(comp -> {
                    boolean hasInput = internalCircuit.getConnectors().stream()
                            .anyMatch(conn -> conn.getSink() == comp);
                    return !hasInput && comp.getInputs().size() > 0;
                })
                .toList();

        // Find output components (components with outputs but no outgoing connectors, or LEDs)
        List<com.logisim.domain.Component> outputComponents = internalCircuit.getComponents().stream()
                .filter(comp -> {
                    boolean hasOutput = internalCircuit.getConnectors().stream()
                            .anyMatch(conn -> conn.getSource() == comp);
                    return (comp.getOutputs().size() > 0 && !hasOutput) || comp instanceof LED;
                })
                .toList();

        // Initialize inputs
        int inputIndex = 0;
        for (com.logisim.domain.Component inputComp : inputComponents) {
            if (inputComp instanceof Switch) {
                inputMapping.put(inputComp.getName(), inputIndex);
                inputIndex++;
            }
        }
        
        // Resize inputs list
        while (inputs.size() < inputIndex) {
            inputs.add(false);
        }

        // Initialize outputs
        int outputIndex = 0;
        for (com.logisim.domain.Component outputComp : outputComponents) {
            if (outputComp.getOutputs().size() > 0 || outputComp instanceof LED) {
                outputMapping.put(outputComp.getName(), outputIndex);
                outputIndex++;
            }
        }
        
        // Resize outputs list
        while (outputs.size() < outputIndex) {
            outputs.add(false);
        }
    }

    /**
     * Gets the internal circuit.
     * 
     * @return The internal circuit
     */
    public Circuit getInternalCircuit() {
        return internalCircuit;
    }

    /**
     * Sets the internal circuit and reinitializes ports.
     * 
     * @param internalCircuit The internal circuit
     */
    public void setInternalCircuit(Circuit internalCircuit) {
        this.internalCircuit = internalCircuit;
        initializePorts();
    }

    /**
     * Gets the input mapping.
     * 
     * @return Map of internal component names to input indices
     */
    public Map<String, Integer> getInputMapping() {
        return inputMapping;
    }

    /**
     * Gets the output mapping.
     * 
     * @return Map of internal component names to output indices
     */
    public Map<String, Integer> getOutputMapping() {
        return outputMapping;
    }

    /**
     * Executes the sub-circuit by simulating the internal circuit.
     */
    @Override
    public void execute() {
        if (internalCircuit == null) {
            // Set all outputs to false if no circuit
            for (int i = 0; i < outputs.size(); i++) {
                outputs.set(i, false);
            }
            return;
        }

        // Prepare inputs for internal circuit simulation
        Map<String, Boolean> internalInputs = new HashMap<>();
        for (Map.Entry<String, Integer> entry : inputMapping.entrySet()) {
            String componentName = entry.getKey();
            int inputIndex = entry.getValue();
            if (inputIndex < inputs.size()) {
                Boolean inputValue = inputs.get(inputIndex);
                internalInputs.put(componentName, inputValue != null ? inputValue : false);
            }
        }

        // Simulate the internal circuit
        Map<String, Boolean> internalOutputs = internalCircuit.simulate(internalInputs);

        // Map internal outputs to this component's outputs
        for (Map.Entry<String, Integer> entry : outputMapping.entrySet()) {
            String componentName = entry.getKey();
            int outputIndex = entry.getValue();
            if (outputIndex < outputs.size()) {
                Boolean outputValue = internalOutputs.get(componentName);
                outputs.set(outputIndex, outputValue != null ? outputValue : false);
            }
        }
    }

    @Override
    public String toString() {
        return "SubCircuit{" +
                "name='" + getName() + '\'' +
                ", internalCircuit=" + (internalCircuit != null ? internalCircuit.getName() : "null") +
                ", inputs=" + inputs.size() +
                ", outputs=" + outputs.size() +
                '}';
    }
}



