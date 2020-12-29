package v4;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class TimerPanel extends JPanel implements Panels {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jtimerLbl1 = new JLabel("", SwingConstants.CENTER); // H
    private JLabel jtimerLbl2 = new JLabel("", SwingConstants.CENTER); // M
    private JLabel jtimerLbl3 = new JLabel("", SwingConstants.CENTER); // S
    private JTextField jtextField1 = new JTextField(2); // Hour textField
    private JTextField jtextField2 = new JTextField(2); // Min textField
    private JTextField jtextField3 = new JTextField(2); // Second textField
    private final JButton jsetAlarmBtn = new JButton("Set");
    private Clock clock;

    public TimerPanel(Clock clock) {
        setClock(clock);
        setMinimumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupTimerPanel(getClock());
        addComponentsToPanel();
    }
    // Getters
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getJTimerlbl1() { return this.jtimerLbl1; }
    public JLabel getJTimerlbl2() { return this.jtimerLbl2; }
    public JLabel getJTimerlbl3() { return this.jtimerLbl3; }
    public JTextField getJTextField1() { return this.jtextField1; }
    public JTextField getJTextField2() { return this.jtextField2; }
    public JTextField getJTextField3() { return this.jtextField3; }

    // Setters
    protected void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    protected void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClock(Clock clock) { this.clock = clock; }
    protected void setJTimerLbl1(JLabel jtimerLbl1) { this.jtimerLbl1 = jtimerLbl1; }
    protected void setJTimerLbl2(JLabel jtimerLbl2) { this.jtimerLbl2 = jtimerLbl2; }
    protected void setJTimerLbl3(JLabel jtimerLbl3) { this.jtimerLbl3 = jtimerLbl3; }
    protected void setJTextField1(JTextField jtextField1) { this.jtextField1 = jtextField1; }
    protected void setJTextField2(JTextField jtextField2) { this.jtextField2 = jtextField2; }
    protected void setJTextField3(JTextField jtextField3) { this.jtextField3 = jtextField3; }

    // Helper methods
    public void setupTimerPanel(Clock clock)
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
//        addComponent(component, 0,4,1,1, 0,0); // All alarms
    }
    @Override
    public void updateLabels()
    {
//        getJLabelLbl1().setFont(getClock().font60);
//        getJAlarmLbl1().setForeground(Color.WHITE);
//        getJAlarmLbl2().setForeground(Color.WHITE);
//        getJAlarmLbl3().setForeground(Color.WHITE);
//        getJAlarmLbl4().setForeground(Color.WHITE);
//        getJTextField1().setVisible(true);
//        getJTextField1().setEnabled(true);
//        getJTextField2().setEnabled(false);
//        getJTextField3().setEnabled(false);
//        getJTextField2().setFocusable(true);
//        getJAlarmLbl1().setText(getClock().defaultText(3)); // H
//        getJAlarmLbl2().setText(getClock().defaultText(5)); // M
//        getJAlarmLbl4().setText(getClock().defaultText(6)); // T
//        getJAlarmLbl3().setText(getClock().defaultText(4)); // All Alarms ...
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
