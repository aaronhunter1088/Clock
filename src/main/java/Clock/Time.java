package Clock;

/**
 * Enumeration class v6.Time
 * The values used by the clock to distinguish
 * before midday or after midday.
 * 
 * @author michael ball
 * @version 2.6
 */
public enum Time
{
    AM(1, "AM"),
    PM(2, "PM");

    private int value;
    private String strValue;

    Time(int value, String strValue)
    {
        this.value = value;
        this.strValue = strValue;
    }

    public int getValue() { return this.value; }
    public String getStrValue() { return this.strValue; }

    public void setValue(int value) { this.value = value; }
    public void setStrValue(String strValue) { this.strValue = strValue; }
}
