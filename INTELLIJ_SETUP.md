# IntelliJ IDEA Setup Guide for LogiSim

This guide will help you set up and run the LogiSim project in IntelliJ IDEA.

## Prerequisites

- **IntelliJ IDEA**: Community or Ultimate Edition (2023.1 or later recommended)
- **Java**: Oracle OpenJDK 23.0.2 (already installed on your system)
- **Maven**: Should be bundled with IntelliJ, or install separately

## Step 1: Open the Project

1. Launch IntelliJ IDEA
2. Click **File** → **Open** (or **Open or Import**)
3. Navigate to your project directory: `C:\Users\maazg\Desktop\SCD_project`
4. Select the folder and click **OK**
5. IntelliJ will detect it as a Maven project and start importing

## Step 2: Configure Project SDK

1. Go to **File** → **Project Structure** (or press `Ctrl+Alt+Shift+S`)
2. In the **Project** section:
   - Set **SDK** to your Java 23 installation
   - Set **Language level** to **23 - Record patterns, pattern matching for switch**
3. Click **Apply** and **OK**

## Step 3: Maven Configuration

1. IntelliJ should automatically detect `pom.xml` and import Maven dependencies
2. Wait for Maven to finish downloading dependencies (check the bottom status bar)
3. If Maven import didn't start automatically:
   - Right-click on `pom.xml` → **Maven** → **Reload Project**
   - Or open **View** → **Tool Windows** → **Maven** and click the refresh icon

## Step 4: Configure JavaFX

Since we're using JavaFX, ensure the JavaFX modules are properly configured:

1. Go to **Run** → **Edit Configurations**
2. If there's already a configuration for `LogiSimApplication`, select it
3. If not, click **+** → **Application** to create a new configuration
4. Configure as follows:
   - **Name**: LogiSim
   - **Main class**: `com.logisim.ui.LogiSimApplication`
   - **VM options**: Add the following (if needed):
     ```
     --module-path %PATH_TO_JAVAFX_SDK%\lib --add-modules javafx.controls,javafx.fxml
     ```
     *Note: With JavaFX 21+, this may not be necessary if using the Maven plugin*
   - **Working directory**: `$PROJECT_DIR$`
5. Click **Apply** and **OK**

## Step 5: Set Up Source Directories

IntelliJ should automatically detect source directories, but verify:

1. Go to **File** → **Project Structure** → **Modules**
2. Expand your module
3. Ensure:
   - `src/main/java` is marked as **Sources** (blue)
   - `src/main/resources` is marked as **Resources** (green)
   - `src/test/java` is marked as **Test Sources** (green with test icon)
4. If not, right-click the folder → **Mark Directory as** → appropriate type

## Step 6: Run the Application

### Option 1: Run from Main Class

1. Navigate to `src/main/java/com/logisim/ui/LogiSimApplication.java`
2. Right-click on the class → **Run 'LogiSimApplication'**
   - Or click the green play button next to the class
   - Or press `Shift+F10`

### Option 2: Run Using Maven

1. Open **View** → **Tool Windows** → **Maven**
2. Expand **Plugins** → **javafx** → **javafx:run**
3. Double-click **javafx:run**

### Option 3: Create Run Configuration

1. **Run** → **Edit Configurations**
2. Click **+** → **Application**
3. Configure:
   - **Name**: LogiSim
   - **Main class**: `com.logisim.ui.LogiSimApplication`
   - **Use classpath of module**: Select your module
4. Click **OK** and run

## Step 7: Run Tests

1. Right-click on `src/test/java` → **Run 'All Tests'`
   - Or use **Run** → **Run 'All Tests'**
   - Or press `Ctrl+Shift+F10` on a test class

## Troubleshooting

### Issue: "JavaFX runtime components are missing"

**Solution**: 
- Ensure JavaFX dependencies are in `pom.xml` (they should be)
- Try: **File** → **Invalidate Caches** → **Invalidate and Restart**
- Rebuild project: **Build** → **Rebuild Project**

### Issue: "Module not found: javafx.controls"

**Solution**:
- Check that JavaFX dependencies are downloaded (check Maven tool window)
- Try reloading Maven: Right-click `pom.xml` → **Maven** → **Reload Project**

### Issue: FXML file not found

**Solution**:
- Ensure `src/main/resources` is marked as a Resources folder
- Check that FXML files are in `src/main/resources/com/logisim/ui/`
- Rebuild project

### Issue: Application doesn't start

**Solution**:
1. Check the **Run** tab for error messages
2. Verify Java version: **File** → **Project Structure** → **Project** → **SDK**
3. Check logs in `logs/logisim.log` (if the directory exists)

### Issue: Maven dependencies not downloading

**Solution**:
1. Check internet connection
2. Go to **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Maven**
3. Verify Maven home directory and settings
4. Try: **File** → **Invalidate Caches** → **Invalidate and Restart**

## Recommended IntelliJ Settings

1. **Code Style**: 
   - **File** → **Settings** → **Editor** → **Code Style** → **Java**
   - Import the project's code style if available

2. **Auto Import**:
   - **File** → **Settings** → **Editor** → **General** → **Auto Import**
   - Enable "Add unambiguous imports on the fly"
   - Enable "Optimize imports on the fly"

3. **Show Line Numbers**:
   - **File** → **Settings** → **Editor** → **General** → **Appearance**
   - Enable "Show line numbers"

## Building the Project

### Build from IntelliJ

1. **Build** → **Build Project** (or `Ctrl+F9`)
2. Output will be in `target/classes`

### Create JAR

1. **File** → **Project Structure** → **Artifacts**
2. Click **+** → **JAR** → **From modules with dependencies**
3. Select main class: `com.logisim.ui.LogiSimApplication`
4. Click **OK**
5. **Build** → **Build Artifacts** → **Build**

## Project Structure in IntelliJ

After setup, your project should look like this in the Project view:

```
SCD_project
├── .idea (IntelliJ config - may be hidden)
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.logisim
│   │   │       ├── domain
│   │   │       ├── service
│   │   │       ├── repository
│   │   │       └── ui
│   │   └── resources
│   │       ├── com.logisim.ui
│   │       └── log4j2.xml
│   └── test
│       └── java
│           └── com.logisim
├── target (generated)
├── pom.xml
└── README.md
```

## Quick Start Checklist

- [ ] Project opened in IntelliJ
- [ ] Maven dependencies downloaded (check Maven tool window)
- [ ] Java SDK set to 23
- [ ] Source directories properly marked
- [ ] Application runs successfully
- [ ] Tests run successfully

## Getting Help

If you encounter issues:
1. Check the **Event Log** (bottom right corner)
2. Check the **Run** tab for error messages
3. Review logs in `logs/logisim.log`
4. Verify all steps above were completed

## Next Steps

Once the project is running:
1. Explore the UI and create a simple circuit
2. Run simulations
3. Review the code structure
4. Run unit tests to understand the domain model
5. Experiment with adding new components

Happy coding! 🚀



