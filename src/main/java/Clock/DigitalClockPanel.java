package Clock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * The DigitalClockPanel is the main panel and is
 * visible first to the user. Here you can
 * see the time and date.
 * Clicking on the menu options under
 * Settings can change how the time and date
 * look.
 *
 * @author michael ball
 * @version 2.5
 */
public class DigitalClockPanel extends JPanel implements IClockPanel
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(DigitalClockPanel.class);
    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jlbl1;
    private JLabel jlbl2;
    private Clock clock;
    public PanelType panelType;

    public DigitalClockPanel(Clock clock)
    {
        setClock(clock);
        setPanelType(PanelType.DIGITAL_CLOCK);
        setMaximumSize(Clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setupClockPanel(getClock());
        addComponentsToPanel();
    }

    public GridBagLayout getGridBagLayout() { return this.layout; }
    public GridBagConstraints getGridBagConstraints() { return this.constraints; }
    public Clock getClock() { return this.clock; }
    public JLabel getJlbl1() { return this.jlbl1; }
    public JLabel getJlbl2() { return this.jlbl2; }

    private void setGridBagLayout(GridBagLayout layout) { this.layout = layout; }
    private void setGridBagConstraints(GridBagConstraints constraints) { this.constraints = constraints; }
    protected void setClock(Clock clock) { this.clock = clock; }
    public void setJlbl1(JLabel jlbl1) { this.jlbl1 = jlbl1; }
    public void setJlbl2(JLabel jlbl2) { this.jlbl2 = jlbl2; }
    protected void setPanelType(PanelType panelType) { this.panelType = panelType; }

    public void setupClockPanel(Clock clock)
    {
        clock.setIsDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
        setJlbl1(new JLabel("", SwingConstants.CENTER));
        setJlbl2(new JLabel("", SwingConstants.CENTER));
    }
    public void printStackTrace(Exception e, String message)
    {
        if (null != message)
            logger.error(message);
        else
            logger.error(e.getMessage());
        for(StackTraceElement ste : e.getStackTrace())
        {
            System.out.println(ste.toString());
        }
    }
    protected void printStackTrace(Exception e)
    { printStackTrace(e, ""); }
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
    @Override
    public void addComponentsToPanel()
    {
        addComponent(getJlbl1(), 0,0,1,1, 0,0);
        addComponent(getJlbl2(), 1,0,1,1, 0,0);
    }

    public void updateLabels()
    {
        getJlbl1().setText(getClock().defaultText(1));
        getJlbl2().setText(getClock().defaultText(2));
        if (getClock().isShowFullDate()) getJlbl1().setFont(Clock.font40);
        else if (getClock().isShowPartialDate()) getJlbl1().setFont(Clock.font50);
        else getJlbl1().setFont(Clock.font60);
        getJlbl2().setFont(Clock.font50);
        getJlbl1().setForeground(Color.WHITE);
        getJlbl2().setForeground(Color.WHITE);
        getClock().repaint();
    }
}