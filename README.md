# LogiSim - Logic Circuit Simulator

LogiSim is a Java-based application for designing, simulating, and analyzing digital logic circuits. It provides an intuitive graphical interface for creating circuits using common logic gates and components.

## Features

- **Circuit Design**: Create circuits using a visual canvas with drag-and-drop components
- **Component Library**: Support for common logic gates (AND, OR, NOT, NAND, NOR, XOR) and components (Switches, LEDs)
- **Simulation**: Run simulations to observe circuit behavior with different input values
- **Analysis**: Generate complete truth tables for circuits
- **Project Management**: Save and load projects in JSON format
- **Modular Architecture**: Circuits can be used as modules in other circuits

## Requirements

- **Java**: Oracle OpenJDK 23.0.2 or higher
- **Maven**: 3.6.0 or higher (for building)
- **IntelliJ IDEA**: Recommended IDE (see setup guide below)

## Project Structure

```
SCD_project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/logisim/
│   │   │       ├── domain/          # Domain model layer
│   │   │       │   ├── gates/       # Logic gate implementations
│   │   │       │   └── components/  # Other components (Switch, LED)
│   │   │       ├── service/         # Business logic layer
│   │   │       ├── repository/      # Data persistence layer
│   │   │       └── ui/              # JavaFX UI layer
│   │   └── resources/
│   │       └── com/logisim/ui/      # FXML and CSS files
│   └── test/
│       └── java/                     # Unit tests
├── pom.xml                           # Maven configuration
└── README.md
```

## Architecture

The project follows a **layered architecture** with clear separation of concerns:

1. **Domain Layer**: Core business entities (Project, Circuit, Component, Connector)
2. **Service Layer**: Business logic and operations (CircuitService, ProjectService)
3. **Repository Layer**: Data persistence (ProjectRepository)
4. **UI Layer**: JavaFX-based user interface

### Design Patterns Used

- **Factory Pattern**: `ComponentFactory` for creating components
- **Observer Pattern**: `SimulationObserver` for simulation updates
- **Repository Pattern**: `ProjectRepository` for data access

## Building the Project

### Using Maven Command Line

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package

# Run the application
mvn javafx:run
```

### Using IntelliJ IDEA

See the [IntelliJ Setup Guide](#intellij-setup-guide) below.

## Running the Application

### From Command Line

```bash
mvn javafx:run
```

### From IntelliJ IDEA

1. Open the project in IntelliJ
2. Run the `LogiSimApplication` class

## Usage

### Creating a Circuit

1. Click on a component in the palette (left panel)
2. Click on the canvas to place the component
3. Double-click a component to start a connection
4. Click on another component to complete the connection

### Running a Simulation

1. Add Switch components to provide inputs
2. Connect switches to logic gates
3. Connect gates to LED components to view outputs
4. Toggle switches using the Properties panel
5. Click "Run Simulation" to execute the circuit

### Analyzing a Circuit

1. Design your circuit
2. Click "Analyze" from the menu or toolbar
3. View the generated truth table

### Saving and Loading Projects

- **Save**: File → Save Project (saves as JSON)
- **Load**: File → Open Project (loads from JSON)

## Testing

Run unit tests using:

```bash
mvn test
```

Tests are located in `src/test/java` and follow TDD principles.

## Logging

The application uses Log4j2 for logging. Logs are written to:
- Console (INFO level and above)
- `logs/logisim.log` file (all levels)

Configuration is in `src/main/resources/log4j2.xml`.

## Documentation

All classes include JavaDoc documentation. Generate documentation using:

```bash
mvn javadoc:javadoc
```

Documentation will be generated in `target/site/apidocs/`.

## Troubleshooting

### JavaFX Module Issues

If you encounter JavaFX module errors, ensure:
1. JavaFX dependencies are correctly configured in `pom.xml`
2. You're using Java 11 or higher
3. The `javafx-maven-plugin` is properly configured

### Build Errors

- Ensure Maven is properly installed: `mvn --version`
- Clean and rebuild: `mvn clean install`
- Check Java version: `java -version` (should be 23.0.2 or compatible)

## Future Enhancements

- Image export (PNG/JPEG) for circuit diagrams
- Additional logic gates (XNOR, etc.)
- Sub-circuit/module support
- Boolean expression generation
- More advanced simulation features

## License

This project is developed for educational purposes.

## Authors

LogiSim Team

## Version

1.0.0



