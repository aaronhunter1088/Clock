package clock.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to distinguish which panel the Clock is using
 *
 * @author michael ball
 * @version since 2.0
 */
public enum Panel
{
	PANEL_DIGITAL_CLOCK,
	PANEL_ANALOGUE_CLOCK,
	PANEL_ALARM,
	PANEL_TIMER,
	PANEL_STOPWATCH;

	private static final Logger logger = LogManager.getLogger(Panel.class);
}