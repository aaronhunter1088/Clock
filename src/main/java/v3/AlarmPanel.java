package v3;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static v3.Time.AMPM.*;

/**
 * TODO: Add feature where we click on Alarm under menu
 * the AlarmPanel will appear and the alarm will appear
 * in the text fields. The alarm will be removed from
 * the list of alarms, the textarea, and from the menu
 *
 * 11/30/2020 Bug:
 * When we have two alarms set, and then one goes off,
 * both alarms are displaying as going off. Only the
 * appropriate alarm(s) should be going off.
 * -Possible cause-
 * When we trigger an alarm, we are possibly displaying
 * all alarms in the list, and not a specific one
 * OR
 * We are looping through the list to determine which
 * alarm to trigger. We may just be triggering them
 * all while looping and not doing for just one.
 */
public class AlarmPanel extends JPanel implements Panels {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jalarmLbl1 = new JLabel("", SwingConstants.CENTER); // H
    private JLabel jalarmLbl2 = new JLabel("", SwingConstants.CENTER); // M
    private JLabel jalarmLbl3 = new JLabel("", SwingConstants.CENTER); // Time (AM/PM)
    private JLabel jalarmLbl4 = new JLabel("", SwingConstants.CENTER); // All Alarms
    private JTextField jtextField1 = new JTextField(2); // Hour textfield
    private JTextField jtextField2 = new JTextField(2); // Min textfield
    private JTextField jtextField3 = new JTextField(2); // Time textfield
    private JButton jSetAlarmButton = new JButton("Set");
    private JTextArea jTextArea = new JTextArea(4, 20);
    private JScrollPane scrollPane = null;
    private Clock clock;
    private Clock.Alarm alarm;
    private Clock.Alarm currentAlarmGoingOff;
    private boolean updatingAlarm;
    private AdvancedPlayer musicPlayer;

