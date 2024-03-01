package org.example.clock;

/**
 * An interface for the clock's panels. Each panel must
 * update the labels accordingly. This reduces the need
 * to create new labels. The panel also needs to make
 * sure it has updated itself accordingly.
 *
 * @author Michael Ball
 * @version 2.6
 */
public interface IClockPanel {
    void addComponentsToPanel();
    void setClock(Clock clock);
    void setPanelType(PanelType typeOfPanel);
    default void printStackTrace(Exception e, String message) {
        System.err.println("Exception: " + e.getClass());
        System.err.println("Message: " + message);
        System.err.println("Cause: " + e.getCause());
    }
}
