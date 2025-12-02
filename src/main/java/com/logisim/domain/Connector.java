package com.logisim.domain;

import java.util.Objects;

/**
 * Represents a connector (wire) between components.
 * Connects the output of a source component to the input of a sink component.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Connector {
    private String name;
    private String color;
    private Position position;
    private Component source;
    private Component sink;
    private int sourceOutputIndex;
    private int sinkInputIndex;

    /**
     * Default constructor.
     */
    public Connector() {
        this.color = "#000000"; // Default black
        this.position = new Position(0, 0);
        this.sourceOutputIndex = 0;
        this.sinkInputIndex = 0;
    }

    /**
     * Constructor with source and sink components.
     * 
     * @param source The source component
     * @param sink The sink component
     */
    public Connector(Component source, Component sink) {
        this();
        this.source = source;
        this.sink = sink;
    }

    /**
     * Gets the connector name.
     * 
     * @return The connector name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the connector name.
     * 
     * @param name The connector name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the connector color.
     * 
     * @return The color (hex format)
     */
    public String getColor() {
        return color;
    }

    /**
     * Sets the connector color.
     * 
     * @param color The color (hex format, e.g., "#FF0000")
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the connector position.
     * 
     * @return The position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the connector position.
     * 
     * @param position The position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Gets the source component.
     * 
     * @return The source component
     */
    public Component getSource() {
        return source;
    }

    /**
     * Sets the source component.
     * 
     * @param source The source component
     */
    public void setSource(Component source) {
        this.source = source;
    }

    /**
     * Gets the sink component.
     * 
     * @return The sink component
     */
    public Component getSink() {
        return sink;
    }

    /**
     * Sets the sink component.
     * 
     * @param sink The sink component
     */
    public void setSink(Component sink) {
        this.sink = sink;
    }

    /**
     * Gets the source output index.
     * 
     * @return The output index
     */
    public int getSourceOutputIndex() {
        return sourceOutputIndex;
    }

    /**
     * Sets the source output index.
     * 
     * @param sourceOutputIndex The output index
     */
    public void setSourceOutputIndex(int sourceOutputIndex) {
        this.sourceOutputIndex = sourceOutputIndex;
    }

    /**
     * Gets the sink input index.
     * 
     * @return The input index
     */
    public int getSinkInputIndex() {
        return sinkInputIndex;
    }

    /**
     * Sets the sink input index.
     * 
     * @param sinkInputIndex The input index
     */
    public void setSinkInputIndex(int sinkInputIndex) {
        this.sinkInputIndex = sinkInputIndex;
    }

    /**
     * Processes the connector by transferring the signal from source to sink.
     */
    public void process() {
        if (source != null && sink != null) {
            Boolean outputValue = source.getOutput(sourceOutputIndex);
            if (outputValue != null && sinkInputIndex < sink.getInputs().size()) {
                sink.setInput(sinkInputIndex, outputValue);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connector connector = (Connector) o;
        return Objects.equals(source, connector.source) &&
               Objects.equals(sink, connector.sink) &&
               sourceOutputIndex == connector.sourceOutputIndex &&
               sinkInputIndex == connector.sinkInputIndex;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, sink, sourceOutputIndex, sinkInputIndex);
    }

    @Override
    public String toString() {
        return "Connector{" +
                "name='" + name + '\'' +
                ", source=" + (source != null ? source.getName() : "null") +
                ", sink=" + (sink != null ? sink.getName() : "null") +
                ", color='" + color + '\'' +
                '}';
    }
}



