package com.logisim.domain.gates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Not gate.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class NotTest {
    private Not notGate;

    @BeforeEach
    void setUp() {
        notGate = new Not("TestNOT");
    }

    @Test
    void testNotGate_True_ReturnsFalse() {
        notGate.setInput(0, true);
        notGate.execute();
        assertFalse(notGate.getOutput(0));
    }

    @Test
    void testNotGate_False_ReturnsTrue() {
        notGate.setInput(0, false);
        notGate.execute();
        assertTrue(notGate.getOutput(0));
    }
}



