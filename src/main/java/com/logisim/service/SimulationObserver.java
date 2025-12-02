package com.logisim.service;

import java.util.Map;

/**
 * Observer interface for simulation updates.
 * Implements the Observer design pattern.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public interface SimulationObserver {
    /**
     * Called when simulation results are updated.
     * 
     * @param outputs Map of component names to their output values
     */
    void onSimulationUpdate(Map<String, Boolean> outputs);
}



