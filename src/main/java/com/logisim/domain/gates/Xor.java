package com.logisim.domain.gates;

import com.logisim.domain.Component;

/**
 * Represents an XOR (exclusive OR) logic gate.
 * Output is true when exactly one input is true.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Xor extends Component {

    /**
     * Default constructor with 2 inputs and 1 output.
     */
    public Xor() {
        super("XOR", 2, 1);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Xor(String name) {
        super(name, 2, 1);
    }

    /**
     * Executes the XOR logic.
     * Output is true when exactly one input is true.
     */
    @Override
    public void execute() {
        if (inputs.size() < 2) {
            outputs.set(0, false);
            return;
        }

        boolean a = inputs.get(0) != null && inputs.get(0);
        boolean b = inputs.get(1) != null && inputs.get(1);
        outputs.set(0, a ^ b);
    }
}



