package v4;

/** This interface is for the enum Time. It
 * requires each new "Time" to have a value,
 * and be represented by an integer as well.
 * This allows for easy looping or checking
 * when needed.
 *
 * @author michael ball
 * @version 2.4
 */
public interface ITime {

    int getValue();
    String getStrValue();

    void setValue(int value);
    void setStrValue(String strValue);
}
