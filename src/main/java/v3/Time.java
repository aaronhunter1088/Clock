package v3;

/**
 * Enumeration class v2.Time - write a description of the enum class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public enum Time {

    Time() {};

    enum Day implements ITime {

        SUNDAY(0, "Sunday"), MONDAY(1, "Monday"), TUESDAY(2, "Tuesday"),
        WEDNESDAY(3, "Wednesday"), THURSDAY(4, "Thursday"), FRIDAY(5, "Friday"),
        SATURDAY(6, "Saturday"), ERROR(7, "Unknown day");

        int value;
        String strValue;

        Day(int value, String strValue) {
            setValue(value);
            setStrValue(strValue);
        }
        @Override
        public int getValue() { return this.value; }
        @Override
        public String getStrValue() { return this.strValue; }
        @Override
        public void setValue(int value) { this.value = value; }
        @Override
        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }

    }

    enum Month implements ITime {

        ERROR(0, "Unknown month"), JANUARY(1, "January"), FEBRUARY(2, "February"),
        MARCH(3, "March"), APRIL(4, "April"), MAY(5, "May"), JUNE(6, "June"),
        JULY(7, "July"), AUGUST(8, "August"), SEPTEMBER(9, "September"),
        OCTOBER(10, "October"), NOVEMBER(11, "November"), DECEMBER(12, "December");

        int value;
        String strValue;

        Month(int value, String strValue) {
            setValue(value);
            setStrValue(strValue);
        }

        @Override
        public int getValue() {
            return this.value;
        }
        @Override
        public String getStrValue() { return this.strValue; }
        @Override
        public void setValue(int value) { this.value = value; }
        @Override
        public void setStrValue(String strValue) {
            this.strValue = strValue;
        }
    }

    enum AMPM implements ITime {

        ERROR(0, "Unknown time"), AM(1, "AM"), PM(2, "PM");

        int value;
        String strValue;

        AMPM(int value, String strValue) {
            setValue(value);
            setStrValue(strValue);
        }

        @Override
        public int getValue() { return this.value; }
        @Override
        public String getStrValue() { return this.strValue; }
        @Override
        public void setValue(int value) { this.value = value; }
        @Override
        public void setStrValue(String strValue) { this.strValue = strValue; }
    }
}
