package com.logisim.domain.components;

import com.logisim.domain.Component;

/**
 * Represents an LED (Light Emitting Diode) component.
 * Displays the output value visually.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class LED extends Component {

    /**
     * Default constructor.
     */
    public LED() {
        super("LED", 1, 0);
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public LED(String name) {
        super(name, 1, 0);
    }

    /**
     * Gets whether the LED is on.
     * 
     * @return true if on, false if off
     */
    public boolean isOn() {
        if (inputs.isEmpty()) {
            return false;
        }
        return inputs.get(0) != null && inputs.get(0);
    }

    /**
     * Executes the LED logic.
     * LED has no output, it just displays the input.
     */
    @Override
    public void execute() {
        // LED doesn't produce output, it just displays input
        // This method is called to update the LED state
    }
}



