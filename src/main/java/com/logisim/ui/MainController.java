package com.logisim.ui;

import com.logisim.domain.*;
import com.logisim.repository.ProjectRepository;
import com.logisim.service.*;
import com.logisim.service.ComponentFactory.ComponentType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.util.Optional;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Main controller for the LogiSim application.
 * Handles UI interactions and coordinates between layers.
 * 
 * @author LogiSim Team
 * @version 1.0
 */
public class MainController implements Initializable, SimulationObserver {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    @FXML private Canvas circuitCanvas;
    @FXML private ComboBox<String> circuitComboBox;
    @FXML private VBox componentPalette;
    @FXML private VBox propertiesPanel;
    @FXML private VBox simulationResults;
    @FXML private Label statusLabel;
    @FXML private ScrollPane canvasScrollPane;

    private ProjectService projectService;
    private CircuitService circuitService;
    private ProjectRepository projectRepository;
    private Circuit currentCircuit;
    private Component selectedComponent;
    private ComponentType selectedComponentType;
    private Map<Component, ComponentView> componentViews;
    private Map<Connector, ConnectorView> connectorViews;
    private Component sourceComponentForConnection;
    private boolean isConnectingMode;

    private static final double COMPONENT_WIDTH = 80;
    private static final double COMPONENT_HEIGHT = 60;

    /**
     * Initializes the controller.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.info("Initializing MainController");
        
        projectService = new ProjectService();
        circuitService = new CircuitService();
        projectRepository = new ProjectRepository();
        circuitService.addObserver(this);
        
        componentViews = new HashMap<>();
        connectorViews = new HashMap<>();
        isConnectingMode = false;

        // Create default project and circuit
        try {
            Project project = projectService.createProject("Untitled Project");
            Circuit circuit = projectService.createCircuit("Circuit 1");
            currentCircuit = circuit;
            updateCircuitComboBox();
            drawCircuit();
        } catch (Exception e) {
            logger.error("Error initializing default project", e);
            showError("Error", "Failed to initialize: " + e.getMessage());
        }

        // Set up canvas mouse handlers
        circuitCanvas.setOnMouseClicked(this::handleCanvasClick);
        circuitCanvas.setOnMouseMoved(this::handleCanvasMouseMove);
        circuitCanvas.setOnMouseDragged(this::handleCanvasMouseDrag);
        circuitCanvas.setOnMousePressed(this::handleCanvasMousePressed);
        circuitCanvas.setOnMouseReleased(this::handleCanvasMouseReleased);

        updateStatus("Ready");
    }

    /**
     * Handles adding a component from the palette.
     */
    @FXML
    private void handleAddComponent(javafx.event.ActionEvent event) {
        Button button = (Button) event.getSource();
        String componentTypeStr = (String) button.getUserData();
        
        try {
            ComponentType type = ComponentType.valueOf(componentTypeStr);
            selectedComponentType = type;
            updateStatus("Click on canvas to place " + ComponentFactory.getDisplayName(type));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid component type: {}", componentTypeStr);
            showError("Error", "Invalid component type");
        }
    }

    /**
     * Handles canvas click events.
     */
    private void handleCanvasClick(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        // Check if clicking on existing component
        Component clickedComponent = findComponentAt(x, y);
        
        if (isConnectingMode && sourceComponentForConnection != null) {
            // Complete connection
            if (clickedComponent != null && clickedComponent != sourceComponentForConnection) {
                createConnector(sourceComponentForConnection, clickedComponent);
                isConnectingMode = false;
                sourceComponentForConnection = null;
                updateStatus("Connection created");
            } else {
                isConnectingMode = false;
                sourceComponentForConnection = null;
                updateStatus("Connection cancelled");
            }
        } else if (clickedComponent != null) {
            // Select existing component
            selectedComponent = clickedComponent;
            updatePropertiesPanel();
            if (event.getClickCount() == 2) {
                // Double-click to start connection
                startConnection(clickedComponent);
            }
        } else if (selectedComponentType != null) {
            // Place new component
            placeComponent(selectedComponentType, x, y);
            selectedComponentType = null;
        } else {
            // Deselect
            selectedComponent = null;
            updatePropertiesPanel();
        }

        drawCircuit();
    }

    /**
     * Handles canvas mouse move events.
     */
    private void handleCanvasMouseMove(MouseEvent event) {
        // Could add hover effects here
    }

