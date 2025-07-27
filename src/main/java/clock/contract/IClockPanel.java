package clock.contract;

import clock.entity.Clock;

/**
 * An contract for the clock's panels. Each panel must
 * update the labels accordingly. This reduces the need
 * to create new labels. The panel also needs to make
 * sure it has updated itself accordingly.
 *
 * @author Michael Ball
*  @version 2.8
 */
public interface IClockPanel
{
    @Deprecated(since = "2.9")
    void addComponentsToPanel();
    void setClock(Clock clock);
    void setupSettingsMenu();
    void printStackTrace(Exception e, String message);
}