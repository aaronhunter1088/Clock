package com.example.clock;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.DayOfWeek.*;
import static com.example.clock.ClockPanel.PANEL_ALARM;

/**
 * The AlarmPanel is used to set and view alarms. The
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
*  @version 2.8
 */
public class AlarmPanel extends JPanel implements IClockPanel
{
    private static final Logger logger = LogManager.getLogger(AlarmPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel alarmLabel1,alarmLabel2, alarmLabel3,alarmLabel4;
    private JCheckBox mondayCheckBox,tuesdayCheckBox,wednesdayCheckBox,thursdayCheckBox,
                      fridayCheckBox,saturdayCheckBox,sundayCheckBox,weekCheckBox,weekendCheckBox;
    private JTextField textField1,textField2,textField3;
    private JButton setAlarmButton;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private Clock clock;
    private Alarm alarm, activeAlarm;
    private boolean updatingAlarm,alarmIsGoingOff;
    private AdvancedPlayer musicPlayer;

    /**
     * Main constructor for creating the AlarmPanel
     * @param clock the clock object reference
     */
    AlarmPanel(Clock clock)
    {
        super();
        this.clock = clock;
        this.clock.setClockPanel(PANEL_ALARM);
        setMaximumSize(Clock.alarmSize);
        this.layout = new GridBagLayout();
        setLayout(layout);
        this.constraints = new GridBagConstraints();
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupAlarmPanel();
        setupAlarmButton();
        setupMusicPlayer();
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
        alarmLabel4 = new JLabel(CURRENT_ALARMS, SwingConstants.CENTER); // Current Alarms
        textField1.requestFocusInWindow();
        textField1.setText(EMPTY);
        //getJTextField1().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField1.setText(EMPTY);
        //getJTextField2().setBorder(BorderFactory.createLineBorder(Color.RED));
        textField1.setText(EMPTY);
        //getJTextField3().setBorder(BorderFactory.createLineBorder(Color.RED));
        alarmLabel1.setFont(Clock.font20); // H
        alarmLabel2.setFont(Clock.font20); // M
        alarmLabel3.setFont(Clock.font20); // T
        alarmLabel4.setFont(Clock.font20); // All Alarms
        alarmLabel1.setForeground(Color.WHITE);
        alarmLabel2.setForeground(Color.WHITE);
        alarmLabel3.setForeground(Color.WHITE);
        alarmLabel4.setForeground(Color.WHITE);
        // setup textarea
        textArea = new JTextArea(2, 2);
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
        // setup Set button
        setAlarmButton = new JButton(SET);
        setAlarmButton.setFont(Clock.font20);
        setAlarmButton.setOpaque(true);
        setAlarmButton.setBackground(Color.BLACK);
        setAlarmButton.setForeground(Color.BLACK);
        // setup checkboxes
        setupCheckBoxes();
    }

    /**
     * Defines the music player object
     */
    void setupMusicPlayer()
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

    /**
     * Resets alarm label 4
     */
    void resetJAlarmLabel4()
    {
        logger.info("reset alarm label 4");
        if (clock.getListOfAlarms().isEmpty())
        { alarmLabel4.setText(clock.defaultText(6)); }// All Alarms label...
        else
        {
            alarmLabel4.setText(
                clock.getListOfAlarms().size() == 1
                    ? clock.getListOfAlarms().size() + SPACE+ALARM+SPACE+ADDED
                    : clock.getListOfAlarms().size() + SPACE+ALARM+S+SPACE+ADDED
            );
        }
    }

    /**
     * Validates the first text field
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
        else if (Integer.parseInt(textField1.getText()) <= 0 ||
                 Integer.parseInt(textField1.getText()) > 23 )
        {
            textField1.grabFocus();
            throw new InvalidInputException("Hours must be between 0 and 12");
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
        else if (Integer.parseInt(textField2.getText()) < 0 ||
                 Integer.parseInt(textField2.getText()) > 59 )
        {
            textField2.grabFocus();
            throw new InvalidInputException("Minutes must be between 0 and 59");
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
     * Validates that at least one checkbox is selected
     * @return boolean true if at least one checkbox is selected
     */
    boolean validateACheckboxWasSelected()
    { return !getDaysChecked().isEmpty(); }

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
        logger.info("Size of viewAlarms before adding " + clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        clock.getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        logger.info("Size of viewAlarms after adding " + clock.getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    /**
     * Removes an alarm from the list in the menu
     * @param alarm the alarm to remove
     */
    void deleteAlarmMenuItemFromViewAlarms(Alarm alarm)
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
    void resetJTextArea()
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
    void setupAlarmButton()
    {
        logger.info("setup alarm button");
        setAlarmButton.addActionListener(action -> {
            logger.info("set alarm button clicked");
            Alarm alarm;
            boolean validated;
            try
            {
                validated = validateFirstTextField() && validateSecondTextField() // Hours and Minutes
                        && validateThirdTextField() && validateOnTheCheckBoxes(); // Time and Checkboxes

                if (!validated)
                {
                    logger.info("not valid inputs. not setting alarm");
                    this.alarm = null; // new Alarm()
                }
                else
                {
                    if (updatingAlarm)
                    {
                        logger.info("updating alarm");
                        logger.info("clock.alarmsList size {}", clock.getListOfAlarms().size());
                        alarm = createAlarm();
                        alarm.setIsAlarmUpdating(false); // at this point, we are doing updating
                        this.alarm = alarm;
                        // add clock to list of alarms
                        if (clock.getListOfAlarms().isEmpty())
                        {
                            addAlarmToAlarmMenu(alarm);
                            clock.getListOfAlarms().add(alarm);
                        }
                        else
                        {
                            boolean addToList = false;
                            for(int i=0; i<clock.getListOfAlarms().size(); i++)
                            {
                                if (!clock.getListOfAlarms().get(i).toString().equals(alarm.toString())
                                    && clock.getListOfAlarms().get(i).getDays() != alarm.getDays())
                                { addToList = true; }
                                else
                                { logger.error("Tried updating an alarm but it already exists! Cannot create duplicate alarm."); }
                            }
                            if (addToList)
                            {
                                addAlarmToAlarmMenu(alarm);
                                clock.getListOfAlarms().add(alarm);
                            }
                        }
                        logger.info("clock.alarmsList size {}", clock.getListOfAlarms().size());
                        // restart viewAlarms menu
                        setupAlarmsInMenuFunctionality();
                        updatingAlarm = false;
                        resetJTextArea();
                        resetJCheckBoxes();
                        resetJAlarmLabel4();
                    }
                    else
                    {
                        logger.info("creating new alarm");
                        alarm = createAlarm();
                        setAlarm(alarm);
                        setUpdatingAlarm(false);
                        if (clock.getListOfAlarms().isEmpty())
                        {
                            addAlarmToAlarmMenu(alarm);
                            clock.getListOfAlarms().add(alarm);
                        }
                        else
                        {
                            boolean addToList = false;
                            for(int i=0; i<clock.getListOfAlarms().size(); i++)
                            {
                                if (!clock.getListOfAlarms().get(i).toString().equals(alarm.toString()) ||
                                    clock.getListOfAlarms().get(i).getDays() != alarm.getDays())
                                { addToList = true; }
                                else
                                { logger.error("Tried adding an alarm but it already exists! Cannot create duplicate alarm."); }
                            }
                            if (addToList)
                            {
                                addAlarmToAlarmMenu(alarm);
                                clock.getListOfAlarms().add(alarm);
                            }
                        }
                        logger.info("clock.alarmsList size {}", clock.getListOfAlarms().size());
                        // display list of alarms below All Alarms
                        resetJTextArea();
                        resetJCheckBoxes();
                        setupAlarmsInMenuFunctionality();
                        // erase input in textFields
                        textField1.setText(EMPTY);
                        textField2.setText(EMPTY);
                        textField3.setText(EMPTY);
                        resetJAlarmLabel4();
                    }
                }
            }
            catch (InvalidInputException | ParseException e)
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
     * @throws ParseException will be thrown if hour, minutes, or time is inappropriate.
     */
    Alarm createAlarm() throws ParseException, InvalidInputException
    {
        logger.info("create alarm");
        int hour = Integer.parseInt(textField1.getText());
        int minutes = Integer.parseInt(textField2.getText());
        String ampm = textField3.getText(); //convertStringToTimeAMPM(getJTextField3().getText());
        boolean valid;
        valid = validateFirstTextField() && validateSecondTextField()
                && validateThirdTextField() && validateACheckboxWasSelected();
        if (valid)
        {
            logger.info("valid alarm values...");
            List<DayOfWeek> days = getDaysChecked();
            Alarm alarm = new Alarm(hour, minutes, ampm, false, days, getClock());
            alarm.setIsAlarmGoingOff(false);
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
        else
        { return null; }
    }

    /**
     * Updates an alarm
     * @param alarmToUpdate the alarm to update
     */
    void updateTheAlarm(Alarm alarmToUpdate)
    {
        logger.info("updating alarm");
        if (null != alarmToUpdate)
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
            setCheckBoxesIfWasSelected(alarmToUpdate);
        }
        updatingAlarm = true;
        // remove alarm from list of alarms
        deleteAlarmMenuItemFromViewAlarms(alarmToUpdate);
        logger.info("Size of listOfAlarms before removing {}", clock.getListOfAlarms().size());
        clock.getListOfAlarms().remove(alarmToUpdate);
        logger.info("Size of listOfAlarms after removing {}", clock.getListOfAlarms().size());
        resetJTextArea();
        alarmLabel4.setText("Updating alarm");
    }

    /**
     * Sets an alarm to go off
     * @param executor the executor service
     */
    void triggerAlarm(ExecutorService executor)
    {
        logger.info("trigger alarm");
        setAlarmIsGoingOff(true);
        clock.getDigitalClockPanel().updateLabels();
        //clock.getDigitalClockPanel().getLabel1().setText(activeAlarm.toString());
        //clock.getDigitalClockPanel().getLabel2().setText("is going off!");
        // play sound
        Callable<String> c = () -> {
            try
            {
                setupMusicPlayer();
                logger.debug("while alarm is going off, play sound");
                while (getActiveAlarm().alarmGoingOff)
                { getMusicPlayer().play(50); }
                logger.debug("alarm has stopped");
                return "Alarm triggered";
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

    /**
     * Stops an actively going off alarm
     */
    void stopAlarm()
    {
        logger.info("stop alarm");
        musicPlayer = null;
        alarmIsGoingOff = false;
        logger.info("{} alarm turned off", alarm.toString());
    }

    void checkIfAnyAlarmsAreGoingOff()
    {
        logger.info("check if any alarms are going off");
        // alarm has reference to time
        // check all alarms
        // if any alarm matches clock's time, an alarm should be going off
        clock.getListOfAlarms().forEach((alarm) -> {
            for(DayOfWeek day : alarm.getDays()) {
                if (alarm.toString().equals(clock.getAlarmTimeAsStr())
                        &&
                        day == getClock().getDayOfWeek()) {
                    // time for alarm to be triggered
                    alarm.setIsAlarmGoingOff(true);
                    setActiveAlarm(alarm);
                    setAlarmIsGoingOff(true);
                    logger.info("Alarm " + getActiveAlarm().toString() + " matches clock's time. ");
                    logger.info("Sounding alarm...");
                }
                else if (getClock().isShowMilitaryTime()) { // if in military time, change clocks hours back temporarily
                    if (getClock().getHours() > 12) {
                        int tempHour = getClock().getHours()-12;
                        String tempHourAsStr = (tempHour < 10) ? "0"+tempHour : String.valueOf(tempHour);
                        if (alarm.toString().equals(tempHourAsStr+":"+getClock().getMinutesAsStr()+" "+getClock().getAMPM())
                                &&
                                day == getClock().getDayOfWeek()) {
                            // time for alarm to be triggered on
                            setActiveAlarm(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            setAlarmIsGoingOff(true);
                            logger.info("Alarm " + getActiveAlarm().toString() + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                    else {
                        if (alarm.toString().equals(getClock().getHoursAsStr()+":"+getClock().getMinutesAsStr()+" "+getClock().getAMPM())
                                &&
                                day == getClock().getDayOfWeek()) {
                            // time for alarm to be triggered on
                            setActiveAlarm(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            setAlarmIsGoingOff(true);
                            logger.info("Alarm " + getActiveAlarm().toString() + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                }
            }
        });
        // update lbl1 and lbl2 to display alarm
        // user must "view that alarm" to turn it off
        Alarm currentAlarm = getActiveAlarm();
        ExecutorService executor = Executors.newCachedThreadPool();
        if (null != currentAlarm && currentAlarm.isAlarmGoingOff()) {
            triggerAlarm(executor);
        }
    }

    /**
     * Adds alarms created to the menu
     */
    void setupAlarmsInMenuFunctionality()
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
        logger.info("setup alarms in menu functionality");
        for(int i=0; i<getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (!SET_ALARMS.equals(clock.getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText()))
            {
                JMenuItem menuItem = clock.getClockMenuBar().getAlarmFeature_Menu().getItem(i);
                menuItem.addActionListener(action -> {
                    clock.getListOfAlarms().forEach(alarm -> {
                        if (alarm.toString().equals(menuItem.getText()))
                        { this.alarm = alarm; }
                    });
                    if (musicPlayer == null) { setupMusicPlayer(); }
                    // if an alarm is going off and we clicked on it in the menuBar
                    if (activeAlarm != null)
                    {
                        alarm = activeAlarm;
                        if (musicPlayer != null) { stopAlarm(); }
                        else { logger.warn("Music player is null!"); }
                        setCheckBoxesIfWasSelected(alarm);
                        alarm.setIsAlarmGoingOff(false);
                        alarm.setIsAlarmUpdating(true); // this and the boolean below we want true
                        updatingAlarm = true; // we want to continue with the logic that's done in the next if
                        activeAlarm = null;
                        logger.info("Size of listOfAlarms before removing {}", clock.getListOfAlarms().size());
                        // remove alarm from list of alarms
                        clock.getListOfAlarms().remove(getAlarm());
                        logger.info("Size of listOfAlarms after removing {}", clock.getListOfAlarms().size());
                        deleteAlarmMenuItemFromViewAlarms(alarm);
                        resetJTextArea();
                        clock.getAlarmPanel().getJTextField1().setText(getAlarm().getHoursAsStr());
                        clock.getAlarmPanel().getJTextField2().setText(getAlarm().getMinutesAsStr());
                        clock.getAlarmPanel().getJTextField3().setText(getAlarm().getAMPM());
                        alarmLabel4.setText("Alarm off.");
                    }
                    // we are updating an alarm by clicking on it in the menuBar
                    else if (alarm != null)
                    {
                        updateTheAlarm(alarm);
                        textField1.setText(alarm.getHoursAsStr());
                        textField2.setText(alarm.getMinutesAsStr());
                        textField3.setText(alarm.getAMPM());
                    }
                    clock.changePanels(PANEL_ALARM, false);
                });
            }
        }
    }

    /**
     * Returns a list of days that were checked
     * @return List<DayOfWeek> the days selected
     */
    List<DayOfWeek> getDaysChecked()
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
    void resetJCheckBoxes() 
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
    void setCheckBoxesIfWasSelected(Alarm alarmToUpdate)
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
    void setupCheckBoxes()
    {
        logger.info("setup checkboxes");
        sundayCheckBox = new JCheckBox(SUNDAY.toString().substring(0,2), false);
        mondayCheckBox = new JCheckBox(MONDAY.toString().substring(0,1), false);
        tuesdayCheckBox = new JCheckBox(TUESDAY.toString().substring(0,1), false);
        wednesdayCheckBox = new JCheckBox(WEDNESDAY.toString().substring(0,1), false);
        thursdayCheckBox = new JCheckBox(THURSDAY.toString().substring(0,1), false);
        fridayCheckBox = new JCheckBox(FRIDAY.toString().substring(0,1), false);
        saturdayCheckBox = new JCheckBox(SATURDAY.toString().substring(0,1), false);
        weekCheckBox = new JCheckBox(WEEK, false);
        weekendCheckBox = new JCheckBox(WEEKEND, false);
        List.of(sundayCheckBox, mondayCheckBox, tuesdayCheckBox, wednesdayCheckBox, thursdayCheckBox, fridayCheckBox, saturdayCheckBox, weekCheckBox, weekendCheckBox)
            .forEach(checkBox -> {
                checkBox.setFont(Clock.font20);
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
    void printStackTrace(Exception e)
    { printStackTrace(e, ""); }

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
        addComponent(textField3,0,5,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
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
        clock.clearSettingsMenu();
        logger.info("No settings defined up for Alarm Panel");
    }

    /* Getters */
    GridBagLayout getGridBagLayout() { return this.layout; }
    GridBagConstraints getGridBagConstraints() { return this.constraints; }
    Clock getClock() { return this.clock; }
    Alarm getAlarm() { return this.alarm; }
    JLabel getJAlarmLbl1() { return this.alarmLabel1; } // H
    JLabel getJAlarmLbl2() { return this.alarmLabel2; } // M
    JLabel getJAlarmLbl3() { return this.alarmLabel3; } // T
    JLabel getJAlarmLbl4() { return this.alarmLabel4; } // All alarms
    JTextField getJTextField1() { return this.textField1; }
    JTextField getJTextField2() { return this.textField2; }
    JTextField getJTextField3() { return this.textField3; }
    JButton getSetAlarmButton() { return this.setAlarmButton; }
    JScrollPane getJScrollPane() { return this.scrollPane; }
    JTextArea getJTextArea() { return this.textArea; }
    boolean isUpdatingAlarm() { return updatingAlarm; }
    AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    Alarm getActiveAlarm() { return this.activeAlarm; }
    JCheckBox getMondayCheckBox() { return mondayCheckBox; }
    JCheckBox getTuesdayCheckBox() { return tuesdayCheckBox; }
    JCheckBox getWednesdayCheckBox() { return wednesdayCheckBox; }
    JCheckBox getThursdayCheckBox() { return thursdayCheckBox; }
    JCheckBox getFridayCheckBox() { return fridayCheckBox; }
    JCheckBox getSaturdayCheckBox() { return saturdayCheckBox; }
    JCheckBox getSundayCheckBox() { return sundayCheckBox; }
    JCheckBox getWeekCheckBox() { return weekCheckBox; }
    JCheckBox getWeekendCheckBox() { return weekendCheckBox; }
    boolean isAlarmIsGoingOff() { return alarmIsGoingOff; }

    /* Setters */
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setAlarm(Alarm alarm) { this.alarm = alarm; }
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
    protected void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    protected void setActiveAlarm(Alarm activeAlarm) { this.activeAlarm = activeAlarm; }
    protected void setMondayCheckBox(JCheckBox mondayCheckBox) { this.mondayCheckBox = mondayCheckBox; }
    protected void setTuesdayCheckBox(JCheckBox tuesdayCheckBox) { this.tuesdayCheckBox = tuesdayCheckBox; }
    protected void setWednesdayCheckBox(JCheckBox wednesdayCheckBox) { this.wednesdayCheckBox = wednesdayCheckBox; }
    protected void setThursdayCheckBox(JCheckBox thursdayCheckBox) { this.thursdayCheckBox = thursdayCheckBox; }
    protected void setFridayCheckBox(JCheckBox fridayCheckBox) { this.fridayCheckBox = fridayCheckBox; }
    protected void setSaturdayCheckBox(JCheckBox saturdayCheckBox) { this.saturdayCheckBox = saturdayCheckBox; }
    protected void setSundayCheckBox(JCheckBox sundayCheckBox) { this.sundayCheckBox = sundayCheckBox; }
    protected void setWeekCheckBox(JCheckBox weekCheckBox) { this.weekCheckBox = weekCheckBox; }
    protected void setWeekendCheckBox(JCheckBox weekendCheckBox) { this.weekendCheckBox = weekendCheckBox; }
    protected void setAlarmIsGoingOff(boolean alarmIsGoingOff) { this.alarmIsGoingOff = alarmIsGoingOff; }
    public void setClock(Clock clock) { this.clock = clock; }
}