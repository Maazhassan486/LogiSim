package com.logisim.domain.gates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Or gate.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class OrTest {
    private Or orGate;

    @BeforeEach
    void setUp() {
        orGate = new Or("TestOR");
    }

    @Test
    void testOrGate_TrueOrTrue_ReturnsTrue() {
        orGate.setInput(0, true);
        orGate.setInput(1, true);
        orGate.execute();
        assertTrue(orGate.getOutput(0));
    }

    @Test
    void testOrGate_TrueOrFalse_ReturnsTrue() {
        orGate.setInput(0, true);
        orGate.setInput(1, false);
        orGate.execute();
        assertTrue(orGate.getOutput(0));
    }

    @Test
    void testOrGate_FalseOrTrue_ReturnsTrue() {
        orGate.setInput(0, false);
        orGate.setInput(1, true);
        orGate.execute();
        assertTrue(orGate.getOutput(0));
    }

    @Test
    void testOrGate_FalseOrFalse_ReturnsFalse() {
        orGate.setInput(0, false);
        orGate.setInput(1, false);
        orGate.execute();
        assertFalse(orGate.getOutput(0));
    }
}



