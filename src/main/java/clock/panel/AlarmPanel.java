package clock.panel;

import clock.entity.Alarm;
import clock.entity.ButtonColumn;
import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
 * Used to set and view alarms. The
 * alarms can be viewed in the textarea and in the
 * menu (if you CTRL+A into the panel).
 * Clicking on an alarm in the View Alarms menu will
 * remove it from the menu and from the textarea inside
 * the AlarmPanel. However it will be visible on the panel
 * itself. Update as needed, or click set to save it.
 * Going away from the alarm panel without clicking Set will
 * delete that selected alarm permanently.
 * To set an alarm, you must enter an Hour, some minutes,
 * and the time, AM or PM, case insensitive. The alarm accepts
 * military time format. Just make sure the value makes sense
 * and an alarm will be created.
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
    private JLabel hoursLabel,
                   minutesLabel,
                   ampmLabel,
                   nameLabel;
    private JComboBox<String> ampmDropDown;
    private JCheckBox mondayCheckBox,tuesdayCheckBox,wednesdayCheckBox,thursdayCheckBox,
                      fridayCheckBox,saturdayCheckBox,sundayCheckBox,weekCheckBox,weekendCheckBox;
    private JTextField nameTextField,
                       hoursTextField,
                       minutesTextField;
    private JTable alarmsTable;
    private JScrollPane scrollTable;
    private JButton setAlarmButton; // set-alarm button
    private ClockFrame clockFrame; // the clockFrame
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
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupAlarmPanel();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating Alarm Panel");
    }

    /**
     * Adds all the components to the AlarmPanel
     */
    void setupAlarmPanel()
    {
        logger.info("setup AlarmPanel");
        nameLabel = new JLabel(NAME, SwingConstants.CENTER);
        nameLabel.setFont(ClockFrame.font20);
        nameLabel.setForeground(Color.WHITE);
        nameTextField = new JTextField(2);
        nameTextField.setName(NAME+TEXT_FIELD);

        hoursLabel = new JLabel(Hours, SwingConstants.CENTER);
        hoursLabel.setFont(ClockFrame.font20);
        hoursLabel.setForeground(Color.WHITE);
        hoursTextField = new JTextField(2);
        hoursTextField.setName(HOUR+TEXT_FIELD);
        hoursTextField.requestFocusInWindow();

        minutesLabel = new JLabel(Minutes, SwingConstants.CENTER);
        minutesLabel.setFont(ClockFrame.font20);
        minutesLabel.setForeground(Color.WHITE);
        minutesTextField = new JTextField(2);
        minutesTextField.setName(MIN+TEXT_FIELD);

        List.of(nameTextField, hoursTextField, minutesTextField).forEach(textField -> {
            textField.setFont(ClockFrame.font20);
            textField.setSize(new Dimension(50,50));
            textField.setMaximumSize(textField.getSize());
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
                            try {
                                validateHoursTextField();
                                hoursTextField.setBorder(new LineBorder(Color.ORANGE));
                            } catch (InvalidInputException ex) {
                                logger.warn("Hour field invalid: {}", hoursTextField.getText());
                                hoursTextField.setBorder(new LineBorder(Color.RED));
                                hoursTextField.requestFocusInWindow();
                            }
                        }
                        case MIN+TEXT_FIELD -> {
                            try {
                                validateMinutesTextField();
                                minutesTextField.setBorder(new LineBorder(Color.ORANGE));
                            } catch (InvalidInputException ex) {
                                logger.warn("Minute field is invalid: {}", minutesTextField.getText());
                                minutesTextField.setBorder(new LineBorder(Color.RED));
                                minutesTextField.requestFocusInWindow();
                            }
                        }
                    }
                }
            });
        });

        ampmLabel = new JLabel(AMPM, SwingConstants.CENTER);
        ampmLabel.setFont(ClockFrame.font20);
        ampmLabel.setForeground(Color.WHITE);
        setupOptionsSelection();
        // setup Set button
        setAlarmButton = new JButton(SET);
        setAlarmButton.setFont(ClockFrame.font20);
        setAlarmButton.setOpaque(true);
        setAlarmButton.setBackground(Color.BLACK);
        setAlarmButton.setForeground(Color.BLACK);
        setAlarmButton.addActionListener(this::setAlarm);
        // setup checkboxes
        setupCheckBoxes();
        setupAlarmsTableDefaults(true);
        setupDefaultValues();
    }

    /**
     * Sets default values for the alarm panel
     */
    public void setupDefaultValues()
    {
        nameTextField.setText(EMPTY);
        hoursTextField.setText(EMPTY);
        minutesTextField.setText(EMPTY);
        resetJCheckBoxes();
        setupSettingsMenu();
        clockFrame.setTitle("Alarm Panel");
        start();
    }

    /**
     * This method sets up the settings menu for the
     * alarm panel.
     */
    public void setupSettingsMenu()
    {
        clockFrame.clearSettingsMenu();
        logger.info("No settings defined up for Alarm Panel");
    }

    /**
     * The main method used to set up
     * the selection dropdown box and adds
     * its functionality
     */
    private void setupOptionsSelection()
    {
        setDateOperationsDropdown(new JComboBox<>(new String[]{AM, PM}));
        ampmDropDown.setSelectedItem(clockFrame.getClock().getAMPM().equals(AM)?AM:PM);
        ampmDropDown.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    /**
     * Sets up the checkboxes for the Alarm Panel
     */
    public void setupCheckBoxes()
    {
        logger.info("setup checkboxes");
        sundayCheckBox = new JCheckBox(SUNDAY.toString().substring(0,2), false);
        mondayCheckBox = new JCheckBox(MONDAY.toString().substring(0,1), false);
        tuesdayCheckBox = new JCheckBox(TUESDAY.toString().substring(0,1), false);
        wednesdayCheckBox = new JCheckBox(WEDNESDAY.toString().substring(0,1), false);
        thursdayCheckBox = new JCheckBox(THURSDAY.toString().substring(0,2), false);
        fridayCheckBox = new JCheckBox(FRIDAY.toString().substring(0,1), false);
        saturdayCheckBox = new JCheckBox(SATURDAY.toString().substring(0,1), false);
        weekCheckBox = new JCheckBox(WEEK, false);
        weekendCheckBox = new JCheckBox(WEEKEND, false);
        List.of(sundayCheckBox, mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox, fridayCheckBox, saturdayCheckBox, weekCheckBox, weekendCheckBox)
                .forEach(checkBox -> {
                            checkBox.setFont(ClockFrame.font20);
                            checkBox.setBackground(Color.BLACK);
                            checkBox.setForeground(Color.WHITE);
                        }
                );
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
        weekCheckBox.addActionListener(action -> {
            if (!weekCheckBox.isSelected())
            {
                logger.info("Week checkbox not selected!");
                weekCheckBox.setSelected(false);
                mondayCheckBox.setSelected(false);
                tuesdayCheckBox.setSelected(false);
                wednesdayCheckBox.setSelected(false);
                thursdayCheckBox.setSelected(false);
                fridayCheckBox.setSelected(false);
            }
            else
            {
                logger.info("Week checkbox selected!");
                weekCheckBox.setSelected(true);
                mondayCheckBox.setSelected(true);
                tuesdayCheckBox.setSelected(true);
                wednesdayCheckBox.setSelected(true);
                thursdayCheckBox.setSelected(true);
                fridayCheckBox.setSelected(true);
            }
        });
        weekendCheckBox.addActionListener(action -> {
            if (!weekendCheckBox.isSelected())
            {
                logger.info("Weekend checkbox not selected!");
                weekendCheckBox.setSelected(false);
                saturdayCheckBox.setSelected(false);
                sundayCheckBox.setSelected(false);
            }
            else
            {
                logger.info("Weekend checkbox selected!");
                weekendCheckBox.setSelected(true);
                saturdayCheckBox.setSelected(true);
                sundayCheckBox.setSelected(true);
            }
        });
    }

    /**
     * Sets the action for the set alarm button
     */
    public void setAlarm(ActionEvent action)
    {
        logger.info("creating new alarm");
        try { validateHoursTextField(); }
        catch (Exception e) { displayPopupMessage(ALARM_ERROR, e.getMessage(), 0); return; }
        try { validateMinutesTextField(); }
        catch (Exception e) { displayPopupMessage(ALARM_ERROR, e.getMessage(), 0); return; }
        Alarm alarm = createAlarm();
        // checks equality
        if (alarm != null && !clock.getListOfAlarms().contains(alarm)) {
            clock.getListOfAlarms().add(alarm);
            resetJCheckBoxes();
            // erase input in textFields
            hoursTextField.setText(EMPTY);
            minutesTextField.setText(EMPTY);
            nameTextField.setText(EMPTY);
        }
        else if (alarm == null) {
            logger.warn("alarm is null");
        }
        else {
            logger.warn("alarm already exists");
            displayPopupMessage(ALARM_ERROR, "Alarm already exists!", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(nameLabel,0,0,1,1,0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(nameTextField,0,1,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(hoursLabel,0,2,1,1,0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(hoursTextField,0,3,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(minutesLabel,0,4,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // M
        addComponent(minutesTextField,0,5,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(ampmLabel,0,6,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // Time (AM/PM)
        addComponent(ampmDropDown,0,7,1,1, 0,0, 1, 0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(setAlarmButton, 0, 8, 2, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, new Insets(0,0,0,0)); // Set button
        // new row
        addComponent(mondayCheckBox, 1,0,1,1, 0,0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Monday
        addComponent(tuesdayCheckBox, 1, 1, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Thursday
        addComponent(wednesdayCheckBox, 1, 2, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Sunday
        addComponent(thursdayCheckBox, 1, 3, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Sunday
        addComponent(fridayCheckBox, 1, 4, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        addComponent(saturdayCheckBox, 1, 5, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Tuesday
        addComponent(sundayCheckBox, 1, 6, 1, 1, 0, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Friday
        addComponent(weekCheckBox, 1, 7, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        addComponent(weekendCheckBox, 1, 8, 1, 1, 1, 0, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
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
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String buttonAction = (String) alarmsTable.getModel().getValueAt(modelRow, columnIndex);

                // find the correct alarm
                Alarm alarm = clock.getListOfAlarms().get(modelRow);
                switch (buttonAction) {
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
                    case REMOVE -> {
                        logger.info("Removing {} at row: {}", alarm, modelRow);
                        alarm.stopAlarm();
                        clock.getListOfAlarms().remove(alarm);
                        ((DefaultTableModel)alarmsTable.getModel()).removeRow(modelRow);
                    }
                }
            }
        };
    }

    /**
     * Creates the snooze action for the snooze button
     * @param columnIndex the index of the column where the snooze button is located
     * @return the Action for the snooze button
     */
    public Action snoozeAction(int columnIndex)
    {
        return new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                JTable table = (JTable)e.getSource();
                int modelRow = Integer.valueOf( e.getActionCommand() );
                String buttonAction = (String) table.getModel().getValueAt(modelRow, columnIndex);
                alarmsTable.getModel().setValueAt(SLEEPING, modelRow, 2);
                Alarm alarm = clock.getListOfAlarms().get(modelRow);
                alarm.snooze();
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
        return new String[]{NAME, ALARM, DAYS, SLEEPING+'/'+SNOOZE, REMOVE};
    }

    /**
     * Sets the default values for the alarms table
     * @param setup true if we are setting up for the first time.
     */
    public void setupAlarmsTableDefaults(boolean setup)
    {
        Object[][] data = getAlarmsTableData();
        String[] columnNames = getAlarmsTableColumnNames();
        if (setup) {
            alarmsTable = new JTable(new DefaultTableModel(data, columnNames));
            alarmsTable.setPreferredScrollableViewportSize(alarmsTable.getPreferredSize());//thanks mKorbel +1 http://stackoverflow.com/questions/10551995/how-to-set-jscrollpane-layout-to-be-the-same-as-jtable
            alarmsTable.setFont(ClockFrame.font10);
            alarmsTable.setBackground(Color.BLACK);
            alarmsTable.setForeground(Color.WHITE);
            alarmsTable.setFillsViewportHeight(true);
            scrollTable = new JScrollPane(alarmsTable);
        } else {
            // only update if the timers count changes
            if(alarmsTable.getModel().getRowCount() != data.length) {
                alarmsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
                new ButtonColumn(alarmsTable, snoozeAction(3), 3);
                new ButtonColumn(alarmsTable, buttonAction(4), 4);
            } else {
                AtomicInteger rowIndex = new AtomicInteger();
                clock.getListOfAlarms().forEach(alarm -> {
                    String currentAlarm = alarmsTable.getValueAt(rowIndex.get(), 0).toString();
                    if (currentAlarm.equals(alarm.getName())) {
                        alarmsTable.setValueAt(alarm.getAlarmAsString(), rowIndex.get(), 1);
                    }
                    // update buttons to show restart or remove
                    if (alarm.isAlarmGoingOff()) {
                        alarmsTable.getModel().setValueAt(SNOOZE, rowIndex.get(), 3);
                        new ButtonColumn(alarmsTable, snoozeAction(3), 3);
                    }
                    else {
                        alarmsTable.getModel().setValueAt(SLEEPING, rowIndex.get(), 3);
                        new ButtonColumn(alarmsTable, buttonAction(3), 3);
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
        logger.info("validateFirstTextField");
        if (hoursTextField.getText().isEmpty() && nameTextField.getText().isEmpty()) {
            throw new InvalidInputException("Hour cannot be blank");
        }
        else if (!nameTextField.getText().isEmpty() && !hoursTextField.getText().isEmpty() &&
                !minutesTextField.getText().isEmpty())
        {
            return true;
        }
        else {
            try {
                int upperLimit = clockFrame.getClock().isShowMilitaryTime() ? 23 : 12;
                if (Integer.parseInt(hoursTextField.getText()) <= 0 || Integer.parseInt(hoursTextField.getText()) > upperLimit)
                {
                    throw new InvalidInputException("Hours must be between 0 and " + upperLimit);
                }
            } catch (NumberFormatException nfe) {
                throw new InvalidInputException("Hours must be a number");
            }
        }
        return true;
    }

    /**
     * Validates the minutes text field
     * @return boolean true if the second text field is valid
     */
    boolean validateMinutesTextField()
    {
        logger.info("validate second text field");
        if (minutesTextField.getText().isEmpty() && nameTextField.getText().isEmpty())
        {
            throw new InvalidInputException("Minutes cannot be blank");
        }
        else if (!nameTextField.getText().isEmpty() && !hoursTextField.getText().isEmpty() &&
                !minutesTextField.getText().isEmpty())
        {
            return true;
        }
        else {
            try {
                if (Integer.parseInt(minutesTextField.getText()) < 0 ||
                        Integer.parseInt(minutesTextField.getText()) > 59 )
                {
                    throw new InvalidInputException("Minutes must be between 0 and 59");
                }
            } catch (NumberFormatException nfe) {
                throw new InvalidInputException("Minutes must be a number");
            }
        }
        return true;
    }

    /**
     * Validates that at least one checkbox is selected
     * @return boolean true if at least one checkbox is selected
     */
    boolean validateTheCheckBoxes(List<DayOfWeek> days)
    {
        logger.info("validate checkboxes");
        if (days.isEmpty()) { throw new InvalidInputException("Days cannot be empty"); }
        return true;
    }

    /**
     * Creates an alarm and sets the latest one created as the currentAlarm
     * defined in setAlarm
     * @return Alarm
     */
    public Alarm createAlarm()
    {
        int hour = Integer.parseInt(hoursTextField.getText());
        int minutes = Integer.parseInt(minutesTextField.getText());
        String ampm = Objects.requireNonNull(ampmDropDown.getSelectedItem()).toString();
        String alarmName = nameTextField.getText();
        List<DayOfWeek> days = getDaysChecked();
        Alarm alarm = null;
        try {
            validateTheCheckBoxes(days);
            logger.info("create alarm");
            alarm = new Alarm(alarmName, hour, minutes, ampm, days, false, getClock());
            StringBuilder daysStr = new StringBuilder();
            daysStr.append("days: ");
            for(DayOfWeek day: days)
            {
                daysStr.append(day);
                daysStr.append("\t");
            }
            logger.info("Created an alarm: {}", alarm);
            logger.info("days: {}", daysStr);
            logger.info("Alarm created");
        } catch (InvalidInputException e) {
            displayPopupMessage(ALARM_ERROR, "At least one checkbox must be selected.", 0);
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
     * Rests all the checkboxes to false
     */
    public void resetJCheckBoxes()
    {
        logger.info("resetJCheckBoxes");
        if (sundayCheckBox.isSelected()) { sundayCheckBox.setSelected(false); }
        if (mondayCheckBox.isSelected()) { mondayCheckBox.setSelected(false); }
        if (tuesdayCheckBox.isSelected()) { tuesdayCheckBox.setSelected(false); }
        if (wednesdayCheckBox.isSelected()) { wednesdayCheckBox.setSelected(false); }
        if (thursdayCheckBox.isSelected()) { thursdayCheckBox.setSelected(false); }
        if (fridayCheckBox.isSelected()) { fridayCheckBox.setSelected(false); }
        if (saturdayCheckBox.isSelected()) { saturdayCheckBox.setSelected(false); }
        if (weekCheckBox.isSelected()) { weekCheckBox.setSelected(false); }
        if (weekendCheckBox.isSelected()) { weekendCheckBox.setSelected(false); }
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
            weekCheckBox.setSelected(true);
        }
        if (alarmToUpdate.getDays().contains(SATURDAY) &&
                alarmToUpdate.getDays().contains(SUNDAY)) {
            saturdayCheckBox.setSelected(true);
            sundayCheckBox.setSelected(true);
            weekendCheckBox.setSelected(true);
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
    public JLabel getHoursLabel() { return this.hoursLabel; }
    public JLabel getMinutesLabel() { return this.minutesLabel; }
    public JLabel getAMPMLabel() { return this.ampmLabel; }
    public JTextField getHoursTextField() { return this.hoursTextField; }
    public JTextField getMinutesTextField() { return this.minutesTextField; }
    public JButton getSetAlarmButton() { return this.setAlarmButton; }
    public JCheckBox getMondayCheckBox() { return mondayCheckBox; }
    public JCheckBox getTuesdayCheckBox() { return tuesdayCheckBox; }
    public JCheckBox getWednesdayCheckBox() { return wednesdayCheckBox; }
    public JCheckBox getThursdayCheckBox() { return thursdayCheckBox; }
    public JCheckBox getFridayCheckBox() { return fridayCheckBox; }
    public JCheckBox getSaturdayCheckBox() { return saturdayCheckBox; }
    public JCheckBox getSundayCheckBox() { return sundayCheckBox; }
    public JCheckBox getWeekCheckBox() { return weekCheckBox; }
    public JCheckBox getWeekendCheckBox() { return weekendCheckBox; }
    public JComboBox<String> getJComboBox() { return this.ampmDropDown; }

    /* Setters */
    private void setClockFrame(ClockFrame clockFrame) { this.clockFrame = clockFrame; logger.debug("clockFrame set"); }
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setJAlarmLbl1(JLabel alarmLabel1) { this.hoursLabel = alarmLabel1; }
    protected void setJAlarmLbl2(JLabel alarmLabel2) { this.minutesLabel = alarmLabel2; }
    protected void setJAlarmLbl3(JLabel alarmLabel3) { this.ampmLabel = alarmLabel3; }
    protected void setJTextField1(JTextField textField1) { this.hoursTextField = textField1; }
    protected void setJTextField2(JTextField textField2) { this.minutesTextField = textField2; }
    protected void setSetAlarmButton(JButton setAlarmButton) { this.setAlarmButton = setAlarmButton; }
    protected void setMondayCheckBox(JCheckBox mondayCheckBox) { this.mondayCheckBox = mondayCheckBox; }
    protected void setTuesdayCheckBox(JCheckBox tuesdayCheckBox) { this.tuesdayCheckBox = tuesdayCheckBox; }
    protected void setWednesdayCheckBox(JCheckBox wednesdayCheckBox) { this.wednesdayCheckBox = wednesdayCheckBox; }
    protected void setThursdayCheckBox(JCheckBox thursdayCheckBox) { this.thursdayCheckBox = thursdayCheckBox; }
    protected void setFridayCheckBox(JCheckBox fridayCheckBox) { this.fridayCheckBox = fridayCheckBox; }
    protected void setSaturdayCheckBox(JCheckBox saturdayCheckBox) { this.saturdayCheckBox = saturdayCheckBox; }
    protected void setSundayCheckBox(JCheckBox sundayCheckBox) { this.sundayCheckBox = sundayCheckBox; }
    protected void setWeekCheckBox(JCheckBox weekCheckBox) { this.weekCheckBox = weekCheckBox; }
    protected void setWeekendCheckBox(JCheckBox weekendCheckBox) { this.weekendCheckBox = weekendCheckBox; }
    public void setClock(Clock clock) { this.clock = clock; logger.info("Clock set in AlarmPanel"); }
    private void setDateOperationsDropdown(JComboBox<String> ampmDropDown) { this.ampmDropDown = ampmDropDown; }

}