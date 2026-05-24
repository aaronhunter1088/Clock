# Clock – Agent Guide

Java 25 + Swing clock app, built with Apache Maven. Entry point: `clock.Main`. Output JARs land in `dist/version#/`.

---

## Build & Run Commands

```bash
# Compile and run tests
mvn clean package

# Run tests only
mvn test

# Install to local repo (skip tests)
mvn install -DskipTests

# Build fat JAR without running tests
mvn clean package -Dmaven.test.skip=true
```

### Running the JAR

```bash
# Default start (digital clock panel)
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar

# Start with specific panel
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar panel_alarm

# Start with specific date/time
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar august 6 2025 10 30 0 pm
```

Set `logLevel` as an environment variable to control log verbosity. If unset, a warning is logged and `all` is used.

---

## Package Structure

```
src/main/java/clock/
  contract/        IClockPanel.java (interfaces)
  entity/          Clock.java, Alarm.java, Timer.java, Stopwatch.java, Lap.java,
                   ClockMenuBar.java, Panel.java (enum)
  exception/       Custom exceptions (InvalidInputException.java)
  panel/           ClockFrame.java, ClockPanel.java (abstract base),
                   DigitalClockPanel.java, AnalogueClockPanel.java,
                   AlarmPanel.java, TimerPanel.java, StopwatchPanel.java,
                   DisplayLapsPanel.java, DisplayTimePanel.java, ButtonColumn.java
  util/            Constants.java
  examples/        Example code
  Main.java        ← entry point

src/test/java/clock/
  entity/          ClockTest.java, AlarmTest.java, TimerTest.java, StopwatchTest.java,
                   LapTest.java, ClockMenuBarTest.java
  exception/       InvalidInputExceptionTest.java
  panel/           DigitalClockPanelTest.java, AnalogueClockPanelTest.java,
                   AlarmPanelTest.java, TimerPanelTest.java, StopwatchPanelTest.java,
                   ClockFrameTest.java, ClockPanelTest.java, PanelTypeTest.java
```

All packages use **lowercase names** (e.g., `clock.entity`, `clock.panel`, `clock.contract`).

---

## Key Types & Entities

| Symbol | Source | Notes |
|---|---|---|
| `Clock` | `clock.entity.Clock` | Core clock entity managing date/time |
| `Alarm` | `clock.entity.Alarm` | Alarm entity with scheduling |
| `Timer` | `clock.entity.Timer` | Countdown timer entity |
| `Stopwatch` | `clock.entity.Stopwatch` | Stopwatch entity with lap tracking |
| `Lap` | `clock.entity.Lap` | Single lap record within a Stopwatch; stores duration and lap time |
| `Panel` | `clock.entity.Panel` | Enum identifying which panel is active (`PANEL_DIGITAL_CLOCK`, etc.) |
| `ClockMenuBar` | `clock.entity.ClockMenuBar` | `JMenuBar` subclass managing the Settings and Features menus |
| `ClockFrame` | `clock.panel.ClockFrame` | Main `JFrame`; owns all panels and drives the scheduled repaint loop |
| `ClockPanel` | `clock.panel.ClockPanel` | Abstract `JPanel` base class implementing `IClockPanel`; all panels extend this |
| `IClockPanel` | `clock.contract.IClockPanel` | Interface for clock UI panels |
| `InvalidInputException` | `clock.exception.InvalidInputException` | Custom exception for invalid inputs |

Never hard-code button labels or UI strings — use appropriate constants or methods from entity classes where applicable.


---

## Architecture: Clock Application State

The application uses a modular architecture with separate entities for Clock, Alarms, Timers, and Stopwatches. Each panel (`DigitalClockPanel`, `AnalogueClockPanel`, `AlarmPanel`, `TimerPanel`, `StopwatchPanel`) extends the abstract `ClockPanel` base class and is instantiated once inside `ClockFrame`.

### Core Entities

