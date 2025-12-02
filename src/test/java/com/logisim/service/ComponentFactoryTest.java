package com.logisim.service;

import com.logisim.domain.Component;
import com.logisim.domain.components.LED;
import com.logisim.domain.components.Switch;
import com.logisim.domain.gates.And;
import com.logisim.domain.gates.Not;
import com.logisim.domain.gates.Or;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ComponentFactory.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class ComponentFactoryTest {

    @Test
    void testCreateComponent_And_ReturnsAndInstance() {
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.AND);
        assertNotNull(component);
        assertTrue(component instanceof And);
    }

    @Test
    void testCreateComponent_Or_ReturnsOrInstance() {
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.OR);
        assertNotNull(component);
        assertTrue(component instanceof Or);
    }

    @Test
    void testCreateComponent_Not_ReturnsNotInstance() {
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.NOT);
        assertNotNull(component);
        assertTrue(component instanceof Not);
    }

    @Test
    void testCreateComponent_Switch_ReturnsSwitchInstance() {
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.SWITCH);
        assertNotNull(component);
        assertTrue(component instanceof Switch);
    }

    @Test
    void testCreateComponent_LED_ReturnsLEDInstance() {
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.LED);
        assertNotNull(component);
        assertTrue(component instanceof LED);
    }

    @Test
    void testCreateComponent_WithName_UsesProvidedName() {
        String name = "MyComponent";
        Component component = ComponentFactory.createComponent(ComponentFactory.ComponentType.AND, name);
        assertEquals(name, component.getName());
    }
}



