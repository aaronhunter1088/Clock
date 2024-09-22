package org.example.clock;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;
import static org.example.clock.ClockPanel.PANEL_TIMER;

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
    private JTextField textField1, // Hour
                       textField2, // Min
                       textField3; // Second
    private JButton timerButton;   // Set Timer button
    private JButton resetButton;   // Reset Timer button
    private Clock clock;           // Reference to the clock
//    private Thread countdownThread = new Thread(this::performCountDown);
    private boolean timerIsGoingOff;
    private AdvancedPlayer musicPlayer;

    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> countdownFuture;
    private volatile boolean isPaused = false;

    /**
     * The main constructor for the Timer Panel
     * @param clock the clock reference
     */
    TimerPanel(Clock clock) {
        super();
        this.clock = clock;
        this.clock.setClockPanel(PANEL_TIMER);
        setSize(Clock.panelSize);
        layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();
        setBackground(Color.BLACK); // Color.BLACK
        setForeground(Color.WHITE);
        setBorder(new LineBorder(Color.BLACK));
        setupTimerPanel();
        setupTimerButton();
        setupResetButton();
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
        textField1 = new JTextField(HOUR, 4);
        textField2 = new JTextField(MIN, 4);
        textField3 = new JTextField(SEC, 4);
        textField1.setSize(new Dimension(50, 50));
        textField2.setSize(new Dimension(50, 50));
        textField3.setSize(new Dimension(50, 50));
        textField1.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (HOUR.equals(textField1.getText())) textField1.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (textField1.getText().isBlank() || textField1.getText().isEmpty())
                { textField1.setText(HOUR); }
                if (NumberUtils.isNumber(textField1.getText()))
                {
                    var hour = Integer.parseInt(textField1.getText());
                    if (hour < 24 && hour >= 0
                        && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    else if (hour >= 24 || hour < 0)
                    {
                        timerButton.setText("0 < Hour > 24");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField1.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField()
                    && !ZERO.equals(textField1.getText()) && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                { timerButton.setEnabled(true); }
                else if (validateFirstTextField() && !validateSecondTextField())
                {
                    textField2.grabFocus();
                    timerButton.setEnabled(false);
                }
                else if (validateFirstTextField() && validateSecondTextField() && !validateThirdTextField())
                {
                    textField3.grabFocus();
                    timerButton.setEnabled(false);
                }
            }
        });
        textField2.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (MIN.equals(textField2.getText())) textField2.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (textField2.getText().isBlank() ||
                    textField2.getText().isEmpty())
                { textField2.setText(MIN); }
                if (NumberUtils.isNumber(textField2.getText()))
                {
                    if (Integer.parseInt(textField2.getText()) < 60 &&
                        Integer.parseInt(textField2.getText()) >= 0 &&
                        !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    if (Integer.parseInt(textField2.getText()) >= 60 ||
                        Integer.parseInt(textField2.getText()) < 0)
                    {
                        timerButton.setText("0 < Min > 60");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField2.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField()
                    && !ZERO.equals(textField1.getText()) && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                { timerButton.setEnabled(true); }
                else if (!validateFirstTextField())
                {
                    textField1.grabFocus();
                    timerButton.setEnabled(false);
                }
            }
        });
        textField3.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e)
            { if (SEC.equals(textField3.getText())) textField3.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (textField3.getText().isBlank() || textField3.getText().isEmpty())
                { textField3.setText(SEC); }
                if (NumberUtils.isNumber(textField3.getText()))
                {
                    var second = Integer.parseInt(textField3.getText());
                    if (second < 60 && second >= 0
                        && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    else if (second >= 60 || second < 0)
                    {
                        timerButton.setText("0 < Sec > 60");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField3.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField()
                    && !ZERO.equals(textField1.getText()) && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText()))
                { timerButton.setEnabled(true); }
                else if (!validateFirstTextField())
                {
                    textField1.grabFocus();
                    timerButton.setEnabled(false);
                }
                else if (!validateSecondTextField())
                {
                    textField2.grabFocus();
                    timerButton.setEnabled(false);
                }
            }
        });
        textField1.setBorder(new LineBorder(Color.WHITE));
        textField2.setBorder(new LineBorder(Color.WHITE));
        textField3.setBorder(new LineBorder(Color.WHITE));
        textField1.setHorizontalAlignment(JTextField.CENTER);
        textField2.setHorizontalAlignment(JTextField.CENTER);
        textField3.setHorizontalAlignment(JTextField.CENTER);
        timerButton = new JButton(SET);
        timerButton.setFont(Clock.font20);
        timerButton.setOpaque(true);
        timerButton.setBackground(Color.BLACK);
        timerButton.setForeground(Color.WHITE);
        timerButton.setBorder(new LineBorder(Color.WHITE));
        timerButton.setEnabled(false);
        resetButton = new JButton(RESET) ;
        resetButton.setFont(Clock.font20);
        resetButton.setOpaque(true);
        resetButton.setBackground(Color.BLACK);
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(new LineBorder(Color.WHITE));
        setBackground(Color.BLACK);
    }

    /**
     * Sets up the timer button functionality
     */
    void setupTimerButton()
    { timerButton.addActionListener(this::run); }

    /**
     * Enables the timer button if the text fields are valid
     */
    void enableTimerButton()
    {
        logger.debug("enable timer button");
        var b1 = validateFirstTextField() && validateSecondTextField() && validateThirdTextField();
        var b2 = !ZERO.equals(textField1.getText()) && !ZERO.equals(textField2.getText()) && !ZERO.equals(textField3.getText());
        logger.debug("enabled: {}", b1 && b2);
        timerButton.setEnabled(b1 && b2);
    }

    /**
     * Sets up the reset button functionality
     */
    void setupResetButton()
    { resetButton.addActionListener(this::resetTimerPanel); }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    boolean validateFirstTextField()
    {
        logger.info("validate first text field");
        if (HOUR.equals(textField1.getText())) { return true; }
        if (!NumberUtils.isNumber(textField1.getText())) { return false; }
        // by default, cannot be more than 23 hours or less than 0
        return Integer.parseInt(textField1.getText()) < 24 &&
               Integer.parseInt(textField1.getText()) >= 0;
    }

    /**
     * Validates the second text field
     * @return true if the text field is valid
     */
    boolean validateSecondTextField()
    {
        logger.info("validateSecondTextField");
        if (MIN.equals(textField2.getText())) { return true; }
        if (!NumberUtils.isNumber(textField2.getText())) { return false; }
        return Integer.parseInt(textField2.getText()) < 60 &&
               Integer.parseInt(textField2.getText()) >= 0;
    }

    /**
     * Validates the third text field
     * @return true if the text field is valid
     */
    boolean validateThirdTextField()
    {
        logger.info("validateThirdTextField");
        if (SEC.equals(textField3.getText())) { return true; }
        if (!NumberUtils.isNumber(textField3.getText())) { return false; }
        return Integer.parseInt(textField3.getText()) < 60 &&
               Integer.parseInt(textField3.getText()) >= 0;
    }

    /**
     * Starts the timer
     */
    void startTimer()
    {
        logger.info("starting timer");
        timerButton.setText(PAUSE_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        resetButton.setEnabled(true);
        isPaused = false;
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
        if (countdownFuture != null && !countdownFuture.isCancelled()) {
            countdownFuture.cancel(true);
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
        enableTimerButton();
    }

    /**
     * Pauses the timer
     * @throws InterruptedException if the thread is interrupted
     */
    void pauseTimer() throws InterruptedException
    {
        logger.info("pausing timer");
        timerButton.setText(RESUME_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        isPaused = true;
//        countdownThread.interrupt();
    }

    /**
     * Resumes an active timer
     */
    void resumeTimer()
    {
        logger.info("resuming timer");
        timerButton.setText(PAUSE_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        isPaused = false;
    }

    /**
     * This method performs the countdown for the timer
     */
    void performCountDown()
    {
        if (isPaused) return;
        else
        {
            logger.info("performing countdown");
            try
            {
                if (HOUR.equals(textField1.getText())) textField1.setText(ZERO);
                if (MIN.equals(textField2.getText())) textField2.setText(ZERO);
                if (SEC.equals(textField3.getText())) textField3.setText(ZERO);

                var hour = Integer.parseInt(textField1.getText());
                var minute = Integer.parseInt(textField2.getText());
                var second = Integer.parseInt(textField3.getText());
                if (second > 0 || minute > 0 || hour > 0 )
                {
                    if (second >= 0)
                    {
                        second -= 1;
                        textField3.setText(Integer.toString(second));
                        // check hours and minutes, to see if they now need to be decreased
                        if (second < 0 && minute >= 0)
                        {
                            textField3.setText(Integer.toString(59));
                            minute -= 1;
                            textField2.setText(Integer.toString(minute));
                            if (minute < 0) //  && hour > 0
                            {
                                textField2.setText(Integer.toString(59));
                                hour -= 1;
                                textField1.setText(Integer.toString(hour));
                            }
                        }
                    }
                    logger.debug("hour: {} min: {} sec: {}", textField1.getText(), textField2.getText(), textField3.getText());
                    sleep(1000);
                }
                else if (ZERO.equals(textField3.getText()) &&
                         ZERO.equals(textField2.getText()) &&
                         ZERO.equals(textField1.getText()) )
                {
                    timerButton.setText(COMPLETE);
                    timerButton.setEnabled(false);
                    clock.setTimerActive(true);
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
    void resetTimerPanel(ActionEvent action)
    {
        logger.info("reset timer fields");
        textField1.setText(HOUR);
        textField2.setText(MIN);
        textField3.setText(SEC);
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
    void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill)
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
    void checkIfTimerHasConcluded()
    {
        logger.info("check if timer has concluded");
        ExecutorService executor = Executors.newCachedThreadPool();
        if (clock.isTimerActive())
        {
            triggerSound(executor);
            clock.setTimerActive(false);
        }
    }

    /**
     * Triggers the sound for the timer
     * @param executor the executor service
     */
    void triggerSound(ExecutorService executor)
    {
        logger.info("triggering timer sound");
        timerIsGoingOff = true;
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
        };
        executor.submit(c);
    }

    /**
     * Stops the sound from playing
     */
    void stopSound()
    {
        logger.info("stop sound");
        setMusicPlayer(null);
        setIsTimerGoingOff(false);
        stopTimer();
        logger.info("timer turned off");
    }

    /**
     * This method adds the components to the timer panel
     */
    public void addComponentsToPanel()
    {
        logger.info("add components to panel");
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addComponent(textField1, 0,0,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 1
        addComponent(textField2, 0,1,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 2
        addComponent(textField3, 0,2,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 3
        addComponent(resetButton, 1, 0, 3, 1, 0, 0, GridBagConstraints.HORIZONTAL); // Reset Button
        addComponent(timerButton, 2,0,3,1,0,0, GridBagConstraints.HORIZONTAL); // Set Timer Button
    }

    /**
     * Places focus on textField1
     */
    void updateLabels()
    {
        logger.info("update textField1");
        textField1.grabFocus();
        clock.repaint();
    }

    /**
     * Executes when we hit the timer button
     * @param action the action event
     */
    void run(ActionEvent action)
    {
        logger.info("run");
        try
        {
            if (SET.equals(timerButton.getText()))
            { startTimer(); }
            else if (RESUME_TIMER.equals(timerButton.getText()))
            { resumeTimer(); }
            else
            { pauseTimer(); }
        }
        catch (InterruptedException e)
        { printStackTrace(e, null); }
    }

    /**
     * Sets up the music player for the timer panel
     */
    void setupMusicPlayer()
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
        clock.clearSettingsMenu();
        logger.info("No settings defined for the Timer Panel");
    }

    /* Getters */
    Clock getClock() { return this.clock; }
    JTextField getJTextField1() { return this.textField1; }
    JTextField getJTextField2() { return this.textField2; }
    JTextField getJTextField3() { return this.textField3; }
    JButton getTimerButton() { return this.timerButton; }
    JButton getResetButton() { return this.resetButton; }
    boolean isTimerGoingOff() { return timerIsGoingOff; }
    AdvancedPlayer getMusicPlayer() { return musicPlayer; }

    /* Setters */
    protected void setIsTimerGoingOff(boolean timerIsGoingOff) { this.timerIsGoingOff = timerIsGoingOff; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    public void setClock(Clock clock) { this.clock = clock; }
}