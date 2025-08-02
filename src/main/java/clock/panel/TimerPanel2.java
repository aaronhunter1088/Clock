package clock.panel;

import clock.entity.ButtonColumn;
import clock.entity.Clock;
import clock.entity.Timer;
import clock.exception.InvalidInputException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
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
public class TimerPanel2 extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(TimerPanel2.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private Thread thread = null;
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
                                    if (hour < 24 && hour >= 0)
                                    {
                                        timerButton.setText(SET);
                                        hourField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        timerButton.setText(TIMER_HOUR_ERROR_24);
                                        hourField.setBorder(new LineBorder(Color.RED));
                                        hourField.requestFocusInWindow();
                                    }
                                }
                            }
                            catch (NumberFormatException ignored) {
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
                            }
                            catch (NumberFormatException ignored) {
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
                            }
                            catch (NumberFormatException ignored) {
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
        timerButton.addActionListener(this::setTimer);
        timerButton.setEnabled(false);
        resetButton.addActionListener(this::resetTimerPanel);
        resetButton.setEnabled(false);
        setupTimersTableDefaults(true);

        //scheduler = Executors.newScheduledThreadPool(10);
        timersAndFutures = new HashMap<>();
    }

    public Action buttonAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {

                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String buttonAction = (String) table.getModel().getValueAt(modelRow, columnIndex);
                Window window = SwingUtilities.windowForComponent(table);

                int result = JOptionPane.showConfirmDialog(
                        window,
                        "Are you sure you want to " + buttonAction,
                        "buttonAction Row Confirmation",
                        JOptionPane.YES_NO_OPTION); // Yes = 0, No = 1

                if (result == JOptionPane.YES_OPTION) // == 0
                {
                    // find the correct timer
                    Timer timer = clockFrame.getListOfTimers().get(modelRow);
                    switch (buttonAction) {
                        case "Pause" -> {
                            logger.info("Pausing timer at row: {}", modelRow);
                            // pause timer
                            timer.pauseTimer();
                            // set button text to "Resume"
                            table.getModel().setValueAt("Resume", modelRow, columnIndex);
                        }
                        case "Resume" -> {
                            logger.info("Resuming timer at row: {}", modelRow);
                            // resume timer
                            timer.resumeTimer();
                            // set button text to "Pause"
                            table.getModel().setValueAt("Pause", modelRow, columnIndex);
                        }
                        case "Cancel" -> {
                            logger.info("Cancelling timer at row: {}", modelRow);
                            stopTimer(timer);
                            timersAndFutures.remove(timer);
                            clockFrame.getListOfTimers().remove(timer);
                        }
                    }

                }
            }
        };
    }

    /**
     * Gets the data for the timers table
     * @return the data for the timers table
     */
    public Object[][] getTimersTableData()
    {
        return clockFrame.getListOfTimers().stream()
                .map(timer -> new Object[] {
                        timer.getName() != null ? timer.getName() : timer.toString(),
                        timer.getCountdown(),
                        "Pause",
                        "Cancel"
                         })
                .toArray(Object[][]::new);
    }

    /**
     * Gets the column names for the timers table
     * @return the column names for the timers table
     */
    public String[] getTimersTableColumnNames()
    {
        return new String[]{"Name", "Countdown", "Resume/Pause", "Cancel"};
    }

    /**
     * Sets the default values for the timers table
     */
    public void setupTimersTableDefaults(boolean setup)
    {
        Object[][] data = getTimersTableData();
        String[] columnNames = getTimersTableColumnNames();
        if (setup) {
            timersTable = new JTable(new DefaultTableModel(data, columnNames));
            timersTable.setPreferredScrollableViewportSize(timersTable.getPreferredSize());//thanks mKorbel +1 http://stackoverflow.com/questions/10551995/how-to-set-jscrollpane-layout-to-be-the-same-as-jtable
            timersTable.setFont(ClockFrame.font10);
            timersTable.setBackground(Color.BLACK);
            timersTable.setForeground(Color.WHITE);
            timersTable.setFillsViewportHeight(true);
            scrollTable = new JScrollPane(timersTable);
        } else {
            // only update if the timers count changes
            if(timersTable.getModel().getRowCount() != data.length) {
                timersTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
                ButtonColumn buttonColumn = new ButtonColumn(timersTable, buttonAction(2), 2);
                ButtonColumn buttonColumn2 = new ButtonColumn(timersTable, buttonAction(3), 3);
            } else {
                int tableRows = timersTable.getModel().getRowCount();
                clockFrame.getListOfTimers().forEach(timer -> {
                   for(int i=0; i<tableRows; i++) {
                       String currentTimer = timersTable.getValueAt(i, 0).toString();
                       if (currentTimer.equals(timer.getName())) {
                           timersTable.setValueAt(timer.getCountdown(), i, 1);
                       }
                   }
                });
            }
        }

    }

    public void updateTimersTable()
    {
        logger.info("update timers table");
        setupTimersTableDefaults(false);
    }

    // TODO: implement
    // public void cancelTimer(Timer timer)

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
        var allAreNotZeroes = !(ZERO.equals(hourField.getText()) && ZERO.equals(minuteField.getText()) && ZERO.equals(secondField.getText()));
        var someNotBlank = areSomeNotBlank();
        logger.debug("enabled?: {}", allValid && allAreNotZeroes);
        timerButton.setEnabled(allValid && allAreNotZeroes && someNotBlank);
    }

    /**
     * Starts the timer panel
     */
    public void start()
    {
        logger.info("starting timer panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    public boolean isRunning() {
        if (thread != null && thread.isAlive())
        {
            logger.debug("Timer thread is running");
            return true;
        }
        else
        {
            logger.debug("Timer thread is not running");
            return false;
        }
    }

    /**
     * Stops the timer clock
     */
    public void stop()
    {
        logger.info("stopping timer thread");
        thread = null;
    }

    /**
     * Called by start
     */
    @Override
    public void run()
    {
        logger.info("starting timer panel");
        while (thread != null)
        {
            try { sleep(1000); }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
            updateTimersTable();
        }
    }

    /**
     * Executes when we hit the timer button
     * @param action the action event
     */
    public void setTimer(ActionEvent action)
    {
        logger.info("run");
        if (SET.equals(timerButton.getText()))
        {
            clock.entity.Timer timer = createTimer();
            startTimer(timer);
            logger.info("timer created: {}", timer);
            clockFrame.getListOfTimers().add(timer);
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
        ScheduledFuture<?> future = clockFrame.getScheduler().scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS);
        timersAndFutures.put(timer, future);
        if (!clockFrame.getListOfTimers().isEmpty() && clockFrame.getCountdownFuture() == null) {
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
        clockFrame.getListOfTimers().stream()
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
            clockFrame.getListOfTimers().forEach(timer -> clockFrame.getScheduler()
                    .scheduleAtFixedRate(timer::performCountDown, 0, 1, TimeUnit.SECONDS));

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
        timersTable.setModel(new javax.swing.table.DefaultTableModel());
        clockFrame.getListOfTimers().clear();
        logger.info("all timers cleared");
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
    public void setClock(Clock clock) { this.clockFrame.setClock(clock); }
    public void setHourField(JTextField hourField) { this.hourField = hourField; }
    public void setMinuteField(JTextField minuteField) { this.minuteField = minuteField; }
    public void setSecondField(JTextField secondField) { this.secondField = secondField; }
    public void setTimerButton(JButton timerButton) { this.timerButton = timerButton; }
    public void setResetButton(JButton resetButton) { this.resetButton = resetButton; }
}
