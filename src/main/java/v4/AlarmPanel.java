package v4;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static v4.Time.AMPM.AM;
import static v4.Time.AMPM.PM;
import static v4.Time.Day;
import static v4.Time.Day.*;

@SuppressWarnings("unused")
/* the AlarmPanel will appear and the alarm will appear
 * in the text fields. The alarm will be removed from
 * the list of alarms, the textarea, and from the menu
 */
public class AlarmPanel extends JPanel implements Panels {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jalarmLbl1 = new JLabel("H", SwingConstants.CENTER); // H
    private JLabel jalarmLbl2 = new JLabel("M", SwingConstants.CENTER); // M
    private JLabel jalarmLbl3 = new JLabel("T", SwingConstants.CENTER); // Time (AM/PM)
    private JLabel jalarmLbl4 = new JLabel("Current Alarms", SwingConstants.CENTER); // Current Alarms
    private JLabel mondayCheckBoxLabel = new JLabel("M");
    private JLabel tuesdayCheckBoxLabel = new JLabel("T");
    private JLabel wednesdayCheckBoxLabel = new JLabel("W");
    private JLabel thursdayCheckBoxLabel = new JLabel("Th");
    private JLabel fridayCheckBoxLabel = new JLabel("F");
    private JLabel saturdayCheckBoxLabel = new JLabel("Sa");
    private JLabel sundayCheckBoxLabel = new JLabel("Su");
    private JLabel weekCheckBoxLabel = new JLabel("Week");
    private JLabel wkendCheckBoxLabel = new JLabel("Wkend");
    private JCheckBox mondayCheckBox = new JCheckBox("M");
    private JCheckBox tuesdayCheckBox = new JCheckBox("T");
    private JCheckBox wednesdayCheckBox = new JCheckBox("W");
    private JCheckBox thursdayCheckBox = new JCheckBox("Th");
    private JCheckBox fridayCheckBox = new JCheckBox("F");
    private JCheckBox saturdayCheckBox = new JCheckBox("Sa");
    private JCheckBox sundayCheckBox = new JCheckBox("Su");
    private JCheckBox weekCheckBox = new JCheckBox("Week");
    private JCheckBox wkendCheckBox = new JCheckBox("Wkend");
    private ButtonGroup checkBoxes =  new ButtonGroup();
    private JTextField jtextField1 = new JTextField(2); // Hour textField
    private JTextField jtextField2 = new JTextField(2); // Min textField
    private JTextField jtextField3 = new JTextField(2); // Time textField
    private JButton jSetAlarmButton = new JButton("Set");
    private JTextArea jTextArea = new JTextArea(2, 4);
    //private JScrollPane scrollPane = null;
    private Clock clock;
    private Alarm alarm;
    private Alarm currentAlarmGoingOff;
    private boolean updatingAlarm;
    private AdvancedPlayer musicPlayer;

