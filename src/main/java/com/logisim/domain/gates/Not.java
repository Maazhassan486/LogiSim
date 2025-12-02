package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents a NOT logic gate (inverter).
 * Output is the inverse of the input.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Not extends Component {

    /**
     * Default constructor with 1 input and 1 output.
     */
    public Not() {
        super("NOT", 1, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Not(String name) {
        super(name, 1, 1);
    }

    /**
     * Executes the NOT logic.
     * Output is the inverse of the input.
     */
    @Override
    public void execute() {
        if (inputs.isEmpty()) {
            outputs.set(0, false);
            return;
        }

        Boolean input = inputs.get(0);
        outputs.set(0, input == null || !input);
    }
}



