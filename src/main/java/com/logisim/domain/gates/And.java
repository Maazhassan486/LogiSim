package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents an AND logic gate.
 * Output is true only when all inputs are true.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class And extends Component {

    /**
     * Default constructor with 2 inputs and 1 output.
     */
    public And() {
        super("AND", 2, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public And(String name) {
        super(name, 2, 1);
    }

    /**
     * Constructor with name and number of inputs.
     * 
     * @param name The component name
     * @param numInputs Number of inputs (default 2)
     */
    public And(String name, int numInputs) {
        super(name, numInputs, 1);
    }

    /**
     * Executes the AND logic.
     * Output is true only if all inputs are true.
     */
    @Override
    public void execute() {
        if (inputs.isEmpty()) {
            outputs.set(0, false);
            return;
        }

        boolean result = true;
        for (Boolean input : inputs) {
            if (input == null || !input) {
                result = false;
                break;
            }
        }
        outputs.set(0, result);
    }
}