    public AlarmPanel(Clock clock) {
        setClock(clock);
        setMinimumSize(Clock.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupAlarmPanel(getClock());
        setupAlarmButton();
        setupMusicPlayer();
        addComponentsToPanel();
    }

    // Getters
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public Alarm getAlarm() { return this.alarm; }
    public JLabel getJAlarmLbl1() { return this.jalarmLbl1; } // H
    public JLabel getJAlarmLbl2() { return this.jalarmLbl2; } // M
    public JLabel getJAlarmLbl3() { return this.jalarmLbl3; } // T
    public JLabel getJAlarmLbl4() { return this.jalarmLbl4; } // All alarms
    public JTextField getJTextField1() { return this.jtextField1; }
    public JTextField getJTextField2() { return this.jtextField2; }
    public JTextField getJTextField3() { return this.jtextField3; }
    public JButton getJSetAlarmButton() { return this.jSetAlarmButton; }
    //public JScrollPane getJScrollPane() { return this.scrollPane; }
    public JTextArea getJTextArea() { return this.jTextArea; }
    public boolean isUpdatingAlarm() { return updatingAlarm; }
    public AdvancedPlayer getMusicPlayer() { return musicPlayer; }
    public Alarm getCurrentAlarmGoingOff() { return this.currentAlarmGoingOff; }
    public JCheckBox getMondayCheckBox() { return mondayCheckBox; }
    public JCheckBox getTuesdayCheckBox() { return tuesdayCheckBox; }
    public JCheckBox getWednesdayCheckBox() { return wednesdayCheckBox; }
    public JCheckBox getThursdayCheckBox() { return thursdayCheckBox; }
    public JCheckBox getFridayCheckBox() { return fridayCheckBox; }public JCheckBox getSaturdayCheckBox() { return saturdayCheckBox; }
    public JCheckBox getSundayCheckBox() { return sundayCheckBox; }
    public JCheckBox getWeekCheckBox() { return weekCheckBox; }
    public JCheckBox getWkendCheckBox() { return wkendCheckBox; }
    public ButtonGroup getCheckBoxes() { return checkBoxes; }
    public JLabel getMondayCheckBoxLabel() { return mondayCheckBoxLabel; }
    public JLabel getTuesdayCheckBoxLabel() { return tuesdayCheckBoxLabel; }
    public JLabel getWednesdayCheckBoxLabel() { return wednesdayCheckBoxLabel; }
    public JLabel getThursdayCheckBoxLabel() { return thursdayCheckBoxLabel; }
    public JLabel getFridayCheckBoxLabel() { return fridayCheckBoxLabel; }
    public JLabel getSaturdayCheckBoxLabel() { return saturdayCheckBoxLabel; }
    public JLabel getSundayCheckBoxLabel() { return sundayCheckBoxLabel; }
    public JLabel getWeekCheckBoxLabel() { return weekCheckBoxLabel; }
    public JLabel getWkendCheckBoxLabel() { return wkendCheckBoxLabel; }

    // Setters
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClock(Clock clock) { this.clock = clock; }
    protected void setAlarm(Alarm alarm) { this.alarm = alarm; }
    protected void setJAlarmLbl1(JLabel jalarmLbl1) { this.jalarmLbl1 = jalarmLbl1; }
    protected void setJAlarmLbl2(JLabel jalarmLbl2) { this.jalarmLbl2 = jalarmLbl2; }
    protected void setJAlarmLbl3(JLabel jalarmLbl3) { this.jalarmLbl3 = jalarmLbl3; }
    protected void setJAlarmLbl4(JLabel jalarmLbl4) { this.jalarmLbl4 = jalarmLbl4; }
    protected void setJTextField1(JTextField jtextField1) { this.jtextField1 = jtextField1; }
    protected void setJTextField2(JTextField jtextField2) { this.jtextField2 = jtextField2; }
    protected void setJTextField3(JTextField jtextField3) { this.jtextField3 = jtextField3; }
    protected void setJSetAlarmButton(JButton jSetAlarmButton) { this.jSetAlarmButton = jSetAlarmButton; }
    //protected void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    protected void setJTextArea(JTextArea jTextArea) { this.jTextArea = jTextArea; }
    protected void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    protected void setCurrentAlarmGoingOff(Alarm currentAlarmGoingOff) { this.currentAlarmGoingOff = currentAlarmGoingOff; }
    protected void setMondayCheckBox(JCheckBox mondayCheckBox) { this.mondayCheckBox = mondayCheckBox; }
    protected void setTuesdayCheckBox(JCheckBox tuesdayCheckBox) { this.tuesdayCheckBox = tuesdayCheckBox; }
    protected void setWednesdayCheckBox(JCheckBox wednesdayCheckBox) { this.wednesdayCheckBox = wednesdayCheckBox; }
    protected void setThursdayCheckBox(JCheckBox thursdayCheckBox) { this.thursdayCheckBox = thursdayCheckBox; }
    protected void setFridayCheckBox(JCheckBox fridayCheckBox) { this.fridayCheckBox = fridayCheckBox; }
    protected void setSaturdayCheckBox(JCheckBox saturdayCheckBox) { this.saturdayCheckBox = saturdayCheckBox; }
    protected void setSundayCheckBox(JCheckBox sundayCheckBox) { this.sundayCheckBox = sundayCheckBox; }
    protected void setWeekCheckBox(JCheckBox weekCheckBox) { this.weekCheckBox = weekCheckBox; }
    protected void setWkendCheckBox(JCheckBox wkendCheckBox) { this.wkendCheckBox = wkendCheckBox; }
    protected void setCheckBoxes(ButtonGroup checkBoxes) { this.checkBoxes = checkBoxes; }
    protected void setMondayCheckBoxLabel(JLabel mondayCheckBoxLabel) { this.mondayCheckBoxLabel = mondayCheckBoxLabel; }
    protected void setTuesdayCheckBoxLabel(JLabel tuesdayCheckBoxLabel) { this.tuesdayCheckBoxLabel = tuesdayCheckBoxLabel; }
    protected void setWednesdayCheckBoxLabel(JLabel wednesdayCheckBoxLabel) { this.wednesdayCheckBoxLabel = wednesdayCheckBoxLabel; }
    protected void setThursdayCheckBoxLabel(JLabel thursdayCheckBoxLabel) { this.thursdayCheckBoxLabel = thursdayCheckBoxLabel; }
    protected void setFridayCheckBoxLabel(JLabel fridayCheckBoxLabel) { this.fridayCheckBoxLabel = fridayCheckBoxLabel; }
    protected void setSaturdayCheckBoxLabel(JLabel saturdayCheckBoxLabel) { this.saturdayCheckBoxLabel = saturdayCheckBoxLabel; }
    protected void setSundayCheckBoxLabel(JLabel sundayCheckBoxLabel) { this.sundayCheckBoxLabel = sundayCheckBoxLabel; }
    protected void setWeekCheckBoxLabel(JLabel weekCheckBoxLabel) { this.weekCheckBoxLabel = weekCheckBoxLabel; }
    protected void setWkendCheckBoxLabel(JLabel wkendCheckBoxLabel) { this.wkendCheckBoxLabel = wkendCheckBoxLabel; }

    // Helper methods
    public void setupMusicPlayer()
    {
        try
        {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { setMusicPlayer(new AdvancedPlayer(inputStream)); }
        }
        catch (NullPointerException | JavaLayerException e)
        {
            e.printStackTrace();
        }
    }
    public Time.AMPM convertStringToTimeAMPM(String ampm)
    {
        if (StringUtils.equals("AM", ampm.toUpperCase()))
        { return AM; }
        else if (StringUtils.equals("PM", ampm.toUpperCase()))
        { return PM; }
        else return Time.AMPM.ERROR;
    }
    public void setupAlarmPanel(Clock clock)
    {
        clock.setCalendar(Calendar.getInstance());
        clock.setCalendarTime(new Date());
        clock.setDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        getJTextField1().requestFocusInWindow();
        getJTextField1().setText("");
        getJTextField1().setText("");
        getJTextField1().setText("");
        getJAlarmLbl1().setFont(Clock.font30); // H
        getJAlarmLbl2().setFont(Clock.font30); // M
        getJAlarmLbl3().setFont(Clock.font30); // T
        getJAlarmLbl4().setFont(Clock.font20); // All Alarms
        getMondayCheckBoxLabel().setForeground(Color.WHITE);
        getMondayCheckBoxLabel().setFont(Clock.font20);
        getTuesdayCheckBoxLabel().setForeground(Color.WHITE);
        getTuesdayCheckBoxLabel().setFont(Clock.font20);
        getWednesdayCheckBoxLabel().setForeground(Color.WHITE);
        getWednesdayCheckBoxLabel().setFont(Clock.font20);
        getThursdayCheckBoxLabel().setForeground(Color.WHITE);
        getThursdayCheckBoxLabel().setFont(Clock.font20);
        getFridayCheckBoxLabel().setForeground(Color.WHITE);
        getFridayCheckBoxLabel().setFont(Clock.font20);
        getSaturdayCheckBoxLabel().setForeground(Color.WHITE);
        getSaturdayCheckBoxLabel().setFont(Clock.font20);
        getSundayCheckBoxLabel().setForeground(Color.WHITE);
        getSundayCheckBoxLabel().setFont(Clock.font20);
        getWeekCheckBoxLabel().setForeground(Color.WHITE);
        getWeekCheckBoxLabel().setFont(Clock.font20);
        getWkendCheckBoxLabel().setForeground(Color.WHITE);
        getWkendCheckBoxLabel().setFont(Clock.font20);
        getCheckBoxes().add(getMondayCheckBox());
        getCheckBoxes().add(getTuesdayCheckBox());
        getCheckBoxes().add(getWednesdayCheckBox());
        getCheckBoxes().add(getThursdayCheckBox());
        getCheckBoxes().add(getFridayCheckBox());
        getCheckBoxes().add(getSaturdayCheckBox());
        getCheckBoxes().add(getSundayCheckBox());
        getCheckBoxes().add(getWeekCheckBox());
        getCheckBoxes().add(getWkendCheckBox());
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        getJTextArea().setFont(Clock.font30); // message
        getJTextArea().setVisible(true);
        getJTextArea().setEditable(false);
        getJTextArea().setWrapStyleWord(true);
        getJTextArea().setLineWrap(false);
        getJTextArea().setBackground(Color.WHITE);
        getJTextArea().setForeground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
        //setJScrollPane(new JScrollPane(getJTextArea()));
        //getJScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //getJScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //getJScrollPane().setMaximumSize(new Dimension(20,50));
    }
    @Override
    public void addComponentsToPanel()
    {
        updateLabels();
        addComponent(getJAlarmLbl1(), 0,0,1,1, 0,0, GridBagConstraints.HORIZONTAL); // H
        addComponent(getJTextField1(), 0,1,1,1, 1,7, GridBagConstraints.NONE); // textField
        addComponent(getJAlarmLbl2(), 0,2,1,1, 0,0, GridBagConstraints.HORIZONTAL); // M
        addComponent(getJTextField2(), 0,3,1,1, 1,7, GridBagConstraints.NONE); // textField
        addComponent(getJAlarmLbl3(), 0,4,1,1, 0,0, GridBagConstraints.HORIZONTAL); // Time (AM/PM)
        addComponent(getJTextField3(), 0,5,3,1, 1,7, GridBagConstraints.WEST); // textField
        addComponent(getJTextArea(), 0,6,3,3, 20,0, GridBagConstraints.NORTHWEST); // textArea
        // new row, first column
        addComponent(getMondayCheckBox(), 1,0,1,1, 2,2, GridBagConstraints.NONE); // Sqr Monday
        addComponent(getMondayCheckBoxLabel(), 1, 1, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getTuesdayCheckBox(), 2, 0, 1, 1, 2, 2, GridBagConstraints.NONE); // Sqr Tuesday
        addComponent(getTuesdayCheckBoxLabel(), 2, 1, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getWednesdayCheckBox(), 3, 0, 1, 1, 2, 2, GridBagConstraints.NONE); // Sqr Wednesday
        addComponent(getWednesdayCheckBoxLabel(), 3, 1, 1, 1, 2, 2, GridBagConstraints.BOTH);
        // new row, second column
        addComponent(getThursdayCheckBox(), 1, 2, 1, 1, 2, 2, GridBagConstraints.NONE); // Sqr Thursday
        addComponent(getThursdayCheckBoxLabel(), 1, 3, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getFridayCheckBox(), 2, 2, 1, 1, 10, 2, GridBagConstraints.NONE); // Sqr Friday
        addComponent(getFridayCheckBoxLabel(), 2, 3, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getSaturdayCheckBox(), 3, 2, 1, 1, 2, 2, GridBagConstraints.NONE); // Sqr Saturday
        addComponent(getSaturdayCheckBoxLabel(), 3, 3, 1, 1, 2, 2, GridBagConstraints.BOTH);
        // new row, third column
        addComponent(getSundayCheckBox(), 1, 4, 1, 1, 2, 2, GridBagConstraints.NONE); // Sqr Sunday
        addComponent(getSundayCheckBoxLabel(), 1, 5, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getWeekCheckBox(), 2, 4, 1, 1, -16, 2, GridBagConstraints.NONE); // Sqr Weekend
        addComponent(getWeekCheckBoxLabel(), 2, 5, 1, 1, 2, 2, GridBagConstraints.BOTH);
        addComponent(getWkendCheckBox(), 3, 4, 1, 1, -23, 2, GridBagConstraints.NONE); // Sqr Weekend
        addComponent(getWkendCheckBoxLabel(), 3, 5, 1, 1, 2, 2, GridBagConstraints.BOTH);
        // set button
        addComponent(getJSetAlarmButton(), 3, 6, 4, 1, 1, 1, GridBagConstraints.CENTER); // Set button
        // header and alarms
        addComponent(getJAlarmLbl4(), 5, 0, 4, 1, 2, 2, GridBagConstraints.BOTH);
    }
    @Override
    public void updateLabels()
    {
        getJAlarmLbl1().setText(getClock().defaultText(3)); // H
        getJAlarmLbl2().setText(getClock().defaultText(4)); // M
        getJAlarmLbl3().setText(getClock().defaultText(5)); // T
        getJAlarmLbl4().setText(getClock().defaultText(6)); // All Alarms ...
        getClock().repaint();
    }
    public void checkIfAnyAlarmsAreGoingOff()
    {
        // alarm has reference to time
        // check all alarms
        // if any alarm matches clock's time, an alarm should be going off
        getClock().getListOfAlarms().forEach(
            (alarm) ->
            {
                if (alarm.getTimeAsStr().equals(getClock().getTimeAsStr()))
                {
                    // time for alarm to be triggered on
                    setCurrentAlarmGoingOff(alarm);
                    alarm.setAlarmGoingOff(true);
                    System.out.print("Alarm " + getCurrentAlarmGoingOff().getTimeAsStr() + " matches clock's time. ");
                    System.out.println("Sounding alarm...");
                    //System.out.println("Clock's time is " + getClock().getTimeAsStr());
                }
            }
        );
        // update lbl1 and lbl2 to display alarm
        // user must "view that alarm" to turn it off
        Clock currentAlarm = getCurrentAlarmGoingOff();
        ExecutorService executor = Executors.newCachedThreadPool();
        if (null != currentAlarm && currentAlarm.isAlarmGoingOff())
        {
            getClock().getClockPanel().getJlbl1().setText(currentAlarm.getTimeAsStr());
            getClock().getClockPanel().getJlbl2().setText("is going off!");
            // play sound
            Runnable r = () -> {
                try
                {
                    getClock().getAlarmPanel().setupMusicPlayer();
                    getClock().getAlarmPanel().getMusicPlayer().play(50);
                    getClock().getAlarmPanel().setMusicPlayer(null);
                }
                catch (Exception e)
                {
                    System.err.println("An exception occurred while playing music: " + e.getMessage());
                }
            };
            executor.submit(r);
        }
        else
        { executor.shutdown(); }
    }
    public void setupCreatedAlarmsFunctionality()
    {
        // get the view alarms menu
        // for each except the Set Alarms (option1)
        // create an action listener which
        // takes the alarmClock, set the hour, min, and ampm
        // in the textFields and we will set a boolean to true
        // which will allow editing the textFields to any value
        // changing all values to 0 or explicitly Time to 0
        // will delete the alarm
        // changing the values and clicking Set will save the alarm
        //05:06:00 PM
        for(int i = 0; i < getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount(); i++)
        {
            if (!getClock().getClockMenuBar().getViewAlarmsMenu().getItem(i).getText().equals("Set Alarms"))
            {
                JMenuItem menuItem = getClock().getClockMenuBar().getViewAlarmsMenu().getItem(i);
                menuItem.addActionListener(action ->
                {
                    getClock().getListOfAlarms().forEach(
                            (alarm) ->
                            {
                                if (alarm.getTimeAsStr().equals(menuItem.getText()))
                                { setAlarm(alarm); }
                            }
                    );
                    if (null == getMusicPlayer()) { setupMusicPlayer(); }
                    if (null != getCurrentAlarmGoingOff())
                    {
                        setAlarm(getCurrentAlarmGoingOff());
                        if (null != getMusicPlayer())
                        {
                            setMusicPlayer(null);
                            System.out.println("Stopping music. New music player setup and ready for new alarm");
                            System.out.println(getAlarm().getTimeAsStr()+" alarm turned off.\n");
                        }
                        else { System.err.println("Music player is null!"); }

                        getAlarm().setAlarmGoingOff(false);
                        getAlarm().setUpdateAlarm(true); // this and the boolean below we want true
                        setUpdatingAlarm(true); // we want to continue with the logic that's done in the next if
                        setCurrentAlarmGoingOff(null);
                        System.err.println("Size of listOfAlarms before removing " + getClock().getListOfAlarms().size());
                        // remove alarm from list of alarms
                        getClock().getListOfAlarms().remove(getAlarm());
                        System.err.println("Size of listOfAlarms after removing " + getClock().getListOfAlarms().size());
                        deleteAlarmMenuItemFromViewAlarms(getAlarm());
                        resetJTextArea();
                        getClock().getAlarmPanel().getJTextField1().setText(getAlarm().getHoursAsStr());
                        getClock().getAlarmPanel().getJTextField2().setText(getAlarm().getMinutesAsStr());
                        getClock().getAlarmPanel().getJTextField3().setText(getAlarm().getAMPM().getStrValue());
                    }
                    if (null != getAlarm() && getAlarm().isUpdateAlarm())
                    {
                        System.out.println("Updating an alarm: " + menuItem.getText());
                        getJTextField1().setText(menuItem.getText().substring(0,2));
                        getJTextField2().setText(menuItem.getText().substring(3,5));
                        getJTextField3().setText(menuItem.getText().substring(9));
                        setUpdatingAlarm(true);
                        // remove alarm from list of alarms
                        deleteAlarmMenuItemFromViewAlarms(getAlarm());
                        System.err.println("Size of listOfAlarms before removing " + getClock().getListOfAlarms().size());
                        getClock().getListOfAlarms().remove(getAlarm());
                        System.err.println("Size of listOfAlarms after removing " + getClock().getListOfAlarms().size());
                        resetJTextArea();
                    }
                    else
                    {
                        System.out.println("menuItem: " + menuItem.getText());
                        System.out.println("alarm: " + getAlarm().getTimeAsStr()); // should be the same
                        getAlarm().setUpdateAlarm(true);
                        System.err.println("Size of listOfAlarms before removal " + getClock().getListOfAlarms().size());
                        deleteAlarmMenuItemFromViewAlarms(getAlarm());
                        getClock().getListOfAlarms().remove(getAlarm());
                        System.err.println("Size of listOfAlarms after removal " + getClock().getListOfAlarms().size());
                        resetJTextArea();
                    }
                    getClock().changeToAlarmPanel();
                });
            }
        }
    }
    public void setupAlarmButton()
    {
        getJSetAlarmButton().addActionListener(action ->
        {
            // check if h, m, and time are set. exit if not
            Alarm alarm = null;
            try
            {
                validateFirstTextField();
                validateSecondTextField();
                validateThirdTextField();
                // Passed validation
                if (isUpdatingAlarm())
                {
                    // update list of alarms
                    Clock clock = new Clock(Integer.parseInt(getJTextField1().getText()), Integer.parseInt(getJTextField2().getText()), 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, convertStringToTimeAMPM(getJTextField3().getText()));
                    alarm = new Alarm(clock, clock.getHours(), clock.getMinutes(), clock.getAMPM(), isUpdatingAlarm(), checkWhichCheckBoxesWereChecked());
                    alarm.setAlarmGoingOff(false);
                    alarm.setUpdateAlarm(true);
                    setAlarm(alarm);
                    // add clock to list of alarms
                    if (getClock().getListOfAlarms().size() == 0)
                    {
                        addAlarmMenuItemFromAlarm(alarm);
                        getClock().getListOfAlarms().add(alarm);
                    }
                    else
                    {
                        boolean addToList = false;
                        for(int i = 0; i < getClock().getListOfAlarms().size(); i++)
                        {
                            if (!getClock().getListOfAlarms().get(i).getTimeAsStr().equals(alarm.getTimeAsStr()))
                            { addToList = true; }
                            else
                            {
                                addToList = false;
                                System.err.println("Tried adding an alarm but it already exists! Cannot create duplicate alarm.");
                            }
                        }
                        if (addToList)
                        {
                            addAlarmMenuItemFromAlarm(alarm);
                            getClock().getListOfAlarms().add(alarm);
                        }
                    }
                    System.err.println("Size of listOfAlarms before adding " + (getClock().getListOfAlarms().size()-1));
                    System.err.println("Size of listOfAlarms after adding " + getClock().getListOfAlarms().size());
                    // restart viewAlarms menu
                    //resetViewAlarmsMenu(getClock().getListOfAlarms());
                    setupCreatedAlarmsFunctionality();
                    setUpdatingAlarm(false);
                    resetJTextArea();
                    // determine how to update alarm (update/delete)
                }
                else // creating a new alarm
                {
                    alarm = createAlarm();
                    setAlarm(alarm);
                    setUpdatingAlarm(false);
                    if (getClock().getListOfAlarms().size() == 0)
                    {
                        addAlarmMenuItemFromAlarm(alarm);
                        getClock().getListOfAlarms().add(alarm);
                    }
                    else
                    {
                        boolean addToList = false;
                        for(int i = 0; i < getClock().getListOfAlarms().size(); i++)
                        {
                            if (!getClock().getListOfAlarms().get(i).getTimeAsStr().equals(alarm.getTimeAsStr()))
                            { addToList = true; }
                            else
                            {
                                addToList = false;
                                System.err.println("Tried adding an alarm but it already exists! Cannot create duplicate alarm.");
                            }
                        }
                        if (addToList)
                        {
                            addAlarmMenuItemFromAlarm(alarm);
                            getClock().getListOfAlarms().add(alarm);
                        }
                    }
                    System.err.println("Size of listOfAlarms before adding " + (getClock().getListOfAlarms().size()-1));
                    System.err.println("Size of listOfAlarms after adding " + getClock().getListOfAlarms().size());
                    // display list of alarms below All Alarms
                    resetJTextArea();
                    setupCreatedAlarmsFunctionality();
                    // erase input in textFields
                    getJTextField1().setText("");
                    getJTextField2().setText("");
                    getJTextField3().setText("");
                }
                getClock().changeToClockPanel();
                getAlarm().printAlarmStatus("Finished setting alarm.");
            }
            catch (InvalidInputException | ParseException e)
            {
                for(StackTraceElement ste : e.getStackTrace())
                { System.err.println(ste); }
                getAlarm().printAlarmStatus("No alarm was set.");
            }

        });
    }
    protected void validateFirstTextField() throws InvalidInputException
    {
        if (StringUtils.isBlank(getJTextField1().getText()))
        {
            throw new InvalidInputException("Hour cannot be blank");
        }
        else if (Integer.parseInt(getJTextField1().getText()) >= 0 &&
                (Integer.parseInt(getJTextField1().getText())) < 24 )
        {
            throw new InvalidInputException("Hour value must be between 0 and 23");
        }
    }
    protected void validateSecondTextField() throws InvalidInputException
    {
        if (StringUtils.isBlank(getJTextField2().getText()))
        {
            throw new InvalidInputException("Minutes cannot be blank");
        }
        else if (Integer.parseInt(getJTextField1().getText()) >= 0 &&
                (Integer.parseInt(getJTextField1().getText())) < 59 )
        {
            throw new InvalidInputException("Hour value must be between 0 and 59");
        }
    }
    protected void validateThirdTextField() throws InvalidInputException
    {
        if (StringUtils.isEmpty(getJTextField3().getText()))
        {
            throw new InvalidInputException("Time cannot be blank");
        }
        else if (!getJTextField3().getText().equals(AM) ||
                 !getJTextField3().getText().equals(PM))
        {
            throw new InvalidInputException("Time must be AM or PM");
        }
        {
            throw new InvalidInputException("Hour value must be between 0 and 59");
        }
    }
    protected void addAlarmMenuItemFromAlarm(Clock alarm)
    {
        JMenuItem alarmItem = new JMenuItem(alarm.getTimeAsStr());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        System.err.println("Size of viewAlarms before adding " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
        getClock().getClockMenuBar().getViewAlarmsMenu().add(alarmItem);
        System.err.println("Size of viewAlarms after adding " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
    }
    protected void deleteAlarmMenuItemFromViewAlarms(Clock alarm)
    {
        System.err.println("Size of viewAlarms before removal " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
        for(int i = 0; i < getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount(); i++)
        {
            if (getClock().getClockMenuBar().getViewAlarmsMenu().getItem(i).getText().equals(alarm.getTimeAsStr()))
            {
                getClock().getClockMenuBar().getViewAlarmsMenu().remove(getClock().getClockMenuBar().getViewAlarmsMenu().getItem(i));
            }
        }
        System.err.println("Size of viewAlarms after removal " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
    }
    protected void resetJTextArea()
    {
        getJTextArea().setText("");
        for(Alarm alarm : getClock().getListOfAlarms())
        {
            if (!StringUtils.isEmpty(getJTextArea().getText()))
            {
                getJTextArea().append("\n");
            }
            getJTextArea().append(alarm.getTimeAsStr());
        }
    }
    /**
     * Creates an alarm and sets the latest one created as the currentAlarm
     * defined in setAlarm
     * @return Alarm
     * @throws ParseException will be thrown if hour, minutes, or time is inappropriate.
     */
    public Alarm createAlarm() throws ParseException
    {
        int hour = Integer.parseInt(getJTextField1().getText());
        int minutes = Integer.parseInt(getJTextField2().getText());
        Time.AMPM ampm = convertStringToTimeAMPM(getJTextField3().getText());
        ArrayList<Day> days = checkWhichCheckBoxesWereChecked();
        Alarm alarm = new Alarm(getClock(), hour, minutes, ampm, true, days);
        alarm.setAlarmGoingOff(false);
        System.err.println("\ncreated an alarm: " + alarm.getTimeAsStr());
        return alarm;
    }
    protected ArrayList<Day> checkWhichCheckBoxesWereChecked()
    {
        ArrayList<Time.Day> daysSelected = new ArrayList<>();
        if (getMondayCheckBox().isSelected())
        { daysSelected.add(MONDAY); }
        if (getTuesdayCheckBox().isSelected())
        { daysSelected.add(TUESDAY); }
        if (getWednesdayCheckBox().isSelected())
        { daysSelected.add(WEDNESDAY); }
        if (getThursdayCheckBox().isSelected())
        { daysSelected.add(THURSDAY); }
        if (getFridayCheckBox().isSelected())
        { daysSelected.add(FRIDAY); }
        if (getSaturdayCheckBox().isSelected())
        { daysSelected.add(SATURDAY); }
        if (getSundayCheckBox().isSelected())
        { daysSelected.add(SUNDAY); }
        if (getWeekCheckBox().isSelected())
        { daysSelected.add(WEEK); }
        if (getWkendCheckBox().isSelected())
        { daysSelected.add(WEEKEND); }
        return daysSelected;
    }
    public void createAlarm(Alarm alarm)
    {
        setAlarm(alarm);
        getAlarm().setAlarmGoingOff(false);
        // add clock to list of alarms
        getClock().getListOfAlarms().add(getAlarm());
        resetJTextArea();
        setupMusicPlayer();
        // display list of alarms below All Alarms
        this.repaint();
        // erase input in textFields
        getJTextField1().setText("");
        getJTextField2().setText("");
        getJTextField3().setText("");
        JMenuItem alarmItem = new JMenuItem(alarm.getTimeAsStr());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        getClock().getClockMenuBar().getViewAlarmsMenu().add(alarmItem);
        setupCreatedAlarmsFunctionality();
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill)
    {
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }
}
