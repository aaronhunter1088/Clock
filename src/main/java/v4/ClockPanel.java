package v4;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
/** The ClockPanel is the main panel and is
 * visible first to the user. Here you can
 * see the time and date.
 * Clicking on the menu options under
 * Settings can change how the time and date
 * look.
 *
 * @author michael ball
 * @version 2.4
 */
public class ClockPanel extends JPanel implements Panels {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jlbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jlbl2 = new JLabel("", SwingConstants.CENTER);
    private Clock clock;

    public ClockPanel(Clock clock) {
        setClock(clock);
        setMinimumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupClockPanel(getClock());
        addComponentsToPanel();
    }

    // Getters
    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getJlbl1() { return this.jlbl1; }
    public JLabel getJlbl2() { return this.jlbl2; }

    // Setters
    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    private void setClock(Clock clock) { this.clock = clock; }
    public void setJlbl1(JLabel jlbl1) { this.jlbl1 = jlbl1; }
    public void setJlbl2(JLabel jlbl2) { this.jlbl2 = jlbl2; }

    // Helper methods
    public void setupClockPanel(Clock clock)
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
        addComponent(getJlbl1(), 0,0,1,1, 0,0);
        addComponent(getJlbl2(), 1,0,1,1, 0,0);
    }
    @Override
    public void updateLabels()
    {
        getJlbl1().setText(getClock().defaultText(1));
        getJlbl2().setText(getClock().defaultText(2));
        getJlbl1().setFont(Clock.font60);
        getJlbl2().setFont(Clock.font50);
        getJlbl1().setForeground(Color.WHITE);
        getJlbl2().setForeground(Color.WHITE);
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