    public AlarmPanel(Clock clock) {
        setClock(clock);
        setMinimumSize(clock.alarmSize);
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
    public Clock.Alarm getAlarm() { return this.alarm; }
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
    public Clock.Alarm getCurrentAlarmGoingOff() { return this.currentAlarmGoingOff; }

    // Setters
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClock(Clock clock) { this.clock = clock; }
    protected void setAlarm(Clock.Alarm alarm) { this.alarm = alarm; }
    protected void setJAlarmLbl1(JLabel jalarmLbl1) { this.jalarmLbl1 = jalarmLbl1; }
    protected void setJAlarmLbl2(JLabel jalarmLbl2) { this.jalarmLbl2 = jalarmLbl2; }
    protected void setJAlarmLbl3(JLabel jalarmLbl3) { this.jalarmLbl3 = jalarmLbl3; }
    protected void setJAlarmLbl4(JLabel jalarmLbl4) { this.jalarmLbl4 = jalarmLbl4; }
    protected void setJTextField1(JTextField jtextField1) { this.jtextField1 = jtextField1; }
    protected void setJTextField2(JTextField jtextField2) { this.jtextField2 = jtextField2; }
    protected void setJTextField3(JTextField jtextField3) { this.jtextField3 = jtextField3; }
    protected void setJSetAlarmButton(JButton jSetAlarmButton) { this.jSetAlarmButton = jSetAlarmButton; }
    protected void setJScrollPane(JScrollPane scrollPane) { this.scrollPane = scrollPane; }
    protected void setJTextArea(JTextArea jTextArea) { this.jTextArea = jTextArea; }
    protected void setUpdatingAlarm(boolean updatingAlarm) { this.updatingAlarm = updatingAlarm; }
    protected void setMusicPlayer(AdvancedPlayer musicPlayer) { this.musicPlayer = musicPlayer; }
    protected void setCurrentAlarmGoingOff(Clock.Alarm currentAlarmGoingOff) { this.currentAlarmGoingOff = currentAlarmGoingOff; }

    // Helper methods
    public void setupMusicPlayer()
    {
        try
        {
            setMusicPlayer(new AdvancedPlayer(new FileInputStream(Paths.get("src/main/resources/alarmSound1.mp3").toUri().getPath())));
        }
        catch (FileNotFoundException | JavaLayerException e)
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
        getJAlarmLbl1().setFont(getClock().font60); // H
        getJAlarmLbl2().setFont(getClock().font60); // M
        getJAlarmLbl3().setFont(getClock().font60); // T
        getJAlarmLbl4().setFont(getClock().font20); // All Alarms
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        getJTextArea().setFont(getClock().font40); // alarms
        getJTextArea().setVisible(true);
        getJTextArea().setEditable(false);
        getJTextArea().setWrapStyleWord(true);
        getJTextArea().setLineWrap(false);
        getJTextArea().setBackground(Color.BLACK);
        getJTextArea().setForeground(Color.WHITE);
        setJScrollPane(new JScrollPane(getJTextArea()));
        getJScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getJScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        getJScrollPane().setMaximumSize(new Dimension(20,50));
    }
    @Override
    public void addComponentsToPanel()
    {
        updateLabels();
        addComponent(getJAlarmLbl1(), 0,0,1,1, 0,0, GridBagConstraints.HORIZONTAL); // H
        addComponent(getJTextField1(), 0,1,1,1, 20,0, GridBagConstraints.HORIZONTAL); // Textfield
        addComponent(getJAlarmLbl2(), 0,2,1,1, 0,0, GridBagConstraints.HORIZONTAL); // M
        addComponent(getJTextField2(), 0,3,1,1, 20,0, GridBagConstraints.HORIZONTAL); // Textfield
        addComponent(getJAlarmLbl3(), 0,4,1,1, 0,0, GridBagConstraints.HORIZONTAL); // Time (AM/PM)
        addComponent(getJTextField3(), 0,5,1,1, 20,0, GridBagConstraints.HORIZONTAL); // Textfield
        addComponent(getJSetAlarmButton(), 0,6,0,1, 0,0, GridBagConstraints.NONE); // Set Alarm button
        addComponent(getJAlarmLbl4(), 1,0,0,1, 0,0, GridBagConstraints.HORIZONTAL); // All alarms
        addComponent(getJScrollPane(), 2, 0, 0, 2, 0, 0, GridBagConstraints.BOTH);
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
                    setCurrentAlarmGoingOff((Clock.Alarm)alarm);
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
        // in the textfields and we will set a boolean to true
        // which will allow editing the textfields to any value
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
        };
    }
    protected void resetViewAlarmsMenu(ArrayList alarms)
    {
        getClock().getClockMenuBar().setViewAlarmsMenu(new JMenu("View Alarms"));
        getClock().getClockMenuBar().setSetAlarms(new JMenuItem("Set Alarms"));
        getClock().getClockMenuBar().getViewAlarmsMenu().add(getClock().getClockMenuBar().getSetAlarms());
        getClock().getClockMenuBar().getSetAlarms().setAccelerator(KeyStroke.getKeyStroke(
                java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK));
        System.err.println("Size of viewAlarms before adding " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
        for(int i = 0; i < alarms.size(); i++)
        {
            JMenuItem alarmItem = new JMenuItem(((Clock)alarms.get(i)).getTimeAsStr());
            getClock().getClockMenuBar().getViewAlarmsMenu().add(alarmItem);
        }
        System.err.println("Size of viewAlarms after adding " + getClock().getClockMenuBar().getViewAlarmsMenu().getItemCount());
    }
    public void setupAlarmButton()
    {
        getJSetAlarmButton().addActionListener(action ->
        {
            // check if h, m, and time are set. exit if not
            Clock.Alarm alarm = null;
            try
            {
                if (StringUtils.isBlank(getJTextField1().getText()))
                {
                    throw new InvalidInputException("Hour cannot be blank");
                }
                if (StringUtils.isBlank(getJTextField2().getText()))
                {
                    throw new InvalidInputException("Minutes cannot be blank");
                }
                if (StringUtils.isEmpty(getJTextField3().getText()))
                {
                    throw new InvalidInputException("Time cannot be blank");
                }
                // Passed validation
                if (isUpdatingAlarm())
                {
                    // update list of alarms
                    Clock clock = new Clock(Integer.parseInt(getJTextField1().getText()), Integer.parseInt(getJTextField2().getText()), 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, convertStringToTimeAMPM(getJTextField3().getText()));
                    alarm = new Clock.Alarm(clock, clock.getHours(), isUpdatingAlarm());
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
                    // erase input in textfields
                    getJTextField1().setText("");
                    getJTextField2().setText("");
                    getJTextField3().setText("");
                }
            }
            catch (InvalidInputException iie)
            {
                System.err.println(iie.getMessage() + "; no alarm set!");
                for(StackTraceElement ste : iie.getStackTrace())
                {
                    System.err.println(ste);
                }
            }
            catch (ParseException pe)
            {
                System.err.println(pe.getMessage() + "; no alarm set!");
                for(StackTraceElement ste : pe.getStackTrace())
                {
                    System.err.println(ste);
                }
            }
            getClock().changeToClockPanel();
            getClock().printClockStatus();
            System.err.println("Finished updating alarm: " + alarm.getTimeAsStr());
        });
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
        for(Clock.Alarm alarm : getClock().getListOfAlarms())
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
     * @return
     * @throws ParseException
     */
    public Clock.Alarm createAlarm() throws ParseException
    {
        int hour = Integer.parseInt(getJTextField1().getText());
        int minutes = Integer.parseInt(getJTextField2().getText());
        Time.AMPM ampm = convertStringToTimeAMPM(getJTextField3().getText());
        Clock clock = new Clock(hour, minutes, 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, ampm);
        Clock.Alarm alarm = new Clock.Alarm(clock, hour, true);
        alarm.setAlarmGoingOff(false);
        System.err.println("\ncreated an alarm: " + alarm.getTimeAsStr());
        return alarm;
    }
    public void createAlarm(Clock alarm)
    {
        setAlarm((Clock.Alarm)alarm);
        getAlarm().setAlarmGoingOff(false);
        // add clock to list of alarms
        getClock().getListOfAlarms().add(getAlarm());
        if (!StringUtils.isEmpty(getJTextArea().getText())) {
            getJTextArea().append("\n");
        }
        getJTextArea().append(getAlarm().getTimeAsStr());
        setupMusicPlayer();
        // display list of alarms below All Alarms
        this.repaint();
        // erase input in textfields
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
