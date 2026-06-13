package clock.entity;

/**
 * Used to distinguish which panel the Clock is using
 *
 * @author michael ball
 * @version since 2.0
 */
public enum Panel
{
	PANEL_DIGITAL_CLOCK("Digital Clock"),
	PANEL_ANALOGUE_CLOCK("Analogue Clock"),
	PANEL_ALARM("Alarms"),
	PANEL_TIMER("Timers"),
	PANEL_STOPWATCH("Stopwatches");

    final String text;

    Panel(String text)
    {
        this.text = text;
    }

    /**
     * Gets the plain panel text to use for this panel choice.
     * @return the panel plain text
     */
    public String getText()
    {
        return text;
    }
}