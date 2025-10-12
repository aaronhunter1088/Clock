package clock.panel;

import clock.entity.Clock;
import clock.entity.Timer;
import clock.exception.InvalidInputException;
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
 * until the user turns it off. You can pause,
 * resume, restart, or remove the timer. There is
 * a settings that allows you to pause or resume
 * all existing timers at once.
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
                       hoursTextField,
                       minutesTextField,
                       secondsTextField;
    private JButton setTimerButton;
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
        setupTimerPanel();
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
        nameTextField.setName(NAME + TEXT_FIELD);

        hoursLabel = new JLabel(Hours, SwingConstants.CENTER);
        hoursLabel.setName(Hours+LABEL);
        hoursTextField = new JTextField(EMPTY, 4);
        hoursTextField.setName(HOUR + TEXT_FIELD);

        minutesLabel = new JLabel(Minutes, SwingConstants.CENTER);
        minutesLabel.setName(Minutes+LABEL);
        minutesTextField = new JTextField(EMPTY, 4);
        minutesTextField.setName(MIN + TEXT_FIELD);

        secondsLabel = new JLabel(Seconds, SwingConstants.CENTER);
        secondsLabel.setName(Seconds+LABEL);
        secondsTextField = new JTextField(EMPTY, 4);
        secondsTextField.setName(SEC + TEXT_FIELD);

        List.of(nameTextField, hoursTextField, minutesTextField, secondsTextField).forEach(textField -> {
            textField.setFont(ClockFrame.font20);
            textField.setForeground(Color.BLACK);
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
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                }

                @Override
                public void focusLost(FocusEvent e)
                {
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+TEXT_FIELD -> {
                            if (nameTextField.getText().isBlank())
                            { nameTextField.setText(TIMER+(Timer.timersCounter+1)); }
                            else if (nameTextField.getText().length() > 10)
                            { nameTextField.setText(nameTextField.getText().substring(0, 10)); }
                        }
                        case HOUR+TEXT_FIELD -> {
                            int upperLimit = clockFrame.getClock().isShowMilitaryTime() ? 23 : 12;
                            boolean validHours = validateHoursTextField();
                            if (validHours)
                            {
                                hoursTextField.setBorder(new LineBorder(Color.ORANGE));
                            }
                            else
                            {
                                displayPopupMessage(TIMER_ERROR, "Hours must be between 0 and "+upperLimit, 0);
                                hoursTextField.setBorder(new LineBorder(Color.RED));
                                hoursTextField.requestFocusInWindow();
                            }
                        }
                        case MIN+TEXT_FIELD -> {
                            boolean validMinutes = validateMinutesTextField();
                            if (validMinutes)
                            {
                                minutesTextField.setBorder(new LineBorder(Color.ORANGE));
                            }
                            else
                            {
                                displayPopupMessage(TIMER_ERROR, "Minutes must be between 0 and 59", 0);
                                minutesTextField.setBorder(new LineBorder(Color.RED));
                                minutesTextField.requestFocusInWindow();
                            }
                        }
                        case SEC+TEXT_FIELD -> {
                            boolean validSeconds = validateSecondsTextField();
                            if (validSeconds) {
                                secondsTextField.setBorder(new LineBorder(Color.ORANGE));
                            } else {
                                displayPopupMessage(TIMER_ERROR, "Seconds must be between 0 and 59", 0);
                                secondsTextField.setBorder(new LineBorder(Color.RED));
                                secondsTextField.requestFocusInWindow();
                            }
                        }
                        default -> throw new InvalidInputException("Lost focus on an unknown text field: " + e.getSource());
                    }
                }
            });
        });
        List.of(nameLabel, hoursLabel, minutesLabel, secondsLabel).forEach(label -> {
            label.setFont(ClockFrame.font20);
            label.setForeground(Color.WHITE);
        });
        // setup Set button
        setTimerButton = new JButton(SET);
        setTimerButton.setFont(ClockFrame.font20);
        setTimerButton.setOpaque(true);
        setTimerButton.setBackground(Color.BLACK);
        setTimerButton.setForeground(Color.BLACK);
        setTimerButton.addActionListener(this::setTimer);
        setupTimersTableDefaults(true);
        setupDefaultValues();
    }

    /**
     * This method sets up the settings menu for the
     * timer panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getPauseResumeAllTimersSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getResetTimersPanelSetting());
    }

    /** Sets up the default values for the timer panel. */
    public void setupDefaultValues()
    {
        nameTextField.requestFocusInWindow();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupSettingsMenu();
        clearTextFields();
        clockFrame.setTitle(TIMER+S.toLowerCase()+SPACE+PANEL);
        start();
    }

    /**
     * This method creates the button action for the
     * buttons in the timers table.
     * @param columnIndex the index of the column where the button is located
     * @return the action to be performed when the button is clicked
     */
    public Action buttonAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
            int modelRow = Integer.parseInt( e.getActionCommand() );
            String buttonAction = (String) timersTable.getModel().getValueAt(modelRow, columnIndex);
            Timer timer = clock.getListOfTimers().get(modelRow);
            logger.debug("{} {} at row: {}", buttonAction, timer, modelRow);
            switch (buttonAction) {
                case RESET -> {
                    timersTable.getModel().setValueAt(PAUSE, modelRow, columnIndex);
                    timer.resetTimer();
                    clock.getListOfTimers().set(modelRow, timer);
                }
                case PAUSE -> {
                    timer.pauseTimer();
                    timersTable.getModel().setValueAt(RESUME, modelRow, columnIndex);
                    clock.getListOfTimers().set(modelRow, timer);
                }
                case RESUME -> {
                    timer.resumeTimer();
                    timersTable.getModel().setValueAt(PAUSE, modelRow, columnIndex);
                    clock.getListOfTimers().set(modelRow, timer);
                }
                case REMOVE -> {
                    timer.stopTimer();
                    clock.getListOfTimers().remove(timer);
                }
            }
            }
        };
    }

    /**
     * Gets the timer data for the timers table:
     * timer.name, timer.countdown, 'Pause', 'Remove'
     * @return the data for the timers table
     */
    public Object[][] getTimersTableData()
    {
        return clock.getListOfTimers().stream()
                .map(timer -> new Object[] {
                        timer.getName(),
                        timer.getCountDownString(),
                        PAUSE,
                        REMOVE
                })
                .toArray(Object[][]::new);
    }

    /**
     * Gets the column names for the timers table
     * @return the column names for the timers table
     */
    public String[] getTimersTableColumnNames()
    { return new String[]{NAME, COUNTDOWN, RESUME+SLASH+PAUSE+SLASH+RESET, REMOVE}; }

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
                new ButtonColumn(timersTable, buttonAction(2), 2);
                new ButtonColumn(timersTable, buttonAction(3), 3);
            } else {
                AtomicInteger rowIndex = new AtomicInteger();
                clock.getListOfTimers().forEach(timer -> {
                    String currentTimer = timersTable.getValueAt(rowIndex.get(), 0).toString();
                    if (currentTimer.equals(timer.getName())) {
                        timersTable.setValueAt(timer.getCountDownString(), rowIndex.get(), 1);
                    }
                    // update buttons to show pause/resume/reset
                    if (timer.isTimerGoingOff()) {
                        timersTable.getModel().setValueAt(RESET, rowIndex.get(), 2);
                        new ButtonColumn(timersTable, buttonAction(2), 2);
                    }
                    else if (timer.isPaused()) {
                        timersTable.getModel().setValueAt(RESUME, rowIndex.get(), 2);
                        new ButtonColumn(timersTable, buttonAction(2), 2);
                    } else {
                        timersTable.getModel().setValueAt(PAUSE, rowIndex.get(), 2);
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
        addComponent(hoursTextField,0,4,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(minutesLabel,0,5,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(minutesTextField,0,6,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(secondsLabel,0,7,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(secondsTextField,0,8,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(setTimerButton,0,9,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));

        addComponent(scrollTable, 1, 0, 10, 1, 0, 0, 1, 2, GridBagConstraints.BOTH, new Insets(0,0,0,0));

        constraints.weighty = 4;
        constraints.weightx = 2;
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
     * Executes when we hit the timer button
     * @param action the action event
     */
    public void setTimer(ActionEvent action)
    {
        logger.debug("set timer");
        try {
            Timer timer = createTimer();
            logger.debug("timer created: {}", timer);
            clock.getListOfTimers().add(timer);
            clearTextFields();
        }
        catch (InvalidInputException iie)
        {
            logger.error("Invalid input exception: {}", iie.getMessage());
            displayPopupMessage(TIMER_ERROR, iie.getMessage(), 0);
        }
    }

    /**
     * Creates a new Timer
     */
    public Timer createTimer()
    {
        logger.debug("creating timer");
        Timer timer = null;
        if (validTextFields())
        {
            timer = new Timer(Integer.parseInt(hoursTextField.getText()), Integer.parseInt(minutesTextField.getText()),
                    Integer.parseInt(secondsTextField.getText()), nameTextField.getText(), clockFrame.getClock());
        }
        else {
            boolean validHours = validateHoursTextField();
            boolean validMinutes = validateMinutesTextField();
            boolean validSeconds = validateSecondsTextField();
            if (areAllBlank()) throw new InvalidInputException("You must enter at least one valid time value");
            if (!validHours) throw new InvalidInputException("Invalid hours");
            if (!validMinutes) throw new InvalidInputException("Invalid minutes");
            if (!validSeconds) throw new InvalidInputException("Invalid seconds");
        }
        return timer;
    }

    /**
     * Resets the timer panel
     */
    public void resetTimerPanel()
    {
        logger.debug("reset timer fields");
        clearTextFields();
        timersTable.setModel(new javax.swing.table.DefaultTableModel());
        clock.getListOfTimers().forEach(timer -> {
            timer.stopTimer();
            timer.setPaused(false);
            timer.setTimerGoingOff(false);
        });
        clock.getListOfTimers().clear();
        logger.debug("all timers cleared");
    }

    /**
     * Sets the textFields to their beginning state
     */
    public void clearTextFields()
    {
        logger.debug("clearing timer text fields");
        nameTextField.setText(EMPTY);
        hoursTextField.setText(EMPTY);
        minutesTextField.setText(EMPTY);
        secondsTextField.setText(EMPTY);
    }

    /**
     * Validates the hours text field
     * @return true if the text field is valid
     */
    public boolean validateHoursTextField()
    {
        boolean result = false;
        if (hoursTextField.getText().isEmpty() && (!minutesTextField.getText().isEmpty()
                || !secondsTextField.getText().isEmpty()))
        {
            result = true;
        }
        else if (areAllBlank())
        {
            return true;
        }
        else if (!hoursTextField.getText().isEmpty())
        {
            try
            {
                int upperLimit = clockFrame.getClock().isShowMilitaryTime() ? 23 : 12;
                if (Integer.parseInt(hoursTextField.getText()) >= 0
                        && Integer.parseInt(hoursTextField.getText()) <= upperLimit)
                {
                    result = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Invalid input exception: {}", nfe.getMessage());
            }
        }
        logger.debug("validate hours text field result: {}", result);
        return result;
    }

    /**
     * Validates the minutes text field
     * @return true if the text field is valid
     */
    public boolean validateMinutesTextField()
    {
        boolean result = false;
        if (minutesTextField.getText().isEmpty() && (!hoursTextField.getText().isEmpty()
                || !secondsTextField.getText().isEmpty()))
        {
            result = true;
        }
        else if (areAllBlank())
        {
            return true;
        }
        else if (!minutesTextField.getText().isEmpty())
        {
            try {
                if (Integer.parseInt(minutesTextField.getText()) >= 0 &&
                        Integer.parseInt(minutesTextField.getText()) <= 59)
                {
                    result = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Invalid input exception: {}", nfe.getMessage());
            }
        }
        logger.debug("validate minutes text field result: {}", result);
        return result;
    }

    /**
     * Validates the seconds text field
     * @return true if the text field is valid
     */
    public boolean validateSecondsTextField()
    {
        boolean result = false;
        if (secondsTextField.getText().isEmpty() && (!hoursTextField.getText().isEmpty()
                || !minutesTextField.getText().isEmpty()))
        {
            result = true;
        }
        else if (areAllBlank())
        {
            return true;
        }
        else if (!secondsTextField.getText().isEmpty())
        {
            try
            {
                if (Integer.parseInt(secondsTextField.getText()) >= 0 &&
                        Integer.parseInt(secondsTextField.getText()) <= 59)
                {
                    result = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Invalid input exception: {}", nfe.getMessage());
            }
        }
        logger.debug("validate seconds text field result: {}", result);
        return result;
    }

    /**
     * Checks if all text fields are not zeroes or empty
     * @return true if all text fields are not zeroes or empty
     */
    public boolean areAllNotZeroes()
    {
        boolean hoursIsZero = ZERO.equals(hoursTextField.getText());
        boolean minutesIsZero = ZERO.equals(minutesTextField.getText());
        boolean secondsIsZero = ZERO.equals(secondsTextField.getText());
        boolean allNotZero = !(hoursIsZero && minutesIsZero && secondsIsZero);
        logger.debug("are all not zeroes: {}", allNotZero);
        return allNotZero;
    }

    /**
     * Checks if all text fields are blank or empty
     * @return true if all text fields are blank or empty
     */
    public boolean areAllBlank()
    {
        boolean hoursIsBlank = StringUtils.isBlank(hoursTextField.getText());
        boolean minutesIsBlank = StringUtils.isBlank(minutesTextField.getText());
        boolean secondsIsBlank = StringUtils.isBlank(secondsTextField.getText());
        boolean allBlank = hoursIsBlank && minutesIsBlank && secondsIsBlank;
        logger.debug("are all blank: {}", allBlank);
        return allBlank;
    }

    /**
     * Validates all text fields
     * @return true if all text fields are valid
     */
    public boolean validTextFields()
    {
        boolean validHours = validateHoursTextField();
        boolean validMinutes = validateMinutesTextField();
        boolean validSeconds = validateSecondsTextField();
        if (EMPTY.equals(hoursTextField.getText()) || ZERO.equals(hoursTextField.getText()))
            hoursTextField.setText(ZERO);
        if (EMPTY.equals(minutesTextField.getText()) || ZERO.equals(minutesTextField.getText()))
            minutesTextField.setText(ZERO);
        if (EMPTY.equals(secondsTextField.getText()) || ZERO.equals(secondsTextField.getText()))
            secondsTextField.setText(ZERO);
        if (!areAllNotZeroes())
        {
            hoursTextField.setText(EMPTY);
            minutesTextField.setText(EMPTY);
            secondsTextField.setText(EMPTY);
            return false;
        }
        boolean areAllNotZeroes = areAllNotZeroes();
        boolean validInputs = validHours && validMinutes && validSeconds && areAllNotZeroes;
        logger.info("inputs are valid: {}", validInputs);
        return validInputs;
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
            try
            {
                setupTimersTableDefaults(false);
                sleep(1000);
            }
            catch (InterruptedException e)
            { printStackTrace(e, e.getMessage()); }
        }
    }

    /** Returns the clock frame */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    /** Returns the layout manager */
    public GridBagLayout getGridBagLayout() { return this.layout; }
    /** Returns the layout constraints */
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    /** Returns the clock */
    public Clock getClock() { return clockFrame.getClock(); }
    /** Returns the name text field */
    public JTextField getNameTextField() { return this.nameTextField; }
    /** Returns the hours text field */
    public JTextField getHoursTextField() { return hoursTextField; }
    /** Returns the minutes text field */
    public JTextField getMinutesTextField() { return minutesTextField; }
    /** Returns the seconds text field */
    public JTextField getSecondsTextField() { return secondsTextField; }
    /** Returns the set timer button */
    public JButton getSetTimerButton() { return setTimerButton; }
    /** Returns the timers table */
    public JTable getTimersTable() { return timersTable; }

    /** Sets the clock frame */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    /** Sets the layout manager */
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; logger.debug("layout set"); }
    /** Sets the layout constraints */
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    /** Sets the clock */
    public void setClock(Clock clock) { this.clock = clock; logger.debug("Clock set in TimerPanel"); }
    /** Sets the name text field */
    public void setNameTextField(JTextField nameTextField) { this.nameTextField = nameTextField; logger.debug("nameTextField set in TimerPanel"); }
    /** Sets the hours text field */
    public void setHoursTextField(JTextField hoursTextField) { this.hoursTextField = hoursTextField; logger.debug("hoursTextField set in TimerPanel"); }
    /** Sets the minutes text field */
    public void setMinutesTextField(JTextField minutesTextField) { this.minutesTextField = minutesTextField; logger.debug("minutesTextField set in TimerPanel"); }
    /** Sets the seconds text field */
    public void setSecondsTextField(JTextField secondsTextField) { this.secondsTextField = secondsTextField; logger.debug("secondsTextField set in TimerPanel"); }
    /** Sets the set timer button */
    public void setSetTimerButton(JButton setTimerButton) { this.setTimerButton = setTimerButton; logger.debug("setTimerButton set in TimerPanel");}
    /** Sets the timers table */
    public void setTimersTable(JTable timersTable) { this.timersTable = timersTable; logger.debug("timersTable set in TimerPanel");  }
}
