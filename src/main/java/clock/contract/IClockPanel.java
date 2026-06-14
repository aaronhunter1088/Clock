package clock.contract;

import clock.entity.Clock;

/**
 * A contract for the clock's panels.
 *
 * @author michael ball
*  @version since 2.8
 */
public interface IClockPanel
{
    /**
     * Sets the clock instance for this panel.
     * @param clock the Clock to associate with this panel
     */
    void setClock(Clock clock);

    /** Sets up the settings menu items specific to this panel. */
    void setupSettingsMenu();
}