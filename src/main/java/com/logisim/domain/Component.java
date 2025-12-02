package com.logisim.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Abstract base class for all circuit components.
 * Each component has inputs, outputs, and a position.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public abstract class Component {
    protected String name;
    protected List<Boolean> inputs;
    protected List<Boolean> outputs;
    protected Position position;

    /**
     * Default constructor.
     */
    public Component() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.position = new Position(0, 0);
    }

    /**
     * Constructor with name and number of inputs/outputs.
     * 
     * @param name The component name
     * @param numInputs Number of input ports
     * @param numOutputs Number of output ports
     */
    public Component(String name, int numInputs, int numOutputs) {
        this.name = name;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.position = new Position(0, 0);
        
        // Initialize inputs and outputs to false
        for (int i = 0; i < numInputs; i++) {
            inputs.add(false);
        }
        for (int i = 0; i < numOutputs; i++) {
            outputs.add(false);
        }
    }

    /**
     * Gets the component name.
     * 
     * @return The component name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the component name.
     * 
     * @param name The component name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of input values.
     * 
     * @return List of input values
     */
    public List<Boolean> getInputs() {
        return inputs;
    }

    /**
     * Sets an input value at the specified index.
     * 
     * @param index The input index
     * @param value The input value
     */
    public void setInput(int index, Boolean value) {
        if (index >= 0 && index < inputs.size()) {
            inputs.set(index, value != null ? value : false);
        }
    }

    /**
     * Gets the list of output values.
     * 
     * @return List of output values
     */
    public List<Boolean> getOutputs() {
        return outputs;
    }

    /**
     * Gets an output value at the specified index.
     * 
     * @param index The output index
     * @return The output value
     */
    public Boolean getOutput(int index) {
        if (index >= 0 && index < outputs.size()) {
            return outputs.get(index);
        }
        return false;
    }

    /**
     * Gets the component position.
     * 
     * @return The position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the component position.
     * 
     * @param position The position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Abstract method to execute the component's logic.
     * Each subclass must implement this to define its behavior.
     */
    public abstract void execute();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(name, component.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}



