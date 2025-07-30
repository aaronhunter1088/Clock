package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Clock;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.io.InputStream;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static clock.panel.Panel.PANEL_TIMER;
import static clock.util.Constants.*;

/**
 * This is the Timer panel.
 * The timer panel is used to set a timer.
 * Giving the panel some Hours, some Minutes,
 * and some Seconds, it will begin counting
 * down. When the timer reaches '00:00:00',
 * it will make a short alarm sound, then turn off.
 *
 * @author michael ball
*  @version 2.8
 */
public class TimerPanel extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(TimerPanel.class);
    private final GridBagLayout layout;
    private final GridBagConstraints constraints;
    private JTextField hourField,
                       minuteField,
                       secondField;
    private JButton timerButton;
    private JButton resetButton;
    private ClockFrame clockFrame;
    private boolean timerGoingOff;
    private volatile boolean paused;
    private AdvancedPlayer musicPlayer;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> countdownFuture;

    /**
     * The main constructor for the Timer Panel
     * @param clockFrame the clockFrame reference
     */
    public TimerPanel(ClockFrame clockFrame) {
        super();
        logger.info("Creating Timer Panel");
        this.clockFrame = clockFrame;
        clockFrame.setClockPanel(PANEL_TIMER);
        setSize(ClockFrame.panelSize);
        layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setBorder(new LineBorder(Color.BLACK));
        setupTimerPanel();
        updateLabels();
        addComponentsToPanel();
        setupMusicPlayer();
        setupSettingsMenu();
        // allows the user to click around the options and if timer values
        // are valid, enables the Set button
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e)
            { enableTimerButton(); }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        logger.info("Finished creating the Timer Panel");
    }

    /**
     * Sets up the timer panel
     */
    void setupTimerPanel()
    {
        logger.info("setup timer panel");
        hourField = new JTextField(HOUR, 4);
        minuteField = new JTextField(MIN, 4);
        secondField = new JTextField(SEC, 4);
        hourField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (HOUR.equals(hourField.getText())) hourField.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (hourField.getText().isBlank() || hourField.getText().isEmpty())
                { hourField.setText(HOUR); }
                if (NumberUtils.isNumber(hourField.getText()))
                {
                    var hour = Integer.parseInt(hourField.getText());
                    if (hour < 24 && hour >= 0)
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    else
                    {
                        timerButton.setText(TIMER_HOUR_ERROR);
                        timerButton.repaint();
                        timerButton.updateUI();
                        hourField.grabFocus();
                    }
                }
                if (!validateSecondTextField())
                {
                    SwingUtilities.invokeLater(() -> minuteField.grabFocus());
                    timerButton.setEnabled(false);
                }
                else if (!validateThirdTextField())
                {
                    secondField.grabFocus();
                    timerButton.setEnabled(false);
                }
                else
                { enableTimerButton(); }
            }
        });
        minuteField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (MIN.equals(minuteField.getText())) minuteField.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (minuteField.getText().isBlank() || minuteField.getText().isEmpty())
                { minuteField.setText(MIN); }
                if (NumberUtils.isNumber(minuteField.getText()))
                {
                    var minute = Integer.parseInt(minuteField.getText());
                    if (minute < 60 && minute >= 0
                        && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText()))
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    if (minute >= 60 || minute < 0)
                    {
                        timerButton.setText(TIMER_MIN_ERROR);
                        timerButton.repaint();
                        timerButton.updateUI();
                        minuteField.grabFocus();
                    }
                }
                if (!validateFirstTextField())
                {
                    hourField.grabFocus();
                    timerButton.setEnabled(false);
                } else {
                    enableTimerButton();
                }
            }
        });
        secondField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (SEC.equals(secondField.getText())) secondField.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (secondField.getText().isBlank() || secondField.getText().isEmpty())
                { secondField.setText(SEC); }
                if (NumberUtils.isNumber(secondField.getText()))
                {
                    var second = Integer.parseInt(secondField.getText());
                    if (second < 60 && second >= 0
                        && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText()))
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    else if (second >= 60 || second < 0)
                    {
                        timerButton.setText(TIMER_SEC_ERROR);
                        timerButton.repaint();
                        timerButton.updateUI();
                        secondField.grabFocus();
                    }
                }
                if (!validateFirstTextField())
                {
                    hourField.grabFocus();
                    timerButton.setEnabled(false);
                }
                else if (!validateSecondTextField())
                {
                    minuteField.grabFocus();
                    timerButton.setEnabled(false);
                }
                else
                { enableTimerButton(); }
            }
        });
        List.of(hourField, minuteField, secondField).forEach(textField -> {
            textField.setSize(new Dimension(50, 50));
            textField.setBorder(new LineBorder(Color.WHITE));
            textField.setHorizontalAlignment(JTextField.CENTER);
        });
        timerButton = new JButton(SET);
        resetButton = new JButton(RESET);
        List.of(timerButton, resetButton).forEach(button -> {
            button.setFont(ClockFrame.font20);
            button.setOpaque(true);
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(Color.WHITE));
        });
        timerButton.addActionListener(this::run);
        resetButton.addActionListener(this::resetTimerPanel);
        timerButton.setEnabled(false);
        setBackground(Color.BLACK);
    }

    /**
     * Enables the timer button if the text fields are valid
     */
    void enableTimerButton()
    {
        logger.debug("enable timer button");
        var allValid = validateFirstTextField() && validateSecondTextField() && validateThirdTextField();
        var allNotZeroes = !ZERO.equals(hourField.getText()) && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText());
        logger.debug("enabled?: {}", allValid && allNotZeroes);
        timerButton.setEnabled(allValid && allNotZeroes);
    }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    boolean validateFirstTextField()
    {
        boolean result;
        if (HOUR.equals(hourField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(hourField.getText())) { result = false; }
        // by default, cannot be more than 23 hours or less than 0
        else {
            result = Integer.parseInt(hourField.getText()) < 24 &&
                     Integer.parseInt(hourField.getText()) >= 0;
        }
        logger.info("validate first text field result: {}", result);
        return result;
    }

    /**
     * Validates the second text field
     * @return true if the text field is valid
     */
    boolean validateSecondTextField()
    {
        boolean result;
        if (MIN.equals(minuteField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(minuteField.getText())) { result = false; }
        else {
            result = Integer.parseInt(minuteField.getText()) < 60 &&
                     Integer.parseInt(minuteField.getText()) >= 0;
        }
        logger.info("validate second text field result: {}", result);
        return result;
    }

    /**
     * Validates the third text field
     * @return true if the text field is valid
     */
    boolean validateThirdTextField()
    {
        boolean result;
        if (SEC.equals(secondField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(secondField.getText())) { result = false; }
        else {
            result = Integer.parseInt(secondField.getText()) < 60 &&
                     Integer.parseInt(secondField.getText()) >= 0;
        }
        logger.info("validate third text field result: {}", result);
        return result;
    }

    /**
     * Starts the timer
     */
    public void startTimer()
    {
        logger.info("starting timer");
        timerButton.setText(PAUSE_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        resetButton.setEnabled(true);
        paused = false;
        if (scheduler == null || scheduler.isShutdown())
        { scheduler = Executors.newScheduledThreadPool(1); }
        if (countdownFuture == null || countdownFuture.isCancelled())
        { countdownFuture = scheduler.scheduleAtFixedRate(this::performCountDown, 0, 1, TimeUnit.SECONDS); }
    }

    /**
     * Stops a timer. Currently only the Timer can call this
     */
    public void stopTimer()
    {
        logger.info("stopping timer");
        if (countdownFuture != null && !countdownFuture.isCancelled())
        { countdownFuture.cancel(true); }
        if (scheduler != null && !scheduler.isShutdown())
        { scheduler.shutdown(); }
        enableTimerButton();
    }

    /**
     * Pauses the timer
     */
    public void pauseTimer()
    {
        logger.info("pausing timer");
        timerButton.setText(RESUME_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        paused = true;
    }

    /**
     * Resumes an active timer
     */
    public void resumeTimer()
    {
        logger.info("resuming timer");
        timerButton.setText(PAUSE_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        paused = false;
    }

    /**
     * This method performs the countdown for the timer
     */
    public void performCountDown()
    {
        if (paused) return;
        else
        {
            logger.info("performing countdown");
            try
            {
                if (HOUR.equals(hourField.getText())) hourField.setText(ZERO);
                if (MIN.equals(minuteField.getText())) minuteField.setText(ZERO);
                if (SEC.equals(secondField.getText())) secondField.setText(ZERO);

                var hour = Integer.parseInt(hourField.getText());
                var minute = Integer.parseInt(minuteField.getText());
                var second = Integer.parseInt(secondField.getText());
                if (second > 0 || minute > 0 || hour > 0 )
                {
                    if (second >= 0)
                    {
                        second -= 1;
                        secondField.setText(Integer.toString(second));
                        // check hours and minutes, to see if they now need to be decreased
                        if (second < 0 && minute >= 0)
                        {
                            secondField.setText(Integer.toString(59));
                            minute -= 1;
                            minuteField.setText(Integer.toString(minute));
                            if (minute < 0) //  && hour > 0
                            {
                                minuteField.setText(Integer.toString(59));
                                hour -= 1;
                                hourField.setText(Integer.toString(hour));
                            }
                        }
                    }
                    logger.debug("hour: {} min: {} sec: {}", hourField.getText(), minuteField.getText(), secondField.getText());
                    sleep(1000);
                }
                else if (ZERO.equals(secondField.getText()) &&
                         ZERO.equals(minuteField.getText()) &&
                         ZERO.equals(hourField.getText()) && !paused )
                {
                    timerButton.setText(COMPLETE);
                    timerButton.setEnabled(false);
                    clockFrame.getClock().setTimerActive(true);
                }
            }
            catch (Exception e)
            { printStackTrace(e, null); }
        }
    }

    /**
     * Resets the timer panel
     * @param action the action event
     */
    public void resetTimerPanel(ActionEvent action)
    {
        logger.info("reset timer fields");
        hourField.setText(HOUR);
        minuteField.setText(MIN);
        secondField.setText(SEC);
        timerButton.setText(SET);
        resetButton.setEnabled(false);
        stopTimer();
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

    /**
     * This method adds the components to the timer panel
     */
    public void addComponentsToPanel()
    {
        logger.info("add components to panel");
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addComponent(hourField, 0,0,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 1
        addComponent(minuteField, 0,1,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 2
        addComponent(secondField, 0,2,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 3
        addComponent(resetButton, 1, 0, 3, 1, 0, 0, GridBagConstraints.HORIZONTAL); // Reset Button
        addComponent(timerButton, 2,0,3,1,0,0, GridBagConstraints.HORIZONTAL); // Set Timer Button
    }

    /**
     * The main method used for adding components
     * to the timer panel
     * @param cpt       the component to add
     * @param gridy     the y position
     * @param gridx     the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param fill      the fill
     */
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill)
    {
        logger.debug("add component");
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = (int)Math.ceil(gwidth);
        constraints.gridheight = (int)Math.ceil(gheight);
        constraints.fill = fill;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.weightx = 0;
        constraints.weighty = 0;
        constraints.insets = new Insets(0,0,0,0);
        layout.setConstraints(cpt, constraints);
        add(cpt);
    }

    /**
     * Checks if the timer has concluded
     */
    public void checkIfTimerHasConcluded()
    {
        logger.info("check if timer has concluded");
        ExecutorService executor = Executors.newCachedThreadPool();
        if (clockFrame.getClock().isTimerActive())
        {
            triggerSound(executor);
            clockFrame.getClock().setTimerActive(false);
        }
    }

    /**
     * Triggers the sound for the timer
     * @param executor the executor service
     */
    public void triggerSound(ExecutorService executor)
    {
        logger.info("triggering timer sound");
        timerGoingOff = true;
        CountDownLatch latch = new CountDownLatch(1);
        // play sound
        Callable<String> c = () -> {
            try
            {
                if (null == musicPlayer) setupMusicPlayer();
                musicPlayer.play(50);
                stopSound();
                return "Timer triggered";
            }
            catch (Exception e)
            {
                logger.error(e.getCause().getClass().getName()+COLON+SPACE+e.getMessage());
                return "Reset music player required";
            }
            finally
            {
                latch.countDown();
            }
        };
        executor.submit(c);
        try
        {
            latch.await();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stops the sound from playing
     */
    public void stopSound()
    {
        logger.info("stop sound");
        musicPlayer = null;
        timerGoingOff = false;
        stopTimer();
        logger.info("timer turned off");
    }

    /**
     * Places focus on textField1
     */
    public void updateLabels()
    {
        logger.info("update textField1");
        hourField.grabFocus();
        repaint();
    }

    /**
     * Executes when we hit the timer button
     * @param action the action event
     */
    public void run(ActionEvent action)
    {
        logger.info("run");
        if (SET.equals(timerButton.getText()))
        { startTimer(); }
        else if (RESUME_TIMER.equals(timerButton.getText()))
        { resumeTimer(); }
        else
        { pauseTimer(); }
    }

    /**
     * Sets up the music player for the timer panel
     */
    public void setupMusicPlayer()
    {
        logger.info("setup music player");
        InputStream inputStream;
        try
        {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { musicPlayer = new AdvancedPlayer(inputStream); }
            else throw new NullPointerException();
        }
        catch (NullPointerException | JavaLayerException e)
        {
            if (e instanceof NullPointerException)
                printStackTrace(e, "An issue occurred while reading the alarm file.");
            else
                printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
        }
        logger.info("Alarm button set!");
    }

    /**
     * This method sets up the settings menu for the
     * timer panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        logger.info("No settings defined for the Timer Panel");
    }

    /* Getters */
    public JTextField getHourField() { return this.hourField; }
    public JTextField getMinuteField() { return this.minuteField; }
    public JTextField getSecondField() { return this.secondField; }
    public JButton getTimerButton() { return this.timerButton; }
    public JButton getResetButton() { return this.resetButton; }
    public boolean isTimerGoingOff() { return timerGoingOff; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    public boolean isPaused() { return paused; }
    public ScheduledExecutorService getScheduler() { return scheduler; }
    public ScheduledFuture<?> getCountdownFuture() { return countdownFuture; }

    /* Setters */
    protected void setTimerGoingOff(boolean timerGoingOff) { this.timerGoingOff = timerGoingOff; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    public void setClock(Clock clock) { this.clockFrame.setClock(clock); }
}