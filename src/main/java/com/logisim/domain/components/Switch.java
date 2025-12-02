package com.logisim.domain.components;

import com.logisim.domain.Component;

/**
 * Represents a switch component that can be toggled on/off.
 * Acts as an input source for circuits.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class Switch extends Component {
    private boolean state;

    /**
     * Default constructor.
     */
    public Switch() {
        super("SWITCH", 0, 1);
        this.state = false;
    }

    /**
     * Constructor with name.
     * 
     * @param name The component name
     */
    public Switch(String name) {
        super(name, 0, 1);
        this.state = false;
    }

    /**
     * Gets the switch state.
     * 
     * @return true if on, false if off
     */
    public boolean getState() {
        return state;
    }

    /**
     * Sets the switch state.
     * 
     * @param state true for on, false for off
     */
    public void setState(boolean state) {
        this.state = state;
        execute();
    }

    /**
     * Toggles the switch state.
     */
    public void toggle() {
        setState(!state);
    }

    /**
     * Executes the switch logic.
     * Output is the current state.
     */
    @Override
    public void execute() {
        outputs.set(0, state);
    }
}



