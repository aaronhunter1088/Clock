package clock.contract;

import clock.entity.Clock;

/**
 * An contract for the clock's panels.
 * <p>
 * @author Michael Ball
*  @version 2.8
 */
public interface IClockPanel
{
    void setClock(Clock clock);
    void setupSettingsMenu();
    void printStackTrace(Exception e, String message);
}