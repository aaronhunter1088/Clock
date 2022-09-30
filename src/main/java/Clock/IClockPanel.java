package Clock;

/**
 * An interface for the clock's panels. Each panel must
 * update the labels accordingly. This reduces the need
 * to create new labels. The panel also needs to make
 * sure it has updated itself accordingly.
 *
 * @author Michael Ball
 * @version 2.5
 */
public interface IClockPanel {

    //void updateLabels();
    void addComponentsToPanel();
    void printStackTrace(Exception e, String message);
}
