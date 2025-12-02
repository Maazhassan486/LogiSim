package com.logisim.service;

import com.logisim.domain.Component;
import com.logisim.domain.components.LED;
import com.logisim.domain.components.SubCircuit;
import com.logisim.domain.components.Switch;
import com.logisim.domain.gates.And;
import com.logisim.domain.gates.Nand;
import com.logisim.domain.gates.Nor;
import com.logisim.domain.gates.Not;
import com.logisim.domain.gates.Or;
import com.logisim.domain.gates.Xor;

/**
 * Factory class for creating components.
 * Implements the Factory design pattern.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class ComponentFactory {

    /**
     * Component types that can be created.
     */
    public enum ComponentType {
        AND, OR, NOT, NAND, NOR, XOR, SWITCH, LED, SUBCIRCUIT
    }

    /**
     * Creates a component of the specified type with a default name.
     * 
     * @param type The component type
     * @return A new component instance
     * @throws IllegalArgumentException if the type is invalid
     */
    public static Component createComponent(ComponentType type) {
        return createComponent(type, generateDefaultName(type));
    }

    /**
     * Creates a component of the specified type with a custom name.
     * 
     * @param type The component type
     * @param name The component name
     * @return A new component instance
     * @throws IllegalArgumentException if the type is invalid
     */
    public static Component createComponent(ComponentType type, String name) {
        if (name == null || name.trim().isEmpty()) {
            name = generateDefaultName(type);
        }

        return switch (type) {
            case AND -> new And(name);
            case OR -> new Or(name);
            case NOT -> new Not(name);
            case NAND -> new Nand(name);
            case NOR -> new Nor(name);
            case XOR -> new Xor(name);
            case SWITCH -> new Switch(name);
            case LED -> new LED(name);
            case SUBCIRCUIT -> new SubCircuit(name, null);
            default -> throw new IllegalArgumentException("Unknown component type: " + type);
        };
    }

    /**
     * Generates a default name for a component type.
     * 
     * @param type The component type
     * @return A default name
     */
    private static String generateDefaultName(ComponentType type) {
        return type.name() + "_" + System.currentTimeMillis();
    }

    /**
     * Gets the display name for a component type.
     * 
     * @param type The component type
     * @return The display name
     */
    public static String getDisplayName(ComponentType type) {
        return switch (type) {
            case AND -> "AND Gate";
            case OR -> "OR Gate";
            case NOT -> "NOT Gate";
            case NAND -> "NAND Gate";
            case NOR -> "NOR Gate";
            case XOR -> "XOR Gate";
            case SWITCH -> "Switch";
            case LED -> "LED";
            case SUBCIRCUIT -> "Sub-Circuit";
        };
    }
}

