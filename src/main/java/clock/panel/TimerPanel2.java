package clock.panel;

import clock.contract.IClockPanel;
import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.Timer;
import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static clock.panel.ClockPanel.*;
import static java.lang.Thread.sleep;
import static clock.util.Constants.*;

// TODO: Replace TimerPanel2 with TimerPanel when done
/**
 * The New Timer Panel
 *
 * This panel will allow you to create multiple
 * timers, and see them executing to the right
 * similar to the Alarm Panel view.
 */
public class TimerPanel2 extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(TimerPanel2.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel alarmLabel4;
    private JTextField hourField,
                       minuteField,
                       secondField;
    private JButton timerButton;
    private JButton resetButton;
    private JButton stopButton;
    private boolean disableTimerFunctionality;
    private AdvancedPlayer musicPlayer;
    private Map<Timer, ScheduledFuture<?>> timersAndFutures;
    //private ScheduledExecutorService scheduler;

    private JTextArea textArea;
    private JScrollPane scrollPane;
    private Clock clock;
    private JPanel setupTimerPanel;
    private List<clock.entity.Timer> activeTimers;

    /**
     * Main constructor for creating the TimerPanel2
     * @param clock the clock object reference
     */
    public TimerPanel2(Clock clock)
    {
        super();
        this.clock = clock;
        this.clock.setClockPanel(PANEL_TIMER2);
        setSize(Clock.panelSize);
        this.layout = new GridBagLayout();
        setLayout(layout);
        this.constraints = new GridBagConstraints();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupTimerPanel2();
        setupMusicPlayer();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating TimerPanel2 Panel");
    }

    /**
     * Adds all the components to the TimerPanel2
     */
    public void setupTimerPanel2()
    {
        logger.info("setup TimerPanel2");
        setupTimerPanel = new JPanel();
        setupTimerPanel.setSize(Clock.panelSize);
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
                else if (!validateFirstTextField())
                {
                    SwingUtilities.invokeLater(() -> hourField.grabFocus());
                    timerButton.setEnabled(false);
                }
                else if (validTextFields())
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
                else if (!validateSecondTextField())
                {
                    SwingUtilities.invokeLater(() -> minuteField.grabFocus());
                    timerButton.setEnabled(false);
                }
                else if (validTextFields())
                { enableTimerButton(); }
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
                else if (!validateThirdTextField())
                {
                    SwingUtilities.invokeLater(() -> secondField.grabFocus());
                    timerButton.setEnabled(false);
                }
                else if (validTextFields())
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
        stopButton = new JButton(STOP);
        List.of(timerButton, resetButton).forEach(button -> {
            button.setFont(Clock.font20);
            button.setOpaque(true);
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(Color.WHITE));
        });
        stopButton.addActionListener(this::stopAllTimers);
        timerButton.addActionListener(this::run);
        timerButton.setEnabled(false);
        resetButton.addActionListener(this::resetTimerPanel);
        resetButton.setEnabled(false);
        setBackground(Color.BLACK);
        alarmLabel4 = new JLabel(CURRENT_ALARMS, SwingConstants.CENTER); // Current Alarms
        alarmLabel4.setFont(Clock.font20); // All Alarms
        alarmLabel4.setForeground(Color.WHITE);
        addComponentsToSetupTimerPanel();

        // setup textarea
        textArea = new JTextArea(2, 2);
        textArea.setText("TextArea");
        textArea.setSize(new Dimension(100, 100));
        textArea.setFont(Clock.font10); // message
        textArea.setVisible(true);
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setWrapStyleWord(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        // setup scrollPane
        scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBar(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setSize(textArea.getSize());

        alarmLabel4 = new JLabel(CURRENT_TIMERS, SwingConstants.CENTER); // Current Timers
        alarmLabel4.setFont(Clock.font20); // All Timers
        alarmLabel4.setForeground(Color.WHITE);

        //scheduler = Executors.newScheduledThreadPool(10);
        timersAndFutures = new HashMap<>();
        activeTimers = new ArrayList<>();
    }

    /**
     * Enables the timer button if the text fields are valid
     */
    public void enableTimerButton()
    {
        logger.debug("enable timer button");
        var allValid = validateFirstTextField() && validateSecondTextField() && validateThirdTextField();
        var allNotZeroes = !ZERO.equals(hourField.getText()) && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText());
        logger.debug("enabled?: {}", allValid && allNotZeroes);
        timerButton.setEnabled(allValid && allNotZeroes);
    }

    /**
     * Executes when we hit the timer button
     * @param action the action event
     */
    public void run(ActionEvent action)
    {
        logger.info("run");
        if (SET.equals(timerButton.getText()))
        {
            clock.entity.Timer timer = createTimer();
            logger.info("timer created: {}", timer);
            startTimer(timer);
            clearTextFields();
            resetButton.setEnabled(true);
            timerButton.setEnabled(false);
        }
        else if (RESUME_TIMER.equals(timerButton.getText()))
        { resumeTimer(); }
        else
        { pauseTimer(); }
    }

    /**
     * Starts the timer
     */
    public void startTimer(clock.entity.Timer timer)
    {
        logger.info("starting countdown");
        ScheduledFuture<?> future = clock.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS);
        timersAndFutures.put(timer, future);
        if (!activeTimers.isEmpty() && clock.getCountdownFuture() == null) {
            future = clock.getScheduler().scheduleAtFixedRate(this::resetJTextArea, 0, 1, TimeUnit.SECONDS);
            clock.setCountdownFuture(future);
        }
        //scheduler = Executors.newScheduledThreadPool(activeTimers.size());
        //scheduler.scheduleAtFixedRate(this::performCountDown, 0, 1, TimeUnit.SECONDS);
        //if (clock.getScheduler() == null || clock.getScheduler().isShutdown())
        //{ scheduler = Executors.newScheduledThreadPool(activeTimers.size()); }
        //if (countdownFuture == null || countdownFuture.isCancelled())
        //{ countdownFuture = scheduler.scheduleAtFixedRate(this::performCountDown, 0, 1, TimeUnit.SECONDS); }
    }

    /**
     * Creates a new Timer
     */
    public clock.entity.Timer createTimer()
    {
        logger.info("creating timer");
        clock.entity.Timer timer = null;
        try
        {
            if (validTextFields()) {
                if (HOUR.equals(hourField.getText())) hourField.setText(ZERO);
                if (MIN.equals(minuteField.getText())) minuteField.setText(ZERO);
                if (SEC.equals(secondField.getText())) secondField.setText(ZERO);
                timer = new clock.entity.Timer(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText()), Integer.parseInt(secondField.getText()), clock);
                activeTimers.add(timer);
                resetJTextArea();
                resetJAlarmLabel4();
            } else {
                logger.error("One of the textfields is not valid");
            }
        }
        catch (InvalidInputException iie)
        {
            logger.error("Invalid input exception: {}", iie.getMessage());
        }
        return timer;
    }

    /**
     * Stops a Timer. Currently only the Timer can call this
     */
    public void stopTimer(clock.entity.Timer timer)
    {
        logger.info("stopping timer");
        if (clock.getCountdownFuture() != null && !clock.getCountdownFuture().isCancelled())
        { clock.getCountdownFuture().cancel(true); }
        timer.setTimerGoingOff(false);
        timer.setStopTimer(true);
        //if (clock.getScheduler() != null && !clock.getScheduler().isShutdown())
        //{ clock.getScheduler().shutdown(); }
        //enableTimerButton();
    }

    public void stopAllTimers(ActionEvent actionEvent)
    {
        activeTimers.stream()
                .parallel()
                .filter(Timer::isHasBeenTriggered)
                .forEach(this::stopTimer);
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
        //paused = true;
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
        //paused = false;
    }

    /**
     * This method performs the countdown for each timer
     * If disableTimerFunctionality is true, then we will
     * not countdown any timers.
     */
    public void performCountDown()
    {
        if (disableTimerFunctionality) return;
        else
        {
            logger.info("performing countdown");
            //ScheduledExecutorService executor = Executors.newScheduledThreadPool(activeTimers.size());
//            activeTimers.stream()
//                    .parallel()
//                    .forEach(Timer::performCountDown);
            //scheduler = Executors.newScheduledThreadPool(activeTimers.size());
            activeTimers.forEach(timer -> clock.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS));

            resetJTextArea(); // leave here
        }
    }

    /**
     * Resets the timer panel
     * @param action the action event
     */
    public void resetTimerPanel(ActionEvent action)
    {
        logger.info("reset timer fields");
        clearTextFields();
        resetButton.setEnabled(false);
        timerButton.setEnabled(false);
    }

    /**
     * Sets the textFields to their beginning state
     */
    public void clearTextFields()
    {
        hourField.setText(HOUR);
        minuteField.setText(MIN);
        secondField.setText(SEC);
        timerButton.setText(SET);
    }

    /**
     * Defines the music player object
     */
    public void setupMusicPlayer()
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

    /* TODO: Rework. Logic should be as follows:
    ?? What happens when two timers are going off at the same time?
     */
    /**
     * Checks if the timer has concluded
     */
    public void checkIfAnyTimersAreGoingOff()
    {
        logger.info("checking if any timers are going off");
        boolean anyTimerIsGoingOff = activeTimers.stream().anyMatch(Timer::isTimerGoingOff);
        if (anyTimerIsGoingOff)
        {
            //List<Timer> toBeRemoved = new ArrayList<>();
            //timer.setTimerGoingOff(false);
            //toBeRemoved.add(timer);
            //clock.getTimerPanel2().stopTimer(timer);
            //ScheduledFuture<?> future = clock.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS);

            activeTimers.stream()
                .parallel()
                .filter(Timer::isTimerGoingOff)
                .filter(Timer::isHasBeenTriggered)
                .forEach(timer -> {
                    if (timersAndFutures.get(timer) == null) {
                        logger.info("timer does not exist in timersAndFutures");
                        var future = clock.getScheduler().scheduleAtFixedRate(timer::triggerTimer, 0, 1, TimeUnit.SECONDS);
                        clock.getTimerPanel2().getTimersAndFutures().put(timer, future);
                    }
                });
            //activeTimers.removeAll(toBeRemoved);
        }
    }

    /**
     * Resets alarm label 4
     */
    public void resetJAlarmLabel4()
    {
        logger.info("reset alarm label 4");
        if (activeTimers.isEmpty())
        { alarmLabel4.setText(clock.defaultText(10)); }// All Alarms label...
        else
        {
            alarmLabel4.setText(activeTimers.size() == 1
                            ? activeTimers.size() + SPACE+TIMER+SPACE+ADDED
                            : activeTimers.size() + SPACE+TIMER+S.toLowerCase()+SPACE+ADDED
            );
        }
    }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    public boolean validateFirstTextField()
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
    public boolean validateSecondTextField()
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
    public boolean validateThirdTextField()
    {
        boolean result;
        if (SEC.equals(secondField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(secondField.getText())) { result = false; }
        else
        {
            result = Integer.parseInt(secondField.getText()) < 60 &&
                    Integer.parseInt(secondField.getText()) >= 0;
        }
        logger.info("validate third text field result: {}", result);
        return result;
    }

    public boolean validTextFields()
    { return validateFirstTextField() && validateSecondTextField() && validateThirdTextField(); }

    // TODO: Update for Timers
    /**
     * Adds the alarm to the menu
     * @param alarm the alarm to set
     */
    public void addAlarmToAlarmMenu(Alarm alarm)
    {
        logger.info("adding alarm to alarm menu");
        JMenuItem alarmItem = new JMenuItem(alarm.toString());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        logger.info("Size of viewAlarms before adding " + clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        clock.getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        logger.info("Size of viewAlarms after adding " + clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    // TODO: Update for Timers
    /**
     * Removes an alarm from the list in the menu
     * @param alarm the alarm to remove
     */
    public void deleteAlarmMenuItemFromViewAlarms(Alarm alarm)
    {
        logger.info("delete alarm menu item from view alarms");
        logger.info("Size of viewAlarms before removal {}", clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        for(int i=0; i<clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (clock.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.toString()))
            { clock.getClockMenuBar().getAlarmFeature_Menu().remove(getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i)); }
        }
        logger.info("Size of viewAlarms after removal {}", clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    /**
     * Resets the text area
     */
    public void resetJTextArea()
    {
        logger.info("reset textarea");
        textArea.setText(EMPTY);
        for(clock.entity.Timer timer : activeTimers)
        {
            if (!textArea.getText().isEmpty())
            { textArea.append(NEWLINE); }
            textArea.append(timer+NEWLINE);
        }
    }

    // TODO: Update for Timers
    /**
     * Sets an alarm to go off
     * @param executor the executor service
     */
    public void triggerAlarm(ExecutorService executor)
    {
        logger.info("trigger timer");
        // TODO: Update for Timer
        //setAlarmIsGoingOff(true);
        //clock.getDigitalClockPanel().updateLabels();
        //clock.getDigitalClockPanel().getLabel1().setText(activeAlarm.toString());
        //clock.getDigitalClockPanel().getLabel2().setText("is going off!");
        // play sound
        Callable<String> c = () -> {
            try
            {
                setupMusicPlayer();
                logger.debug("while alarm is going off, play sound");
                // TODO: Update for Timer
                //while (getActiveAlarm().alarmGoingOff)
                //{ getMusicPlayer().play(50); }
                logger.debug("timer has stopped");
                return "Timer triggered";
            }
            catch (Exception e)
            {
                logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
                printStackTrace(e);
                setupMusicPlayer();
                getMusicPlayer().play(50);
                return "Reset music player required";
            }
        };
        executor.submit(c);
    }

    // TODO: Update for Timers
    /**
     * Stops an actively going off alarm
     */
    void stopAlarm()
    {
        logger.info("stop timer");
        musicPlayer = null;
        // TODO: Update for Timer, maybe timerIsGoingOff = false;
        //alarmIsGoingOff = false;
        // TODO: Update for Timer, maybe timer.toString...
        //logger.info(alarm.toString()+" alarm turned off.");
    }

    // TODO: Update for Timers
    /**
     * Adds alarms created to the menu
     */
    public void setupAlarmsInMenuFunctionality()
    {
        // get the view alarms menu
        // for each except the Set Alarms (option1)
        // create an action listener which
        // takes the alarmClock, set the hour, min, and ampm
        // in the textFields, and we will set a boolean to true
        // which will allow editing the textFields to any value
        // changing all values to 0 or explicitly Time to 0
        // will delete the alarm
        // changing the values and clicking Set will save the alarm
        //05:06:00 PM
        logger.info("setup timers in menu functionality");
        // TODO: Update for TimersFeature_Menu
        for(int i=0; i<getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (!"Set Timers".equals(clock.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText()))
            {
                JMenuItem menuItem = clock.getClockMenuBar().getAlarmFeature_Menu().getItem(i);
                menuItem.addActionListener(action -> {
                    clock.getListOfAlarms().forEach(alarm -> {
                        // TODO: Update for Timers
                        //if (alarm.toString().equals(menuItem.getText()))
                        //{ this.alarm = alarm; }
                    });
                    if (musicPlayer == null) { setupMusicPlayer(); }
                    // if an alarm is going off and we clicked on it in the menuBar
//                    if (activeAlarm != null)
//                    {
//                        alarm = activeAlarm;
//                        if (musicPlayer != null) { stopAlarm(); }
//                        else { logger.warn("Music player is null!"); }
//                        setCheckBoxesIfWasSelected(alarm);
//                        alarm.setIsAlarmGoingOff(false);
//                        alarm.setIsAlarmUpdating(true); // this and the boolean below we want true
//                        updatingAlarm = true; // we want to continue with the logic that's done in the next if
//                        activeAlarm = null;
//                        logger.info("Size of listOfAlarms before removing {}", clock.getListOfAlarms().size());
//                        // remove alarm from list of alarms
//                        clock.getListOfAlarms().remove(getAlarm());
//                        logger.info("Size of listOfAlarms after removing {}", clock.getListOfAlarms().size());
//                        deleteAlarmMenuItemFromViewAlarms(alarm);
//                        resetJTextArea();
//                        clock.getAlarmPanel().getJTextField1().setText(getAlarm().getHoursAsStr());
//                        clock.getAlarmPanel().getJTextField2().setText(getAlarm().getMinutesAsStr());
//                        clock.getAlarmPanel().getJTextField3().setText(getAlarm().getAMPM());
//                        alarmLabel4.setText("Alarm off.");
//                    }
                    // we are updating an alarm by clicking on it in the menuBar
                    //else if (alarm != null)
                    //{
                        // TODO: Update for Timers
                        //updateTheAlarm(alarm);
                        //hourField.setText(alarm.getHoursAsStr());
                        //minuteField.setText(alarm.getMinutesAsStr());
                        //secondField.setText(alarm.getAMPM());
                    //}
                    clock.changePanels(PANEL_TIMER, false);
                });
            }
        }
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
     * This method prints the stack trace of an exception
     * that may occur when the digital panel is in use.
     * @param e the exception
     */
    public void printStackTrace(Exception e)
    { printStackTrace(e, ""); }

    /**
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(setupTimerPanel,0,0,1,1,0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(alarmLabel4,0,1,1,1,0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // All Timers
        constraints.weighty = 4;
        constraints.weightx = 2;
        // 1, 6
        addComponent(scrollPane,1,1,2,4, 0,0, GridBagConstraints.BOTH, new Insets(1,1,1,1)); // textArea
    }

    /**
     * This method adds the components to the timer panel
     */
    public void addComponentsToSetupTimerPanel()
    {
        logger.info("add components to setup timer panel");
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        GridBagLayout layout = new GridBagLayout();
        setupTimerPanel.setLayout(layout);
        addComponentToSetupTimerPanel(hourField, 0,0,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 1
        addComponentToSetupTimerPanel(minuteField, 0,1,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 2
        addComponentToSetupTimerPanel(secondField, 0,2,1,1,0,0, GridBagConstraints.HORIZONTAL); // TextField 3
        addComponentToSetupTimerPanel(resetButton, 1, 0, 3, 1, 0, 0, GridBagConstraints.HORIZONTAL); // Reset Button
        addComponentToSetupTimerPanel(timerButton, 2,0,3,1,0,0, GridBagConstraints.HORIZONTAL); // Set Timer Button
        addComponentToSetupTimerPanel(stopButton, 3, 0, 3, 1, 0, 0, 2); // horizontal
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
    public void addComponentToSetupTimerPanel(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill)
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
        ((GridBagLayout)setupTimerPanel.getLayout()).setConstraints(cpt, constraints);
        setupTimerPanel.add(cpt, constraints);
    }

    /**
     * The main method used for adding
     * components to the setup timer panel
     * @param cpt       the component to add
     * @param gridy     the y position
     * @param gridx     the x position
     * @param gwidth    the width
     * @param gheight   the height
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param fill      the fill
     * @param insets    the insets
     */
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight,
                      int ipadx, int ipady, int fill, Insets insets)
    {
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = (int)Math.ceil(gwidth);
        constraints.gridheight = (int)Math.ceil(gheight);
        constraints.fill = fill;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.insets = insets;
        layout.setConstraints(cpt,constraints);
        add(cpt);
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
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getJAlarmLbl4() { return this.alarmLabel4; } // All alarms
    public JScrollPane getJScrollPane() { return this.scrollPane; }
    public JTextArea getJTextArea() { return this.textArea; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    public List<clock.entity.Timer> getActiveTimers() { return activeTimers; }
    public boolean isDisableTimerFunctionality() { return disableTimerFunctionality; }
    public JTextField getHourField() { return hourField; }
    public JTextField getMinuteField() { return minuteField; }
    public JTextField getSecondField() { return secondField; }
    public JButton getResetButton() { return resetButton; }
    public JButton getTimerButton() { return timerButton; }
    public Map<clock.entity.Timer, ScheduledFuture<?>> getTimersAndFutures() { return timersAndFutures; }

    /* Setters */
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setJAlarmLbl4(JLabel alarmLabel4) { this.alarmLabel4 = alarmLabel4; }
    protected void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    protected void setJTextArea(final JTextArea textArea) { this.textArea = textArea; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    public void setClock(Clock clock) { this.clock = clock; }
    public void setActiveTimers(List<clock.entity.Timer> activeTimers) { this.activeTimers = activeTimers; }
    public void setHourField(JTextField hourField) { this.hourField = hourField; }
    public void setMinuteField(JTextField minuteField) { this.minuteField = minuteField; }
    public void setSecondField(JTextField secondField) { this.secondField = secondField; }
    public void setTimerButton(JButton timerButton) { this.timerButton = timerButton; }
    public void setResetButton(JButton resetButton) { this.resetButton = resetButton; }
}