    /**
     * Handles canvas mouse drag events.
     */
    private void handleCanvasMouseDrag(MouseEvent event) {
        if (selectedComponent != null) {
            double x = event.getX();
            double y = event.getY();
            selectedComponent.getPosition().setX(x - COMPONENT_WIDTH / 2);
            selectedComponent.getPosition().setY(y - COMPONENT_HEIGHT / 2);
            drawCircuit();
        }
    }

    /**
     * Handles canvas mouse press events.
     */
    private void handleCanvasMousePressed(MouseEvent event) {
        Component component = findComponentAt(event.getX(), event.getY());
        selectedComponent = component;
        updatePropertiesPanel();
    }

    /**
     * Handles canvas mouse release events.
     */
    private void handleCanvasMouseReleased(MouseEvent event) {
        // Could add logic here
    }

    /**
     * Places a component on the canvas.
     */
    private void placeComponent(ComponentType type, double x, double y) {
        if (currentCircuit == null) {
            showError("Error", "No circuit selected");
            return;
        }

        try {
            Component component = ComponentFactory.createComponent(type);
            
            // Special handling for SubCircuit
            if (type == ComponentType.SUBCIRCUIT) {
                // Prompt user to select a circuit to use as module
                Project project = projectService.getCurrentProject();
                if (project == null || project.getCircuits().size() < 2) {
                    showError("Error", "Need at least 2 circuits in project to create a sub-circuit module");
                    return;
                }
                
                // Show dialog to select circuit
                ChoiceDialog<Circuit> dialog = new ChoiceDialog<>();
                dialog.setTitle("Select Circuit");
                dialog.setHeaderText("Choose a circuit to use as a module:");
                dialog.setContentText("Circuit:");
                
                ObservableList<Circuit> circuits = FXCollections.observableArrayList(
                    project.getCircuits().stream()
                        .filter(c -> c != currentCircuit) // Exclude current circuit
                        .toList()
                );
                
                if (circuits.isEmpty()) {
                    showError("Error", "No other circuits available to use as module");
                    return;
                }
                
                dialog.getItems().addAll(circuits);
                dialog.setSelectedItem(circuits.get(0));
                
                Optional<Circuit> result = dialog.showAndWait();
                if (result.isPresent()) {
                    com.logisim.domain.components.SubCircuit subCircuit = 
                        (com.logisim.domain.components.SubCircuit) component;
                    subCircuit.setInternalCircuit(result.get());
                    component = subCircuit;
                } else {
                    return; // User cancelled
                }
            }
            
            component.getPosition().setX(x - COMPONENT_WIDTH / 2);
            component.getPosition().setY(y - COMPONENT_HEIGHT / 2);
            
            circuitService.addComponent(currentCircuit, component);
            updateStatus("Component added: " + component.getName());
            logger.info("Placed component {} at ({}, {})", component.getName(), x, y);
        } catch (Exception e) {
            logger.error("Error placing component", e);
            showError("Error", "Failed to place component: " + e.getMessage());
        }
    }

    /**
     * Finds a component at the given coordinates.
     */
    private Component findComponentAt(double x, double y) {
        if (currentCircuit == null) return null;

        for (Component component : currentCircuit.getComponents()) {
            Position pos = component.getPosition();
            if (x >= pos.getX() && x <= pos.getX() + COMPONENT_WIDTH &&
                y >= pos.getY() && y <= pos.getY() + COMPONENT_HEIGHT) {
                return component;
            }
        }
        return null;
    }

    /**
     * Starts a connection from a component.
     */
    private void startConnection(Component source) {
        if (source.getOutputs().isEmpty()) {
            showError("Error", "Component has no outputs");
            return;
        }
        sourceComponentForConnection = source;
        isConnectingMode = true;
        updateStatus("Click on target component to connect");
    }

    /**
     * Creates a connector between two components.
     */
    private void createConnector(Component source, Component sink) {
        if (currentCircuit == null) return;
        if (sink.getInputs().isEmpty()) {
            showError("Error", "Target component has no inputs");
            return;
        }

        try {
            Connector connector = new Connector(source, sink);
            connector.setColor("#0000FF"); // Blue default
            circuitService.addConnector(currentCircuit, connector);
            updateStatus("Connector created");
            logger.info("Created connector from {} to {}", source.getName(), sink.getName());
        } catch (Exception e) {
            logger.error("Error creating connector", e);
            showError("Error", "Failed to create connector: " + e.getMessage());
        }
    }

