package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents a NOR logic gate.
 * Output is true only when all inputs are false.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Nor extends Component {

    /**
     * Default constructor with 2 inputs and 1 output.
     */
    public Nor() {
        super("NOR", 2, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Nor(String name) {
        super(name, 2, 1);
    }

    /**
     * Executes the NOR logic.
     * Output is true only if all inputs are false.
     */
    @Override
    public void execute() {
        if (inputs.isEmpty()) {
            outputs.set(0, true);
            return;
        }

        boolean result = false;
        for (Boolean input : inputs) {
            if (input != null && input) {
                result = true;
                break;
            }
        }
        outputs.set(0, !result);
    }
}



