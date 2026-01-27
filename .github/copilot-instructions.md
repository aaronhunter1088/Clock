# Copilot Instructions for Clock Application

## Project Overview

This is a Java Swing GUI application that displays date and time in both digital and analogue modes. The application supports multiple features including alarms, timers, and stopwatches.

**Current Version:** 3.0.3  
**Language:** Java 21  
**Build Tool:** Maven  
**Testing Framework:** JUnit 5 and Mockito

## Project Structure

```
src/
├── main/
│   ├── java/clock/
│   │   ├── Main.java              # Application entry point
│   │   ├── contract/              # Interfaces (e.g., IClockPanel)
│   │   ├── entity/                # Core entities (Clock, Alarm, Timer, Stopwatch)
│   │   ├── exception/             # Custom exceptions
│   │   ├── panel/                 # GUI panels for different features
│   │   ├── util/                  # Utility classes
│   │   └── examples/              # Example code
│   └── resources/                 # Resources (sounds, images, config)
└── test/
    └── java/clock/                # JUnit tests mirroring main structure
```

## Build and Test

### Building the Application
```bash
# Build the project (creates jar with dependencies)
mvn clean package

# Skip tests during build
mvn package -Dmaven.test.skip=true

# Install to local repository
mvn install
```

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClockTest

# Note: Some tests have GUI popups that need to be closed manually
```

### Running the Application
```bash
# Default start (digital clock panel)
java -jar dist/{version}/Clock-{version}-jar-with-dependencies.jar

# Start with specific panel
java -jar dist/{version}/Clock-{version}-jar-with-dependencies.jar panel_alarm

# Start with specific date/time
java -jar dist/{version}/Clock-{version}-jar-with-dependencies.jar august 6 2025 10 30 0 pm
```

## Code Style and Conventions

### General Guidelines
- **Java Version:** Java 21
- **Logging:** Use Log4j2 (logger instance per class)
- **Code Style:** Follow existing patterns in the codebase
- **Documentation:** Include Javadoc for public methods and classes
- **Naming Conventions:**
  - Classes: PascalCase (e.g., `ClockFrame`, `AlarmPanel`)
  - Methods: camelCase (e.g., `startClock`, `setAlarm`)
  - Constants: UPPER_SNAKE_CASE (e.g., `DEFAULT_TIMEZONE`)
  - Packages: lowercase (e.g., `clock.entity`, `clock.panel`)

### Swing GUI Conventions
- Panels should implement `IClockPanel` interface when appropriate
- Use `SwingUtilities.invokeLater()` for GUI updates
- Follow existing panel structure for consistency
- Settings and features should be consistent across panels

### Entity Classes
- Entities (Clock, Alarm, Timer, Stopwatch) should include:
  - Proper encapsulation with getters/setters
  - Equals and hashCode methods when needed
  - toString() for debugging
  - Javadoc documentation

### Testing
- Use JUnit 5 for tests
- Use Mockito for mocking dependencies
- Test class names should match source class name + "Test" suffix
- Tests should be in the same package structure as source files
- Some GUI tests may require manual interaction (closing popups)

## Key Features

### Panels
1. **Digital Clock Panel** (Ctrl+D)
   - Military/Standard time toggle
   - Full/Partial date display
   - Timezone selection
   - Daylight Savings Time toggle

2. **Analogue Clock Panel** (Ctrl+C)
   - Analogue clock display with hands
   - Optional digital time display
   - Timezone selection

3. **Alarm Panel** (Ctrl+A)
   - Set multiple alarms
   - Day-specific alarms (weekdays, weekends, specific days)
   - Pause/Resume all alarms
   - Snooze functionality (7 minutes)

4. **Timers Panel** (Ctrl+T)
   - Multiple countdown timers
   - Pause/Resume individual or all timers
   - Reset panel functionality

5. **Stopwatches Panel** (Ctrl+S)
   - Multiple stopwatches
   - Lap tracking
   - Digital/Analogue display modes
   - Reverse lap order

## Dependencies

### Core Dependencies
- **javazoom.jlayer** (1.0.1): Audio playback for alarms/timers
- **Google Gson** (2.10.1): JSON serialization/deserialization
- **Log4j2**: Logging framework
- **Apache Commons Lang3**: Utility functions

### Testing Dependencies
- **JUnit Jupiter**: Testing framework
- **Mockito**: Mocking framework

## Common Tasks

### Adding a New Panel
1. Create a new class in `src/main/java/clock/panel/`
2. Implement `IClockPanel` interface if applicable
3. Add panel to the Features menu in `ClockMenuBar`
4. Add keyboard shortcut if needed
5. Create corresponding test class

### Adding a New Entity
1. Create entity class in `src/main/java/clock/entity/`
2. Include proper getters/setters
3. Add Javadoc documentation
4. Create corresponding test class
5. Consider equals/hashCode if needed for collections

### Modifying Settings
- Settings are panel-specific and managed through `ClockMenuBar`
- Use keyboard shortcuts consistently (Ctrl+X format)
- Ensure settings persist across panel switches if needed

## Environment Variables

- **logLevel**: Set to `DEBUG` or `INFO` for logging verbosity

## Important Notes

- The application uses a parent POM (`com.skvllprodvctions:parent:1.0.0`)
- JAR output directory is `dist/{version}/`
- Some Swing components may require event dispatch thread handling
- Daylight Savings Time is enabled by default
- Default timezone is the user's system timezone

## Error Handling

- Use `InvalidInputException` for invalid user inputs
- Log errors appropriately with Log4j2
- Provide meaningful error messages to users via GUI dialogs
- Invalid command-line arguments should prevent application start

## Contributing Guidelines

When making changes:
1. Maintain backward compatibility where possible
2. Update Javadoc for modified methods
3. Add/update tests for new functionality
4. Follow existing code patterns and structure
5. Test GUI changes manually (some tests require interaction)
6. Update README.md if adding new features or changing behavior
7. Keep the minimal change philosophy - don't refactor unnecessarily
