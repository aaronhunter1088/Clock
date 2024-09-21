package org.example.clock;

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
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.time.DayOfWeek.*;

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
 * @version 2.7
 */
public class AlarmPanel extends JPanel implements ClockConstants, IClockPanel
{
    private static final Logger logger = LogManager.getLogger(AlarmPanel.class);
    GridBagLayout layout;
    GridBagConstraints constraints;
    JLabel alarmLabel1,alarmLabel2,
            alarmLabel3,alarmLabel4;
    JCheckBox mondayCheckBox,tuesdayCheckBox,wednesdayCheckBox,thursdayCheckBox,
    fridayCheckBox,saturdayCheckBox,sundayCheckBox,weekCheckBox,weekendCheckBox;
    JTextField textField1,textField2,textField3;
    JButton setAlarmButton;
    JTextArea textArea;
    JScrollPane scrollPane;
    Clock clock;
    Alarm alarm,currentAlarmGoingOff;
    boolean updatingAlarm,alarmIsGoingOff;
    AdvancedPlayer musicPlayer;
    PanelType panelType;

    AlarmPanel(Clock clock) {
        super();
        setClock(clock);
        setPanelType(PanelType.ALARM);
        setMaximumSize(Clock.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(layout);
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.BLACK);
        setupAlarmPanel(getClock());
        setupAlarmButton();
        setupMusicPlayer();
        addComponentsToPanel();
        SwingUtilities.updateComponentTreeUI(this);
        logger.info("Finished creating Alarm Panel");
    }

    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public Alarm getAlarm() { return this.alarm; }
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
    public boolean isUpdatingAlarm() { return updatingAlarm; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    public Alarm getCurrentAlarmGoingOff() { return this.currentAlarmGoingOff; }
    public JCheckBox getMondayCheckBox() { return mondayCheckBox; }
    public JCheckBox getTuesdayCheckBox() { return tuesdayCheckBox; }
    public JCheckBox getWednesdayCheckBox() { return wednesdayCheckBox; }
    public JCheckBox getThursdayCheckBox() { return thursdayCheckBox; }
    public JCheckBox getFridayCheckBox() { return fridayCheckBox; }
    public JCheckBox getSaturdayCheckBox() { return saturdayCheckBox; }
    public JCheckBox getSundayCheckBox() { return sundayCheckBox; }
    public JCheckBox getWeekCheckBox() { return weekCheckBox; }
    public JCheckBox getWeekendCheckBox() { return weekendCheckBox; }
    public boolean isAlarmIsGoingOff() { return alarmIsGoingOff; }

    void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    void setAlarm(Alarm alarm) { this.alarm = alarm; }
    void setJAlarmLbl1(JLabel alarmLabel1) { this.alarmLabel1 = alarmLabel1; }
    void setJAlarmLbl2(JLabel alarmLabel2) { this.alarmLabel2 = alarmLabel2; }
    void setJAlarmLbl3(JLabel alarmLabel3) { this.alarmLabel3 = alarmLabel3; }
    void setJAlarmLbl4(JLabel alarmLabel4) { this.alarmLabel4 = alarmLabel4; }
    void setJTextField1(JTextField textField1) { this.textField1 = textField1; }
    void setJTextField2(JTextField textField2) { this.textField2 = textField2; }
    void setJTextField3(JTextField textField3) { this.textField3 = textField3; }
    void setSetAlarmButton(JButton setAlarmButton) { this.setAlarmButton = setAlarmButton; }
    void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    void setJTextArea(final JTextArea textArea) { this.textArea = textArea; }
    void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    void setCurrentAlarmGoingOff(Alarm currentAlarmGoingOff) { this.currentAlarmGoingOff = currentAlarmGoingOff; }
    void setMondayCheckBox(JCheckBox mondayCheckBox) { this.mondayCheckBox = mondayCheckBox; }
    void setTuesdayCheckBox(JCheckBox tuesdayCheckBox) { this.tuesdayCheckBox = tuesdayCheckBox; }
    void setWednesdayCheckBox(JCheckBox wednesdayCheckBox) { this.wednesdayCheckBox = wednesdayCheckBox; }
    void setThursdayCheckBox(JCheckBox thursdayCheckBox) { this.thursdayCheckBox = thursdayCheckBox; }
    void setFridayCheckBox(JCheckBox fridayCheckBox) { this.fridayCheckBox = fridayCheckBox; }
    void setSaturdayCheckBox(JCheckBox saturdayCheckBox) { this.saturdayCheckBox = saturdayCheckBox; }
    void setSundayCheckBox(JCheckBox sundayCheckBox) { this.sundayCheckBox = sundayCheckBox; }
    void setWeekCheckBox(JCheckBox weekCheckBox) { this.weekCheckBox = weekCheckBox; }
    void setWeekendCheckBox(JCheckBox weekendCheckBox) { this.weekendCheckBox = weekendCheckBox; }
    void setAlarmIsGoingOff(boolean alarmIsGoingOff) { this.alarmIsGoingOff = alarmIsGoingOff; }
    @Override
    public void setClock(Clock clock) { this.clock = clock; }
    @Override
    public void setPanelType(PanelType panelType) { this.panelType = panelType; }
    // Helper methods
    public void setupAlarmPanel(Clock clock) {
        logger.info("setupAlarmPanel");
        clock.setIsDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        setJAlarmLbl1(new JLabel("Hours", SwingConstants.CENTER)); // H
        //getJAlarmLbl1().setBorder(BorderFactory.createLineBorder(Color.RED));
        setJAlarmLbl2(new JLabel("Minutes", SwingConstants.CENTER)); // M
        //getJAlarmLbl2().setBorder(BorderFactory.createLineBorder(Color.RED));
        setJAlarmLbl3(new JLabel("AM/PM", SwingConstants.CENTER)); // Time (AM/PM)
        //getJAlarmLbl3().setBorder(BorderFactory.createLineBorder(Color.RED));
        setJTextField1(new JTextField(2)); // Hour textField
        getJTextField1().setSize(new Dimension(50,50));
        setJTextField2(new JTextField(2)); // Min textField
        getJTextField2().setSize(new Dimension(50,50));
        getJTextField2().setMaximumSize(getJTextField2().getSize());
        setJTextField3(new JTextField(2)); // Time textField
        getJTextField3().setSize(new Dimension(50,50));
        setJAlarmLbl4(new JLabel("Current Alarms", SwingConstants.CENTER)); // Current Alarms
        getJTextField1().requestFocusInWindow();
        getJTextField1().setText("");
        //getJTextField1().setBorder(BorderFactory.createLineBorder(Color.RED));
        getJTextField1().setText("");
        //getJTextField2().setBorder(BorderFactory.createLineBorder(Color.RED));
        getJTextField1().setText("");
        //getJTextField3().setBorder(BorderFactory.createLineBorder(Color.RED));
        getJAlarmLbl1().setFont(Clock.font20); // H
        getJAlarmLbl2().setFont(Clock.font20); // M
        getJAlarmLbl3().setFont(Clock.font20); // T
        getJAlarmLbl4().setFont(Clock.font20); // All Alarms
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        // setup textarea
        setJTextArea(new JTextArea(2, 2));
        getJTextArea().setSize(new Dimension(100, 100));
        getJTextArea().setFont(Clock.font10); // message
        getJTextArea().setVisible(true);
        getJTextArea().setEditable(false);
        getJTextArea().setLineWrap(false);
        getJTextArea().setWrapStyleWord(false);
        getJTextArea().setBackground(Color.BLACK);
        getJTextArea().setForeground(Color.WHITE);
        // setup scrollPane
        setJScrollPane(new JScrollPane(getJTextArea()));
        getJScrollPane().setHorizontalScrollBar(null);
        getJScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getJScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getJScrollPane().setSize(getJTextArea().getSize());
        // setup Set button
        setSetAlarmButton(new JButton("Set"));
        getSetAlarmButton().setFont(Clock.font20);
        getSetAlarmButton().setOpaque(true);
        getSetAlarmButton().setBackground(Color.BLACK);
        getSetAlarmButton().setForeground(Color.BLACK);
        // setup checkboxes
        setupCheckBoxes();
    }

    public void setupMusicPlayer() {
        logger.info("setupMusicPlayer");
        InputStream inputStream = null;
        try {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { setMusicPlayer(new AdvancedPlayer(inputStream)); }
            logger.info("Music Player set");
        }
        catch (NullPointerException | JavaLayerException e) {
            logger.error("Music Player not set!");
            if (null == inputStream)
                printStackTrace(e, "An issue occurred while reading the alarm file.");
            printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
        }
    }

    protected void resetJAlarmLabel4() {
        logger.info("resetJAlarmLabel4");
        if (getClock().getListOfAlarms().isEmpty()) { getJAlarmLbl4().setText(getClock().defaultText(6)); }// All Alarms label...
        else {
            getJAlarmLbl4().setText(
                getClock().getListOfAlarms().size() == 1 ?
                    getClock().getListOfAlarms().size() + " Alarm Added"
                :   getClock().getListOfAlarms().size() + " Alarms Added"
            );
        }
    }

    protected void printStackTrace(Exception e) { printStackTrace(e, ""); }

    protected boolean validateFirstTextField() throws InvalidInputException {
        logger.info("validateFirstTextField");
        if (StringUtils.isBlank(getJTextField1().getText())) {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Hour cannot be blank");
        }
        else if (Integer.parseInt(getJTextField1().getText()) <= 0 ||
                 Integer.parseInt(getJTextField1().getText()) > 23 ) {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Hour must be between 0 and 23");
        }
        return true;
    }

    protected boolean validateSecondTextField() throws InvalidInputException {
        logger.info("validateSecondTextField");
        if (StringUtils.isBlank(getJTextField2().getText())) {
            getJTextField2().grabFocus();
            throw new InvalidInputException("Minutes cannot be blank");
        }
        else if (Integer.parseInt(getJTextField2().getText()) < 0 ||
                 Integer.parseInt(getJTextField2().getText()) > 59 ) {
            getJTextField2().grabFocus();
            throw new InvalidInputException("Minutes must be between 0 and 59");
        }
        return true;
    }

    protected boolean validateThirdTextField() throws InvalidInputException {
        logger.info("validateThirdTextField");
        if (StringUtils.isEmpty(getJTextField3().getText())) {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Time cannot be blank");
        }
        else if ((!AM.equalsIgnoreCase(getJTextField3().getText()) || !PM.equalsIgnoreCase(getJTextField3().getText()))
                 && getJTextField3().getText().length() != 2) {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Time must be AM or PM");
        }
        else if (Integer.parseInt(getJTextField1().getText()) > 12 &&
                 Integer.parseInt(getJTextField1().getText()) < 24 &&
                 AM.equalsIgnoreCase(getJTextField3().getText())) {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Hours can't be " + getJTextField1().getText() + " when Time is " + getJTextField3().getText());
        }
        else if (Integer.parseInt(getJTextField1().getText()) == 0 &&
                 StringUtils.equalsIgnoreCase(getJTextField3().getText(), PM)) {
            // basically setting militaryTime 00 hours, and saying PM
            // this makes no sense
            getJTextField3().grabFocus();
            throw new InvalidInputException("Hours can't be " + getJTextField1().getText() + " when Time is " + getJTextField3().getText());
        }
        return true;
    }

    protected boolean validateOnTheCheckBoxes() throws InvalidInputException {
        logger.info("validateOnTheCheckBoxes");
        ArrayList<DayOfWeek> days = checkWhichCheckBoxesWereChecked();
        if (days.isEmpty()) { throw new InvalidInputException("At least one checkbox must be selected."); }
        return true;
    }

    protected boolean validateACheckboxWasSelected() { return !checkWhichCheckBoxesWereChecked().isEmpty(); }

    protected void addAlarmMenuItemFromAlarm(Alarm alarm) {
        logger.info("addAlarmMenuItemFromAlarm");
        JMenuItem alarmItem = new JMenuItem(alarm.getAlarmAsString());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        logger.info("Size of viewAlarms before adding " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        getClock().getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        logger.info("Size of viewAlarms after adding " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    protected void deleteAlarmMenuItemFromViewAlarms(Alarm alarm) {
        logger.info("deleteAlarmMenuItemFromViewAlarms");
        logger.info("Size of viewAlarms before removal " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        for(int i = 0; i < getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.getAlarmAsString()))
            {
                getClock().getClockMenuBar().getAlarmFeature_Menu().remove(getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i));
            }
        }
        logger.info("Size of viewAlarms after removal " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }

    protected void resetJTextArea() {
        logger.info("resetJTextArea");
        getJTextArea().setText(EMPTY);
        for(Alarm alarm : clock.getListOfAlarms()) {
            if (!getJTextArea().getText().isEmpty())
            { getJTextArea().append(NEWLINE); }
            getJTextArea().append(alarm.getAlarmAsString()+NEWLINE);
            alarm.getDaysShortened().forEach(day -> getJTextArea().append(day));
        }
    }

    protected void setupAlarmButton() {
        logger.info("setupAlarmButton");
        getSetAlarmButton().addActionListener(action -> {
            //JCheckBox cbLog = (JCheckBox)(AbstractButton)action.getSource();
            //logger.error("cbLog isSelected: " + cbLog.isSelected());
            //logger.error("cbLog isEnabled: " + cbLog.isEnabled());
            // check if h, m, and time are set. exit if not
            logger.info("set alarm button clicked");
            Alarm alarm;
            boolean validated;
            try {
                validated = validateFirstTextField() && validateSecondTextField() // Hours and Minutes
                        && validateThirdTextField() && validateOnTheCheckBoxes(); // Time and Checkboxes

                if (!validated) {
                    logger.info("not valid inputs. not setting alarm");
                    setAlarm(null); // new Alarm()
                    //getClock().changeToAlarmPanel();
                }
                else { // validated is true
                    // Passed validation
                    if (isUpdatingAlarm()) {
                        logger.info("updating alarm");
                        alarm = createAlarm();
                        alarm.setIsAlarmUpdating(false); // at this point, we are doing updating
                        setAlarm(alarm);
                        // add clock to list of alarms
                        if (getClock().getListOfAlarms().isEmpty()) {
                            addAlarmMenuItemFromAlarm(alarm);
                            getClock().getListOfAlarms().add(alarm);
                        }
                        else {
                            boolean addToList = false;
                            for(int i = 0; i < getClock().getListOfAlarms().size(); i++) {
                                if (!getClock().getListOfAlarms().get(i).getAlarmAsString().equals(alarm.getAlarmAsString()) &&
                                        getClock().getListOfAlarms().get(i).getDays() != alarm.getDays())
                                { addToList = true; }
                                else
                                { logger.error("Tried updating an alarm but it already exists! Cannot create duplicate alarm."); }
                            }
                            if (addToList) {
                                addAlarmMenuItemFromAlarm(alarm);
                                getClock().getListOfAlarms().add(alarm);
                            }
                        }
                        logger.info("Size of listOfAlarms before adding " + (getClock().getListOfAlarms().size()-1));
                        logger.info("Size of listOfAlarms after adding " + getClock().getListOfAlarms().size());
                        // restart viewAlarms menu
                        //resetViewAlarmsMenu(getClock().getListOfAlarms());
                        setupAlarmsInMenuFunctionality();
                        setUpdatingAlarm(false);
                        resetJTextArea();
                        resetJCheckBoxes();
                        resetJAlarmLabel4();
                        // determine how to update alarm (update/delete)
                    }
                    else { // creating a new alarm
                        logger.info("creating new alarm");
                        alarm = createAlarm();
                        setAlarm(alarm);
                        setUpdatingAlarm(false);
                        if (getClock().getListOfAlarms().isEmpty()) {
                            addAlarmMenuItemFromAlarm(alarm);
                            getClock().getListOfAlarms().add(alarm);
                        }
                        else {
                            boolean addToList = false;
                            for(int i = 0; i < getClock().getListOfAlarms().size(); i++) {
                                if (!getClock().getListOfAlarms().get(i).getAlarmAsString().equals(alarm.getAlarmAsString()) ||
                                        getClock().getListOfAlarms().get(i).getDays() != alarm.getDays())
                                { addToList = true; }
                                else
                                { logger.error("Tried adding an alarm but it already exists! Cannot create duplicate alarm."); }
                            }
                            if (addToList) {
                                addAlarmMenuItemFromAlarm(alarm);
                                getClock().getListOfAlarms().add(alarm);
                            }
                        }
                        logger.info("Size of listOfAlarms after adding " + getClock().getListOfAlarms().size());
                        // display list of alarms below All Alarms
                        resetJTextArea();
                        resetJCheckBoxes();
                        setupAlarmsInMenuFunctionality();
                        // erase input in textFields
                        getJTextField1().setText("");
                        getJTextField2().setText("");
                        getJTextField3().setText("");
                        resetJAlarmLabel4();
                    }
                }
            }
            catch (InvalidInputException | ParseException e) {
                getJTextArea().setLineWrap(true);
                getJTextArea().setWrapStyleWord(true);
                getJTextArea().setText(e.getMessage());
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
    public Alarm createAlarm() throws ParseException, InvalidInputException {
        logger.info("createAlarm");
        int hour = Integer.parseInt(getJTextField1().getText());
        int minutes = Integer.parseInt(getJTextField2().getText());
        String ampm = getJTextField3().getText(); //convertStringToTimeAMPM(getJTextField3().getText());
        boolean valid;
        valid = validateFirstTextField() && validateSecondTextField()
                && validateThirdTextField() && validateACheckboxWasSelected();
        if (valid)
        {
            logger.info("valid alarm values...");
            ArrayList<DayOfWeek> days = checkWhichCheckBoxesWereChecked();
            Alarm alarm = new Alarm(hour, minutes, ampm, false, days, getClock());
            alarm.setIsAlarmGoingOff(false);
            logger.info("Created an alarm: " + alarm.getAlarmAsString());
            logger.info("days: ");
            if (null != alarm.getDays())
            {
                for(DayOfWeek day: days)
                { logger.info(day+"\t"); }
            }
            logger.info("Alarm created");
            return alarm;
        }
        else
        { return null; }
    }

    protected void updateTheAlarm(Alarm alarmToUpdate) {
        logger.info("updateTheAlarm");
        if (null != alarmToUpdate) {
            logger.info("Updating an alarm: " + alarmToUpdate.getAlarmAsString() + alarmToUpdate.getDays());
            if (alarmToUpdate.getHours() < 10 && alarmToUpdate.getHours() != 0) {
                getJTextField1().setText("0" + alarmToUpdate.getHours());
            } else if (alarmToUpdate.getHours() == 0 && alarmToUpdate.getMinutes() != 0) {
                getJTextField1().setText("00");
            } else {
                getJTextField1().setText(alarmToUpdate.getHoursAsStr());
            }
            if (alarmToUpdate.getMinutes() < 10 && alarmToUpdate.getMinutes() != 0) {
                getJTextField2().setText("0" + alarmToUpdate.getMinutes());
            } else if (alarmToUpdate.getMinutes() == 0 && alarmToUpdate.getHours() != 0) {
                getJTextField2().setText("00");
            } else {
                getJTextField2().setText(alarmToUpdate.getMinutesAsStr());
            }
            getJTextField3().setText(alarmToUpdate.getAMPM());
            setCheckBoxesIfWasSelected(alarmToUpdate);
        }
        setUpdatingAlarm(true);
        // remove alarm from list of alarms
        deleteAlarmMenuItemFromViewAlarms(alarmToUpdate);
        logger.info("Size of listOfAlarms before removing " + getClock().getListOfAlarms().size());
        getClock().getListOfAlarms().remove(alarmToUpdate);
        logger.info("Size of listOfAlarms after removing " + getClock().getListOfAlarms().size());
        resetJTextArea();
        getJAlarmLbl4().setText("Updating alarm");
    }

    public void triggerAlarm(ExecutorService executor) {
        logger.info("triggerAlarm");
        setAlarmIsGoingOff(true);
        getClock().getDigitalClockPanel().getLabel1().setText(getCurrentAlarmGoingOff().getAlarmAsString());
        getClock().getDigitalClockPanel().getLabel2().setText("is going off!");
        // play sound
        Callable<String> c = () -> {
            try {
                setupMusicPlayer();
                logger.debug("while alarm is going off, play sound");
                while (getCurrentAlarmGoingOff().alarmGoingOff) {
                    getMusicPlayer().play(50);
                }
                logger.debug("alarm has stopped");
                return "Alarm triggered";
            }
            catch (Exception e) {
                logger.error(e.getCause().getClass().getName() + " - " + e.getMessage());
                printStackTrace(e);
                setupMusicPlayer();
                getMusicPlayer().play(50);
                return "Reset music player required";
            }
        };
        executor.submit(c);
    }

    public void stopAlarm() {
        logger.info("stopAlarm");
        setMusicPlayer(null);
        setAlarmIsGoingOff(false);
        logger.info("Stopping music.");
        logger.info(getAlarm().getAlarmAsString()+" alarm turned off.");
    }

    public void checkIfAnyAlarmsAreGoingOff() {
        logger.info("checkIfAnyAlarmsAreGoingOff");
        // alarm has reference to time
        // check all alarms
        // if any alarm matches clock's time, an alarm should be going off
        clock.getListOfAlarms().forEach((alarm) -> {
            for(DayOfWeek day : alarm.getDays()) {
                if (alarm.getAlarmAsString().equals(clock.getAlarmTimeAsStr())
                        &&
                        day == getClock().getDayOfWeek()) {
                    // time for alarm to be triggered
                    alarm.setIsAlarmGoingOff(true);
                    setCurrentAlarmGoingOff(alarm);
                    setAlarmIsGoingOff(true);
                    logger.info("Alarm " + getCurrentAlarmGoingOff().getAlarmAsString() + " matches clock's time. ");
                    logger.info("Sounding alarm...");
                }
                else if (getClock().isShowMilitaryTime()) { // if in military time, change clocks hours back temporarily
                    if (getClock().getHours() > 12) {
                        int tempHour = getClock().getHours()-12;
                        String tempHourAsStr = (tempHour < 10) ? "0"+tempHour : String.valueOf(tempHour);
                        if (alarm.getAlarmAsString().equals(tempHourAsStr+":"+getClock().getMinutesAsStr()+" "+getClock().getAMPM())
                                &&
                                day == getClock().getDayOfWeek()) {
                            // time for alarm to be triggered on
                            setCurrentAlarmGoingOff(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            setAlarmIsGoingOff(true);
                            logger.info("Alarm " + getCurrentAlarmGoingOff().getAlarmAsString() + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                    else {
                        if (alarm.getAlarmAsString().equals(getClock().getHoursAsStr()+":"+getClock().getMinutesAsStr()+" "+getClock().getAMPM())
                                &&
                                day == getClock().getDayOfWeek()) {
                            // time for alarm to be triggered on
                            setCurrentAlarmGoingOff(alarm);
                            alarm.setIsAlarmGoingOff(true);
                            setAlarmIsGoingOff(true);
                            logger.info("Alarm " + getCurrentAlarmGoingOff().getAlarmAsString() + " matches clock's time. ");
                            logger.info("Sounding alarm...");
                        }
                    }
                }
            }
        });
        // update lbl1 and lbl2 to display alarm
        // user must "view that alarm" to turn it off
        Alarm currentAlarm = getCurrentAlarmGoingOff();
        ExecutorService executor = Executors.newCachedThreadPool();
        if (null != currentAlarm && currentAlarm.isAlarmGoingOff()) {
            triggerAlarm(executor);
        }
    }

    public void setupAlarmsInMenuFunctionality() {
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
        logger.info("setupAlarmsInMenuFunctionality");
        for(int i=0; i<getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++) {
            if (!"Set Alarms".equals(getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText())) {
                JMenuItem menuItem = getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i);
                menuItem.addActionListener(action -> {
                    getClock().getListOfAlarms().forEach(alarm -> {
                        if (alarm.getAlarmAsString().equals(menuItem.getText()))
                        { setAlarm(alarm); }
                    });
                    if (getMusicPlayer() == null) { setupMusicPlayer(); }
                    // if an alarm is going off and we clicked on it in the menuBar
                    if (getCurrentAlarmGoingOff() != null) {
                        setAlarm(getCurrentAlarmGoingOff());
                        if (getMusicPlayer() != null) { stopAlarm(); }
                        else { logger.info("Music player is null!"); }
                        setCheckBoxesIfWasSelected(getAlarm());
                        getAlarm().setIsAlarmGoingOff(false);
                        getAlarm().setIsAlarmUpdating(true); // this and the boolean below we want true
                        setUpdatingAlarm(true); // we want to continue with the logic that's done in the next if
                        setCurrentAlarmGoingOff(null);
                        logger.info("Size of listOfAlarms before removing " + getClock().getListOfAlarms().size());
                        // remove alarm from list of alarms
                        getClock().getListOfAlarms().remove(getAlarm());
                        logger.info("Size of listOfAlarms after removing " + getClock().getListOfAlarms().size());
                        deleteAlarmMenuItemFromViewAlarms(getAlarm());
                        resetJTextArea();
                        getClock().getAlarmPanel().getJTextField1().setText(getAlarm().getHoursAsStr());
                        getClock().getAlarmPanel().getJTextField2().setText(getAlarm().getMinutesAsStr());
                        getClock().getAlarmPanel().getJTextField3().setText(getAlarm().getAMPM());
                        getJAlarmLbl4().setText("Alarm off.");
                    }
                    // we are updating an alarm by clicking on it in the menuBar
                    else if (getAlarm() != null) { // && getAlarm().isUpdatingAlarm())
                        //updateTheAlarm(menuItem);
                        updateTheAlarm(getAlarm());
                        getJTextField1().setText(getAlarm().getHoursAsStr());
                        getJTextField2().setText(getAlarm().getMinutesAsStr());
                        getJTextField3().setText(getAlarm().getAMPM());
                    }
                    getClock().changeToAlarmPanel(false);
                });
            }
        }
    }

    protected ArrayList<DayOfWeek> checkWhichCheckBoxesWereChecked() {
        logger.info("checkWhichCheckBoxesWereChecked");
        ArrayList<DayOfWeek> daysSelected = new ArrayList<>();
        if (getMondayCheckBox().isSelected()) { daysSelected.add(MONDAY); }
        if (getTuesdayCheckBox().isSelected()) { daysSelected.add(TUESDAY); }
        if (getWednesdayCheckBox().isSelected()) { daysSelected.add(WEDNESDAY); }
        if (getThursdayCheckBox().isSelected()) { daysSelected.add(THURSDAY); }
        if (getFridayCheckBox().isSelected()) { daysSelected.add(FRIDAY); }
        if (getSaturdayCheckBox().isSelected()) { daysSelected.add(SATURDAY); }
        if (getSundayCheckBox().isSelected()) { daysSelected.add(SUNDAY); }
        if (getWeekCheckBox().isSelected()) {
            if (!daysSelected.contains(MONDAY)) daysSelected.add(MONDAY);
            if (!daysSelected.contains(TUESDAY)) daysSelected.add(TUESDAY);
            if (!daysSelected.contains(WEDNESDAY)) daysSelected.add(WEDNESDAY);
            if (!daysSelected.contains(THURSDAY)) daysSelected.add(THURSDAY);
            if (!daysSelected.contains(FRIDAY)) daysSelected.add(FRIDAY);
        } // add Monday - Friday
        if (getWeekendCheckBox().isSelected()) {
            if (!daysSelected.contains(SATURDAY)) daysSelected.add(SATURDAY);
            if (!daysSelected.contains(SUNDAY)) daysSelected.add(SUNDAY);
        } // add Saturday and Sunday
        return daysSelected;
    }

    protected void resetJCheckBoxes() {
        logger.info("resetJCheckBoxes");
        if (getSundayCheckBox().isSelected()) { getSundayCheckBox().setSelected(false); }
        if (getMondayCheckBox().isSelected()) { getMondayCheckBox().setSelected(false); }
        if (getTuesdayCheckBox().isSelected()) { getTuesdayCheckBox().setSelected(false); }
        if (getWednesdayCheckBox().isSelected()) { getWednesdayCheckBox().setSelected(false); }
        if (getThursdayCheckBox().isSelected()) { getThursdayCheckBox().setSelected(false); }
        if (getFridayCheckBox().isSelected()) { getFridayCheckBox().setSelected(false); }
        if (getSaturdayCheckBox().isSelected()) { getSaturdayCheckBox().setSelected(false); }
        if (getWeekCheckBox().isSelected()) { getWeekCheckBox().setSelected(false); }
        if (getWeekendCheckBox().isSelected()) { getWeekendCheckBox().setSelected(false); }
    }

    public void setCheckBoxesIfWasSelected(Alarm alarmToUpdate) {
        logger.info("setCheckBoxesIfWasSelected");
        if (alarmToUpdate.getDays().contains(MONDAY)) { getMondayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(TUESDAY)) { getTuesdayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(WEDNESDAY)) { getWednesdayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(THURSDAY)) { getThursdayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(FRIDAY)) { getFridayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(SATURDAY)) { getSaturdayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(SUNDAY)) { getSundayCheckBox().setSelected(true); }
        if (alarmToUpdate.getDays().contains(MONDAY) &&
                alarmToUpdate.getDays().contains(TUESDAY) &&
                alarmToUpdate.getDays().contains(WEDNESDAY) &&
                alarmToUpdate.getDays().contains(THURSDAY) &&
                alarmToUpdate.getDays().contains(FRIDAY)) {
            getMondayCheckBox().setSelected(true);
            getTuesdayCheckBox().setSelected(true);
            getWednesdayCheckBox().setSelected(true);
            getThursdayCheckBox().setSelected(true);
            getFridayCheckBox().setSelected(true);
            getWeekCheckBox().setSelected(true);
        }
        if (alarmToUpdate.getDays().contains(SATURDAY) &&
                alarmToUpdate.getDays().contains(SUNDAY)) {
            getSaturdayCheckBox().setSelected(true);
            getSundayCheckBox().setSelected(true);
            getWeekendCheckBox().setSelected(true);
        }
    }

    protected void setupCheckBoxes() {
        logger.info("setupCheckBoxes");
        setSundayCheckBox(new JCheckBox(SUNDAY.toString().substring(0,2), false));
        getSundayCheckBox().setFont(Clock.font20);
        getSundayCheckBox().setBackground(Color.BLACK);
        getSundayCheckBox().setForeground(Color.WHITE);
        getSundayCheckBox().addActionListener(action -> {
            if (!getSundayCheckBox().isSelected())
            {
                logger.info("Sunday checkbox not selected!");
                getSundayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Sunday checkbox selected!");
                getSundayCheckBox().setSelected(true);
            }
        });

        setMondayCheckBox(new JCheckBox(MONDAY.toString().substring(0,1), false));
        getMondayCheckBox().setFont(Clock.font20);
        getMondayCheckBox().setBackground(Color.BLACK);
        getMondayCheckBox().setForeground(Color.WHITE);
        getMondayCheckBox().addActionListener(action -> {
            if (!getMondayCheckBox().isSelected())
            {
                logger.info("Monday checkbox not selected!");
                getMondayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Monday checkbox selected!");
                getMondayCheckBox().setSelected(true);
            }
        });

        setTuesdayCheckBox(new JCheckBox(TUESDAY.toString().substring(0,1), false));
        getTuesdayCheckBox().setFont(Clock.font20);
        getTuesdayCheckBox().setBackground(Color.BLACK);
        getTuesdayCheckBox().setForeground(Color.WHITE);
        getTuesdayCheckBox().addActionListener(action -> {
            if (!getTuesdayCheckBox().isSelected())
            {
                logger.info("Tuesday checkbox not selected!");
                getTuesdayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Tuesday checkbox selected!");
                getTuesdayCheckBox().setSelected(true);
            }
        });

        setWednesdayCheckBox(new JCheckBox(WEDNESDAY.toString().substring(0,1), false));
        getWednesdayCheckBox().setFont(Clock.font20);
        getWednesdayCheckBox().setBackground(Color.BLACK);
        getWednesdayCheckBox().setForeground(Color.WHITE);
        getWednesdayCheckBox().addActionListener(action -> {
            if (!getWednesdayCheckBox().isSelected())
            {
                logger.info("Wednesday checkbox not selected!");
                getWednesdayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Wednesday checkbox selected!");
                getWednesdayCheckBox().setSelected(true);
            }
        });

        setThursdayCheckBox(new JCheckBox(THURSDAY.toString().substring(0,1)+THURSDAY.toString().substring(1,2).toLowerCase(Locale.ROOT), false));
        getThursdayCheckBox().setFont(Clock.font20);
        getThursdayCheckBox().setBackground(Color.BLACK);
        getThursdayCheckBox().setForeground(Color.WHITE);
        getThursdayCheckBox().addActionListener(action -> {
            if (!getThursdayCheckBox().isSelected())
            {
                logger.info("Thursday checkbox not selected!");
                getThursdayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Thursday checkbox selected!");
                getThursdayCheckBox().setSelected(true);
            }
        });

        setFridayCheckBox(new JCheckBox(FRIDAY.toString().substring(0,1), false));
        getFridayCheckBox().setFont(Clock.font20);
        getFridayCheckBox().setBackground(Color.BLACK);
        getFridayCheckBox().setForeground(Color.WHITE);
        getFridayCheckBox().addActionListener(action -> {
            if (!getFridayCheckBox().isSelected())
            {
                logger.info("Friday checkbox not selected!");
                getFridayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Friday checkbox selected!");
                getFridayCheckBox().setSelected(true);
            }
        });

        setSaturdayCheckBox(new JCheckBox(SATURDAY.toString().substring(0,1), false));
        getSaturdayCheckBox().setFont(Clock.font20);
        getSaturdayCheckBox().setBackground(Color.BLACK);
        getSaturdayCheckBox().setForeground(Color.WHITE);
        getSaturdayCheckBox().addActionListener(action -> {
            if (!getSaturdayCheckBox().isSelected())
            {
                logger.info("Saturday checkbox not selected!");
                getSaturdayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Saturday checkbox selected!");
                getSaturdayCheckBox().setSelected(true);
            }
        });
        // setup WEEK
        setWeekCheckBox(new JCheckBox("WK", false));
        getWeekCheckBox().setFont(Clock.font20);
        getWeekCheckBox().setBackground(Color.BLACK);
        getWeekCheckBox().setForeground(Color.WHITE);
        getWeekCheckBox().addActionListener(action -> {
            if (!getWeekCheckBox().isSelected())
            {
                logger.info("Week checkbox not selected!");
                getWeekCheckBox().setSelected(false);
                getMondayCheckBox().setSelected(false);
                getTuesdayCheckBox().setSelected(false);
                getWednesdayCheckBox().setSelected(false);
                getThursdayCheckBox().setSelected(false);
                getFridayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Week checkbox selected!");
                getWeekCheckBox().setSelected(true);
                getMondayCheckBox().setSelected(true);
                getTuesdayCheckBox().setSelected(true);
                getWednesdayCheckBox().setSelected(true);
                getThursdayCheckBox().setSelected(true);
                getFridayCheckBox().setSelected(true);
            }
        });
        // setup WEEKEND
        setWeekendCheckBox(new JCheckBox("WKD", false));
        getWeekendCheckBox().setFont(Clock.font20);
        getWeekendCheckBox().setBackground(Color.BLACK);
        getWeekendCheckBox().setForeground(Color.WHITE);
        getWeekendCheckBox().addActionListener(action -> {
            if (!getWeekendCheckBox().isSelected())
            {
                logger.info("Weekend checkbox not selected!");
                getWeekendCheckBox().setSelected(false);
                getSaturdayCheckBox().setSelected(false);
                getSundayCheckBox().setSelected(false);
            }
            else
            {
                logger.info("Weekend checkbox selected!");
                getWeekendCheckBox().setSelected(true);
                getSaturdayCheckBox().setSelected(true);
                getSundayCheckBox().setSelected(true);
            }
        });
    }

    public void printStackTrace(Exception e, String message) {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace()) {
            logger.error(ste.toString());
        }
    }

    @Override
    public void addComponentsToPanel() {
        logger.info("addComponentsToPanel");
        addComponent(getJAlarmLbl1(),0,0,1,1,0,0,   GridBagConstraints.BOTH, new Insets(0,0,0,0)); // H
        addComponent(getJTextField1(),0,1,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(getJAlarmLbl2(),0,2,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // M
        addComponent(getJTextField2(),0,3,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(getJAlarmLbl3(),0,4,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // Time (AM/PM)
        addComponent(getJTextField3(),0,5,1,1, 0,0, GridBagConstraints.BOTH, new Insets(0,0,0,0)); // textField
        addComponent(getJAlarmLbl4(), 0, 6, 2, 1, 2, 2, GridBagConstraints.BOTH, new Insets(0,0,0,0));
        getGridBagConstraints().weighty = 4;
        getGridBagConstraints().weightx = 2;
        addComponent(getJScrollPane(),1,6,2,4, 0,0, GridBagConstraints.BOTH, new Insets(1,1,1,1)); // textArea
        getGridBagConstraints().weighty = 1;
        getGridBagConstraints().weightx = 1;
        // new column, first row
        addComponent(getMondayCheckBox(), 1,0,2,1, 0,0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Monday
        addComponent(getTuesdayCheckBox(), 2, 0, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Tuesday
        addComponent(getWednesdayCheckBox(), 3, 0, 2, 1, 2, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Wednesday
        // new column, second row
        addComponent(getThursdayCheckBox(), 1, 2, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Thursday
        addComponent(getFridayCheckBox(), 2, 2, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Friday
        addComponent(getSaturdayCheckBox(), 3, 2, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Saturday
        // new column, third row
        addComponent(getSundayCheckBox(), 1, 4, 2, 1, 0, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Sunday
        addComponent(getWeekCheckBox(), 2, 4, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        addComponent(getWeekendCheckBox(), 3, 4, 2, 1, 1, 0, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Sqr Weekend
        // set button
        addComponent(getSetAlarmButton(), 4, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, new Insets(0,0,0,0)); // Set button
        // header and alarms
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill, Insets insets) {
        //logger.info("addComponent");
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().insets = insets;
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }

    public void setupSettingsMenu() {
        clock.clearSettingsMenu();
        logger.info("No settings set up for Alarm Panel");
    }
}
