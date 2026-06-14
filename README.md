# Clock v: 3.1.0

This is a Java Swing GUI application which displays the date and time to the user in
both digital and analogue modes. It also has the capability to set multiple alarms,
timers and stopwatches. You can also start the clock by specifying a panel or a specific
date and time. The following is the default java command to run the clock:

### Running the JAR

```bash
# Default start (digital clock panel)
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar

# Start with specific panel
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar panel_alarm

# Start with specific date/time
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar august 6 2025 10 30 0 pm

# Start with specific date/time in military time
java -jar dist/{version}/clock-{version}-jar-with-dependencies.jar august 6 2025 10 30 0 pm true

# Generate javadocs
mvn javadoc:jar
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
* Reset Laps: This will reset the laps for the current stopwatch, removing all laps and resetting the list.
* Reset All Laps: This will reset the laps for all stopwatches, removing all laps and resetting the lists.
* Rest Panel: This will reset the stopwatches panel, removing all stopwatches and resetting the list.

The Help Menu displays instructions on how to use the application and its features. It builds the text based on the value
set on the menu item's setName method. When you change the panel or a setting, the help menu will adjust according to what
is currently visible to the user.

Running The Tests:<br>
The application is tested using JUnit 5 and Mockito. Some tests have popups that requires you to close them.
Without the tests will 'pause' until that popup is closed.

```
# History

v3.1.0:
Updated all objects to utilize the ScheduledExecutorService defined in the ClockFrame class.
Now, the Clock, Alarm, Timer and Stopwatch classes have been updated to use this scheduling service.
Added an option to allow 'true' to be added to enforce military time on a clock instead of the hour.
Added the ability to edit a Timer. Now, if you Pause a running Timer, you can click a new Edit button
which will update the text fields with this Timer's values and you can update the values accordingly, 
and then click Set. This will start that Timer with those new/selected values. You can also 'toss out' 
that Timer by not clicking Set, effectively deleting it without clicking Remove.
Added the date to the Analogue clock panel. It is displayed above the time, similar to the digital
clock panel. There is currently only one supported view, like JUNE 13, 2026. The setting, 'Show/Hide
Digital Time' is still supported and will support show/hiding the date as well. The setting has been
renamed to 'Show/Hide Date and Time'.
Added some new menu options to the Stopwatch panel to reset the current stopwatch's laps, reset all stopwatches' laps,
and reset the panel which removes all stopwatches.
Added a new Help menu that displays instructions on what each setting or feature does. This has been set up
such that as long as the existing pattern for adding a new setting or feature is followed, the help text generated will
automatically update to include the new setting or feature.
Removed code that is not used minus getters and setters. Also added more javadocs to these methods.

v3.0.4:
Added github instructions for better utilization of Copilot and other new AI tools. 

v3.0.3:
Generated this new version after cleaning up the pom.properties, and xml files. 

v3.0.2:
Updated the pom to exclude the openapi-generator-maven-plugin from the build process, as it is not necessary for this 
project. 

v3.0.1:
Update arguments parsing when starting the application on the command line.
Now, either provide nothing for default start, a panel for a specific start,
or a date and time for a specific clock start.
Fixed a bug where the specific clock start using military time was not
incrementing the day after midnight.
Now, if you provide invalid arguments, the application will fail to start the
clock instead starting up with a default clock. 

v3.0:
Updated POM to use the parent pom more effectively.

v2.9:
Updated to use my parent pom. Multiple timers. Multiple ways to start the application.
Java was upgraded to 21. Multiple stopwatches were added.

v2.8:
Added Javadocs and cleaned up the code quite a bit. When running the application, you
can set this envVar: logLevel to be DEBUG, or INFO depending on how much info you want.

v2.7:
You can now set particular timezones which will update the clocks time.
Specific panel's have their own settings and are now only visible when in that particular
panel. We also upgraded Java from 11 to 18.

v2.6:
You can now view the time in Analogue mode. Click Features --> View Analogue Clock 
(or click Ctrl + C). This takes the current time of the Clock instance and displays
it in Analogue mode. This feature has its own setting which lets you decide if you
still want the digital time displayed. Date is not supported. 

Sorry, no history was recorded before v2.6. It is assumed that as this point, the project
was only the clock in digital clock mode, maybe some settings, and that is it.