package v1;
/**
 * Enumeration class Time - write a description of the enum class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public enum Time
{
    MONDAY(1, "Monday"), TUESDAY(2, "Tuesday"), WEDNESDAY(3, "Wednesday"), 
    THURSDAY(4, "Thursday"), FRIDAY(5, "Saturday"), SATURDAY(6, "Saturday"), SUNDAY(0, "Sunday"),
    
    JANUARY(1, "January"), FEBRUARY(2, "February"), MARCH(3, "March"), APRIL(4, "April"), MAY(5, "May"), JUNE(6, "June"), 
    JULY(7, "July"), AUGUST(8, "August"), SEPTEMBER(9, "September"), OCTOBER(10, "October"), NOVEMBER(11, "November"), DECEMBER(12, "December"),
    
    AM(0, "AM"), PM(1, "PM"),
	
	ERR(-1, "Unknown value");
    
    int value;
    String strValue;
    
    Time(int value, String strValue) {
        setValue(value);
        setStrValue(strValue);
    }
    public int getValue() {
        return this.value;
    }
    public String getStrValue() {
        return this.strValue;
    }
    private void setValue(int value) {
        this.value = value;
    }
    private void setStrValue(String strValue) {
        this.strValue = strValue;
    }
    
    @Override
    public String toString() {
    	return this.getStrValue();
    }
}
