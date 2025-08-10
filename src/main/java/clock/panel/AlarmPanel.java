package clock.panel;

import clock.entity.Alarm;
import clock.entity.ButtonColumn;
import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.plexus.util.StringUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static clock.util.Constants.*;
import static java.lang.Thread.sleep;
import static java.time.DayOfWeek.*;

/**
 * Alarm Panel
 * <p>
 * Used to set and view alarms. The alarms are displayed
 * in a table below where they're created.
 * To set an alarm, you must enter an Hour, minutes,
 * and the time, AM or PM, case insensitive. A name can
 * also be given but it is not required. You must also
 * provide some combination of day or days the alarm will
 * sound off on.
 * Once created, the alarm will be displayed in the table,
 * most likely currently sleeping. You can edit the sleeping
 * alarm by clicking on the Sleeping button. This will remove
 * the alarm from the table.
 * Once the alarm goes off, a sound will play and you can click
 * snooze or stop. Snoozing will stop the alarm for 7 more
 * minutes and then it will sound off again. Stopping the alarm
 * will stop the sound but the alarm will remain on the table.
 *
 * @author michael ball
 * @version 2.0
 */
public class AlarmPanel extends ClockPanel implements Runnable
{
    private static final Logger logger = LogManager.getLogger(AlarmPanel.class);
    private Thread thread;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel nameLabel,
                   hoursLabel,
                   minutesLabel,
                   ampmLabel;
    private JTextField nameTextField,
                       hoursTextField,
                       minutesTextField;
    private JComboBox<String> ampmDropDown;
    private JCheckBox mondayCheckBox,tuesdayCheckBox,
                      wednesdayCheckBox,thursdayCheckBox,
                      fridayCheckBox,saturdayCheckBox,
                      sundayCheckBox, weekdaysCheckBox,
                      weekendsCheckBox;
    private JButton setAlarmButton;
    private JTable alarmsTable;
    private JScrollPane scrollTable;
    private ClockFrame clockFrame;
    private Clock clock;

