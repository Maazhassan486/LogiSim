package com.logisim.ui;

import com.logisim.domain.Connector;

/**
 * View representation of a connector for UI rendering.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
class ConnectorView {
    private Connector connector;

    /**
     * Constructor.
     * 
     * @param connector The connector
     */
    ConnectorView(Connector connector) {
        this.connector = connector;
    }

    Connector getConnector() {
        return connector;
    }
}



