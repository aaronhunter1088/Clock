package clock.entity;

/**
 * Used to distinguish which panel the Clock is using
 *
 * @author michael ball
 * @version since 2.0
 */
public enum Panel
{
	/** The digital clock panel. */
	PANEL_DIGITAL_CLOCK("Digital Clock"),
	/** The analogue clock panel. */
	PANEL_ANALOGUE_CLOCK("Analogue Clock"),
	/** The alarms panel. */
	PANEL_ALARM("Alarms"),
	/** The timers panel. */
	PANEL_TIMER("Timers"),
	/** The stopwatches panel. */
	PANEL_STOPWATCH("Stopwatches");

    /** The human-readable panel label text. */
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