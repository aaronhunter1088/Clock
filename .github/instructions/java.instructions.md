---
applyTo: 'src/main/java/**/*.java'
description: 'Java Coding Standards'
---
Apply these instructions when editing Java source files.

## General

- Use Java 25 language features where appropriate (`var`, sealed types, pattern matching, etc.)
- Prefer Streams and method references over manual loops when readability improves
- Use `final` for local variables whenever practical
- Avoid wildcard imports for regular imports; only use static wildcard imports for tightly related constants (e.g., `import static clock.util.Constants.*`, `import static java.time.Month.*`)
- Keep methods under 50 lines when possible
- Prefer `Optional` over null returns in new code; existing code may use null checks (`if (null != x)`)

## Logging

Every class declares a logger as the first static field:

```java
private static final Logger logger = LogManager.getLogger(ClassName.class);
```

Use Log4j2 throughout — never `System.out.println` or `e.printStackTrace()`.

All classes that have threaded behaviour or catch exceptions must implement a `printStackTrace` helper that logs via Log4j2:

```java
public void printStackTrace(Exception e, String message) {
    if (message != null) logger.error(message);
    if (e.getMessage() != null) logger.error(e.getMessage());
    for (StackTraceElement ste : e.getStackTrace()) { logger.error(ste.toString()); }
}
```

## Javadoc

- Provide a class-level Javadoc block for every class, including `@author michael ball` and `@version since X.Y`
- Provide a Javadoc block for every public method (including getters that do something non-trivial)
- Simple getters and setters use a single-line `/** Returns/Sets the X */` comment — no `@param`/`@return` tags needed for these

## Constants

Never hard-code UI strings or labels. Reference constants from `clock.util.Constants` using:

```java
import static clock.util.Constants.*;
```

Add new constants to `Constants.java`, alphabetised within their section, before using them.

## Entity Classes

Entities (`Clock`, `Alarm`, `Timer`, `Stopwatch`, `Lap`) follow this structure:

1. `@Serial private static final long serialVersionUID` (required — all entities implement `Serializable`)
2. `private static final Logger logger`
3. `public static long entityCounter = 0L` where the entity tracks how many instances have been created (resets at 100)
4. Fields grouped logically; related primitives declared on one line (e.g., `private int hours, minutes, seconds;`)
5. `volatile Thread selfThread` for entities that run on their own thread
6. Constructor chain delegation (`this(...)`) from convenience constructors to the main constructor
7. Input validation in constructors using `InvalidInputException` (for business rules) or `IllegalArgumentException` (for hard limits)
8. `equals()` using `Objects.equals()` and field comparisons
9. `hashCode()` using `Objects.hash(...)`
10. `toString()` returning a human-readable summary
11. `compareTo()` implementing `Comparable<T>`

## Threaded Entities

Entities that implement `Runnable` follow this pattern:

```java
private volatile Thread selfThread;

public synchronized void startEntity() {
    if (selfThread == null) {
        setSelfThread(new Thread(this));
        selfThread.start();
    }
}

@Override
public void run() {
    while (!selfThread.isInterrupted()) {
        try {
            performWork();
            sleep(1000);
        } catch (InterruptedException e) {
            printStackTrace(e, null);
            Thread.currentThread().interrupt();
        }
    }
}
```

- State-mutating methods (`pause*`, `resume*`, `stop*`) must be `synchronized`
- Methods called from `run()` that access shared state must also be `synchronized` to avoid race conditions with the public synchronized methods

## Getters and Setters

Getters are single-line. Setters are single-line and always log the new value at `debug`:

```java
/** Returns the name */
public String getName() { return name; }

/** Sets the name */
public void setName(String name) { this.name = name; logger.debug("name set to {}", name); }
```

Setters that derive a companion string field (e.g., `hoursAsStr` from `hours`) must update both in the same setter.

## Brace Style

Opening braces go on a **new line** for class and method declarations:

```java
public class MyClass
{
    public void myMethod()
    {
        // body
    }
}
```

Single-statement bodies on the same line are acceptable for getters/setters:
```java
public int getHours() { return hours; }
```

## Swing / GUI

- All GUI updates must use `SwingUtilities.invokeLater()`
- Use `Color.BLACK` / `Color.WHITE` for the standard theme
- Panel constants (sizes, fonts) are `public static final` fields on `ClockFrame`
- Use constants from `Constants` for all menu item labels
- Keyboard shortcuts follow `Ctrl+X` format using `InputEvent.CTRL_DOWN_MASK`

## Error Handling

- Throw `InvalidInputException` (from `clock.exception`) for invalid user-facing input in entities
- Throw `IllegalArgumentException` for hard programming constraints (e.g., value out of possible range)
- Catch `InterruptedException` in threaded loops, log it, then call `Thread.currentThread().interrupt()` and break/return
- Never swallow exceptions silently
