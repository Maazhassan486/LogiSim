package com.logisim.domain;

import com.logisim.domain.components.Switch;
import com.logisim.domain.gates.And;
import com.logisim.domain.gates.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for Circuit.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class CircuitTest {
    private Circuit circuit;

    @BeforeEach
    void setUp() {
        circuit = new Circuit("TestCircuit");
    }

    @Test
    void testAddComponent_AddsComponent() {
        Component component = new And("TestAND");
        circuit.addComponent(component);
        assertTrue(circuit.getComponents().contains(component));
    }

    @Test
    void testRemoveComponent_RemovesComponent() {
        Component component = new And("TestAND");
        circuit.addComponent(component);
        assertTrue(circuit.removeComponent(component));
        assertFalse(circuit.getComponents().contains(component));
    }

    @Test
    void testAddConnector_AddsConnector() {
        Component source = new Switch("Switch1");
        Component sink = new And("AND1");
        circuit.addComponent(source);
        circuit.addComponent(sink);
        
        Connector connector = new Connector(source, sink);
        circuit.addConnector(connector);
        assertTrue(circuit.getConnectors().contains(connector));
    }

    @Test
    void testSimulate_SimpleCircuit_ReturnsOutputs() {
        Switch sw1 = new Switch("SW1");
        Switch sw2 = new Switch("SW2");
        And andGate = new And("AND1");
        
        circuit.addComponent(sw1);
        circuit.addComponent(sw2);
        circuit.addComponent(andGate);
        
        Connector conn1 = new Connector(sw1, andGate);
        conn1.setSinkInputIndex(0);
        Connector conn2 = new Connector(sw2, andGate);
        conn2.setSinkInputIndex(1);
        
        circuit.addConnector(conn1);
        circuit.addConnector(conn2);
        
        Map<String, Boolean> inputs = new HashMap<>();
        inputs.put("SW1", true);
        inputs.put("SW2", true);
        
        Map<String, Boolean> outputs = circuit.simulate(inputs);
        assertNotNull(outputs);
    }
}



