package org.example.clock;

/**
 * This enum is used to distinguish which panel the
 * main Clock GUI is using, i.e., PanelType or AlarmFace,
 * TimerFace....
 *
 * @author michael ball
 * @version 2.7
 */
public enum PanelType {
	ANALOGUE_CLOCK,
	DIGITAL_CLOCK,
	ALARM,
	TIMER;

	PanelType() {
		
	}
}