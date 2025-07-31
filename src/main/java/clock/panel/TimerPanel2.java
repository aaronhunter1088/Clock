package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import clock.entity.Timer;
import clock.exception.InvalidInputException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

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

import static clock.panel.Panel.*;
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
public class TimerPanel2 extends ClockPanel
{
    private static final Logger logger = LogManager.getLogger(TimerPanel2.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel nameLabel,
                   hoursLabel,
                   minutesLabel,
                   secondsLabel;
    private JTextField nameField,
                       hourField,
                       minuteField,
                       secondField;
    private JButton timerButton;
    private JButton resetButton;
    private JButton stopButton;
    private boolean disableTimerFunctionality;
    private AdvancedPlayer musicPlayer;
    private Map<Timer, ScheduledFuture<?>> timersAndFutures;
    //private ScheduledExecutorService scheduler;

    private JTable timersTable;
    private JScrollPane scrollTable;
    private ClockFrame clockFrame;
    //private JPanel setupTimerPanel;
    private List<clock.entity.Timer> activeTimers;

    /**
     * Main constructor for creating the TimerPanel2
     * @param clockFrame the clockFrame object reference
     */
    public TimerPanel2(ClockFrame clockFrame)
    {
        super();
        logger.info("Creating TimerPanel2");
        clockFrame.setClockPanel(PANEL_TIMER2);
        this.clockFrame = clockFrame;
        setSize(ClockFrame.panelSize);
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
        nameLabel = new JLabel(NAME, SwingConstants.CENTER);
        nameLabel.setName(NAME+LABEL);
        nameField = new JTextField(EMPTY, 10);
        nameField.setName(NAME+FIELD);
        nameField.requestFocusInWindow();
        hoursLabel = new JLabel(Hours, SwingConstants.CENTER);
        hoursLabel.setName(Hours+LABEL);
        hourField = new JTextField(EMPTY, 4);
        hourField.setName(HOUR+FIELD);
        minutesLabel = new JLabel(Minutes, SwingConstants.CENTER);
        minutesLabel.setName(Minutes+LABEL);
        minuteField = new JTextField(EMPTY, 4);
        minuteField.setName(MIN+FIELD);
        secondsLabel = new JLabel(Seconds, SwingConstants.CENTER);
        secondsLabel.setName(Seconds+LABEL);
        secondField = new JTextField(EMPTY, 4);
        secondField.setName(SEC+FIELD);
        List.of(nameField, hourField, minuteField, secondField).forEach(textField -> {
            textField.setFont(ClockFrame.font20);
            textField.setForeground(Color.BLACK);
            //textField.setPreferredSize(new Dimension(50, 50));
            textField.setBorder(new LineBorder(Color.ORANGE));
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e)
                {
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+FIELD -> {
                            if (nameField.getText().equals(NO+SPACE+NAME))
                            { nameField.setText(EMPTY); }
                            logger.debug("Focus gained on name field");
                        }
                        case HOUR+FIELD -> { logger.debug("Focus gained on hour field"); }
                        case MIN+FIELD -> { logger.debug("Focus gained on minute field"); }
                        case SEC+FIELD -> { logger.debug("Focus gained on second field"); }
                        case null -> logger.warn("Lost focus on a text field with no name");
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                }

                @Override
                public void focusLost(FocusEvent e)
                {
                    enableDisableTimerButton();
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+FIELD -> {
                            if (nameField.getText().isBlank() || nameField.getText().isEmpty())
                            { nameField.setText(NO+SPACE+NAME); }
                        }
                        case HOUR+FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(hourField.getText()) && Integer.parseInt(hourField.getText()) >= 0)
                                {
                                    int hour = Integer.parseInt(hourField.getText());
                                    if (clockFrame.getClock().isShowMilitaryTime()) {
                                        if (hour < 24 && hour >= 0)
                                        {
                                            timerButton.setText(SET);
                                            hourField.setBorder(new LineBorder(Color.ORANGE));
                                        }
                                        else
                                        {
                                            timerButton.setText(TIMER_HOUR_ERROR);
                                            hourField.setBorder(new LineBorder(Color.RED));
                                            hourField.requestFocusInWindow();
                                        }
                                    }
                                    else {
                                        if (hour < 12 && hour >= 0)
                                        {
                                            timerButton.setText(SET);
                                            hourField.setBorder(new LineBorder(Color.ORANGE));
                                        }
                                        else
                                        {
                                            timerButton.setText(TIMER_HOUR_ERROR);
                                            hourField.setBorder(new LineBorder(Color.RED));
                                            hourField.requestFocusInWindow();
                                        }
                                    }
                                }
                            } catch (NumberFormatException ignored) {
                                logger.warn("Hour field is not a number: {}", hourField.getText());
                                hourField.setBorder(new LineBorder(Color.RED));
                                hourField.requestFocusInWindow();
                            }
                        }
                        case MIN+FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(minuteField.getText()) && Integer.parseInt(minuteField.getText()) >= 0)
                                {
                                    int minute = Integer.parseInt(minuteField.getText());
                                    if (minute < 60 && minute >= 0)
                                    {
                                        timerButton.setText(SET);
                                        minuteField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        timerButton.setText(TIMER_MIN_ERROR);
                                        minuteField.setBorder(new LineBorder(Color.RED));
                                        minuteField.requestFocusInWindow();
                                    }
                                }
                            } catch (NumberFormatException ignored) {
                                logger.warn("Minute field is not a number: {}", minuteField.getText());
                                minuteField.setBorder(new LineBorder(Color.RED));
                                minuteField.requestFocusInWindow();
                            }
                        }
                        case SEC+FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(secondField.getText()) && Integer.parseInt(secondField.getText()) >= 0)
                                {
                                    int second = Integer.parseInt(secondField.getText());
                                    if (second < 60 && second >= 0)
                                    {
                                        timerButton.setText(SET);
                                        secondField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        timerButton.setText(TIMER_SEC_ERROR);
                                        secondField.setBorder(new LineBorder(Color.RED));
                                        secondField.requestFocusInWindow();
                                    }
                                }
                            } catch (NumberFormatException ignored) {
                                logger.warn("Second field is not a number: {}", secondField.getText());
                                secondField.setBorder(new LineBorder(Color.RED));
                                secondField.requestFocusInWindow();
                            }
                        }
                        case null -> logger.warn("Lost focus on a text field with no name");
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                }
            });
        });
        List.of(nameLabel, hoursLabel, minutesLabel, secondsLabel).forEach(label -> {
            label.setFont(ClockFrame.font20);
            label.setForeground(Color.WHITE);
        });
        timerButton = new JButton(SET);
        resetButton = new JButton(RESET);
        stopButton = new JButton(STOP);
        List.of(timerButton, resetButton).forEach(button -> {
            button.setFont(ClockFrame.font20);
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
        //alarmLabel4 = new JLabel(CURRENT_ALARMS, SwingConstants.CENTER); // Current Alarms
        //alarmLabel4.setFont(ClockFrame.font20); // All Alarms
        //alarmLabel4.setForeground(Color.WHITE);
        Object[][] data = clockFrame.getListOfTimers().stream()
                .map(timer -> new Object[] { timer.getName() != null ? timer.getName() : timer.toString(),
                        timer.getHours(), timer.getMinutes(), timer.getSeconds() })
                .toArray(Object[][]::new);
        timersTable = new JTable(data, new String[]{"Name", "Hours", "Minutes", "Seconds"});
        timersTable.setPreferredScrollableViewportSize(new Dimension(400, 300));
        timersTable.setFillsViewportHeight(true);
        timersTable.setFont(ClockFrame.font10);
        timersTable.setForeground(Color.WHITE);
        timersTable.setBackground(Color.BLACK);
        scrollTable = new JScrollPane(timersTable);

        setBackground(Color.BLACK);
        //scheduler = Executors.newScheduledThreadPool(10);
        timersAndFutures = new HashMap<>();
        activeTimers = new ArrayList<>();
    }

    /**
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(nameLabel,0,0,1,1,0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // H
        addComponent(nameField,0,1,1,1,0,0, 3, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // All Timers
        addComponent(hoursLabel,0,3,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // M
        addComponent(hourField,0,4,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(minutesLabel,0,5,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // M
        addComponent(minuteField,0,6,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(secondsLabel,0,7,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // M
        addComponent(secondField,0,8,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(resetButton,0,9,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(timerButton,0,10,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Set Timer

        // leaving row 1 blank for spacing

        addComponent(scrollTable, 2, 0, 11, 1, 0, 0, 1, 2, GridBagConstraints.BOTH, new Insets(0,0,0,0));

        constraints.weighty = 4;
        constraints.weightx = 2;
        // 1, 6
        //addComponent(scrollPane,1,1,2,4, 0,0, GridBagConstraints.BOTH, new Insets(1,1,1,1)); // textArea
    }

    /**
     * The main method used for adding
     * components to the setup timer panel
     * @param cpt       the component to add
     * @param gridy     the y position or the row
     * @param gridx     the x position or the column
     * @param gwidth    the width how many columns it takes up
     * @param gheight   the height how many rows it takes up
     * @param ipadx     the x padding
     * @param ipady     the y padding
     * @param fill      the fill
     * @param insets    the insets
     */
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight,
                             int ipadx, int ipady, int weightx, int weighty, int fill, Insets insets)
    {
        logger.debug("add component");
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = (int)Math.ceil(gwidth);
        constraints.gridheight = (int)Math.ceil(gheight);
        constraints.fill = fill;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.insets = insets;
        constraints.weightx = Math.max(weightx, 0);
        constraints.weighty = Math.max(weighty, 0);
        layout.setConstraints(cpt,constraints);
        add(cpt);
    }

    /**
     * Enables the timer button if the text fields are valid
     */
    public void enableDisableTimerButton()
    {
        logger.debug("enable timer button");
        var allValid = validateFirstTextField() && validateSecondTextField() && validateThirdTextField();
        var allNotZeroes = !ZERO.equals(hourField.getText()) && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText());
        var someNotBlank = areSomeNotBlank();
        logger.debug("enabled?: {}", allValid && allNotZeroes);
        timerButton.setEnabled(allValid && allNotZeroes && someNotBlank);
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
            clockFrame.getListOfTimers().add(timer);
            startTimer(timer);
            clearTextFields();
            updateTimersTable();
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
        ScheduledFuture<?> future = clockFrame.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS);
        timersAndFutures.put(timer, future);
        if (!activeTimers.isEmpty() && clockFrame.getCountdownFuture() == null) {
            //future = clockFrame.getScheduler().scheduleAtFixedRate(this::resetJTextArea, 0, 1, TimeUnit.SECONDS);
            clockFrame.setCountdownFuture(future);
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
                if (EMPTY.equals(hourField.getText())) hourField.setText(ZERO);
                if (EMPTY.equals(minuteField.getText())) minuteField.setText(ZERO);
                if (EMPTY.equals(secondField.getText())) secondField.setText(ZERO);
                timer = new clock.entity.Timer(Integer.parseInt(hourField.getText()), Integer.parseInt(minuteField.getText()),
                        Integer.parseInt(secondField.getText()), nameField.getText(), clockFrame.getClock());
                activeTimers.add(timer);
                //resetJTextArea();
                //resetJAlarmLabel4();
            }
            else {
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
        if (clockFrame.getCountdownFuture() != null && !clockFrame.getCountdownFuture().isCancelled())
        { clockFrame.getCountdownFuture().cancel(true); }
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
            activeTimers.forEach(timer -> clockFrame.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS));

            //resetJTextArea(); // leave here
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
        nameField.setText(EMPTY);
        hourField.setText(EMPTY);
        minuteField.setText(EMPTY);
        secondField.setText(EMPTY);
        timerButton.setText(SET);
    }

    public void updateTimersTable()
    {
        logger.info("update timers table");
        Object[][] data = clockFrame.getListOfTimers().stream()
                .map(timer -> new Object[] { timer.getName() != null ? timer.getName() : timer.toString(),
                        timer.getHours(), timer.getMinutes(), timer.getSeconds() })
                .toArray(Object[][]::new);
        timersTable.setModel(new javax.swing.table.DefaultTableModel(data, new String[]{"Name", "Hours", "Minutes", "Seconds"}));
        timersTable.setFillsViewportHeight(true);
        timersTable.setFont(ClockFrame.font10);
        timersTable.setForeground(Color.WHITE);
        timersTable.setBackground(Color.BLACK);
        //timersTable = new JTable(data, new String[]{"Name", "Hours", "Minutes", "Seconds"});
        scrollTable.repaint();
        scrollTable.updateUI();
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

//    /**
//     * Resets alarm label 4
//     */
//    public void resetJAlarmLabel4()
//    {
//        logger.info("reset alarm label 4");
//        if (activeTimers.isEmpty())
//        { alarmLabel4.setText(clockFrame.getClock().defaultText(10)); }// All Alarms label...
//        else
//        {
//            alarmLabel4.setText(activeTimers.size() == 1
//                            ? activeTimers.size() + SPACE+TIMER+SPACE+ADDED
//                            : activeTimers.size() + SPACE+TIMER+S.toLowerCase()+SPACE+ADDED
//            );
//        }
//    }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    public boolean validateFirstTextField()
    {
        boolean result;
        if (EMPTY.equals(hourField.getText())) { result = true; }
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
        if (EMPTY.equals(minuteField.getText())) { result = true; }
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
        if (EMPTY.equals(secondField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(secondField.getText())) { result = false; }
        else
        {
            result = Integer.parseInt(secondField.getText()) < 60 &&
                    Integer.parseInt(secondField.getText()) >= 0;
        }
        logger.info("validate third text field result: {}", result);
        return result;
    }

    /**
     * Checks if all text fields are not zeroes or empty
     * @return true if all text fields are not zeroes or empty
     */
    public boolean areAllNotZeroes()
    {
        boolean allNotZero = !ZERO.equals(hourField.getText()) && !ZERO.equals(minuteField.getText()) && !ZERO.equals(secondField.getText());
        logger.info("are all not zeroes: {}", allNotZero);
        return allNotZero;
    }

    /**
     * Checks if all text fields are blank or empty
     * @return true if all text fields are blank or empty
     */
    public boolean areSomeNotBlank()
    {
        boolean someNotBlank = !StringUtils.isBlank(hourField.getText()) || !StringUtils.isBlank(minuteField.getText()) ||
                !StringUtils.isBlank(secondField.getText());
        logger.info("are some not blank: {}", someNotBlank);
        return someNotBlank;
    }

    public boolean validTextFields()
    {
        return validateFirstTextField() && validateSecondTextField() && validateThirdTextField()
                && areAllNotZeroes() && areSomeNotBlank();
    }

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
        logger.info("Size of viewAlarms before adding " + clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        clockFrame.getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        logger.info("Size of viewAlarms after adding " + clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    // TODO: Update for Timers
    /**
     * Removes an alarm from the list in the menu
     * @param alarm the alarm to remove
     */
    public void deleteAlarmMenuItemFromViewAlarms(Alarm alarm)
    {
        logger.info("delete alarm menu item from view alarms");
        logger.info("Size of viewAlarms before removal {}", clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        for(int i=0; i<clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.toString()))
            { clockFrame.getClockMenuBar().getAlarmFeature_Menu().remove(clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i)); }
        }
        logger.info("Size of viewAlarms after removal {}", clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

//    /**
//     * Resets the text area
//     */
//    public void resetJTextArea()
//    {
//        logger.info("reset textarea");
//        textArea.setText(EMPTY);
//        for(clock.entity.Timer timer : activeTimers)
//        {
//            if (!textArea.getText().isEmpty())
//            { textArea.append(NEWLINE); }
//            textArea.append(timer+NEWLINE);
//        }
//    }

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
        for(int i=0; i<clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (!"Set Timers".equals(clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText()))
            {
                JMenuItem menuItem = clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i);
                menuItem.addActionListener(action -> {
                    clockFrame.getListOfAlarms().forEach(alarm -> {
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
                    clockFrame.changePanels(PANEL_TIMER, false);
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
     * This method sets up the settings menu for the
     * timer panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        logger.info("No settings defined for the Timer Panel");
    }

    /* Getters */
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return clockFrame.getClock(); }
    //public JLabel getJAlarmLbl4() { return this.alarmLabel4; } // All alarms }
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
    //protected void setJAlarmLbl4(JLabel alarmLabel4) { this.alarmLabel4 = alarmLabel4; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    public void setClock(Clock clock) { this.clockFrame.setClock(clock); }
    public void setActiveTimers(List<clock.entity.Timer> activeTimers) { this.activeTimers = activeTimers; }
    public void setHourField(JTextField hourField) { this.hourField = hourField; }
    public void setMinuteField(JTextField minuteField) { this.minuteField = minuteField; }
    public void setSecondField(JTextField secondField) { this.secondField = secondField; }
    public void setTimerButton(JButton timerButton) { this.timerButton = timerButton; }
    public void setResetButton(JButton resetButton) { this.resetButton = resetButton; }
}