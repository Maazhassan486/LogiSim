package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents an OR logic gate.
 * Output is true when at least one input is true.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Or extends Component {

    /**
     * Default constructor with 2 inputs and 1 output.
     */
    public Or() {
        super("OR", 2, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Or(String name) {
        super(name, 2, 1);
    }

    /**
     * Constructor with name and number of inputs.
     * 
     * @param name The component name
     * @param numInputs Number of inputs (default 2)
     */
    public Or(String name, int numInputs) {
        super(name, numInputs, 1);
    }

    /**
     * Executes the OR logic.
     * Output is true if at least one input is true.
     */
    @Override
    public void execute() {
        if (inputs.isEmpty()) {
            outputs.set(0, false);
            return;
        }

        boolean result = false;
        for (Boolean input : inputs) {
            if (input != null && input) {
                result = true;
                break;
            }
        }
        outputs.set(0, result);
    }
}



