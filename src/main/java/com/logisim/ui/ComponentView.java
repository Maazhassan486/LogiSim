package com.logisim.ui;

import com.logisim.domain.Component;

/**
 * View representation of a component for UI rendering.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class ComponentView {
    private Component component;
    private double x;
    private double y;

    /**
     * Constructor.
     * 
     * @param component The component
     */
    ComponentView(Component component) {
        this.component = component;
        this.x = component.getPosition().getX();
        this.y = component.getPosition().getY();
    }

    Component getComponent() {
        return component;
    }

    double getX() {
        return x;
    }

    double getY() {
        return y;
    }
}



