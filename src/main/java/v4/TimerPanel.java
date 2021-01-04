package v4;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
/** This is the newest panel, the Timer panel.
 * It is part of version 2.4
 *
 * The timer panel is used to set a timer.
 * Giving the panel some Hours, some Minutes,
 * and some Seconds, it will sound an alarm
 * when the timer reaches '00:00:00'.
 *
 * Timers are a new "Feature".
 * They are visible under the View Timers menu
 * which is under the Features menu
 *
 * @author michael ball
 * @version 2.4
 */
public class TimerPanel extends JPanel implements IClockFace {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jtimerLbl1 = new JLabel("", SwingConstants.CENTER); // H
    private JLabel jtimerLbl2 = new JLabel("", SwingConstants.CENTER); // M
    private JLabel jtimerLbl3 = new JLabel("", SwingConstants.CENTER); // S
    private JTextField jtextField1 = new JTextField(2); // Hour textField
    private JTextField jtextField2 = new JTextField(2); // Min textField
    private JTextField jtextField3 = new JTextField(2); // Second textField
    private JButton timerButton = null;
    private Clock clock;
    private Clock timer;
    // Constructor
    public TimerPanel(Clock clock) {
        setClock(clock);
        setSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupTimerPanel(clock);
        setupTimerButton();
        updateLabels();
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
    public JButton getTimerButton() { return this.timerButton; }

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
    protected void setTimerButton(JButton timerButton) { this.timerButton = timerButton; }

    // Helper methods
    public void setupTimerPanel(Clock clock)
    {
        getJTimerlbl1().setFont(Clock.font30);
        getJTimerlbl1().setForeground(Color.WHITE);
        getJTimerlbl1().setSize(new Dimension(50, 50));
        getJTimerlbl2().setFont(Clock.font30);
        getJTimerlbl2().setForeground(Color.WHITE);
        getJTimerlbl2().setSize(new Dimension(50, 50));
        getJTimerlbl3().setFont(Clock.font30);
        getJTimerlbl3().setForeground(Color.WHITE);
        getJTimerlbl3().setSize(new Dimension(50, 50));
        getJTextField1().setSize(new Dimension(50, 50));
        getJTextField2().setSize(new Dimension(50, 50));
        getJTextField3().setSize(new Dimension(50, 50));
        setTimerButton(new JButton("Set"));
        getTimerButton().setFont(Clock.font20);
        getTimerButton().setOpaque(true);
        getTimerButton().setBackground(Color.BLACK);
        getTimerButton().setForeground(Color.BLACK);
    }
    @Override
    public void addComponentsToPanel()
    {
        setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        addComponent(getJTimerlbl1(),0,0,1,1,0,0, GridBagConstraints.RELATIVE); // Timer Label 1
        addComponent(getJTextField1(), 0,1,1,1,0,0, GridBagConstraints.RELATIVE); // TextField 1
        addComponent(getJTimerlbl2(), 0,2,1,1,0,0, GridBagConstraints.RELATIVE); // Timer Label 2
        addComponent(getJTextField2(), 0,3,1,1,0,0, GridBagConstraints.RELATIVE); // TextField 2
        addComponent(getJTimerlbl3(), 0,4,1,1,0,0, GridBagConstraints.RELATIVE); // Timer Label 3
        addComponent(getJTextField3(), 0,5,1,1,0,0, GridBagConstraints.RELATIVE); // TextField 3
        addComponent(getTimerButton(), 2,2,2,1,0,0, GridBagConstraints.NONE); // Set Timer
    }
    @Override
    public void updateLabels()
    {
        getJTextField1().setFocusable(true);
        getJTimerlbl1().setText(getClock().defaultText(3)); // H
        getJTimerlbl2().setText(getClock().defaultText(4)); // M
        getJTimerlbl3().setText(getClock().defaultText(7)); // S
        getClock().repaint();
    }
    protected void setupTimerButton()
    {
        getTimerButton().addActionListener(action ->
        {
            // check if h, m, and s are set. do nothing if not
            boolean validated = false;
            try
            {
                validated = validateThirdTextField(); // Seconds
                validated = validateSecondTextField(); // Minutes
                validated = validateFirstTextField(); // Hours

                if (!validated)
                {

                }
                else // validated is true
                {

                }
            }
            catch (InvalidInputException iie)
            { printStackTrace(iie); }
        });
    }
    protected boolean validateFirstTextField() throws InvalidInputException
    {
        if (StringUtils.isBlank(getJTextField1().getText()))
        {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Hour cannot be blank");
        }
        else if (!NumberUtils.isNumber(getJTextField1().getText()))
        {
            getJTextField1().grabFocus();
            throw new InvalidInputException("Given value is invalid");
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
        else if (!NumberUtils.isNumber(getJTextField2().getText()))
        {
            getJTextField2().grabFocus();
            throw new InvalidInputException("Given value is invalid");
        }
        else if (Integer.parseInt(getJTextField2().getText()) >= 60) // 70 minutes given
        {
            System.err.println("minutes: " + getJTextField2().getText());
            int hours = Integer.parseInt(getJTextField2().getText());
            while (hours >= 60)
            {
                hours -= 60; // 70 - 60 = 1R10
                getJTextField1().setText(Integer.toString(Integer.parseInt(getJTextField1().getText()) + 1)); // extra hours added, minutes shows remaining
                getJTextField2().setText(Integer.toString(hours));
            }
            System.err.println("minutes: " + getJTextField2().getText());
            System.err.println("hours: " + getJTextField1().getText());
        }
        return true;
    }
    protected boolean validateThirdTextField() throws InvalidInputException
    {
        if (StringUtils.isEmpty(getJTextField3().getText()))
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Seconds cannot be blank");
        }
        else if (getJTextField3().getText().length() > 3)
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Cannot enter more than 3 digits");
        }
        else if (!NumberUtils.isNumber(getJTextField3().getText()))
        {
            getJTextField3().grabFocus();
            throw new InvalidInputException("Given value is invalid");
        }
        else if (Integer.parseInt(getJTextField3().getText()) >= 60) // 70 seconds given
        {
            System.err.println("seconds: " + getJTextField3().getText());
            int seconds = Integer.parseInt(getJTextField3().getText());
            while (seconds >= 60)
            {
                seconds -= 60; // 70 - 60 = 1R10
                getJTextField2().setText(Integer.toString(Integer.parseInt(getJTextField2().getText()) + 1));
                getJTextField3().setText(Integer.toString(seconds));
            }
            System.err.println("seconds: " + getJTextField3().getText());
            System.err.println("minutes: " + getJTextField2().getText());
        }
        return true;
    }
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
    @SuppressWarnings("Duplicates")
    public void addComponent(Component cpt, int gridy, int gridx, double gwidth, double gheight, int ipadx, int ipady, int fill)
    {
        getGridBagConstraints().gridx = gridx;
        getGridBagConstraints().gridy = gridy;
        getGridBagConstraints().gridwidth = (int)Math.ceil(gwidth);
        getGridBagConstraints().gridheight = (int)Math.ceil(gheight);
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().ipadx = ipadx;
        getGridBagConstraints().ipady = ipady;
        getGridBagConstraints().fill = fill;
        getGridBagConstraints().weightx = 0;
        getGridBagConstraints().weighty = 0;
        getGridBagConstraints().insets = new Insets(0,0,0,0);
        getGridBagLayout().setConstraints(cpt, getGridBagConstraints());
        add(cpt);
    }
}
