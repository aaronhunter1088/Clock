package clock.panel;

import clock.entity.ButtonColumn;
import clock.entity.Clock;
import clock.entity.Timer;
import clock.exception.InvalidInputException;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;
import static clock.util.Constants.*;

/**
 * Timer Panel
 * <p>
 * This panel will allow you to create multiple
 * timers, and see them displayed in a table
 * below the timer creation fields. Each timer
 * will have a name, hours, minutes, and seconds.
 * Once it reaches zero, it will sound an alarm
 * until the user turns it off.
 *
 * @author Michael Ball
 * @version since 2.9
 */
public class TimerPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(TimerPanel.class);
    private Thread thread;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel nameLabel,
                   hoursLabel,
                   minutesLabel,
                   secondsLabel;
    private JTextField nameTextField,
                       hourTextField,
                       minuteTextField,
                       secondTextField;
    private JButton setTimerButton,
                    resetButton;
    private JTable timersTable;
    private JScrollPane scrollTable;
    private ClockFrame clockFrame;
    private Clock clock;

    /**
     * Main constructor for creating the TimerPanel
     * @param clockFrame the clockFrame object reference
     */
    public TimerPanel(ClockFrame clockFrame)
    {
        super();
        logger.debug("Creating TimerPanel");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setMaximumSize(ClockFrame.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupTimerPanel();
        setupSettingsMenu();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating Timer Panel");
    }

    /**
     * Adds all the components to the TimerPanel
     */
    public void setupTimerPanel()
    {
        logger.info("setup TimerPanel");
        nameLabel = new JLabel(NAME, SwingConstants.CENTER);
        nameLabel.setName(NAME+LABEL);
        nameTextField = new JTextField(EMPTY, 10);
        nameTextField.setName(NAME+ TEXT_FIELD);
        nameTextField.requestFocusInWindow();
        hoursLabel = new JLabel(Hours, SwingConstants.CENTER);
        hoursLabel.setName(Hours+LABEL);
        hourTextField = new JTextField(EMPTY, 4);
        hourTextField.setName(HOUR+ TEXT_FIELD);
        minutesLabel = new JLabel(Minutes, SwingConstants.CENTER);
        minutesLabel.setName(Minutes+LABEL);
        minuteTextField = new JTextField(EMPTY, 4);
        minuteTextField.setName(MIN+ TEXT_FIELD);
        secondsLabel = new JLabel(Seconds, SwingConstants.CENTER);
        secondsLabel.setName(Seconds+LABEL);
        secondTextField = new JTextField(EMPTY, 4);
        secondTextField.setName(SEC+ TEXT_FIELD);
        List.of(nameTextField, hourTextField, minuteTextField, secondTextField).forEach(textField -> {
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
                        case NAME+TEXT_FIELD -> {
                            if (nameTextField.getText().equals(TIMER+(Timer.timersCounter+1)))
                            { nameTextField.setText(EMPTY); }
                            logger.debug("Focus gained on name field");
                        }
                        case HOUR+TEXT_FIELD -> { logger.debug("Focus gained on hour field"); }
                        case MIN+TEXT_FIELD -> { logger.debug("Focus gained on minute field"); }
                        case SEC+TEXT_FIELD -> { logger.debug("Focus gained on second field"); }
                        case null -> logger.warn("Lost focus on a text field with no name");
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                }

                @Override
                public void focusLost(FocusEvent e)
                {
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+TEXT_FIELD -> {
                            if (nameTextField.getText().isBlank() || nameTextField.getText().isEmpty())
                            { nameTextField.setText(TIMER+(Timer.timersCounter+1)); }
                        }
                        case HOUR+TEXT_FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(hourTextField.getText()) && Integer.parseInt(hourTextField.getText()) >= 0)
                                {
                                    int hour = Integer.parseInt(hourTextField.getText());
                                    if (hour < 24 && hour >= 0)
                                    {
                                        setTimerButton.setText(SET);
                                        hourTextField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        setTimerButton.setText(TIMER_HOUR_ERROR_24);
                                        hourTextField.setBorder(new LineBorder(Color.RED));
                                        hourTextField.requestFocusInWindow();
                                    }
                                }
                            }
                            catch (NumberFormatException ignored) {
                                logger.warn("Hour field is not a number: {}", hourTextField.getText());
                                hourTextField.setBorder(new LineBorder(Color.RED));
                                hourTextField.requestFocusInWindow();
                            }
                        }
                        case MIN+TEXT_FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(minuteTextField.getText()) && Integer.parseInt(minuteTextField.getText()) >= 0)
                                {
                                    int minute = Integer.parseInt(minuteTextField.getText());
                                    if (minute < 60 && minute >= 0)
                                    {
                                        setTimerButton.setText(SET);
                                        minuteTextField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        setTimerButton.setText(TIMER_MIN_ERROR);
                                        minuteTextField.setBorder(new LineBorder(Color.RED));
                                        minuteTextField.requestFocusInWindow();
                                    }
                                }
                            }
                            catch (NumberFormatException ignored) {
                                logger.warn("Minute field is not a number: {}", minuteTextField.getText());
                                minuteTextField.setBorder(new LineBorder(Color.RED));
                                minuteTextField.requestFocusInWindow();
                            }
                        }
                        case SEC+TEXT_FIELD -> {
                            try {
                                if (StringUtils.isNotBlank(secondTextField.getText()) && Integer.parseInt(secondTextField.getText()) >= 0)
                                {
                                    int second = Integer.parseInt(secondTextField.getText());
                                    if (second < 60 && second >= 0)
                                    {
                                        setTimerButton.setText(SET);
                                        secondTextField.setBorder(new LineBorder(Color.ORANGE));
                                    }
                                    else
                                    {
                                        setTimerButton.setText(TIMER_SEC_ERROR);
                                        secondTextField.setBorder(new LineBorder(Color.RED));
                                        secondTextField.requestFocusInWindow();
                                    }
                                }
                            }
                            catch (NumberFormatException ignored) {
                                logger.warn("Second field is not a number: {}", secondTextField.getText());
                                secondTextField.setBorder(new LineBorder(Color.RED));
                                secondTextField.requestFocusInWindow();
                            }
                        }
                        case null -> logger.warn("Lost focus on a text field with no name");
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                    enableDisableTimerButton();
                }
            });
        });
        List.of(nameLabel, hoursLabel, minutesLabel, secondsLabel).forEach(label -> {
            label.setFont(ClockFrame.font20);
            label.setForeground(Color.WHITE);
        });
        setTimerButton = new JButton(SET);
        resetButton = new JButton(RESET);
        List.of(setTimerButton, resetButton).forEach(button -> {
            button.setFont(ClockFrame.font20);
            button.setOpaque(true);
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setBorder(new LineBorder(Color.WHITE));
        });
        setTimerButton.addActionListener(this::setTimer);
        setTimerButton.setEnabled(false);
        resetButton.addActionListener(this::resetTimerPanel);
        resetButton.setEnabled(false);
        setupTimersTableDefaults(true);
        start();
    }

    /**
     * This method sets up the settings menu for the
     * timer panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting());
    }

    /**
     * This method creates the button action for the
     * buttons in the timers table.
     *
     * @param columnIndex
     * @return
     */
    public Action buttonAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String buttonAction = (String) timersTable.getModel().getValueAt(modelRow, columnIndex);

                // find the correct timer
                Timer timer = clock.getListOfTimers().get(modelRow);
                switch (buttonAction) {
                    case "Reset" -> {
                        // set button text to "Pause"
                        timersTable.getModel().setValueAt("Pause", modelRow, columnIndex);
                        timer.resetTimer();
                    }
                    case "Pause" -> {
                        logger.info("Pausing {} at row: {}", timer, modelRow);
                        // pause timer
                        timer.pauseTimer();
                        // set button text to "Resume"
                        timersTable.getModel().setValueAt("Resume", modelRow, columnIndex);
                    }
                    case "Resume" -> {
                        logger.info("Resuming {} at row: {}", timer, modelRow);
                        // resume timer
                        timer.resumeTimer();
                        // set button text to "Pause"
                        timersTable.getModel().setValueAt("Pause", modelRow, columnIndex);
                    }
                    case "Remove" -> {
                        logger.info("Removing {} at row: {}", timer, modelRow);
                        timer.stopTimer();
                        clock.getListOfTimers().remove(timer);
                        ((DefaultTableModel)timersTable.getModel()).removeRow(modelRow);
                    }
                }
            }
        };
    }

    /**
     * Resets the timer and updates the button text
     * @param columnIndex the index of the column
     * @return the action to reset the timer
     */
    public Action resetAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String buttonAction = (String) table.getModel().getValueAt(modelRow, columnIndex);
                timersTable.getModel().setValueAt("Pause", modelRow, 2);
                Timer timer = clock.getListOfTimers().get(modelRow);
                timer.resetTimer();
            }
        };
    }

    /**
     * Gets the data for the timers table
     * @return the data for the timers table
     */
    public Object[][] getTimersTableData()
    {
        return clock.getListOfTimers().stream()
                .map(timer -> new Object[] {
                        timer.getName() != null ? timer.getName() : timer.toString(),
                        timer.getCountdownString(),
                        "Pause",
                        "Remove"
                })
                .toArray(Object[][]::new);
    }

    /**
     * Gets the column names for the timers table
     * @return the column names for the timers table
     */
    public String[] getTimersTableColumnNames()
    {
        return new String[]{"Name", "Countdown", "Resume/Pause/Reset", "Remove"};
    }

    /**
     * Sets the default values for the timers table
     * @param setup true if we are setting up for the first time.
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
                AtomicInteger rowIndex = new AtomicInteger();
                clock.getListOfTimers().forEach(timer -> {
                    String currentTimer = timersTable.getValueAt(rowIndex.get(), 0).toString();
                    if (currentTimer.equals(timer.getName())) {
                        timersTable.setValueAt(timer.getCountdownString(), rowIndex.get(), 1);
                    }
                    // update buttons to show restart or remove
                    if (timer.isTimerGoingOff()) {
                        //timersTable.setValueAt(timer.getCountdown(), rowIndex, 1);
                        timersTable.getModel().setValueAt("Reset", rowIndex.get(), 2);
                        new ButtonColumn(timersTable, resetAction(2), 2);
                    }
                    else if (timer.isPaused()) {
                        timersTable.getModel().setValueAt("Resume", rowIndex.get(), 2);
                        new ButtonColumn(timersTable, buttonAction(2), 2);
                    } else {
                        timersTable.getModel().setValueAt("Pause", rowIndex.get(), 2);
                        new ButtonColumn(timersTable, buttonAction(2), 2);
                    }
                    rowIndex.getAndIncrement();
                });
            }
        }
    }

    /**
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(nameLabel,0,0,1,1,0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(nameTextField,0,1,1,1,0,0, 3, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(hoursLabel,0,3,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(hourTextField,0,4,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(minutesLabel,0,5,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(minuteTextField,0,6,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(secondsLabel,0,7,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(secondTextField,0,8,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(resetButton,0,9,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(setTimerButton,0,10,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));

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
        var allAreNotZeroes = !(ZERO.equals(hourTextField.getText()) && ZERO.equals(minuteTextField.getText()) && ZERO.equals(secondTextField.getText()));
        var someNotBlank = areSomeNotBlank();
        logger.debug("enabled?: {}", allValid && allAreNotZeroes);
        setTimerButton.setEnabled(allValid && allAreNotZeroes && someNotBlank);
    }

    /**
     * Executes when we hit the timer button
     * @param action the action event
     */
    public void setTimer(ActionEvent action)
    {
        logger.debug("set timer");
        clock.entity.Timer timer = createTimer();
        logger.debug("timer created: {}", timer);
        clock.getListOfTimers().add(timer);
        clearTextFields();
        resetButton.setEnabled(true);
        setTimerButton.setEnabled(false);
    }

    /**
     * Creates a new Timer
     */
    public clock.entity.Timer createTimer()
    {
        logger.debug("creating timer");
        clock.entity.Timer timer = null;
        try
        {
            if (validTextFields()) {
                if (EMPTY.equals(nameTextField.getText())) nameTextField.setText(TIMER+(Timer.timersCounter+1));
                if (EMPTY.equals(hourTextField.getText())) hourTextField.setText(ZERO);
                if (EMPTY.equals(minuteTextField.getText())) minuteTextField.setText(ZERO);
                if (EMPTY.equals(secondTextField.getText())) secondTextField.setText(ZERO);

                timer = new clock.entity.Timer(Integer.parseInt(hourTextField.getText()), Integer.parseInt(minuteTextField.getText()),
                        Integer.parseInt(secondTextField.getText()), nameTextField.getText(), clockFrame.getClock());
            }
            else {
                logger.error("One of the text fields is not valid");
            }
        }
        catch (InvalidInputException iie)
        {
            logger.error("Invalid input exception: {}", iie.getMessage());
        }
        return timer;
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
        clock.getListOfTimers().clear();
        logger.info("all timers cleared");
        resetButton.setEnabled(false);
        setTimerButton.setEnabled(false);
    }

    /**
     * Sets the textFields to their beginning state
     */
    public void clearTextFields()
    {
        logger.debug("clearing timer fields");
        nameTextField.setText(EMPTY);
        hourTextField.setText(EMPTY);
        minuteTextField.setText(EMPTY);
        secondTextField.setText(EMPTY);
        logger.debug("timer button set to {}", SET);
        setTimerButton.setText(SET);
    }

    /**
     * Validates the first text field
     * @return true if the text field is valid
     */
    public boolean validateFirstTextField()
    {
        boolean result;
        if (EMPTY.equals(hourTextField.getText())) { result = true; }
        // by default, cannot be more than 23 hours or less than 0
        else {
            result = Integer.parseInt(hourTextField.getText()) < 24 &&
                    Integer.parseInt(hourTextField.getText()) >= 0;
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
        if (EMPTY.equals(minuteTextField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(minuteTextField.getText())) { result = false; }
        else {
            result = Integer.parseInt(minuteTextField.getText()) < 60 &&
                    Integer.parseInt(minuteTextField.getText()) >= 0;
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
        if (EMPTY.equals(secondTextField.getText())) { result = true; }
        else if (!NumberUtils.isNumber(secondTextField.getText())) { result = false; }
        else
        {
            result = Integer.parseInt(secondTextField.getText()) < 60 &&
                    Integer.parseInt(secondTextField.getText()) >= 0;
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
        boolean hoursIsZero = ZERO.equals(hourTextField.getText());
        boolean minutesIsZero = ZERO.equals(minuteTextField.getText());
        boolean secondsIsZero = ZERO.equals(secondTextField.getText());
        boolean allNotZero = !(hoursIsZero && minutesIsZero && secondsIsZero);
        logger.info("are all not zeroes: {}", allNotZero);
        return allNotZero;
    }

    /**
     * Checks if all text fields are blank or empty
     * @return true if all text fields are blank or empty
     */
    public boolean areSomeNotBlank()
    {
        boolean someNotBlank = !StringUtils.isBlank(hourTextField.getText()) || !StringUtils.isBlank(minuteTextField.getText()) ||
                !StringUtils.isBlank(secondTextField.getText());
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

    /** Starts the timer clock panel thread and internally calls the run method. */
    public void start()
    {
        logger.debug("starting timer panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the timer panel thread. */
    public void stop()
    {
        logger.debug("stopping timer panel");
        thread = null;
    }

    /** This method runs the timer panel thread and updates the timers table every second. */
    @Override
    public void run()
    {
        logger.debug("running timer panel");
        while (thread != null)
        {
            try {
                setupTimersTableDefaults(false);
                sleep(1000);
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
        }
    }

    /* Getters */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return clockFrame.getClock(); }
    public JTextField getHourTextField() { return hourTextField; }
    public JTextField getMinuteTextField() { return minuteTextField; }
    public JTextField getSecondTextField() { return secondTextField; }
    public JButton getResetButton() { return resetButton; }
    public JButton getSetTimerButton() { return setTimerButton; }

    /* Setters */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; logger.debug("layout set"); }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    public void setClock(Clock clock) { this.clock = clock; logger.debug("Clock set in TimerPanel"); }
    public void setHourTextField(JTextField hourTextField) { this.hourTextField = hourTextField; }
    public void setMinuteTextField(JTextField minuteTextField) { this.minuteTextField = minuteTextField; }
    public void setSecondTextField(JTextField secondTextField) { this.secondTextField = secondTextField; }
    public void setSetTimerButton(JButton setTimerButton) { this.setTimerButton = setTimerButton; }
    public void setResetButton(JButton resetButton) { this.resetButton = resetButton; }
}
