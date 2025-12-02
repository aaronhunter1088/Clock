package clock.contract;

import clock.entity.Clock;

/**
 * A contract for the clock's panels.
 * <p>
 * @author michael ball
*  @version since 2.8
 */
public interface IClockPanel
{
    void setClock(Clock clock);
    void setupSettingsMenu();
    void printStackTrace(Exception e, String message);
}