    /**
     * Main constructor for creating the AlarmPanel
     * @param clockFrame the clockFrame object reference
     */
    public AlarmPanel(ClockFrame clockFrame)
    {
        super();
        logger.debug("Creating AlarmPanel");
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setMaximumSize(ClockFrame.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setupAlarmPanel();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating Alarm Panel");
    }

    /**
     * Adds all the components to the AlarmPanel
     */
    private void setupAlarmPanel()
    {
        logger.info("setup AlarmPanel");
        setNameLabel(new JLabel(NAME, SwingConstants.CENTER));
        getNameLabel().setName(NAME+LABEL);
        setNameTextField(new JTextField(EMPTY, 10));
        getNameTextField().setName(NAME + TEXT_FIELD);

        setHoursLabel(new JLabel(Hours, SwingConstants.CENTER));
        getHoursLabel().setName(Hours+LABEL);
        setHoursTextField(new JTextField(EMPTY, 4));
        getHoursTextField().setName(HOUR + TEXT_FIELD);

        setMinutesLabel(new JLabel(Minutes, SwingConstants.CENTER));
        getMinutesLabel().setName(Minutes+LABEL);
        setMinutesTextField(new JTextField(EMPTY, 4));
        getMinutesTextField().setName(MIN + TEXT_FIELD);

        setAmpmLabel(new JLabel(AMPM, SwingConstants.CENTER));
        getAmpmLabel().setName(AMPM+LABEL);
        setupAmpmDropDownSelection();

        List.of(nameTextField, hoursTextField, minutesTextField).forEach(textField -> {
            textField.setFont(ClockFrame.font20);
            textField.setForeground(Color.BLACK);
            textField.setBorder(new LineBorder(Color.ORANGE));
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+TEXT_FIELD -> {
                            if (nameTextField.getText().equals(ALARM+(Alarm.alarmsCounter+1)))
                            { nameTextField.setText(EMPTY); }
                            logger.debug("Focus gained on name field");
                        }
                        case HOUR+TEXT_FIELD -> logger.info("focus gained on hours field");
                        case MIN+TEXT_FIELD -> logger.info("focus gained on minutes field");
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    switch (e.getSource() instanceof JTextField textField ? textField.getName() : null)
                    {
                        case NAME+TEXT_FIELD -> {
                            if (nameTextField.getText().isBlank() || nameTextField.getText().isEmpty())
                            { nameTextField.setText(ALARM+(Alarm.alarmsCounter+1)); }
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
                                displayPopupMessage(ALARM_ERROR, "Hours must be between 0 and "+upperLimit, 0);
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
                                displayPopupMessage(ALARM_ERROR, "Minutes must be between 0 and 59", 0);
                                minutesTextField.setBorder(new LineBorder(Color.RED));
                                minutesTextField.requestFocusInWindow();
                            }
                        }
                    }
                }
            });
        });
        List.of(nameLabel, hoursLabel, minutesLabel, ampmLabel).forEach(label -> {
            label.setFont(ClockFrame.font20);
            label.setForeground(Color.WHITE);
        });

        // setup Set button
        setSetAlarmButton(new JButton(SET));
        getSetAlarmButton().setFont(ClockFrame.font20);
        getSetAlarmButton().setOpaque(true);
        getSetAlarmButton().setBackground(Color.BLACK);
        getSetAlarmButton().setForeground(Color.BLACK);
        getSetAlarmButton().addActionListener(this::setAlarm);
        // setup checkboxes
        setupCheckBoxes();
        setupAlarmsTableDefaults(true);
        setupDefaultValues();
    }

    /**
     * The main method used to set up
     * the selection dropdown box and adds
     * its functionality
     */
    private void setupAmpmDropDownSelection()
    {
        setAmpmDropDown(new JComboBox<>(new String[]{AM, PM}));
        ampmDropDown.setSelectedItem(clockFrame.getClock().getAMPM().equals(AM)?AM:PM);
        ampmDropDown.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    /**
     * Sets up the checkboxes for the Alarm Panel
     */
    private void setupCheckBoxes()
    {
        logger.info("setup checkboxes");
        setSundayCheckBox(new JCheckBox(SU.toUpperCase(), false));
        setMondayCheckBox(new JCheckBox(M, false));
        setTuesdayCheckBox(new JCheckBox(T, false));
        setWednesdayCheckBox(new JCheckBox(W, false));
        setThursdayCheckBox(new JCheckBox(TH.toUpperCase(), false));
        setFridayCheckBox(new JCheckBox(F, false));
        setSaturdayCheckBox(new JCheckBox(S, false));
        setWeekdaysCheckBox(new JCheckBox(WEEK, false));
        setWeekendsCheckBox(new JCheckBox(WEEKEND, false));
        List.of(sundayCheckBox, mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox,
                fridayCheckBox, saturdayCheckBox, weekdaysCheckBox, weekendsCheckBox).forEach(checkBox -> {
                    checkBox.setFont(ClockFrame.font20);
                    checkBox.setBackground(Color.BLACK);
                    checkBox.setForeground(Color.WHITE);
        });
        sundayCheckBox.addActionListener(action -> {
            sundayCheckBox.setSelected(sundayCheckBox.isSelected());
            logger.info("sundayCheckBox: {}", sundayCheckBox.isSelected());
        });
        mondayCheckBox.addActionListener(action -> {
            mondayCheckBox.setSelected(mondayCheckBox.isSelected());
            logger.info("mondayCheckBox: {}", mondayCheckBox.isSelected());
        });
        tuesdayCheckBox.addActionListener(action -> {
            tuesdayCheckBox.setSelected(tuesdayCheckBox.isSelected());
            logger.info("tuesdayCheckBox: {}", tuesdayCheckBox.isSelected());
        });
        wednesdayCheckBox.addActionListener(action -> {
            wednesdayCheckBox.setSelected(wednesdayCheckBox.isSelected());
            logger.info("wednesdayCheckBox: {}", wednesdayCheckBox.isSelected());
        });
        thursdayCheckBox.addActionListener(action -> {
            thursdayCheckBox.setSelected(thursdayCheckBox.isSelected());
            logger.info("thursdayCheckBox: {}", thursdayCheckBox.isSelected());
        });
        fridayCheckBox.addActionListener(action -> {
            fridayCheckBox.setSelected(fridayCheckBox.isSelected());
            logger.info("fridayCheckBox: {}", fridayCheckBox.isSelected());
        });
        saturdayCheckBox.addActionListener(action -> {
            saturdayCheckBox.setSelected(saturdayCheckBox.isSelected());
            logger.info("saturdayCheckBox: {}", saturdayCheckBox.isSelected());
        });
        weekdaysCheckBox.addActionListener(action -> {
            if (!weekdaysCheckBox.isSelected())
            {
                logger.info("Week checkbox not selected!");
                weekdaysCheckBox.setSelected(false);
                mondayCheckBox.setSelected(false);
                tuesdayCheckBox.setSelected(false);
                wednesdayCheckBox.setSelected(false);
                thursdayCheckBox.setSelected(false);
                fridayCheckBox.setSelected(false);
            }
            else
            {
                logger.info("Week checkbox selected!");
                weekdaysCheckBox.setSelected(true);
                mondayCheckBox.setSelected(true);
                tuesdayCheckBox.setSelected(true);
                wednesdayCheckBox.setSelected(true);
                thursdayCheckBox.setSelected(true);
                fridayCheckBox.setSelected(true);
            }
        });
        weekendsCheckBox.addActionListener(action -> {
            if (!weekendsCheckBox.isSelected())
            {
                logger.info("Weekend checkbox not selected!");
                weekendsCheckBox.setSelected(false);
                saturdayCheckBox.setSelected(false);
                sundayCheckBox.setSelected(false);
            }
            else
            {
                logger.info("Weekend checkbox selected!");
                weekendsCheckBox.setSelected(true);
                saturdayCheckBox.setSelected(true);
                sundayCheckBox.setSelected(true);
            }
        });
    }

    /**
     * Sets default values for the alarm panel
     */
    public void setupDefaultValues()
    {
        nameTextField.requestFocusInWindow();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        resetAlarmPanel();
        setupSettingsMenu();
        clockFrame.setTitle("Alarm Panel");
        start();
    }

    /**
     * Rests all the checkboxes to false
     */
    public void resetAlarmPanel()
    {
        logger.info("reset alarm panel");
        hoursTextField.setText(EMPTY);
        minutesTextField.setText(EMPTY);
        nameTextField.setText(EMPTY);
        sundayCheckBox.setSelected(false);
        mondayCheckBox.setSelected(false);
        tuesdayCheckBox.setSelected(false);
        wednesdayCheckBox.setSelected(false);
        thursdayCheckBox.setSelected(false);
        fridayCheckBox.setSelected(false);
        saturdayCheckBox.setSelected(false);
        weekdaysCheckBox.setSelected(false);
        weekendsCheckBox.setSelected(false);
    }

    /**
     * This method sets up the settings menu for the
     * alarm panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getPauseResumeAllAlarmsSetting());
        clockFrame.getClockMenuBar().getSettingsMenu().add(clockFrame.getClockMenuBar().getResetAlarmsPanelSetting());
    }

    /**
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(nameLabel,0,0,1,1,0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(nameTextField,0,1,1,1, 0,0, 3, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(hoursLabel,0,2,1,1,0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(hoursTextField,0,3,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(minutesLabel,0,4,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(minutesTextField,0,5,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(ampmLabel,0,6,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(ampmDropDown,0,7,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        addComponent(setAlarmButton, 0, 8, 2, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, new Insets(0,0,0,0));
        // new row
        addComponent(mondayCheckBox, 1,0,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(tuesdayCheckBox, 1, 1, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(wednesdayCheckBox, 1, 2, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(thursdayCheckBox, 1, 3, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(fridayCheckBox, 1, 4, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(saturdayCheckBox, 1, 5, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(sundayCheckBox, 1, 6, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(weekdaysCheckBox, 1, 7, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        addComponent(weekendsCheckBox, 1, 8, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0));
        // new row
        addComponent(scrollTable, 2, 0, 9, 1, 0, 0, 1, 2, GridBagConstraints.BOTH, new Insets(0,0,0,0));

        constraints.weighty = 4;
        constraints.weightx = 2;
    }

    /**
     * The main method used for adding components
     * to the alarm panel
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
     * Creates the action for the buttons in the alarms table
     * @param columnIndex the index of the column where the button is located
     * @return the Action for the button
     */
    public Action buttonAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                int modelRow = Integer.parseInt( e.getActionCommand() );
                String buttonAction = (String) alarmsTable.getModel().getValueAt(modelRow, columnIndex);
                // find the correct alarm
                Alarm alarm = clock.getListOfAlarms().get(modelRow);
                switch (buttonAction) {
                    case SNOOZE -> {
                        alarmsTable.getModel().setValueAt(SLEEPING, modelRow, 3);
                        alarm.snooze();
                        clock.getListOfAlarms().set(modelRow, alarm);
                    }
                    case SLEEPING -> {
                        logger.info("Edit alarm");
                        alarm.stopAlarm();
                        nameTextField.setText(alarm.getName());
                        hoursTextField.setText(Integer.toString(alarm.getHours()));
                        minutesTextField.setText(Integer.toString(alarm.getMinutes()));
                        ampmDropDown.setSelectedItem(alarm.getAMPM());
                        setCheckBoxesIfWasSelected(alarm);
                        clock.getListOfAlarms().remove(alarm);
                    }
                    case STOP -> {
                        logger.info("Stopping alarm");
                        alarm.stopAlarm();
                        clock.getListOfAlarms().set(modelRow, alarm);
                    }
                    case REMOVE -> {
                        logger.info("Removing {} at row: {}", alarm, modelRow);
                        alarm.stopAlarm();
                        clock.getListOfAlarms().remove(alarm);
                    }
                }
            }
        };
    }

    /**
     * Gets the data for the alarms table
     * @return the data for the alarms table
     */
    public Object[][] getAlarmsTableData()
    {
        return clock.getListOfAlarms().stream()
                .map(alarm -> new Object[] {
                        alarm.getName() != null ? alarm.getName() : alarm.toString(),
                        alarm.getAlarmAsString(),
                        String.join(COMMA+SPACE, alarm.getDaysShortened()),
                        SLEEPING,
                        REMOVE
                })
                .toArray(Object[][]::new);
    }

    /**
     * Gets the column names for the alarms table
     * @return the column names for the alarms table
     */
    public String[] getAlarmsTableColumnNames()
    {
        return new String[]{NAME, ALARM, DAYS, SLEEPING+SLASH+SNOOZE, REMOVE+SLASH+STOP};
    }

    /**
     * Sets the default values for the alarms table
     * @param setup true if we are setting up for the first time.
     */
    public void setupAlarmsTableDefaults(boolean setup)
    {
        Object[][] data = getAlarmsTableData();
        String[] columnNames = getAlarmsTableColumnNames();
        if (setup)
        {
            alarmsTable = new JTable(new DefaultTableModel(data, columnNames));
            alarmsTable.setPreferredScrollableViewportSize(alarmsTable.getPreferredSize());
            alarmsTable.setFont(ClockFrame.font10);
            alarmsTable.setBackground(Color.BLACK);
            alarmsTable.setForeground(Color.WHITE);
            alarmsTable.setFillsViewportHeight(true);
            scrollTable = new JScrollPane(alarmsTable);
        }
        else
        {
            // only update if the timers count changes
            if(alarmsTable.getModel().getRowCount() != data.length)
            {
                alarmsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
                new ButtonColumn(alarmsTable, buttonAction(3), 3);
                new ButtonColumn(alarmsTable, buttonAction(4), 4);
            }
            else
            {
                AtomicInteger rowIndex = new AtomicInteger();
                clock.getListOfAlarms().forEach(alarm -> {
                    String currentAlarm = alarmsTable.getValueAt(rowIndex.get(), 0).toString();
                    if (currentAlarm.equals(alarm.getName())) {
                        alarmsTable.setValueAt(alarm.getAlarmAsString(), rowIndex.get(), 1);
                    }
                    // update buttons to show restart or remove
                    if (alarm.isAlarmGoingOff() && !alarm.isSnoozing()) {
                        alarmsTable.getModel().setValueAt(SNOOZE, rowIndex.get(), 3);
                        alarmsTable.getModel().setValueAt(STOP, rowIndex.get(), 4);
                        new ButtonColumn(alarmsTable, buttonAction(3), 3);
                        new ButtonColumn(alarmsTable, buttonAction(4), 4);
                    }
                    else {
                        alarmsTable.getModel().setValueAt(SLEEPING, rowIndex.get(), 3);
                        alarmsTable.getModel().setValueAt(REMOVE, rowIndex.get(), 4);
                        new ButtonColumn(alarmsTable, buttonAction(3), 3);
                        new ButtonColumn(alarmsTable, buttonAction(4), 4);
                    }
                    rowIndex.getAndIncrement();
                });
            }
        }
    }

    /**
     * Validates the hours text field is a valid hour
     * Allows military time format if the clock is set to show military time
     * @return boolean true if the first text field is valid
     */
    boolean validateHoursTextField()
    {
        boolean result = false;
        if (areAllBlank())
        {
            return true;
        }
        else if (!hoursTextField.getText().isEmpty())
        {
            int upperLimit = clockFrame.getClock().isShowMilitaryTime() ? 23 : 12;
            try
            {
                if (Integer.parseInt(hoursTextField.getText()) >= 0
                        && Integer.parseInt(hoursTextField.getText()) <= upperLimit)
                {
                    result = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Invalid input in hours text field: {}", nfe.getMessage());
            }
        }
        logger.debug("validate hours {} text field: {}", hoursTextField.getText(), result);
        return result;
    }

    /**
     * Validates the minutes text field
     * @return boolean true if the second text field is valid
     */
    boolean validateMinutesTextField()
    {
        boolean result = false;
        if (areAllBlank())
        {
            return true;
        }
        else if (!minutesTextField.getText().isEmpty())
        {
            try
            {
                if (Integer.parseInt(minutesTextField.getText()) >= 0 &&
                        Integer.parseInt(minutesTextField.getText()) <= 59)
                {
                    result = true;
                }
            }
            catch (NumberFormatException nfe)
            {
                logger.debug("Invalid input in minutes text field: {}", nfe.getMessage());
            }
        }
        logger.debug("validate minutes {} text field: {}", minutesTextField.getText(), result);
        return result;
    }

    /**
     * Validates that at least one checkbox is selected
     * @return boolean true if at least one checkbox is selected
     */
    boolean validateTheCheckBoxes(List<DayOfWeek> days)
    {
        boolean result = !days.isEmpty();
        logger.debug("checkboxes are {}", result);
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
        boolean allNotZero = !(hoursIsZero && minutesIsZero);
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
        boolean allBlank = hoursIsBlank && minutesIsBlank;
        logger.debug("are all blank: {}", allBlank);
        return allBlank;
    }

    /** Validates all the inputs used to create an alarm */
    public boolean validateAllInputs()
    {
        boolean allInputsAreValid;
        boolean validHours = validateHoursTextField();
        boolean validMinutes = validateMinutesTextField();
        if (areAllBlank() && !nameTextField.getText().isEmpty())
        {
            return false;
        }
        boolean validCheckboxes = validateTheCheckBoxes(getDaysChecked());
        allInputsAreValid = validHours && validMinutes && validCheckboxes
                && areAllNotZeroes() && !areAllBlank();
        logger.debug("all inputs are valid: {}", allInputsAreValid);
        return allInputsAreValid;
    }

    /**
     * Sets the action for the set alarm button
     */
    public void setAlarm(ActionEvent action)
    {
        logger.info("set alarm");
        try {
            Alarm alarm = createAlarm();
            // checks equality
            if (!clock.getListOfAlarms().contains(alarm))
            {
                clock.getListOfAlarms().add(alarm);
                resetAlarmPanel();
            }
            else
            {
                logger.warn("alarm already exists");
                displayPopupMessage(ALARM_ERROR, "Alarm already exists!", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (InvalidInputException iie)
        {
            logger.error("Invalid input: {}", iie.getMessage());
            displayPopupMessage(ALARM_ERROR, iie.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates an alarm and sets the latest one created as the currentAlarm
     * defined in setAlarm
     * @return Alarm
     */
    private Alarm createAlarm()
    {
        logger.info("creating alarm");
        Alarm alarm = null;
        if (validateAllInputs())
        {
            int hour = Integer.parseInt(hoursTextField.getText());
            int minutes = Integer.parseInt(minutesTextField.getText());
            String ampm = Objects.requireNonNull(ampmDropDown.getSelectedItem()).toString();
            if (hour == 0 && ampm.equals(AM)) hour = 12; // convert 0 AM to 12 AM
            String alarmName = nameTextField.getText();
            List<DayOfWeek> days = getDaysChecked();
            alarm = new Alarm(alarmName, hour, minutes, ampm, days, false, getClock());
            logger.info("Created an alarm: {}", alarm);
        }
        else {
            boolean validHours = validateHoursTextField();
            boolean validMinutes = validateMinutesTextField();
            boolean validCheckboxes = validateTheCheckBoxes(getDaysChecked());
            if (areAllBlank()) throw new InvalidInputException("Hours and minutes must not be blank");
            if (!validHours) throw new InvalidInputException("Invalid hours input");
            if (!validMinutes) throw new InvalidInputException("Invalid minutes input");
            if (!validCheckboxes) throw new InvalidInputException("At least one checkbox must be selected");
        }
        return alarm;
    }

    /**
     * Returns a list of days that were checked
     * @return List<DayOfWeek> the days selected
     */
    public List<DayOfWeek> getDaysChecked()
    {
        logger.info("check which checkBoxes were checked");
        List<DayOfWeek> daysSelected = new ArrayList<>();
        if (mondayCheckBox.isSelected()) { daysSelected.add(MONDAY); }
        if (tuesdayCheckBox.isSelected()) { daysSelected.add(TUESDAY); }
        if (wednesdayCheckBox.isSelected()) { daysSelected.add(WEDNESDAY); }
        if (thursdayCheckBox.isSelected()) { daysSelected.add(THURSDAY); }
        if (fridayCheckBox.isSelected()) { daysSelected.add(FRIDAY); }
        if (saturdayCheckBox.isSelected()) { daysSelected.add(SATURDAY); }
        if (sundayCheckBox.isSelected()) { daysSelected.add(SUNDAY); }
        return daysSelected;
    }

    /**
     * Sets the checkboxes if the alarm was selected
     * because it is going to be updated
     * @param alarmToUpdate the alarm to update
     */
    public void setCheckBoxesIfWasSelected(Alarm alarmToUpdate)
    {
        logger.info("setCheckBoxesIfWasSelected");
        if (alarmToUpdate.getDays().contains(MONDAY)) { mondayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(TUESDAY)) { tuesdayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(WEDNESDAY)) { wednesdayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(THURSDAY)) { thursdayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(FRIDAY)) { fridayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(SATURDAY)) { saturdayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(SUNDAY)) { sundayCheckBox.setSelected(true); }
        if (alarmToUpdate.getDays().contains(MONDAY) &&
                alarmToUpdate.getDays().contains(TUESDAY) &&
                alarmToUpdate.getDays().contains(WEDNESDAY) &&
                alarmToUpdate.getDays().contains(THURSDAY) &&
                alarmToUpdate.getDays().contains(FRIDAY)) {
            mondayCheckBox.setSelected(true);
            tuesdayCheckBox.setSelected(true);
            wednesdayCheckBox.setSelected(true);
            thursdayCheckBox.setSelected(true);
            fridayCheckBox.setSelected(true);
            weekdaysCheckBox.setSelected(true);
        }
        if (alarmToUpdate.getDays().contains(SATURDAY) &&
                alarmToUpdate.getDays().contains(SUNDAY)) {
            saturdayCheckBox.setSelected(true);
            sundayCheckBox.setSelected(true);
            weekendsCheckBox.setSelected(true);
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

    /** Starts the alarm panel thread and internally calls the run method. */
    public void start()
    {
        logger.debug("starting alarm panel");
        if (thread == null)
        {
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Stops the alarm panel thread. */
    public void stop()
    {
        logger.debug("stopping alarm panel");
        thread = null;
    }

    /** This method runs the alarm panel thread and updates the alarms table every second. */
    @Override
    public void run()
    {
        logger.debug("running alarm panel");
        while (thread != null)
        {
            try {
                setupAlarmsTableDefaults(false);
                sleep(1000);
            }
            catch (InterruptedException e) { printStackTrace(e, e.getMessage());}
        }
    }

    /* Getters */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clockFrame.getClock(); }
    public JLabel getNameLabel() { return this.nameLabel; }
    public JLabel getHoursLabel() { return this.hoursLabel; }
    public JLabel getMinutesLabel() { return this.minutesLabel; }
    public JLabel getAmpmLabel() { return this.ampmLabel; }
    public JTextField getNameTextField() { return this.nameTextField; }
    public JTextField getHoursTextField() { return this.hoursTextField; }
    public JTextField getMinutesTextField() { return this.minutesTextField; }
    public JComboBox<String> getAmpmDropDown() { return this.ampmDropDown; }
    public JButton getSetAlarmButton() { return this.setAlarmButton; }
    public JCheckBox getMondayCheckBox() { return mondayCheckBox; }
    public JCheckBox getTuesdayCheckBox() { return tuesdayCheckBox; }
    public JCheckBox getWednesdayCheckBox() { return wednesdayCheckBox; }
    public JCheckBox getThursdayCheckBox() { return thursdayCheckBox; }
    public JCheckBox getFridayCheckBox() { return fridayCheckBox; }
    public JCheckBox getSaturdayCheckBox() { return saturdayCheckBox; }
    public JCheckBox getSundayCheckBox() { return sundayCheckBox; }
    public JCheckBox getWeekdaysCheckBox() { return weekdaysCheckBox; }
    public JCheckBox getWeekendsCheckBox() { return weekendsCheckBox; }
    public JTable getAlarmsTable() { return this.alarmsTable; }

    /* Setters */
    public void setClock(Clock clock) { this.clock = clock; logger.info("clock set"); }
    protected void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; logger.debug("layout set"); }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; logger.debug("constraints set"); }
    protected void setNameLabel(JLabel nameLabel) { this.nameLabel = nameLabel; logger.debug("nameLabel set"); }
    protected void setHoursLabel(JLabel alarmLabel1) { this.hoursLabel = alarmLabel1; logger.debug("hoursLabel set"); }
    protected void setMinutesLabel(JLabel alarmLabel2) { this.minutesLabel = alarmLabel2; logger.debug("minutesLabel set"); }
    protected void setAmpmLabel(JLabel alarmLabel3) { this.ampmLabel = alarmLabel3; logger.debug("ampmLabel set"); }
    protected void setAmpmDropDown(JComboBox<String> ampmDropDown) { this.ampmDropDown = ampmDropDown; logger.debug("ampmDropDown set"); }
    protected void setNameTextField(JTextField nameTextField) { this.nameTextField = nameTextField; logger.debug("nameTextField set"); }
    protected void setHoursTextField(JTextField textField1) { this.hoursTextField = textField1; logger.debug("hoursTextField set"); }
    protected void setMinutesTextField(JTextField textField2) { this.minutesTextField = textField2; logger.debug("minutesTextField set"); }
    protected void setSetAlarmButton(JButton setAlarmButton) { this.setAlarmButton = setAlarmButton; logger.debug("setAlarmButton set"); }
    protected void setMondayCheckBox(JCheckBox mondayCheckBox) { this.mondayCheckBox = mondayCheckBox; logger.debug("mondayCheckBox set"); }
    protected void setTuesdayCheckBox(JCheckBox tuesdayCheckBox) { this.tuesdayCheckBox = tuesdayCheckBox; logger.debug("tuesdayCheckBox set"); }
    protected void setWednesdayCheckBox(JCheckBox wednesdayCheckBox) { this.wednesdayCheckBox = wednesdayCheckBox; logger.debug("wednesdayCheckBox set"); }
    protected void setThursdayCheckBox(JCheckBox thursdayCheckBox) { this.thursdayCheckBox = thursdayCheckBox; logger.debug("thursdayCheckBox set"); }
    protected void setFridayCheckBox(JCheckBox fridayCheckBox) { this.fridayCheckBox = fridayCheckBox; logger.debug("fridayCheckBox set"); }
    protected void setSaturdayCheckBox(JCheckBox saturdayCheckBox) { this.saturdayCheckBox = saturdayCheckBox; logger.debug("saturdayCheckBox set"); }
    protected void setSundayCheckBox(JCheckBox sundayCheckBox) { this.sundayCheckBox = sundayCheckBox; logger.debug("sundayCheckBox set"); }
    protected void setWeekdaysCheckBox(JCheckBox weekdaysCheckBox) { this.weekdaysCheckBox = weekdaysCheckBox; logger.debug("weekCheckBox set"); }
    protected void setWeekendsCheckBox(JCheckBox weekendsCheckBox) { this.weekendsCheckBox = weekendsCheckBox; logger.debug("weekendCheckBox set"); }
}