package org.example.clock;

/**
 * Enumeration class Time
 * The values used by the clock to distinguish
 * before midday or after midday.
 * 
 * @author michael ball
 * @version 2.7
 */
public enum Time {
    AM(1, "AM"),
    PM(2, "PM");

    private int value;
    private String strValue;

    Time(int value, String strValue) {
        setValue(value);
        setStrValue(strValue);
    }

    public int getValue() { return this.value; }
    public String getStrValue() { return this.strValue; }

    private void setValue(int value) { this.value = value; }
    private void setStrValue(String strValue) { this.strValue = strValue; }
}