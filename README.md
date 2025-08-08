# Clock v2.9

This is a Java Swing GUI project which displays the date and time to the user in both
digital and analogue modes. It also has the ability to set multiple alarms and, with
this release, multiple timers. You can also start the clock specifying a panel or a
specific date and time. Following is the default command to run the clock:

```
# Both commands achieve the same result:
java -jar Clock-2.9-jar-with-dependencies.jar
or
java -jar Clock-2.9-jar-with-dependencies.jar 0

# Start the jar file with a specific panel:
java -jar Clock-2.9-jar-with-dependencies.jar 1,panel_alarm

# Start the jar file with a specific date and time:
java -jar Clock-2.9-jar-with-dependencies.jar 2,10,30,0,august,wednesday,6,2025,pm
```

The clock starts on the DigitalClock panel, displaying the current date like AUGUST 6, 2025
and the current time below as 10:30:45 AM.
By clicking on Features, you can change the mode of the clock. The modes are: 
* View Digital Clock or Ctrl + D
* View Analogue Clock or Ctrl + C
* View Alarms or Ctrl + A
* View Timers or Ctrl + T

On the DigitalClock panel, you can change the following settings:
* (Ctrl + M) Show Time in Military Time: This will display the time in 24-hour like 1030 hours 45
* (Ctrl + F) Expand Date: This will display the full date like FRIDAY AUGUST 6, 2025
* (Ctrl + P) Show Partial Date: This will display a shorter version of the date like FRI AUG 6, 2025
* (Shift + T) Turn On/Off Daylight Savings Time: This will turn on or off the daylight savings time
* Change Timezone: This will change the timezone to any USA timezone like EST, CST, MST, PST, or AST.

If you switch it to Analogue mode, the settings are slightly different. The digital time
is displayed on the clock and if you don't want it, you can turn it off in the settings.

On the AnalogueClock panel, you can change the following settings:
* (Ctrl + E) Show/Hide Digital Time: This will display the digital time on the Analogue clock
* (Shift + T) Turn On/Off Daylight Savings Time: This will turn on or off the daylight savings time
* Change Timezone: This will change the timezone to any USA timezone like EST, CST, MST, PST, or AST.

On the Alarms panel, you can set multiple alarms. Here you can enter the Name, Hour, Minutes, and Time 
(AM/PM) to set an alarm. You can choose a particular day or set of days, week days or weekends, 
or all days of the week for this alarm to sound off. Once created, the alarm will be sleeping and you
can either edit it or delete it. Once it is going off, it will sound off until you stop it. You can
choose to stop the alarm, which will stop the sound and reset it for the next time it goes off, or you
can snooze it, which will stop the sound temporarily for 8 minutes and then sound off again.
The Alarms panel has no specific settings.

On the Timers panel, you can set multiple timers. Here you can enter the Name, Hours, Minutes, 
and Seconds of a Timer. Once created, the timer will begin counting down from the set time. While
it is counting down, you can pause it, resume it, or remove it. Once it is going off, it will play 
a sound until you stop it. You can choose to reset the timer or stop the timer, which will also remove
it from the list of timers.

On the Timers panel, you can change the following settings:
* Pause/Resume All Timers: This will pause or resume all timers at once.

# History
v2.9 Updated to use my parent pom. Multiple timers. Multiple ways to start the application.
Java was upgraded to 21.

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