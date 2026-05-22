---
applyTo: 'src/test/java/**/*.java'
description: 'Java Testing Standards'
---
Apply these instructions when generating test classes.

- Use JUnit 5
- Use AssertThrows for exception testing
- Use @DisplayName to provide a readable name for the test
- Use @ParameterizedTest with @MethodSource for testing multiple scenarios.
  Place the method for the parameterized test directly below the test method for quick access.
- Use the ArgumentsForTests class for setting up calculator scenarios and expected results when applicable.
- Do not use Thread.sleep()
- Mock external dependencies
- Keep tests independent