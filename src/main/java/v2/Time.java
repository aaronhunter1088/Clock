package v2;

/**
 * Enumeration class v2.Time - write a description of the enum class here
 * 
 * @author (your name here)
 * @version (version number or date here)
 */
public enum Time implements ITime {

    Time() {
        int value;
        String strValue;
        @Override
        public int getValue() { return value; }
        @Override
        public String getStrValue() { return strValue; }
        @Override
        public void setValue(int value) { this.value = value; }
        @Override
        public void setStrValue(String strValue) { this.strValue = strValue; }
    };

    enum Day implements ITime {

        MONDAY(1, "Monday"), TUESDAY(2, "Tuesday"), WEDNESDAY(3, "Wednesday"),
        THURSDAY(4, "Thursday"), FRIDAY(5, "Saturday"), SATURDAY(6, "Saturday"), SUNDAY(0, "Sunday"),
        UNKNOWN_DAY(8, "Unknown day");

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

        JANUARY(1, "January"), FEBRUARY(2, "February"), MARCH(3, "March"), APRIL(4, "April"), MAY(5, "May"), JUNE(6, "June"),
        JULY(7, "July"), AUGUST(8, "August"), SEPTEMBER(9, "September"), OCTOBER(10, "October"), NOVEMBER(11, "November"), DECEMBER(12, "December"),
        ERR(13, "Err");
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

        AM(0, "AM"), PM(1, "PM");

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
