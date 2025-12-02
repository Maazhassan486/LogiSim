package com.logisim.domain.gates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for And gate.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class AndTest {
    private And andGate;

    @BeforeEach
    void setUp() {
        andGate = new And("TestAND");
    }

    @Test
    void testAndGate_TrueAndTrue_ReturnsTrue() {
        andGate.setInput(0, true);
        andGate.setInput(1, true);
        andGate.execute();
        assertTrue(andGate.getOutput(0));
    }

    @Test
    void testAndGate_TrueAndFalse_ReturnsFalse() {
        andGate.setInput(0, true);
        andGate.setInput(1, false);
        andGate.execute();
        assertFalse(andGate.getOutput(0));
    }

    @Test
    void testAndGate_FalseAndTrue_ReturnsFalse() {
        andGate.setInput(0, false);
        andGate.setInput(1, true);
        andGate.execute();
        assertFalse(andGate.getOutput(0));
    }

    @Test
    void testAndGate_FalseAndFalse_ReturnsFalse() {
        andGate.setInput(0, false);
        andGate.setInput(1, false);
        andGate.execute();
        assertFalse(andGate.getOutput(0));
    }
}