    /**
     * Draws the circuit on the canvas.
     */
    private void drawCircuit() {
        GraphicsContext gc = circuitCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, circuitCanvas.getWidth(), circuitCanvas.getHeight());

        if (currentCircuit == null) return;

        // Draw grid
        drawGrid(gc);

        // Draw connectors first (behind components)
        for (Connector connector : currentCircuit.getConnectors()) {
            drawConnector(gc, connector);
        }

        // Draw components
        for (Component component : currentCircuit.getComponents()) {
            drawComponent(gc, component);
        }
    }

    /**
     * Draws a grid on the canvas.
     */
    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        
        double spacing = 20;
        for (double x = 0; x < circuitCanvas.getWidth(); x += spacing) {
            gc.strokeLine(x, 0, x, circuitCanvas.getHeight());
        }
        for (double y = 0; y < circuitCanvas.getHeight(); y += spacing) {
            gc.strokeLine(0, y, circuitCanvas.getWidth(), y);
        }
    }

    /**
     * Draws a component on the canvas.
     */
    private void drawComponent(GraphicsContext gc, Component component) {
        Position pos = component.getPosition();
        double x = pos.getX();
        double y = pos.getY();

        // Highlight if selected
        if (component == selectedComponent) {
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(x - 2, y - 2, COMPONENT_WIDTH + 4, COMPONENT_HEIGHT + 4);
        }

        // Draw component rectangle
        gc.setFill(Color.WHITE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.fillRect(x, y, COMPONENT_WIDTH, COMPONENT_HEIGHT);
        gc.strokeRect(x, y, COMPONENT_WIDTH, COMPONENT_HEIGHT);

        // Draw component name
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(10));
        String displayName = component.getClass().getSimpleName();
        gc.fillText(displayName, x + 5, y + 20);
        gc.fillText(component.getName(), x + 5, y + 35);

        // Draw input/output ports
        drawPorts(gc, component);
    }

    /**
     * Draws input/output ports for a component.
     */
    private void drawPorts(GraphicsContext gc, Component component) {
        Position pos = component.getPosition();
        double x = pos.getX();
        double y = pos.getY();

        // Draw input ports (left side)
        int numInputs = component.getInputs().size();
        for (int i = 0; i < numInputs; i++) {
            double portY = y + (i + 1) * (COMPONENT_HEIGHT / (numInputs + 1));
            gc.setFill(Color.RED);
            gc.fillOval(x - 5, portY - 3, 6, 6);
        }

        // Draw output ports (right side)
        int numOutputs = component.getOutputs().size();
        for (int i = 0; i < numOutputs; i++) {
            double portY = y + (i + 1) * (COMPONENT_HEIGHT / (numOutputs + 1));
            gc.setFill(Color.GREEN);
            gc.fillOval(x + COMPONENT_WIDTH - 1, portY - 3, 6, 6);
        }
    }

    /**
     * Draws a connector on the canvas.
     */
    private void drawConnector(GraphicsContext gc, Connector connector) {
        Component source = connector.getSource();
        Component sink = connector.getSink();
        
        if (source == null || sink == null) return;

        Position sourcePos = source.getPosition();
        Position sinkPos = sink.getPosition();

        double sourceX = sourcePos.getX() + COMPONENT_WIDTH;
        double sourceY = sourcePos.getY() + COMPONENT_HEIGHT / 2;
        double sinkX = sinkPos.getX();
        double sinkY = sinkPos.getY() + COMPONENT_HEIGHT / 2;

        // Draw connector line
        try {
            Color color = Color.web(connector.getColor());
            gc.setStroke(color);
        } catch (Exception e) {
            gc.setStroke(Color.BLUE);
        }
        gc.setLineWidth(2);
        gc.strokeLine(sourceX, sourceY, sinkX, sinkY);
    }

    /**
     * Updates the properties panel.
     */
    private void updatePropertiesPanel() {
        propertiesPanel.getChildren().clear();
        
        if (selectedComponent == null) {
            propertiesPanel.getChildren().add(new Label("Select a component to view properties"));
            return;
        }

        Label nameLabel = new Label("Name: " + selectedComponent.getName());
        Label typeLabel = new Label("Type: " + selectedComponent.getClass().getSimpleName());
        Label positionLabel = new Label(String.format("Position: (%.0f, %.0f)", 
            selectedComponent.getPosition().getX(), selectedComponent.getPosition().getY()));
        
        propertiesPanel.getChildren().addAll(nameLabel, typeLabel, positionLabel);

        // Add component-specific properties
        if (selectedComponent instanceof com.logisim.domain.components.Switch) {
            com.logisim.domain.components.Switch sw = (com.logisim.domain.components.Switch) selectedComponent;
            CheckBox stateCheckBox = new CheckBox("State (On/Off)");
            stateCheckBox.setSelected(sw.getState());
            stateCheckBox.setOnAction(e -> {
                sw.setState(stateCheckBox.isSelected());
                drawCircuit();
            });
            propertiesPanel.getChildren().add(stateCheckBox);
        }

        // Add delete button
        Button deleteButton = new Button("Delete Component");
        deleteButton.setOnAction(e -> {
            if (currentCircuit != null) {
                circuitService.removeComponent(currentCircuit, selectedComponent);
                selectedComponent = null;
                updatePropertiesPanel();
                drawCircuit();
                updateStatus("Component deleted");
            }
        });
        propertiesPanel.getChildren().add(deleteButton);

        // Add connect button
        if (selectedComponent.getOutputs().size() > 0) {
            Button connectButton = new Button("Connect");
            connectButton.setOnAction(e -> startConnection(selectedComponent));
            propertiesPanel.getChildren().add(connectButton);
        }
    }

    /**
     * Updates the circuit combo box.
     */
    private void updateCircuitComboBox() {
        Project project = projectService.getCurrentProject();
        if (project == null) return;

        ObservableList<String> circuitNames = FXCollections.observableArrayList();
        for (Circuit circuit : project.getCircuits()) {
            circuitNames.add(circuit.getName());
        }
        circuitComboBox.setItems(circuitNames);
        if (currentCircuit != null) {
            circuitComboBox.setValue(currentCircuit.getName());
        }
    }

    /**
     * Handles circuit selection from combo box.
     */
    @FXML
    private void handleCircuitSelection(javafx.event.ActionEvent event) {
        String circuitName = circuitComboBox.getValue();
        if (circuitName == null) return;

        Project project = projectService.getCurrentProject();
        if (project == null) return;

        Circuit circuit = project.findCircuitByName(circuitName);
        if (circuit != null) {
            currentCircuit = circuit;
            selectedComponent = null;
            updatePropertiesPanel();
            drawCircuit();
            updateStatus("Switched to circuit: " + circuitName);
        }
    }

    /**
     * Handles new project creation.
     */
    @FXML
    private void handleNewProject() {
        TextInputDialog dialog = new TextInputDialog("New Project");
        dialog.setTitle("New Project");
        dialog.setHeaderText("Enter project name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                Project project = projectService.createProject(name);
                Circuit circuit = projectService.createCircuit("Circuit 1");
                currentCircuit = circuit;
                updateCircuitComboBox();
                drawCircuit();
                updateStatus("Created new project: " + name);
            } catch (Exception e) {
                logger.error("Error creating project", e);
                showError("Error", "Failed to create project: " + e.getMessage());
            }
        });
    }

    /**
     * Handles opening a project.
     */
    @FXML
    private void handleOpenProject() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Project");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("LogiSim Projects", "*.json")
        );

        Stage stage = (Stage) circuitCanvas.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);
        
        if (file != null) {
            try {
                Project project = projectRepository.load(file.getAbsolutePath());
                projectService.setCurrentProject(project);
                if (!project.getCircuits().isEmpty()) {
                    currentCircuit = project.getCircuits().get(0);
                }
                updateCircuitComboBox();
                drawCircuit();
                updateStatus("Opened project: " + project.getName());
            } catch (IOException e) {
                logger.error("Error opening project", e);
                showError("Error", "Failed to open project: " + e.getMessage());
            }
        }
    }

    /**
     * Handles saving a project.
     */
    @FXML
    private void handleSaveProject() {
        Project project = projectService.getCurrentProject();
        if (project == null) {
            showError("Error", "No project to save");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("LogiSim Projects", "*.json")
        );

        Stage stage = (Stage) circuitCanvas.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                String filePath = file.getAbsolutePath();
                if (!filePath.endsWith(".json")) {
                    filePath += ".json";
                }
                projectRepository.save(project, filePath);
                updateStatus("Project saved: " + filePath);
            } catch (IOException e) {
                logger.error("Error saving project", e);
                showError("Error", "Failed to save project: " + e.getMessage());
            }
        }
    }

    /**
     * Handles exporting a diagram.
     */
    @FXML
    private void handleExportDiagram() {
        if (currentCircuit == null) {
            showError("Error", "No circuit to export");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Circuit Diagram");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG Image", "*.png"),
            new FileChooser.ExtensionFilter("JPEG Image", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("All Images", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) circuitCanvas.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try {
                // Capture canvas as image
                javafx.scene.image.WritableImage image = circuitCanvas.snapshot(null, null);
                
                String filePath = file.getAbsolutePath();
                projectRepository.export(projectService.getCurrentProject(), filePath, image);
                updateStatus("Diagram exported to: " + filePath);
                showInfo("Success", "Circuit diagram exported successfully!");
            } catch (IOException e) {
                logger.error("Error exporting diagram", e);
                showError("Error", "Failed to export diagram: " + e.getMessage());
            }
        }
    }

    /**
     * Handles creating a new circuit.
     */
    @FXML
    private void handleNewCircuit() {
        TextInputDialog dialog = new TextInputDialog("New Circuit");
        dialog.setTitle("New Circuit");
        dialog.setHeaderText("Enter circuit name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                Circuit circuit = projectService.createCircuit(name);
                currentCircuit = circuit;
                updateCircuitComboBox();
                drawCircuit();
                updateStatus("Created new circuit: " + name);
            } catch (Exception e) {
                logger.error("Error creating circuit", e);
                showError("Error", "Failed to create circuit: " + e.getMessage());
            }
        });
    }

    /**
     * Handles deleting a circuit.
     */
    @FXML
    private void handleDeleteCircuit() {
        if (currentCircuit == null) {
            showError("Error", "No circuit selected");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Circuit");
        alert.setHeaderText("Delete Circuit");
        alert.setContentText("Are you sure you want to delete " + currentCircuit.getName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            projectService.removeCircuit(currentCircuit);
            Project project = projectService.getCurrentProject();
            if (project != null && !project.getCircuits().isEmpty()) {
                currentCircuit = project.getCircuits().get(0);
            } else {
                currentCircuit = null;
            }
            updateCircuitComboBox();
            drawCircuit();
            updateStatus("Circuit deleted");
        }
    }

    /**
     * Handles running a simulation.
     */
    @FXML
    private void handleRunSimulation() {
        if (currentCircuit == null) {
            showError("Error", "No circuit selected");
            return;
        }

        try {
            // Get input values from switches
            Map<String, Boolean> inputs = new HashMap<>();
            for (Component component : currentCircuit.getComponents()) {
                if (component instanceof com.logisim.domain.components.Switch) {
                    com.logisim.domain.components.Switch sw = (com.logisim.domain.components.Switch) component;
                    inputs.put(component.getName(), sw.getState());
                }
            }

            Map<String, Boolean> outputs = circuitService.runSimulation(currentCircuit, inputs);
            updateSimulationResults(outputs);
            drawCircuit(); // Redraw to show updated states
            updateStatus("Simulation completed");
        } catch (Exception e) {
            logger.error("Error running simulation", e);
            showError("Error", "Simulation failed: " + e.getMessage());
        }
    }

    /**
     * Handles analyzing a circuit.
     */
    @FXML
    private void handleAnalyzeCircuit() {
        if (currentCircuit == null) {
            showError("Error", "No circuit selected");
            return;
        }

        try {
            List<Map<String, Boolean>> truthTable = circuitService.analyzeCircuit(currentCircuit);
            showTruthTable(truthTable);
            updateStatus("Analysis completed");
        } catch (Exception e) {
            logger.error("Error analyzing circuit", e);
            showError("Error", "Analysis failed: " + e.getMessage());
        }
    }

    /**
     * Handles clearing the canvas.
     */
    @FXML
    private void handleClearCanvas() {
        if (currentCircuit == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Clear Canvas");
        alert.setHeaderText("Clear Canvas");
        alert.setContentText("Remove all components and connectors?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentCircuit.getComponents().clear();
            currentCircuit.getConnectors().clear();
            selectedComponent = null;
            updatePropertiesPanel();
            drawCircuit();
            updateStatus("Canvas cleared");
        }
    }

    /**
     * Handles exit.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    /**
     * Handles about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About LogiSim");
        alert.setHeaderText("LogiSim - Logic Circuit Simulator");
        alert.setContentText("Version 1.0\n\nA tool for designing and simulating logic circuits.");
        alert.showAndWait();
    }

    /**
     * Updates simulation results display.
     */
    private void updateSimulationResults(Map<String, Boolean> outputs) {
        simulationResults.getChildren().clear();
        
        if (outputs.isEmpty()) {
            simulationResults.getChildren().add(new Label("No outputs"));
            return;
        }

        for (Map.Entry<String, Boolean> entry : outputs.entrySet()) {
            Label label = new Label(entry.getKey() + ": " + (entry.getValue() ? "1" : "0"));
            simulationResults.getChildren().add(label);
        }
    }

    /**
     * Shows truth table in a dialog.
     */
    private void showTruthTable(List<Map<String, Boolean>> truthTable) {
        if (truthTable.isEmpty()) {
            showInfo("Info", "No truth table generated");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Truth Table:\n\n");

        // Header
        Set<String> keys = truthTable.get(0).keySet();
        for (String key : keys) {
            sb.append(String.format("%-15s", key));
        }
        sb.append("\n");
        sb.append("-".repeat(keys.size() * 15)).append("\n");

        // Rows
        for (Map<String, Boolean> row : truthTable) {
            for (String key : keys) {
                sb.append(String.format("%-15s", row.get(key) ? "1" : "0"));
            }
            sb.append("\n");
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(80);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Truth Table");
        alert.setHeaderText("Circuit Analysis");
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(600, 400);
        alert.showAndWait();
    }

    /**
     * Shows truth table with boolean expressions in a dialog.
     */
    private void showTruthTableWithExpressions(CircuitService.AnalysisResult result) {
        List<Map<String, Boolean>> truthTable = result.getTruthTable();
        Map<String, String> expressions = result.getBooleanExpressions();

        if (truthTable.isEmpty()) {
            showInfo("Info", "No truth table generated");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Circuit Analysis\n");
        sb.append("=".repeat(80)).append("\n\n");

        // Boolean Expressions
        if (!expressions.isEmpty()) {
            sb.append("Boolean Expressions:\n");
            sb.append("-".repeat(80)).append("\n");
            for (Map.Entry<String, String> entry : expressions.entrySet()) {
                String outputName = entry.getKey().replace("Output_", "");
                sb.append(String.format("%s = %s\n", outputName, entry.getValue()));
            }
            sb.append("\n");
        }

        // Truth Table
        sb.append("Truth Table:\n");
        sb.append("-".repeat(80)).append("\n");

        // Header
        Set<String> keys = truthTable.get(0).keySet();
        for (String key : keys) {
            String displayKey = key.replace("Input_", "I_").replace("Output_", "O_");
            sb.append(String.format("%-12s", displayKey));
        }
        sb.append("\n");
        sb.append("-".repeat(keys.size() * 12)).append("\n");

        // Rows
        for (Map<String, Boolean> row : truthTable) {
            for (String key : keys) {
                sb.append(String.format("%-12s", row.get(key) ? "1" : "0"));
            }
            sb.append("\n");
        }

        TextArea textArea = new TextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setPrefRowCount(25);
        textArea.setPrefColumnCount(90);
        textArea.setStyle("-fx-font-family: 'Courier New', monospace;");

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Circuit Analysis");
        alert.setHeaderText("Truth Table and Boolean Expressions");
        alert.getDialogPane().setContent(scrollPane);
        alert.getDialogPane().setPrefSize(700, 500);
        alert.showAndWait();
    }

    /**
     * Updates the status label.
     */
    private void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText("Status: " + message));
    }

    /**
     * Shows an error dialog.
     */
    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Shows an info dialog.
     */
    private void showInfo(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Called when simulation results are updated.
     */
    @Override
    public void onSimulationUpdate(Map<String, Boolean> outputs) {
        Platform.runLater(() -> updateSimulationResults(outputs));
    }
}

