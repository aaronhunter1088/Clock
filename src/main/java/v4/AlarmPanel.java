package v4;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;
import org.springframework.lang.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static v4.Time.AMPM.AM;
import static v4.Time.AMPM.PM;
import static v4.Time.Day;
import static v4.Time.Day.*;

@SuppressWarnings("unused")
/** The AlarmPanel is used to set and view alarms. The
 * alarms can be viewed in the textarea and in the
 * menu (if you ctrl + a into the panel).
 * Clicking on an alarm in the View Alarms menu will
 * remove it from the menu and from the textarea inside
 * the AlarmPanel. However it will be visible on the panel
 * itself. Update as needed, or click set to save it.
 * Returning to the Clock panel without clicking Set will
 * delete it permanently.
 *
 * To set an alarm, you must enter an Hour, some minutes,
 * and the time, AM or PM. The alarm accepts military time
 * format. Just make sure the value makes sense and an
 * alarm will be created.
 *
 * @author michael ball
 * @version 2.4
 */
public class AlarmPanel extends JPanel implements IClockFace
{
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jalarmLbl1;
    private JLabel jalarmLbl2;
    private JLabel jalarmLbl3;
    private JLabel jalarmLbl4;
    private JCheckBox mondayCheckBox;
    private JCheckBox tuesdayCheckBox;
    private JCheckBox wednesdayCheckBox;
    private JCheckBox thursdayCheckBox;
    private JCheckBox fridayCheckBox;
    private JCheckBox saturdayCheckBox;
    private JCheckBox sundayCheckBox;
    private JCheckBox weekCheckBox;
    private JCheckBox wkendCheckBox;
    private JTextField jtextField1;
    private JTextField jtextField2;
    private JTextField jtextField3;
    private JButton jSetAlarmButton;
    private JTextArea jTextArea;
    private JScrollPane scrollPane;
    private Clock clock;
    private Alarm alarm;
    private Alarm currentAlarmGoingOff;
    private boolean updatingAlarm;
    private boolean alarmIsGoingOff;
    private AdvancedPlayer musicPlayer;
    // Constructor
    public AlarmPanel(Clock clock)
    {
        super();
        setClock(clock);
        setMinimumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupAlarmPanel(getClock());
        setupAlarmButton();
        setupMusicPlayer();
        updateLabels();
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
    public JScrollPane getJScrollPane() { return this.scrollPane; }
    public JTextArea getJTextArea() { return this.jTextArea; }
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
    public JCheckBox getWkendCheckBox() { return wkendCheckBox; }
    public boolean isAlarmIsGoingOff() { return alarmIsGoingOff; }

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
    protected void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    protected void setJTextArea(final JTextArea jTextArea) { this.jTextArea = jTextArea; }
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
    protected void setAlarmIsGoingOff(boolean alarmIsGoingOff) { this.alarmIsGoingOff = alarmIsGoingOff; }
    // Helper methods
    public void setupMusicPlayer()
    {
        InputStream inputStream = null;
        try
        {
            inputStream = ClassLoader.getSystemResourceAsStream("sounds/alarmSound1.mp3");
            if (null != inputStream) { setMusicPlayer(new AdvancedPlayer(inputStream)); }
        }
        catch (NullPointerException | JavaLayerException e)
        {
            if (null == inputStream)
                printStackTrace(e, "An issue occurred while reading the alarm file.");
            printStackTrace(e, "A JavaLayerException occurred: " + e.getMessage());
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
        //clock.setCalendar(Calendar.getInstance());
        //clock.setCalendarTime(new Date());
        clock.setDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        setJAlarmLbl1(new JLabel("H", SwingConstants.CENTER)); // H
        setJAlarmLbl2(new JLabel("M", SwingConstants.CENTER)); // M
        setJAlarmLbl3(new JLabel("T", SwingConstants.CENTER)); // Time (AM/PM)
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
        getJTextField1().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getJTextField1().setText("");
        getJTextField2().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getJTextField1().setText("");
        getJTextField3().setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getJAlarmLbl1().setFont(Clock.font20); // H
        getJAlarmLbl2().setFont(Clock.font20); // M
        getJAlarmLbl3().setFont(Clock.font20); // T
        getJAlarmLbl4().setFont(Clock.font20); // All Alarms
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        // setup textarea
        setJTextArea(new JTextArea(2, 4));
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
        getJScrollPane().setMaximumSize(new Dimension(20,50));
        // setup Set button
        setJSetAlarmButton(new JButton("Set"));
        getJSetAlarmButton().setFont(Clock.font20);
        getJSetAlarmButton().setOpaque(true);
        getJSetAlarmButton().setBackground(Color.BLACK);
        getJSetAlarmButton().setForeground(Color.BLACK);
        // setup checkboxes
        setupCheckboxBasedOnDay(null);
    }
    @Override
    public void addComponentsToPanel()
    {
        //addComponent(getJAlarmLbl1(), 0,0,1,1, 15,7, GridBagConstraints.VERTICAL); // H
        addComponent(getJAlarmLbl1(),0,0,1,1,0,0,   GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // H
        addComponent(getJTextField1(),0,1,1,1, 0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // textField
        addComponent(getJAlarmLbl2(),0,2,1,1, 0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // M
        addComponent(getJTextField2(),0,3,1,1, 0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // textField
        addComponent(getJAlarmLbl3(),0,4,1,1, 0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // Time (AM/PM)
        addComponent(getJTextField3(),0,5,1,1, 0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0)); // textField
        addComponent(getJScrollPane(),0,6,4,3, 0,0, GridBagConstraints.BOTH, GridBagConstraints.NONE, new Insets(1,1,1,1)); // textArea
        // new column, first column
        addComponent(getMondayCheckBox(), 1,0,2,1, 0,0, GridBagConstraints.NONE); // Sqr Monday
        addComponent(getTuesdayCheckBox(), 2, 0, 2, 1, 1, 0, GridBagConstraints.NONE); // Sqr Tuesday
        addComponent(getWednesdayCheckBox(), 3, 0, 2, 1, 2, 0, GridBagConstraints.NONE); // Sqr Wednesday
        // new column, second column
        addComponent(getThursdayCheckBox(), 1, 2, 2, 1, 0, 0, GridBagConstraints.NONE); // Sqr Thursday
        addComponent(getFridayCheckBox(), 2, 2, 2, 1, 1, 0, GridBagConstraints.NONE); // Sqr Friday
        addComponent(getSaturdayCheckBox(), 3, 2, 2, 1, 0, 0, GridBagConstraints.NONE); // Sqr Saturday
        // new column, third column
        addComponent(getSundayCheckBox(), 1, 4, 2, 1, 0, 0, GridBagConstraints.NONE); // Sqr Sunday
        addComponent(getWeekCheckBox(), 2, 4, 2, 1, 1, 0, GridBagConstraints.NONE); // Sqr Weekend
        addComponent(getWkendCheckBox(), 3, 4, 2, 1, 1, 0, GridBagConstraints.NONE); // Sqr Weekend
        // set button
        addComponent(getJSetAlarmButton(), 3, 6, 2, 1, 1, 1, GridBagConstraints.CENTER); // Set button
        // header and alarms
        addComponent(getJAlarmLbl4(), 5, 0, 4, 1, 2, 2, GridBagConstraints.BOTH);
    }

    @Override
    public void updateLabels()
    {
        getJAlarmLbl1().setText(getClock().defaultText(3)); // H
        getJAlarmLbl2().setText(getClock().defaultText(4)); // M
        getJAlarmLbl3().setText(getClock().defaultText(5)); // T
        getJAlarmLbl4().setText(getClock().defaultText(6)); // All Alarms label...
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
                for(Day day : alarm.getDays())
                {
                    if (alarm.getTimeAsStr().equals(getClock().getTimeAsStr()) &&
                        day == getClock().getDay())
                    {
                        // time for alarm to be triggered on
                        setCurrentAlarmGoingOff(alarm);
                        alarm.setAlarmGoingOff(true);
                        setAlarmIsGoingOff(true);
                        System.out.print("Alarm " + getCurrentAlarmGoingOff().getTimeAsStr() + " matches clock's time. ");
                        System.out.println("Sounding alarm...");
                        //System.out.println("Clock's time is " + getClock().getTimeAsStr());
                    }
                }
            }
        );
        // update lbl1 and lbl2 to display alarm
        // user must "view that alarm" to turn it off
        Alarm currentAlarm = getCurrentAlarmGoingOff();
        ExecutorService executor = Executors.newCachedThreadPool();
        if (null != currentAlarm && currentAlarm.isAlarmGoingOff())
        {
            triggerAlarm(executor);
        }
    }
    public void setupAlarmsInMenuFunctionality()
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
        for(int i = 0; i < getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (!getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals("Set Alarms"))
            {
                JMenuItem menuItem = getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i);
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
                    // if an alarm is going off and we clicked on it in the menuBar
                    if (null != getCurrentAlarmGoingOff())
                    {
                        setAlarm(getCurrentAlarmGoingOff());
                        if (null != getMusicPlayer())
                        {
                            stopAlarm();
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
                        getJAlarmLbl4().setText("Turning alarm off");
                        resetJCheckboxes(getAlarm().getDays(), true);
                    }
                    // we are updating an alarm by clicking on it in the menuBar
                    else if (null != getAlarm() && getAlarm().isUpdateAlarm())
                    {
                        updateTheAlarm(menuItem);
                    }
                    else // when is this reachable
                    {
                        System.out.println("menuItem: " + menuItem.getText());
                        System.out.println("alarm: " + getAlarm().getTimeAsStr()); // should be the same
                        getAlarm().setUpdateAlarm(true);
                        deleteAlarmMenuItemFromViewAlarms(getAlarm());
                        System.err.println("Size of listOfAlarms before removal " + getClock().getListOfAlarms().size());
                        getClock().getListOfAlarms().remove(getAlarm());
                        System.err.println("Size of listOfAlarms after removal " + getClock().getListOfAlarms().size());
                        resetJTextArea();
                        try
                        { setCheckBoxesBasedOnDays(getAlarm().getDays()); }
                        catch (InvalidInputException iie)
                        { printStackTrace(iie); }
                    }
                    getClock().changeToAlarmPanel();
                });
            }
        }
    }
    protected void updateTheAlarm(@Nullable JMenuItem menuItem)
    {
        if (null != menuItem)
        {
            System.out.println("Updating an alarm: " + menuItem.getText());
            getJTextField1().setText(menuItem.getText().substring(0,2));
            getJTextField2().setText(menuItem.getText().substring(3,5));
            getJTextField3().setText(menuItem.getText().substring(9));
        }
        setUpdatingAlarm(true);
        // remove alarm from list of alarms
        deleteAlarmMenuItemFromViewAlarms(getAlarm());
        System.err.println("Size of listOfAlarms before removing " + getClock().getListOfAlarms().size());
        getClock().getListOfAlarms().remove(getAlarm());
        System.err.println("Size of listOfAlarms after removing " + getClock().getListOfAlarms().size());
        resetJTextArea();
        resetJCheckboxes(getAlarm().getDays(), true);
        getJAlarmLbl4().setText("Updating alarm");
    }
    protected void setupAlarmButton()
    {
        getJSetAlarmButton().addActionListener(action ->
        {
            //JCheckBox cbLog = (JCheckBox)(AbstractButton)action.getSource();
            //System.err.println("cbLog isSelected: " + cbLog.isSelected());
            //System.err.println("cbLog isEnabled: " + cbLog.isEnabled());
            // check if h, m, and time are set. exit if not
            Alarm alarm = null;
            boolean validated = false;
            try
            {
                validated = validateFirstTextField(); // Hours
                validated = validateSecondTextField(); // Minutes
                validated = validateThirdTextField(); // Time
                validated = validateOnTheCheckBoxes();

                if (!validated)
                {
                    setAlarm(new Alarm());
                    getClock().changeToAlarmPanel();
                    getAlarm().printAlarmStatus("No alarm set.");
                }
                else // validated is true
                {
                    // Passed validation
                    if (isUpdatingAlarm())
                    {
                        alarm = createAlarm();
                        alarm.setUpdateAlarm(false); // at this point, we are doing updating
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
                        setupAlarmsInMenuFunctionality();
                        setUpdatingAlarm(false);
                        resetJTextArea();
                        resetJCheckboxes(getAlarm().getDays(), false);
                        resetJAlarmLabel4();
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
                        resetJCheckboxes(null, false);
                        setupAlarmsInMenuFunctionality();
                        // erase input in textFields
                        getJTextField1().setText("");
                        getJTextField2().setText("");
                        getJTextField3().setText("");
                        resetJCheckboxes(null, false);
                        resetJAlarmLabel4();
                    }
                    getClock().changeToClockPanel();
                    getAlarm().printAlarmStatus("Finished setting alarm.");
                }
            }
            catch (InvalidInputException | ParseException e)
            {
                getJTextArea().setLineWrap(true);
                getJTextArea().setWrapStyleWord(true);
                getJTextArea().setText(e.getMessage());
                try
                {
                    setAlarm(new Alarm());
                    getClock().changeToAlarmPanel();
                    getAlarm().printAlarmStatus("No alarm set: " + e.getMessage());
                }
                catch (ParseException pe)
                {
                    System.err.println("Couldn't create a new alarm");
                    printStackTrace(pe);
                }
                //if (validated) { validated = false; }
            }
        });
    }
    protected void resetJAlarmLabel4()
    {
        if (getClock().getListOfAlarms().size() == 0)
        {
            getJAlarmLbl4().setText(getClock().defaultText(6)); // All Alarms label...
        }
        else
        {
            getJAlarmLbl4().setText
                    (
                            getClock().getListOfAlarms().size() == 1 ?
                                    getClock().getListOfAlarms().size() + " Alarm Added" :
                                    getClock().getListOfAlarms().size() + " Alarms Added"
                    );
        }
    }
    @Override
    public void printStackTrace(Exception e, String message)
    {
        if (null != message)
            System.err.println(message);
        else
            System.err.println(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            System.out.println(ste.toString());
        }
    }
    protected void printStackTrace(Exception e)
    { printStackTrace(e, ""); }
    protected boolean validateFirstTextField() throws InvalidInputException
    {
        if (StringUtils.isBlank(getJTextField1().getText()))
        {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Hour cannot be blank");
        }
        else if (Integer.parseInt(getJTextField1().getText()) >= 0 &&
                 Integer.parseInt(getJTextField1().getText()) > 23 )
        {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Hour must be between 0 and 23");
        }
        return true;
    }
    protected boolean validateSecondTextField() throws InvalidInputException
    {
        if (StringUtils.isBlank(getJTextField2().getText()))
        {
            getJTextField2().grabFocus();
            throw new InvalidInputException("Minutes cannot be blank");
        }
        else if (Integer.parseInt(getJTextField2().getText()) >= 0 &&
                 Integer.parseInt(getJTextField2().getText()) > 59 )
        {
            getJTextField2().grabFocus();
            throw new InvalidInputException("Minutes must be between 0 and 59");
        }
        return true;
    }
    protected boolean validateThirdTextField() throws InvalidInputException
    {
        if (StringUtils.isEmpty(getJTextField3().getText()))
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Time cannot be blank");
        }
        else if ((!StringUtils.equalsIgnoreCase(getJTextField3().getText(), AM.strValue) ||
                  !StringUtils.equalsIgnoreCase(getJTextField3().getText(), PM.strValue)) &&
                 getJTextField3().getText().length() == 1)
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Time must be equal to either AM or PM: " + getJTextField3().getText());
        }
        else if (Integer.parseInt(getJTextField1().getText()) >= 12 &&
                 Integer.parseInt(getJTextField1().getText()) < 24 &&
                StringUtils.equalsIgnoreCase(getJTextField3().getText(), AM.strValue))
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Hours can't be " + getJTextField1().getText() + " when Time is " + getJTextField3().getText());
        }
        else if (Integer.parseInt(getJTextField1().getText()) == 0 &&
                 StringUtils.equalsIgnoreCase(getJTextField3().getText(), PM.strValue))
        {
            // basically setting militaryTime 00 hours, and saying PM
            // this makes no sense
            getJTextField3().grabFocus();
            throw new InvalidInputException("Hours can't be " + getJTextField1().getText() + " when Time is " + getJTextField3().getText());
        }
        return true;
    }
    protected boolean validateOnTheCheckBoxes() throws InvalidInputException
    {
        ArrayList<Day> days = checkWhichCheckBoxesWereChecked();
        if (days.size() == 0)
        { throw new InvalidInputException("At least one checkbox must be selected."); }
        return true;
    }
    protected void setupCheckboxBasedOnDay(ArrayList<Day> days)
    {
        try
        {
            if (days == null) // default values
            {
                days = new ArrayList<>();
                for(Day day : Day.values() )
                    days.add(day); // defaulting days
            }
            for(Day day : days )
            {
                createAndSetupCheckBoxes(day);
            }
        }
        catch (InvalidInputException iie)
        { printStackTrace(iie); }
    }
    private void createAndSetupCheckBoxes(Day day) throws InvalidInputException
    {
        switch(day)
        {
            case SUNDAY:
            {
                setSundayCheckBox(new JCheckBox(SUNDAY.strValue.substring(0,2), false));
                getSundayCheckBox().setFont(Clock.font20);
                getSundayCheckBox().setBackground(Color.BLACK);
                getSundayCheckBox().setForeground(Color.WHITE);
                getSundayCheckBox().addActionListener(action -> {
                    if (!getSundayCheckBox().isSelected())
                    {
                        System.err.println("Sunday checkbox not selected!");
                        getSundayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Sunday checkbox selected!");
                        getSundayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case MONDAY:
            {
                setMondayCheckBox(new JCheckBox(MONDAY.strValue.substring(0,1), false));
                getMondayCheckBox().setFont(Clock.font20);
                getMondayCheckBox().setBackground(Color.BLACK);
                getMondayCheckBox().setForeground(Color.WHITE);
                getMondayCheckBox().addActionListener(action -> {
                    if (!getMondayCheckBox().isSelected())
                    {
                        System.err.println("Monday checkbox not selected!");
                        getMondayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Monday checkbox selected!");
                        getMondayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case TUESDAY:
            {
                setTuesdayCheckBox(new JCheckBox(TUESDAY.strValue.substring(0,1), false));
                getTuesdayCheckBox().setFont(Clock.font20);
                getTuesdayCheckBox().setBackground(Color.BLACK);
                getTuesdayCheckBox().setForeground(Color.WHITE);
                getTuesdayCheckBox().addActionListener(action -> {
                    if (!getTuesdayCheckBox().isSelected())
                    {
                        System.err.println("Tuesday checkbox not selected!");
                        getTuesdayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Tuesday checkbox selected!");
                        getTuesdayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case WEDNESDAY:
            {
                setWednesdayCheckBox(new JCheckBox(WEDNESDAY.strValue.substring(0,1), false));
                getWednesdayCheckBox().setFont(Clock.font20);
                getWednesdayCheckBox().setBackground(Color.BLACK);
                getWednesdayCheckBox().setForeground(Color.WHITE);
                getWednesdayCheckBox().addActionListener(action -> {
                    if (!getWednesdayCheckBox().isSelected())
                    {
                        System.err.println("Wednesday checkbox not selected!");
                        getWednesdayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Wednesday checkbox selected!");
                        getWednesdayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case THURSDAY:
            {
                setThursdayCheckBox(new JCheckBox(THURSDAY.strValue.substring(0,1), false));
                getThursdayCheckBox().setFont(Clock.font20);
                getThursdayCheckBox().setBackground(Color.BLACK);
                getThursdayCheckBox().setForeground(Color.WHITE);
                getThursdayCheckBox().addActionListener(action -> {
                    if (!getThursdayCheckBox().isSelected())
                    {
                        System.err.println("Thursday checkbox not selected!");
                        getThursdayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Thursday checkbox selected!");
                        getThursdayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case FRIDAY:
            {
                setFridayCheckBox(new JCheckBox(FRIDAY.strValue.substring(0,1), false));
                getFridayCheckBox().setFont(Clock.font20);
                getFridayCheckBox().setBackground(Color.BLACK);
                getFridayCheckBox().setForeground(Color.WHITE);
                getFridayCheckBox().addActionListener(action -> {
                    if (!getFridayCheckBox().isSelected())
                    {
                        System.err.println("Friday checkbox not selected!");
                        getFridayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Friday checkbox selected!");
                        getFridayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case SATURDAY:
            {
                setSaturdayCheckBox(new JCheckBox(SATURDAY.strValue.substring(0,1), false));
                getSaturdayCheckBox().setFont(Clock.font20);
                getSaturdayCheckBox().setBackground(Color.BLACK);
                getSaturdayCheckBox().setForeground(Color.WHITE);
                getSaturdayCheckBox().addActionListener(action -> {
                    if (!getSaturdayCheckBox().isSelected())
                    {
                        System.err.println("Saturday checkbox not selected!");
                        getSaturdayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Saturday checkbox selected!");
                        getSaturdayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case WEEK:
            {
                setWeekCheckBox(new JCheckBox(WEEK.strValue.substring(0,1) + WEEK.strValue.substring(3), false));
                getWeekCheckBox().setFont(Clock.font20);
                getWeekCheckBox().setBackground(Color.BLACK);
                getWeekCheckBox().setForeground(Color.WHITE);
                getWeekCheckBox().addActionListener(action -> {
                    if (!getWeekCheckBox().isSelected())
                    {
                        System.err.println("Week checkbox not selected!");
                        getWeekCheckBox().setSelected(false);
                        getMondayCheckBox().setSelected(false);
                        getTuesdayCheckBox().setSelected(false);
                        getWednesdayCheckBox().setSelected(false);
                        getThursdayCheckBox().setSelected(false);
                        getFridayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Week checkbox selected!");
                        getWeekCheckBox().setSelected(true);
                        getMondayCheckBox().setSelected(true);
                        getTuesdayCheckBox().setSelected(true);
                        getWednesdayCheckBox().setSelected(true);
                        getThursdayCheckBox().setSelected(true);
                        getFridayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case WEEKEND:
            {
                setWkendCheckBox(new JCheckBox(WEEKEND.strValue.substring(0,1) + WEEKEND.strValue.substring(6), false));
                getWkendCheckBox().setFont(Clock.font20);
                getWkendCheckBox().setBackground(Color.BLACK);
                getWkendCheckBox().setForeground(Color.WHITE);
                getWkendCheckBox().addActionListener(action -> {
                    if (!getWkendCheckBox().isSelected())
                    {
                        System.err.println("Weekend checkbox not selected!");
                        getWkendCheckBox().setSelected(false);
                        getSaturdayCheckBox().setSelected(false);
                        getSundayCheckBox().setSelected(false);
                    }
                    else
                    {
                        System.err.println("Weekend checkbox selected!");
                        getWkendCheckBox().setSelected(true);
                        getSaturdayCheckBox().setSelected(true);
                        getSundayCheckBox().setSelected(true);
                    }
                });
                break;
            }
            case ERROR:
            { break; }
            default: throw new InvalidInputException(Day.ERROR.strValue + ": " + day);
        }
    }
    protected void addAlarmMenuItemFromAlarm(Clock alarm)
    {
        JMenuItem alarmItem = new JMenuItem(alarm.getTimeAsStr());
        alarmItem.setForeground(Color.WHITE);
        alarmItem.setBackground(Color.BLACK);
        System.err.println("Size of viewAlarms before adding " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        getClock().getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        System.err.println("Size of viewAlarms after adding " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
    }
    protected void deleteAlarmMenuItemFromViewAlarms(Clock alarm)
    {
        System.err.println("Size of viewAlarms before removal " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
        for(int i = 0; i < getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount(); i++)
        {
            if (getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i).getText().equals(alarm.getTimeAsStr()))
            {
                getClock().getClockMenuBar().getAlarmFeature_Menu().remove(getClock().getClockMenuBar().getAlarmFeature_Menu().getItem(i));
            }
        }
        System.err.println("Size of viewAlarms after removal " + getClock().getClockMenuBar().getAlarmFeature_Menu().getItemCount());
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
    protected void resetJCheckboxes(ArrayList<Day> days, boolean set)
    {
        if (null == days)
        {
            for(Day day : Day.values())
            {
                resetJCheckbox(day, false);
            }
        }
        else
        {
            for(Day day : days)
            {
                resetJCheckbox(day, set);
            }
        }
    }
    protected void resetJCheckbox(Day day, boolean set)
    {
        switch(day)
        {
            case SUNDAY:
            {
                getSundayCheckBox().setSelected(set);
                break;
            }
            case MONDAY:
            {
                getMondayCheckBox().setSelected(set);
                break;
            }
            case TUESDAY:
            {
                getTuesdayCheckBox().setSelected(set);
                break;
            }
            case WEDNESDAY:
            {
                getWednesdayCheckBox().setSelected(set);
                break;
            }
            case THURSDAY:
            {
                getThursdayCheckBox().setSelected(set);
                break;
            }
            case FRIDAY:
            {
                getFridayCheckBox().setSelected(set);
                break;
            }
            case SATURDAY:
            {
                getSaturdayCheckBox().setSelected(set);
                break;
            }
            case WEEK:
            {
                getWeekCheckBox().setSelected(set);
                break;
            }
            case WEEKEND:
            {
                getWkendCheckBox().setSelected(set);
                break;
            }
            case ERROR: break;
            default:
            {
                System.err.println(Day.ERROR.strValue + " : " + day);
            }
        }
    }
    /**
     * Creates an alarm and sets the latest one created as the currentAlarm
     * defined in setAlarm
     * @return Alarm
     * @throws ParseException will be thrown if hour, minutes, or time is inappropriate.
     */
    public Alarm createAlarm() throws ParseException, InvalidInputException {
        int hour = Integer.parseInt(getJTextField1().getText());
        int minutes = Integer.parseInt(getJTextField2().getText());
        Time.AMPM ampm = convertStringToTimeAMPM(getJTextField3().getText());
        boolean valid = false;
        try
        {
            valid = validateFirstTextField();
            valid = validateSecondTextField();
            valid = validateThirdTextField();
        }
        catch (InvalidInputException iie)
        {
            throw iie;
        }
        if (valid)
        {
            ArrayList<Day> days = checkWhichCheckBoxesWereChecked();
            Alarm alarm = new Alarm(getClock(), hour, minutes, ampm, true, days);
            alarm.setAlarmGoingOff(false);
            System.err.println("\ncreated an alarm: " + alarm.getTimeAsStr());
            return alarm;
        }
        else
        {
            return null;
        }
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
        {
            if (!daysSelected.contains(MONDAY)) daysSelected.add(MONDAY);
            if (!daysSelected.contains(TUESDAY)) daysSelected.add(TUESDAY);
            if (!daysSelected.contains(WEDNESDAY)) daysSelected.add(WEDNESDAY);
            if (!daysSelected.contains(THURSDAY)) daysSelected.add(THURSDAY);
            if (!daysSelected.contains(FRIDAY)) daysSelected.add(FRIDAY);
        }
        if (getWkendCheckBox().isSelected())
        {
            if (!daysSelected.contains(SATURDAY)) daysSelected.add(SATURDAY);
            if (!daysSelected.contains(SUNDAY)) daysSelected.add(SUNDAY);
        }
        return daysSelected;
    }
    protected void setCheckBoxesBasedOnDays(ArrayList<Day> days) throws InvalidInputException
    {
        // reset to default
        if (null == days)
            setupCheckboxBasedOnDay(null);
        else
            setupCheckboxBasedOnDay(days);
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
        getClock().getClockMenuBar().getAlarmFeature_Menu().add(alarmItem);
        setupAlarmsInMenuFunctionality();
    }
    public void triggerAlarm(ExecutorService executor)
    {
        setAlarmIsGoingOff(true);
        getClock().getClockPanel().getJlbl1().setText(getCurrentAlarmGoingOff().getTimeAsStr());
        getClock().getClockPanel().getJlbl2().setText("is going off!");
        // play sound
        Callable<String> c = () -> {
            try
            {
                getClock().getAlarmPanel().setupMusicPlayer();
                getClock().getAlarmPanel().getMusicPlayer().play(50);
                System.err.println("Alarm is going off.");
                return "Alarm triggered";
            }
            catch (Exception e)
            {
                printStackTrace(e);
                return "An exception occurred while playing music: " + e.getMessage();
            }
        };
        executor.submit(c);
    }
    public void stopAlarm()
    {
        setMusicPlayer(null);
        setAlarmIsGoingOff(false);
        System.err.println("Stopping music. New music player setup and ready for new alarm");
        System.err.println(getAlarm().getTimeAsStr()+" alarm turned off.\n");
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
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill, int none, Insets insets) {
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
}
