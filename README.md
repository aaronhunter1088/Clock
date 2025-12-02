# Clock v3.0

This is a Java Swing GUI application which displays the date and time to the user in
both digital and analogue modes. It also has the capability to set multiple alarms,
timers and stopwatches. You can also start the clock specifying a panel or a specific
date and time. The following is the default java command to run the clock:

```
# Both commands achieve the same result:
java -jar Clock-3.0-jar-with-dependencies.jar
or with 0 as the argument:
java -jar Clock-3.0-jar-with-dependencies.jar 0

# Start the jar file with a specific panel:
java -jar Clock-3.0-jar-with-dependencies.jar 1,panel_alarm

# Start the jar file with a specific date and time:
java -jar Clock-3.0-jar-with-dependencies.jar 2,10,30,0,august,wednesday,6,2025,pm
```

The clock defaults on the Digital Clock panel, displaying the current date like
AUGUST 6, 2025 and the current time below the date as 10:30:45 AM.
The clock has Settings and Features. Features will be consistent across all panels.
By clicking on Features, you can change the panel. The panels are:
* (Ctrl + D) View Digital Clock
* (Ctrl + C) View Analogue Clock
* (Ctrl + A) View Alarm
* (Ctrl + T) View Timers
* (Ctrl + S) View Stopwatches

On the Digital Clock panel, you can change the following settings:
* (Ctrl + M) Show Military/Standard Time: Toggles the time to show in military time (like 1030 hours 45)
             or standard time (like 10:30:45 AM)
* (Ctrl + F) Show/Hide Full Date: This will display the full date like FRIDAY AUGUST 6, 2025
* (Ctrl + P) Show/Hide Partial Date: This will display a shorter version of the date like FRI AUG 6, 2025
             Only one, Full or Partial date, can be displayed at a time
* (Shift + T) Turn On/Off Daylight Savings Time: Toggles daylight savings time, default setting is on
* Change Timezone: This will change the timezone to any US timezone. Default timezone is the user's timezone

On the Analogue Clock panel, the clock's time is displayed in analogue mode with the digital time
displayed below the clock hands. The date is not displayed in this mode.

On the Analogue Clock panel, you can change the following settings:
* (Ctrl + E) Show/Hide Digital Time: This will show/hide the digital time on the Analogue clock
* (Shift + T) Turn On/Off Daylight Savings Time: Toggles daylight savings time; default setting is on.
* Change Timezone: This will change the timezone to any US timezone. Default timezone is the user's timezone

On the Alarms panel, you can set and manage alarms. Simply enter a Name, Hour, Minutes, and Time 
(AM/PM) to set an alarm. You can choose a particular day or set of days, week days or weekends, 
or all days of the week for this alarm to sound off. If no name is provided, the alarm will default
to "Alarm" + the current count of alarms, plus 1, e.g. "Alarm 1", "Alarm 2", etc.
Once created, the alarm will be sleeping and you can either edit it or delete it. Once it is going off, 
it will sound off until you stop it. You can choose to stop the alarm, which will stop the sound and 
reset it for the next time it goes off, or you can snooze it, which will stop the sound temporarily for 
7 minutes and then sound off again.

The Alarms panel two settings:
* (Ctrl + P) Pause/Resume All Alarms: This will pause or resume all alarms at once.
* (Ctrl + R) Reset Panel: This will reset the alarms panel, removing all alarms and resetting the list.

On the Timers panel, you can set multiple timers. Here you can enter the Name, Hours, Minutes, 
and Seconds of a Timer. If no name is provided, the name will default to "Timer" + the current count 
of timers, plus 1, e.g. "Timer 1", "Timer 2", etc.
Once created, the timer will begin counting down from the set time. While it is counting down, you can 
pause it, resume it, or remove it. Once it is going off, it will play a sound until you stop it. You can 
choose to reset the timer, which will restart the count down, or stop the timer, which will also remove 
it from the list of timers.

On the Timers panel, you can change the following settings:
* (Ctrl + P) Pause/Resume All Timers: This will pause or resume all timers at once.
* (Ctrl + R) Reset Panel: This will reset the timers panel, removing all timers and resetting the list.

On the Stopwatches panel, you can set multiple stopwatches. Here you can enter the Name of a Stopwatch,
click start, pause and resume it, create a new one, switch between stopwatches, and reset the panel. You
can also reverse the order of the laps (for all stopwatches). Each stopwatch can have its own laps, which
is displayed in a table to the right of the stopwatch. You can view the elapsed time of the stopwatch in
digital or analogue mode. The default is digital mode.

On the Stopwatches panel, you can change the following settings:
* (Ctrl + T) Show Analogue/Digital Time: This will toggle the stopwatch time between analogue and digital modes.
* (Ctrl + R) Reverse Laps Order: This toggle will reverse the order of the laps for all stopwatches.

Running The Tests:<br>
The application is tested using JUnit 5 and Mockito. Some tests have popups that requires you to close them.
Without the tests will 'pause' until that popup is closed.

```
# History
v3.0 Updated POM to use the parent pom more effectively.

v2.9 Updated to use my parent pom. Multiple timers. Multiple ways to start the application.
Java was upgraded to 21. Multiple stopwatches were added.

v2.8 Added Javadocs and cleaned up the code quite a bit. When running the application, you
can set this envVar: logLevel to be DEBUG, or INFO depending on how much info you want.

v2.7 You can now set particular timezones which will update the clocks time.
Specific panel's have their own settings and are now only visible when in that particular
panel. We also upgraded Java from 11 to 18.

v2.6
You can now view the time in Analogue mode. Click Features --> View Analogue Clock 
(or click Ctrl + C). This takes the current time of the Clock instance and displays
it in Analogue mode. This feature has its own setting which lets you decide if you
still want the digital time displayed. Date is not supported. 

Sorry, no history was recorded before v2.6. It is assumed that as this point, the project
was only the clock in digital clock mode, maybe some settings, and that is it.