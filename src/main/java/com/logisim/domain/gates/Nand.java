package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents a NAND logic gate.
 * Output is false only when all inputs are true.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Nand extends Component {

    /**
     * Default constructor with 2 inputs and 1 output.
     */
    public Nand() {
        super("NAND", 2, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Nand(String name) {
        super(name, 2, 1);
    }

    /**
     * Executes the NAND logic.
     * Output is false only if all inputs are true.
     */
    @Override
    public void execute() {
        if (inputs.isEmpty()) {
            outputs.set(0, true);
            return;
        }

        boolean result = true;
        for (Boolean input : inputs) {
            if (input == null || !input) {
                result = true;
                break;
            }
        }
        outputs.set(0, !result);
    }
}



