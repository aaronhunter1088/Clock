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
    private Thread countdownThread = new Thread(this::performCountDown);
    //private ScheduledExecutorService countdownScheduler;
    private boolean timerIsGoingOff;
    private AdvancedPlayer musicPlayer;

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
            public void mouseClicked(MouseEvent e) {
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
                    timerButton.setEnabled(true);
            }

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
            { textField1.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(textField1.getText()) ||
                    StringUtils.isEmpty(textField1.getText()))
                { textField1.setText(HOUR); }
                if (NumberUtils.isNumber(textField1.getText()))
                {
                    if (Integer.parseInt(textField1.getText()) < 24 &&
                        Integer.parseInt(textField1.getText()) >= 0)
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    if (Integer.parseInt(textField1.getText()) >= 24 ||
                        Integer.parseInt(textField1.getText()) < 0)
                    {
                        timerButton.setText("Hour between 0 and 24");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField1.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
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
            { textField2.setText(EMPTY); }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(textField2.getText()) ||
                    StringUtils.isEmpty(textField2.getText()))
                { textField2.setText(MIN); }
                if (NumberUtils.isNumber(textField2.getText()))
                {
                    if (Integer.parseInt(textField2.getText()) < 60 &&
                        Integer.parseInt(textField2.getText()) >= 0)
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    if (Integer.parseInt(textField2.getText()) >= 60 ||
                        Integer.parseInt(textField2.getText()) < 0)
                    {
                        timerButton.setText("Min between 0 and 60");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField2.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
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
            {
                textField3.setText(EMPTY);
                if (validateThirdTextField()) timerButton.setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e)
            {
                if (StringUtils.isBlank(textField3.getText()) ||
                    StringUtils.isEmpty(textField3.getText()))
                { textField3.setText(SEC); }
                if (NumberUtils.isNumber(textField3.getText()))
                {
                    if (Integer.parseInt(textField3.getText()) < 60 &&
                        Integer.parseInt(textField3.getText()) >= 0)
                    {
                        timerButton.setText(SET);
                        timerButton.repaint();
                        timerButton.updateUI();
                    }
                    if (Integer.parseInt(textField3.getText()) >= 60 ||
                        Integer.parseInt(textField3.getText()) < 0)
                    {
                        timerButton.setText("Sec between 0 and 60");
                        timerButton.repaint();
                        timerButton.updateUI();
                        textField3.grabFocus();
                    }
                }
                if (validateFirstTextField() && validateSecondTextField() && validateThirdTextField())
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
    protected void setupTimerButton()
    { timerButton.addActionListener(this::run); }

    /**
     * Sets up the reset button functionality
     */
    protected void setupResetButton()
    { getResetButton().addActionListener(this::resetTimerFields); }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    boolean validateFirstTextField()
    {
        logger.info("validateFirstTextField");
        if (StringUtils.equals(textField1.getText(), "Hour")) { return true; }
        if (!NumberUtils.isNumber(textField1.getText())) { return false; }
        // cannot be more than 23 hours or less than 0
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
        if (StringUtils.equals(textField2.getText(), "Min")) { return true; }
        if (!NumberUtils.isNumber(textField2.getText())) { return false; }
        // 70 minutes given
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
        if (StringUtils.equals(textField3.getText(), "Sec")) { return true;}
        if (!NumberUtils.isNumber(textField3.getText())) { return false; }
        // 70 seconds given
        return Integer.parseInt(textField3.getText()) < 60 &&
                Integer.parseInt(textField3.getText()) >= 0;
    }

    /**
     * Starts the timer
     */
    void startTimer()
    {
        logger.info("startTimer");
        timerButton.setText(PAUSE_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
//        if (countdownScheduler == null || countdownScheduler.isShutdown()) {
//            clock.setTimerActive(false);
//            countdownScheduler = Executors.newScheduledThreadPool(1);
//            countdownScheduler.scheduleAtFixedRate(this::performCountDown, 0, 1, TimeUnit.SECONDS);
//        }
        //logger.error(countdownThread.getName() + " is in state: " + countdownThread.getState());
        if (countdownThread.getState() == Thread.State.NEW) {
            countdownThread.start(); // calls the run method of this class
            //setTimerHasConcluded(false);
            clock.setTimerActive(false);
        }
        else if (countdownThread.getState() == Thread.State.TERMINATED) {
            countdownThread = new Thread(this::performCountDown);
            countdownThread.start();
        }
        else if (countdownThread.getState() == Thread.State.TIMED_WAITING) {
            countdownThread.notify();
        }
    }

    /**
     * Pauses the timer
     * @throws InterruptedException if the thread is interrupted
     */
    void pauseTimer() throws InterruptedException
    {
        logger.info("pauseTimer");
        timerButton.setText(RESUME_TIMER);
        timerButton.repaint();
        timerButton.updateUI();
        countdownThread.interrupt();
    }

    /**
     * This method performs the countdown for the timer
     */
    void performCountDown()
    {
        logger.info("perform countdown");
        try
        {
            // get total number of seconds to count down
            // reduce the total, keeping hours, minutes, and seconds current
            if (HOUR.equals(textField1.getText())) textField1.setText("0");
            if (MIN.equals(textField2.getText())) textField2.setText("0");
            if (SEC.equals(textField3.getText())) textField3.setText("0");

            while (Integer.parseInt(textField3.getText()) > 0 ||
                    Integer.parseInt(textField2.getText()) > 0 ||
                    Integer.parseInt(textField1.getText()) > 0 )
            {
                if (Integer.parseInt(textField3.getText()) >= 0)
                {
                    int seconds = Integer.parseInt(textField3.getText())-1;
                    textField3.setText(Integer.toString(seconds));
                    // check hours and minutes, to see if they now need to be decreased
                    if (Integer.parseInt(textField3.getText()) < 0 && Integer.parseInt(textField2.getText()) >= 0)
                    {
                        textField3.setText(Integer.toString(59));
                        int minutes = Integer.parseInt(textField2.getText())-1;
                        textField2.setText(Integer.toString(minutes));
                        if (Integer.parseInt(textField2.getText()) < 0 && Integer.parseInt(textField1.getText()) > 0)
                        {
                            textField2.setText(Integer.toString(59));
                            int hours = Integer.parseInt(textField1.getText())-1;
                            textField1.setText(Integer.toString(hours));
                        }
                    }
                }
                sleep(1000);
            }
            // when done counting, change the Set button back to say Set
            timerButton.setText(SET);
            timerButton.setEnabled(false);
            clock.setTimerActive(true);
        }
        catch (Exception e) 
        { printStackTrace(e, null); }
    }

    /**
     * Resets the timer fields
     * @param action the action event
     */
    void resetTimerFields(ActionEvent action)
    {
        logger.info("reset timer fields");
        textField1.setText("Hour");
        textField2.setText("Min");
        textField3.setText("Sec");
        timerButton.setText(SET);
        resetButton.setEnabled(false);
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
        logger.debug("addComponent");
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
            try {
                setupMusicPlayer();
                musicPlayer.play(50);
                stopSound();
                return "Timer triggered";
            }
            catch (Exception e) {
                logger.error(e.getCause().getClass().getName() + ": " + e.getMessage());
                printStackTrace(e, null);
                setupMusicPlayer();
                musicPlayer.play(50);
                stopSound();
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
        logger.info("stopSound");
        setMusicPlayer(null);
        setIsTimerGoingOff(false);
        logger.info("timer turned off");
    }

    /**
     * This method adds the components to the timer panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
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
            if (StringUtils.equals(timerButton.getText(), SET) ||
                StringUtils.equals(timerButton.getText(), RESUME_TIMER))
            {
                logger.info("starting timer");
                startTimer();
            }
            else
            {
                logger.info("pausing timer");
                pauseTimer();
            }
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
        logger.info("No settings set for the Timer Panel");
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