- **Clock**: Manages current date/time, timezone, and display settings (military/standard, full/partial date)
- **Alarm**: Manages alarm scheduling with day-specific rules, pause/resume, and snooze (7 minutes)
- **Timer**: Manages countdown timer state with pause/resume functionality
- **Stopwatch**: Manages stopwatch with lap tracking and display modes
- **Lap**: Immutable-ish record of a single stopwatch interval; `getFormattedDuration()` / `getFormattedLapTime()` return `mm:ss.SSS`
- **Panel** (enum): Identifies the active panel; passed to `ClockFrame` to switch views
- **ClockMenuBar**: Owns Settings and Features menus; reads `ClockFrame` reference to switch panels

Each concrete panel extends `ClockPanel` (abstract), which provides the shared `displayPopupMessage()` helper.

---

## Logging Conventions

```java
// Standard logger declaration (instance context)
private final Logger logger = LogManager.getLogger(MyClass.class.getSimpleName());

// Static context (e.g. in Clock entity or panel classes)
private static final Logger logger = LogManager.getLogger(MyClass.class.getSimpleName());
```

> **Note:** Logger field name is lowercase `logger` throughout the codebase — not `LOGGER`.

Use Log4j2 for logging consistently across the codebase.

---

## UI Conventions

- All UI is hand-built Swing — no GUI designer, no JavaFX.
- `MetalLookAndFeel` is the default look preference.
- Panels should implement `IClockPanel` interface when appropriate.
- Use `SwingUtilities.invokeLater()` for GUI updates to ensure thread safety.
- Keyboard shortcuts follow Ctrl+X format (e.g., Ctrl+D for Digital, Ctrl+A for Alarm, Ctrl+C for Analogue).
- Panel switching is managed through the menu bar and keyboard shortcuts.
- Display components should handle timezone and date/time formatting consistently.
- Snooze functionality is standardized at 7 minutes across panels.

---

## Testing

### Infrastructure

All test classes:
1. Use JUnit 5 for testing framework
2. Use Mockito for mocking dependencies
3. Test class names should match source class name + "Test" suffix (e.g., `ClockTest`, `AlarmTest`)
4. Tests should be in the same package structure as source files
5. Declare a `static Logger LOGGER` in test classes
6. Set up mocks properly with `@BeforeAll` / `@BeforeEach` as needed

```java
@BeforeAll
static void beforeAll() {
    mocks = MockitoAnnotations.openMocks(MyTest.class);
}

@AfterEach
void afterEach() {
    // Clean up if necessary
}
```

### Writing Tests

- Test entity behavior thoroughly (getters, setters, state changes)
- Mock external dependencies like system time, timezones
- Test panel integration with entities
- Some GUI tests may require manual interaction (closing popups)
- Use parameterized tests when testing multiple scenarios with same logic

### Test Execution

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClockTest

# Run specific test method
mvn test -Dtest=ClockTest#testSpecificMethod
```

---

## Dependencies

Managed in the parent POM (`com.skvllprodvctions:parent:1.0.0`). Do **not** add version tags in `Clock/pom.xml`.

| Dependency | Use |
|---|---|
| `org.apache.logging.log4j:log4j-core/api` | Logging |
| `org.apache.commons:commons-lang3` | String utilities |
| `javazoom.jlayer:jlayer` (1.0.1) | Audio playback for alarms/timers |
| `com.google.code.gson:gson` (2.10.1) | JSON serialization for settings/data |
| JUnit Jupiter (api / engine / params) | Testing |
| Mockito (core + junit-jupiter) | Test mocking |

## Common Tasks

### Adding a New Panel
1. Create class in `src/main/java/clock/panel/`
2. Implement `IClockPanel` interface
3. Add panel to menu bar
4. Add keyboard shortcut if appropriate
5. Create corresponding test class

### Adding a New Entity
1. Create entity class in `src/main/java/clock/entity/`
2. Include proper getters/setters
3. Add Javadoc documentation
4. Create corresponding test class
5. Consider equals/hashCode if needed for collections

### Modifying Settings
- Settings are panel-specific and managed in individual panel classes
- Use keyboard shortcuts consistently (Ctrl+X format)
- Ensure settings persist appropriately across panel switches
