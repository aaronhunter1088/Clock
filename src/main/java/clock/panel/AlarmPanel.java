package clock.panel;

import clock.entity.Alarm;
import clock.entity.Clock;
import clock.exception.InvalidInputException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static clock.util.Constants.*;
import static java.time.DayOfWeek.*;
import static clock.panel.Panel.PANEL_ALARM;

/**
 * Alarm Panel
 *
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
public class AlarmPanel extends ClockPanel
{
    private static final Logger logger = LogManager.getLogger(AlarmPanel.class);
    public static final Panel PANEL = PANEL_ALARM;
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel alarmLabel1, // hours
                   alarmLabel2, // minutes
                   alarmLabel3, // am/pm
                   alarmLabel4, // current alarms
                   alarmLabel5; // name of alarm
    private JComboBox<String> ampmDropDown; // am/pm dropdown
    private JCheckBox mondayCheckBox,tuesdayCheckBox,wednesdayCheckBox,thursdayCheckBox,
                      fridayCheckBox,saturdayCheckBox,sundayCheckBox,weekCheckBox,weekendCheckBox;
    private JTextField textField1, // hours textfield
            textField2, // minutes textfield
            textField3, // am/pm textfield
            textField4; // name textfield
    private JButton setAlarmButton; // set-alarm button
    private JTextArea textArea; // displays all alarms
    private JScrollPane scrollPane; // scrollable textarea
    private ClockFrame clockFrame; // the clockFrame
    private Clock clock;
    private Alarm alarm;
    private boolean updatingAlarm;

    /**
     * Main constructor for creating the AlarmPanel
     * @param clockFrame the clockFrame object reference
     */
    public AlarmPanel(ClockFrame clockFrame)
    {
        super();
        setClockFrame(clockFrame);
        setClock(clockFrame.getClock());
        setMaximumSize(ClockFrame.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupAlarmPanel();
        setupAlarmButton();
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
        alarmLabel1 = new JLabel(Hours, SwingConstants.CENTER); // H
        //getJAlarmLbl1().setBorder(BorderFactory.createLineBorder(Color.RED));
        alarmLabel2 = new JLabel(Minutes, SwingConstants.CENTER); // M
        //getJAlarmLbl2().setBorder(BorderFactory.createLineBorder(Color.RED));
        alarmLabel3 = new JLabel(AMPM, SwingConstants.CENTER); // Time (AM/PM)
        //getJAlarmLbl3().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField1 = new JTextField(2); // Hour textField
        textField1.setSize(new Dimension(50,50));
        textField2 = new JTextField(2); // Min textField
        textField2.setSize(new Dimension(50,50));
        textField2.setMaximumSize(textField2.getSize());
        textField3 = new JTextField(2); // Time textField
        textField3.setSize(new Dimension(50,50));
        textField4 = new JTextField(2); // Name textField
        textField4.setSize(new Dimension(50,50));
        textField4.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                logger.info("focus gained on name field");
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtils.isBlank(textField4.getText()))
                {
                    textField4.setText(ALARM+(Alarm.alarmsCounter+1));
                }
                else if (textField4.getText().length() > 10)
                {
                    textField4.setText(textField4.getText().substring(0, 10));
                }
            }
        });
        alarmLabel4 = new JLabel(CURRENT_ALARMS, SwingConstants.CENTER); // Current Alarms
        alarmLabel5 = new JLabel("Name", SwingConstants.CENTER);
        setupOptionsSelection();
        textField1.requestFocusInWindow();
        textField1.setText(EMPTY);
        //getJTextField1().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField2.setText(EMPTY);
        //getJTextField2().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField3.setText(EMPTY);
        //getJTextField3().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField4.setText(EMPTY);
        alarmLabel1.setFont(ClockFrame.font20); // H
        alarmLabel2.setFont(ClockFrame.font20); // M
        alarmLabel3.setFont(ClockFrame.font20); // T
        alarmLabel4.setFont(ClockFrame.font20); // All Alarms
        alarmLabel5.setFont(ClockFrame.font20);
        alarmLabel1.setForeground(Color.WHITE);
        alarmLabel2.setForeground(Color.WHITE);
        alarmLabel3.setForeground(Color.WHITE);
        alarmLabel4.setForeground(Color.WHITE);
        alarmLabel5.setForeground(Color.WHITE);
        // setup textarea
        textArea = new JTextArea(2, 2);
        textArea.setSize(new Dimension(100, 100));
        textArea.setFont(ClockFrame.font10); // message
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
        // setup Set button
        setAlarmButton = new JButton(SET);
        setAlarmButton.setFont(ClockFrame.font20);
        setAlarmButton.setOpaque(true);
        setAlarmButton.setBackground(Color.BLACK);
        setAlarmButton.setForeground(Color.BLACK);
        // setup checkboxes
        setupCheckBoxes();
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
     * Resets alarm label 4
     */
    void resetJAlarmLabel4()
    {
        logger.info("reset alarm label 4");
        if (clock.getListOfAlarms().isEmpty())
        { alarmLabel4.setText(clockFrame.getClock().defaultText(6)); }// All Alarms label...
        else
        {
            alarmLabel4.setText(
                clock.getListOfAlarms().size() == 1
                    ? clock.getListOfAlarms().size() + SPACE+ALARM+SPACE+ADDED
                    : clock.getListOfAlarms().size() + SPACE+ALARM+S.toLowerCase()+SPACE+ADDED
            );
        }
    }

    /**
     * Validates the first text field is a valid hour
     * Allows military time format if the clock is set to show military time
     * @return boolean true if the first text field is valid
     * @throws InvalidInputException if the first text field is invalid
     */
    boolean validateFirstTextField() throws InvalidInputException
    {
        logger.info("validateFirstTextField");
        if (StringUtils.isBlank(textField1.getText())) {
            textField1.grabFocus();
            throw new InvalidInputException("Hour cannot be blank");
        }
        else {
            try {
                if (clockFrame.getClock().isShowMilitaryTime()) {
                    if (Integer.parseInt(textField1.getText()) <= 0 ||
                            Integer.parseInt(textField1.getText()) > 23)
                    {
                        textField1.grabFocus();
                        throw new InvalidInputException("Hours must be between 0 and 23");
                    }
                } else {
                    if (Integer.parseInt(textField1.getText()) <= 0 ||
                            Integer.parseInt(textField1.getText()) > 12)
                    {
                        textField1.grabFocus();
                        throw new InvalidInputException("Hours must be between 0 and 12");
                    }
                }
            } catch (NumberFormatException nfe) {
                textField1.grabFocus();
                throw new InvalidInputException("Hours must be a number");
            }
        }
        return true;
    }

    /**
     * Validates the second text field
     * @return boolean true if the second text field is valid
     * @throws InvalidInputException if the second text field is invalid
     */
    boolean validateSecondTextField() throws InvalidInputException
    {
        logger.info("validate second text field");
        if (textField2.getText().isBlank())
        {
            textField2.grabFocus();
            throw new InvalidInputException("Minutes cannot be blank");
        }
        else {
            try {
                if (Integer.parseInt(textField2.getText()) < 0 ||
                        Integer.parseInt(textField2.getText()) > 59 )
                {
                    textField2.grabFocus();
                    throw new InvalidInputException("Minutes must be between 0 and 59");
                }
            } catch (NumberFormatException nfe) {
                textField2.grabFocus();
                throw new InvalidInputException("Minutes must be a number");
            }
        }
        return true;
    }

    /**
     * Validates the third text field
     * @return boolean true if the third text field is valid
     * @throws InvalidInputException if the third text field is invalid
     */
    boolean validateThirdTextField() throws InvalidInputException
    {
        logger.info("validateThirdTextField");
        if (textField3.getText().isBlank())
        {
            textField3.grabFocus();
            throw new InvalidInputException("AMPM cannot be blank");
        }
        else if ((!AM.equalsIgnoreCase(textField3.getText()) || !PM.equalsIgnoreCase(textField3.getText()))
                 && textField3.getText().length() != 2)
        {
            textField3.grabFocus();
            throw new InvalidInputException("AMPM must be 'AM' or 'PM'");
        }
        else if (Integer.parseInt(textField1.getText()) > 12 &&
                 Integer.parseInt(textField1.getText()) < 24 &&
                 AM.equalsIgnoreCase(textField3.getText()))
        {
            textField3.grabFocus();
            throw new InvalidInputException("Hours can't be " + textField1.getText() + " when Time is " + textField3.getText());
        }
        else if (Integer.parseInt(textField1.getText()) == 0 && PM.equalsIgnoreCase(textField3.getText()) )
        {
            // basically setting militaryTime 00 hours, and saying PM
            // this makes no sense
            textField3.grabFocus();
            throw new InvalidInputException("Hours can't be " + textField1.getText() + " when Time is " + textField3.getText());
        }
        return true;
    }

    /**
     * Validates that at least one checkbox is selected
     * @return boolean true if at least one checkbox is selected
     * @throws InvalidInputException if no checkboxes are selected
     */
    boolean validateOnTheCheckBoxes() throws InvalidInputException
    {
        logger.info("validateOnTheCheckBoxes");
        List<DayOfWeek> days = getDaysChecked();
        if (days.isEmpty()) { throw new InvalidInputException("At least one checkbox must be selected."); }
        return true;
    }

    /**
     * Adds the alarm to the menu
     * @param alarm the alarm to set
     */
    void addAlarmToAlarmMenu(Alarm alarm)
    {
        logger.info("adding alarm to alarm menu");
        JMenuItem alarmItem = new JMenuItem(alarm.toString());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        logger.info("Size of viewAlarms before adding " + clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        clockFrame.getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        //setupAlarmsInMenuFunctionality();
        alarmItem.addActionListener(action -> {
            //if (alarm.getMusicPlayer() == null) { alarm.setupMusicPlayer(); }
            // if an alarm is going off and we clicked on it in the menuBar
            if (alarm.isAlarmGoingOff())
            {
                alarm.stopAlarm();
                setCheckBoxesIfWasSelected(alarm);
                alarm.setIsAlarmGoingOff(false);
                alarm.setIsAlarmUpdating(true); // this and the boolean below we want true
                updatingAlarm = true; // we want to continue with the logic that's done in the next if
                this.alarm = alarm;
                //activeAlarm = null;
                logger.info("Size of listOfAlarms before removing {}", clock.getListOfAlarms().size());
                // remove alarm from list of alarms
                clock.getListOfAlarms().remove(alarm);
                logger.info("Size of listOfAlarms after removing {}", clock.getListOfAlarms().size());
                deleteAlarmMenuItemFromViewAlarms(alarm);
                resetJTextArea();
                clockFrame.getAlarmPanel().getJTextField1().setText(alarm.getHoursAsStr());
                clockFrame.getAlarmPanel().getJTextField2().setText(alarm.getMinutesAsStr());
                clockFrame.getAlarmPanel().getJTextField3().setText(alarm.getAMPM());
                textField4.setText(alarm.getName());
                alarmLabel4.setText("Alarm off.");
            }
            // we are updating an alarm by clicking on it in the menuBar
            else
            {
                updateTheAlarm(alarm);
                textField1.setText(alarm.getHoursAsStr());
                textField2.setText(alarm.getMinutesAsStr());
                textField3.setText(alarm.getAMPM());
            }
            clockFrame.changePanels(PANEL_ALARM, false);
        });
        logger.info("Size of viewAlarms after adding " + clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

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
            if (clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.toString())
                    ||
                    clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.getName()) )
            { clockFrame.getClockMenuBar().getAlarmFeature_Menu().remove(clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItem(i)); }
        }
        logger.info("Size of viewAlarms after removal {}", clockFrame.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    /**
     * Resets the text area
     */
    public void resetJTextArea()
    {
        logger.info("reset textarea");
        textArea.setText(EMPTY);
        for(Alarm alarm : clock.getListOfAlarms())
        {
            if (!textArea.getText().isEmpty())
            { textArea.append(NEWLINE); }
            textArea.append(alarm.toString()+NEWLINE);
            alarm.getDaysShortened().forEach(day -> textArea.append(day));
        }
    }

    /**
     * Sets up the alarm button
     */
    public void setupAlarmButton()
    {
        logger.info("setup alarm button");
        setAlarmButton.addActionListener(action -> {
            logger.info("set alarm button clicked");
            try
            {
                boolean validated = validateFirstTextField() && validateSecondTextField() // Hours and Minutes
                        && validateOnTheCheckBoxes(); // Checkboxes

                if (!validated)
                {
                    logger.error("not valid inputs. not setting alarm");
                }
                else
                {
                    logger.info("creating new alarm");
                    Alarm alarm = null;
                    if (updatingAlarm) {
                        updateTheAlarm(this.alarm);
                        alarm = this.alarm;
                        this.alarm = null;
                        updatingAlarm = false;
                    } else {
                        alarm = createAlarm();
                    }
                    // checks equality
                    if (!clock.getListOfAlarms().contains(alarm)) {
                        addAlarmToAlarmMenu(alarm);
                        clock.getListOfAlarms().add(alarm);
                        // display list of alarms below All Alarms
                        resetJTextArea();
                    }
                    else {
                        logger.warn("alarm already exists");
                        // display list of alarms below All Alarms
                        resetJTextArea();
                        textArea.append(NEWLINE+"Alarm already exists!"+NEWLINE);
                    }
                    resetJCheckBoxes();
                    // erase input in textFields
                    textField1.setText(EMPTY);
                    textField2.setText(EMPTY);
                    textField3.setText(EMPTY);
                    textField4.setText(EMPTY);
                    resetJAlarmLabel4();
                }
            }
            catch (InvalidInputException | NumberFormatException e)
            {
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setText(e.getMessage());
                logger.error("Couldn't create a new alarm");
            }
        });
        logger.info("Alarm button set!");
    }

    /**
     * Creates an alarm and sets the latest one created as the currentAlarm
     * defined in setAlarm
     * @return Alarm
     */
    public Alarm createAlarm()
    {
        int hour = Integer.parseInt(textField1.getText());
        int minutes = Integer.parseInt(textField2.getText());
        String ampm = Objects.requireNonNull(ampmDropDown.getSelectedItem()).toString();
        String alarmName = textField4.getText();
        List<DayOfWeek> days = getDaysChecked();
        logger.info("create alarm");
        Alarm alarm = new Alarm(alarmName, hour, minutes, ampm, days, false, getClock());
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
        return alarm;
    }

    /**
     * Updates an alarm
     * @param alarmToUpdate the alarm to update
     */
    public void updateTheAlarm(Alarm alarmToUpdate)
    {
        logger.info("updating alarm");
        if (alarmToUpdate != null)
        {
            logger.info("Updating an alarm: {} days: {}", alarmToUpdate, alarmToUpdate.getDays());
            // hours
            if (alarmToUpdate.getHours() < 10 && alarmToUpdate.getHours() != 0)
            { textField1.setText("0" + alarmToUpdate.getHours()); }
            else if (alarmToUpdate.getHours() == 0 && alarmToUpdate.getMinutes() != 0)
            { textField1.setText("00"); }
            else
            { textField1.setText(alarmToUpdate.getHoursAsStr()); }
            // minutes
            if (alarmToUpdate.getMinutes() < 10 && alarmToUpdate.getMinutes() != 0)
            { textField2.setText("0" + alarmToUpdate.getMinutes()); }
            else if (alarmToUpdate.getMinutes() == 0 && alarmToUpdate.getHours() != 0)
            { textField2.setText("00"); }
            else
            { textField2.setText(alarmToUpdate.getMinutesAsStr()); }
            textField3.setText(alarmToUpdate.getAMPM());
            textField4.setText(alarmToUpdate.getName());
            setCheckBoxesIfWasSelected(alarmToUpdate);
            alarmToUpdate.setupMusicPlayer();
            if (clockFrame.getClock().isDateChanged()) {
                alarmToUpdate.setTriggeredToday(false);
            }
        }
        // remove alarm from list of alarms
        deleteAlarmMenuItemFromViewAlarms(alarmToUpdate);
        logger.info("Size of listOfAlarms before removing {}", clock.getListOfAlarms().size());
        clock.getListOfAlarms().remove(alarmToUpdate);
        logger.info("Size of listOfAlarms after removing {}", clock.getListOfAlarms().size());
        resetJTextArea();
        alarmLabel4.setText("Updating alarm");
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
        if (weekCheckBox.isSelected()) {
            if (!daysSelected.contains(MONDAY)) daysSelected.add(MONDAY);
            if (!daysSelected.contains(TUESDAY)) daysSelected.add(TUESDAY);
            if (!daysSelected.contains(WEDNESDAY)) daysSelected.add(WEDNESDAY);
            if (!daysSelected.contains(THURSDAY)) daysSelected.add(THURSDAY);
            if (!daysSelected.contains(FRIDAY)) daysSelected.add(FRIDAY);
        } // add Monday - Friday
        if (weekendCheckBox.isSelected()) {
            if (!daysSelected.contains(SATURDAY)) daysSelected.add(SATURDAY);
            if (!daysSelected.contains(SUNDAY)) daysSelected.add(SUNDAY);
        } // add Saturday and Sunday
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
     * This method adds the components to the alarm panel
     */
    public void addComponentsToPanel()
    {
        logger.info("addComponentsToPanel");
        addComponent(alarmLabel1,0,0,1,1,0,0,   GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(textField1,0,1,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(alarmLabel2,0,2,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // M
        addComponent(textField2,0,3,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(alarmLabel3,0,4,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // Time (AM/PM)
        //addComponent(textField3,0,5,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(ampmDropDown,0,5,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(alarmLabel4, 0, 6, 2, 1, 2, 2, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        constraints.weighty = 1;
        constraints.weightx = 1;
        // new column, first row
        addComponent(mondayCheckBox, 1,0,2,1, 0,0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Monday
        addComponent(tuesdayCheckBox, 2, 0, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Tuesday
        addComponent(wednesdayCheckBox, 3, 0, 2, 1, 2, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Wednesday
        // new column, second row
        addComponent(thursdayCheckBox, 1, 2, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Thursday
        addComponent(fridayCheckBox, 2, 2, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Friday
        addComponent(saturdayCheckBox, 3, 2, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Saturday
        // new column, third row
        addComponent(sundayCheckBox, 1, 4, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Sunday
        addComponent(weekCheckBox, 2, 4, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        addComponent(weekendCheckBox, 3, 4, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        constraints.weighty = 4;
        constraints.weightx = 2;
        addComponent(scrollPane,1,6,2,4, 0,0, GridBagConstraints.BOTH, new Insets(1,1,1,1)); // textArea
        // set-alarm button
        constraints.weighty = 0;
        constraints.weightx = 0;
        addComponent(alarmLabel5,4,0,1,1,0,0,   GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(textField4,4,1,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(setAlarmButton, 4, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, new Insets(0,0,0,0)); // Set button
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
    void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill, Insets insets)
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
        layout.setConstraints(cpt,constraints);
        add(cpt);
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

    /* Getters */
    public ClockFrame getClockFrame() { return this.clockFrame; }
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clockFrame.getClock(); }
    public JLabel getJAlarmLbl1() { return this.alarmLabel1; } // H
    public JLabel getJAlarmLbl2() { return this.alarmLabel2; } // M
    public JLabel getJAlarmLbl3() { return this.alarmLabel3; } // T
    public JLabel getJAlarmLbl4() { return this.alarmLabel4; } // All alarms
    public JTextField getJTextField1() { return this.textField1; }
    public JTextField getJTextField2() { return this.textField2; }
    public JTextField getJTextField3() { return this.textField3; }
    public JButton getSetAlarmButton() { return this.setAlarmButton; }
    public JScrollPane getJScrollPane() { return this.scrollPane; }
    public JTextArea getJTextArea() { return this.textArea; }
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
    protected void setJAlarmLbl1(JLabel alarmLabel1) { this.alarmLabel1 = alarmLabel1; }
    protected void setJAlarmLbl2(JLabel alarmLabel2) { this.alarmLabel2 = alarmLabel2; }
    protected void setJAlarmLbl3(JLabel alarmLabel3) { this.alarmLabel3 = alarmLabel3; }
    protected void setJAlarmLbl4(JLabel alarmLabel4) { this.alarmLabel4 = alarmLabel4; }
    protected void setJTextField1(JTextField textField1) { this.textField1 = textField1; }
    protected void setJTextField2(JTextField textField2) { this.textField2 = textField2; }
    protected void setJTextField3(JTextField textField3) { this.textField3 = textField3; }
    protected void setSetAlarmButton(JButton setAlarmButton) { this.setAlarmButton = setAlarmButton; }
    protected void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    protected void setJTextArea(final JTextArea textArea) { this.textArea = textArea; }
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