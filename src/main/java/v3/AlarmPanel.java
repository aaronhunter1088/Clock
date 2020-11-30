package v3;

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import static v3.Time.AMPM.*;

/**
 * TODO: Add feature where we click on Alarm under menu
 * the AlarmPanel will appear and the alarm will appear
 * in the text fields. The alarm will be removed from
 * the list of alarms, the textarea, and from the menu
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
    private JTextArea jTextArea = new JTextArea();
    private JScrollPane scrollPane = null;
    private Clock clock;
    private boolean updatingAlarm;

    public AlarmPanel(Clock clock)
    {
        setClock(clock);
        setMinimumSize(clock.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupAlarmPanel(getClock());
        setupAlarmButton();
        //setupCreatedAlarmsFunctionality();
        addComponentsToPanel();
    }

    // Getters
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
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

    // Setters
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    private void setClock(Clock clock) { this.clock = clock; }
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

    // Helper methods
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
        getJAlarmLbl1().setFont(getClock().font60); // H
        getJAlarmLbl2().setFont(getClock().font60); // M
        getJAlarmLbl3().setFont(getClock().font60); // T
        getJAlarmLbl4().setFont(getClock().font50); // All Alarms
        getJTextArea().setFont(getClock().font50); // alarms
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        getJTextArea().setVisible(true);
        getJTextArea().setEditable(false);
        getJTextArea().setBackground(Color.BLACK);
        getJTextArea().setForeground(Color.WHITE);
        setJScrollPane(new JScrollPane(getJTextArea()));
        getJScrollPane().setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getJScrollPane().setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
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
    public void checkAlarms()
    {
        // alarm has reference to time
        // check all alarms
        // if any alarm matches clock's time
        // update lbl1 and lbl2 to display alarm
        // user must view that alarm to set it off
        getClock().getListOfAlarms().forEach(
            (alarm) ->
            {
                if (alarm.getTimeAsStr().equals(getClock().getTimeAsStr()) || getClock().isAlarm())
                {
                    System.out.println("Alarm " + alarm.getTimeAsStr() + " is going off");
                    getClock().getClockPanel().getJlbl1().setText(alarm.getTimeAsStr());
                    getClock().getClockPanel().getJlbl2().setText("is going off!");
                    getClock().setAlarm(true);
                }
            }
        );
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
        Component[] components = getClock().getClockMenuBar().getViewAlarmsMenu().getMenuComponents();
        for(Component c : components)
        {
            AtomicReference<Clock> alarmToRemove = new AtomicReference<>();
            AtomicReference<String> stringToRemove = new AtomicReference<>();
            if (!((JMenuItem) c).getText().equals("Set Alarm") &&
                !((JMenuItem) c).getText().equals("View all") )
            {
                //05:06:00 PM
                JMenuItem menuItem = (JMenuItem) c;
                menuItem.addActionListener(action -> {
                    getClock().getListOfAlarms().forEach(
                        (alarm) -> {
                            if ((alarm).getTimeAsStr().equals(menuItem.getText()))
                            {
                                alarmToRemove.set(alarm);
                                stringToRemove.set((alarm).getTimeAsStr());
                                getClock().setAlarm(false);
                            }
                        }
                    );
                    getJTextField1().setText(menuItem.getText().substring(0,2));
                    getJTextField2().setText(menuItem.getText().substring(3,5));
                    getJTextField3().setText(menuItem.getText().substring(9));
                    setUpdatingAlarm(true);
                    // remove time as option from menu
                    getClock().getClockMenuBar().getViewAlarmsMenu().remove(menuItem);
                    // remove alarm from list of alarms
                    getClock().getListOfAlarms().remove(alarmToRemove.getAcquire());
                    String[] strings = getJTextArea().getText().split("\n");
                    getJTextArea().setText("");
                    for(String stringAlarm : strings)
                    {
                        if (alarmToRemove.getAcquire() != null && !stringAlarm.equals((alarmToRemove.getAcquire()).getTimeAsStr()))
                        {
                            if (!StringUtils.isEmpty(getJTextArea().getText())) {
                                getJTextArea().append("\n");
                            }
                            getJTextArea().append(stringAlarm);
                        }
                    }
                    System.out.println("Clicked on " + alarmToRemove.getAcquire().getTimeAsStr());
                    getClock().changeToAlarmPanel();
                });
            }
            // TODO: is this necessary. Clicking 'Set Alarm' brings us to the same view
            else if (((JMenuItem) c).getText().equals("View all"))
            {
                getClock().changeToAlarmPanel();
            }
        }
    }
    public void setupAlarmButton()
    {
        getJSetAlarmButton().addActionListener(action -> {
            // check if h, m, and time are set. exit if not
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
                if (isUpdatingAlarm())
                {
                    // update list of alarms
                    Clock alarm = null;
                    alarm = new Clock(Integer.parseInt(getJTextField1().getText()), Integer.parseInt(getJTextField2().getText()), 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, convertStringToTimeAMPM(getJTextField3().getText()));
                    getClock().getListOfAlarms().add(alarm);
                    setUpdatingAlarm(false);
                    // determine how to update alarm (update/delete)
                }
                createAlarm();
                getClock().changeToClockPanel();
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
        });
    }
    public void createAlarm() throws ParseException
    {
        int hour = Integer.parseInt(getJTextField1().getText());
        int minutes = Integer.parseInt(getJTextField2().getText());
        Time.AMPM ampm = convertStringToTimeAMPM(getJTextField3().getText());
        Clock alarm = null;
        alarm = new Clock(hour, minutes, 0, Time.Month.NOVEMBER, Time.Day.SUNDAY, 29, 2020, ampm);
        // add clock to list of alarms
        getClock().getListOfAlarms().add(alarm);
        if (!StringUtils.isEmpty(getJTextArea().getText())) {
            getJTextArea().append("\n");
        }
        getJTextArea().append(alarm.getTimeAsStr());
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
