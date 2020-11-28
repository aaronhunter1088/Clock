package v3;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ClockPanel extends JPanel {

    private GridBagLayout layout;
    private GridBagConstraints constraints;
    private JLabel jlbl1 = new JLabel("", SwingConstants.CENTER);
    private JLabel jlbl2 = new JLabel("", SwingConstants.CENTER);
    private Clock clock;

    public ClockPanel(Clock clock) throws ParseException {
        setMinimumSize(clock.defaultSize);
        setGridBagLayout(new GridBagLayout());
        setLayout(getGridBagLayout());
        setGridBagConstraints(new GridBagConstraints());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        setBackground(Color.BLACK);
        setupClockPanel(clock);
        setClock(clock);
        addComponentsToPanel_v3();
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
    public void tick()
    {
        getClock().setSeconds(getClock().getSeconds()+1);
        getClock().setSecondsAsStr(getClock().getSeconds() <= 9 ? "0"+getClock().getSeconds() : Integer.toString(getClock().getSeconds()));
        if (getClock().getSeconds() == 60)
        {
            getClock().setSeconds(0);
            getClock().setSecondsAsStr("00");
            getClock().setMinutes(getClock().getMinutes()+1);
            getClock().setMinutesAsStr(getClock().getMinutes() <= 9 ? "0"+getClock().getMinutes() : Integer.toString(getClock().getMinutes()));
            if (getClock().getMinutes() == 60)
            {
                getClock().setMinutes(0);
                getClock().setMinutesAsStr("00");
                getClock().setHours(getClock().getHours()+1);
                if (getClock().getHours() == 12 && getClock().getMinutes() == 0 && getClock().getSeconds() == 0 && !getClock().isShowMilitaryTime())
                {
                    getClock().setHours(12);
                    getClock().setHoursAsStr("12");
                    if (getClock().getAMPM() == Time.AMPM.PM)
                    {
                        getClock().setAMPM(Time.AMPM.AM);
                        getClock().setDateChanged(true);
                    }
                    else
                    {
                        getClock().setDateChanged(false);
                        getClock().setAMPM(Time.AMPM.PM);
                    }
                }
                else if (getClock().getHours() == 13 && !getClock().isShowMilitaryTime())
                {
                    getClock().setHours(1);
                    getClock().setHoursAsStr("01");
                    getClock().setDateChanged(false);
                }
                else if (getClock().getHours() == 24 && getClock().getMinutes() == 0 && getClock().getSeconds() == 0 && getClock().isShowMilitaryTime())
                {
                    getClock().setHours(0);
                    getClock().setHoursAsStr("00");
                    getClock().setAMPM(Time.AMPM.AM);
                    getClock().setDateChanged(true);
                }
                else if (getClock().getHours() >= 13 && getClock().isShowMilitaryTime())
                {
                    getClock().setHoursAsStr(Integer.toString(getClock().getHours()));
                    getClock().setDateChanged(false);
                }
                else
                {
                    getClock().setHours(getClock().getHours());
                }
            }
        }
        else
        {
            getClock().setDateChanged(false);
        }

        if (getClock().isDateChanged())
        {
            getClock().setDate(getClock().getDate()+1);
            getClock().setDaylightSavingsTime(getClock().isTodayDaylightSavingsTime());
        }
        switch (getClock().getMonth())
        {
            case JANUARY: {
                if (getClock().getDate() == 31 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.FEBRUARY);
                }
                break;
            }
            case FEBRUARY: {
                if ((getClock().getDate() == 28 || getClock().getDate() == 30) && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.MARCH);
                }
                else if (getClock().getDate() == 28 && getClock().isLeapYear() && getClock().isDateChanged())
                {
                    getClock().setDate(29);
                    getClock().setMonth(Time.Month.FEBRUARY);
                }
                break;
            }
            case MARCH: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.APRIL);
                }
                break;
            }
            case APRIL: {
                if (getClock().getDate() == 31 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.MAY);
                }
                break;
            }
            case MAY: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.JUNE);
                }
                break;
            }
            case JUNE: {
                if (getClock().getDate() == 31 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.JULY);
                }
                break;
            }
            case JULY: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.AUGUST);
                }
                break;
            }
            case AUGUST: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.SEPTEMBER);
                }
                break;
            }
            case SEPTEMBER: {
                if (getClock().getDate() == 31 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.OCTOBER);
                }
                break;
            }
            case OCTOBER: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.NOVEMBER);
                }
                break;
            }
            case NOVEMBER: {
                if (getClock().getDate() == 31 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.DECEMBER);
                }
                break;
            }
            case DECEMBER: {
                if (getClock().getDate() == 32 && getClock().isDateChanged())
                {
                    getClock().setDate(1);
                    getClock().setMonth(Time.Month.JANUARY);
                    getClock().setYear(getClock().getYear()+1);
                }
                break;
            }
            default : {}
        }
        getClock().setClockValues(getClock().getAMPM(), getClock().isShowMilitaryTime());

        if (getClock().isDateChanged())
        {
            int month = 0;
            try
            {
                month = getClock().convertTimeMonthToInt(getClock().getMonth());
            }
            catch (InvalidInputException iie) {
                System.err.println(iie.getMessage());
            }
            int date = getClock().getDate();
            String monthStr = month <= 9 ? "0"+month : Integer.toString(month);
            String dateStr = date <= 9 ? "0"+date : Integer.toString(date);
            Date updatedDate = null;
            try
            {
                updatedDate = getClock().sdf.parse(monthStr+"-"+dateStr+"-"+getClock().getYear());
            }
            catch (ParseException e) { System.err.println(e.getMessage()); }
            getClock().getCalendar().setTime(updatedDate);
            getClock().setDay(getClock().convertIntToTimeDay(getClock().getCalendar().get(Calendar.DAY_OF_WEEK)));
        }
        if (getClock().isDaylightSavingsTime())
        {
            if (getClock().getMonth() == Time.Month.MARCH && getClock().getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
            {
                getClock().setHours(3);
                getClock().setDaylightSavingsTime(false);
            }
            else if (getClock().getMonth() == Time.Month.NOVEMBER && getClock().getAMPM().getStrValue().equals(Time.AMPM.AM.strValue))
            { // && daylightSavingsTime
                getClock().setHours(1);
                getClock().setDaylightSavingsTime(false);
            }
        }
        updateClockLabels();
    }
    public void setupClockPanel(Clock clock)
    {
        clock.calendar = Calendar.getInstance();
        clock.calendar.setTime(new Date());
        clock.setDateChanged(false);
        clock.setShowFullDate(false);
        clock.setShowPartialDate(false);
        clock.setShowMilitaryTime(false);
    }
    public void addComponentsToPanel_v3()
    {
        updateClockLabels();
        addComponent(getJlbl1(), 0,0,1,1, 0,0);
        addComponent(getJlbl2(), 1,0,1,1, 0,0);
    }
    public void updateClockLabels()
    {
        getJlbl1().setText(getClock().defaultText(1, getClock().isALeapYear(getClock().getYear())));
        getJlbl2().setText(getClock().defaultText(2, getClock().isALeapYear(getClock().getYear())));
        getJlbl1().setFont(getClock().font60);
        getJlbl2().setFont(getClock().font50);
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
