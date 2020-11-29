package v3;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

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
    private JButton jsetAlarmBtn = new JButton("Set");
    private ScrollPane scrollPane = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
    private Clock clock;

    public AlarmPanel(Clock clock)
    {
        setClock(clock);
        setMinimumSize(clock.alarmSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupAlarmPanel(getClock());
        addComponentsToPanel();
    }

    // Getters
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getJAlarmLbl1() { return this.jalarmLbl1; }
    public JLabel getJAlarmLbl2() { return this.jalarmLbl2; }
    public JLabel getJAlarmLbl3() { return this.jalarmLbl3; }
    public JLabel getJAlarmLbl4() { return this.jalarmLbl4; }
    public JTextField getJTextField1() { return this.jtextField1; }
    public JTextField getJTextField2() { return this.jtextField2; }
    public JTextField getJTextField3() { return this.jtextField3; }

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

    // Helper methods
    public void setupAlarmPanel(Clock clock)
    {
        clock.setCalendar(Calendar.getInstance());
        clock.setCalendarTime(new Date());
        clock.setDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
    }
    @Override
    public void addComponentsToPanel()
    {
        updateLabels();
        addComponent(jalarmLbl1, 0,0,1,1, 0,0); // H
        addComponent(jtextField1, 0,1,1,1, 0,0); // Textfield
        addComponent(jalarmLbl2, 0,2,1,1, 0,0); // M
        addComponent(jtextField2, 0,3,1,1, 0,0); // Textfield
        addComponent(jalarmLbl3, 1,0,7,1, 0,0); // Time (AM/PM)
        addComponent(jtextField3, 0,5,1,1, 0,0); // Textfield
        addComponent(jsetAlarmBtn, 0,6,1,1, 0,0); // Set Alarm button
        addComponent(jalarmLbl4, 0,4,1,1, 0,0); // All alarms
    }
    @Override
    public void updateLabels()
    {
        getJAlarmLbl1().setFont(getClock().font60);
        getJAlarmLbl2().setFont(getClock().font60);
        getJAlarmLbl3().setFont(getClock().font60);
        getJAlarmLbl4().setFont(getClock().font60);
        getJAlarmLbl1().setForeground(Color.WHITE);
        getJAlarmLbl2().setForeground(Color.WHITE);
        getJAlarmLbl3().setForeground(Color.WHITE);
        getJAlarmLbl4().setForeground(Color.WHITE);
        getJTextField1().setVisible(true);
        getJTextField1().setEnabled(true);
        getJTextField2().setEnabled(false);
        getJTextField3().setEnabled(false);
        getJTextField2().setFocusable(true);
        getJAlarmLbl1().setText(getClock().defaultText(3)); // H
        getJAlarmLbl2().setText(getClock().defaultText(5)); // M
        getJAlarmLbl4().setText(getClock().defaultText(6)); // T
        getJAlarmLbl3().setText(getClock().defaultText(4)); // All Alarms ...
        getClock().repaint();
    }
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady)
    {
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().fill = GridBagConstraints.NONE;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }
}
