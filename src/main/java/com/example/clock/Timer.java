package com.example.clock;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class Timer
{
    private static final Logger logger = LogManager.getLogger(Timer.class);
    private int hour, minute, second;
    private String hourAsStr, minuteAsStr, secondAsStr;
    private boolean timerGoingOff, paused;
    private Clock clock;
    private AdvancedPlayer musicPlayer;

    /**
     * Creates a new Timer object with default values
     */
    public Timer() throws InvalidInputException
    {
        this(0, 0, 0, false, false, null);
        logger.info("Timer created");
    }

    /**
     * The main constructor for creating a Timer
     */
    public Timer(int hour, int minute, int second, boolean timerGoingOff, boolean paused, Clock clock) throws InvalidInputException
    {
        if (hour < 0 || hour > 12) throw new IllegalArgumentException("Hours must be between 0 and 12");
        else setHour(hour);
        if (minute < 0 || minute > 59) throw new IllegalArgumentException("Minutes must be between 0 and 59");
        else setMinute(minute);
        this.second = second;
        this.timerGoingOff = timerGoingOff;
        this.paused = paused;
        this.clock = clock;
        logger.info("Timer created");
    }

    @Override
    public String toString()
    {
        return hourAsStr+":"+minuteAsStr+":"+secondAsStr;
    }

    /**
     * Defines the music player object
     */
    void setupMusicPlayer()
    {
        logger.info("setup music player");
        InputStream inputStream = null;
        try
        {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { musicPlayer = new AdvancedPlayer(inputStream); }
            else throw new NullPointerException();
        }
        catch (NullPointerException | JavaLayerException e)
        {
            logger.error("Music Player not set!");
            if (null == inputStream) printStackTrace(e, "An issue occurred while reading the alarm file.");
            else printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
        }
    }

    /**
     * Sets an alarm to go off
     * @param executor the executor service
     */
    void triggerTimer(ExecutorService executor)
    {
        logger.info("trigger alarm");
        timerGoingOff = true;
        clock.getDigitalClockPanel().updateLabels();
        //clock.getDigitalClockPanel().getLabel1().setText(activeAlarm.toString());
        //clock.getDigitalClockPanel().getLabel2().setText("is going off!");
        // play sound
        Callable<String> c = () -> {
            try
            {
                setupMusicPlayer();
                logger.debug("while timer is going off, play sound");
                List<Timer> activeTimers = ((TimerPanel2)clock.getCurrentPanel()).getActiveTimers();
                while (!activeTimers.isEmpty())
                { musicPlayer.play(50); }
                logger.debug("alarm has stopped");
                return "Alarm triggered";
            }
            catch (Exception e)
            {
                logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
                printStackTrace(e, e.getMessage());
                setupMusicPlayer();
                musicPlayer.play(50);
                return "Reset music player required";
            }
        };
        executor.submit(c);
    }

    /**
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     * @param message the message to print
     */
    public void printStackTrace(Exception e, String message)
    {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        { logger.error(ste.toString()); }
    }

    /* Getters */
    public Clock getClock() { return clock; }
    public int getHour() { return hour; }
    public String getHourAsStr() { return hourAsStr; }
    public int getMinute() { return minute; }
    public String getMinuteAsStr() { return minuteAsStr; }
    public boolean isPaused() { return paused; }
    public int getSecond() { return second; }
    public String getSecondAsStr() { return secondAsStr; }
    public boolean isTimerGoingOff() { return timerGoingOff; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; }
    public void setHour(int hour) { this.hour = hour; }
    public void setHourAsStr(String hourAsStr) { this.hourAsStr = hourAsStr; }
    public void setMinute(int minute) { this.minute = minute; }
    public void setMinuteAsStr(String minuteAsStr) { this.minuteAsStr = minuteAsStr; }
    public void setPaused(boolean paused) { this.paused = paused; }
    public void setSecond(int second) { this.second = second; }
    public void setSecondAsStr(String secondAsStr) { this.secondAsStr = secondAsStr; }
    public void setTimerGoingOff(boolean timerGoingOff) { this.timerGoingOff = timerGoingOff; }
}
