# Clock v2.9

This is a Java Swing GUI project which displays the date and time to the user in both
digital and analogue modes. It also has the ability to set multiple alarms however just
one timer.

The clock starts up in Digital Mode, displaying the current date like July 24, 2025 and
the current time below as 10:30:45 AM. The clock is updated every second.
You can switch between modes at any time by clicking on Features --> View Digital Clock,
View Analogue Clock, View Alarms or View Timers. View alarms is another menu option used
to set or update alarms.

Starting on the Digital Clock mode, you can change the following settings:
You can show the time in military time, or not, expand to show the full date, or not,
and show the partial date, which displays less of the day and month on the screen while
still displaying enough to the user to tell the date and time. You can turn on or off
daylight savings time, and finally, you can change the timezone to any USA timezone.

If you switch it to Analogue mode, the settings are slightly different. The digital time
is displayed on the clock and if you don't want it, you can turn it off in the settings. You
can also turn on or off the daylight savings time here and change the timezone as well. These
changes will persist while the clock is ticking. 

You can set multiple alarms. Click Features --> View Alarms --> Set Alarms (or click Ctrl + A).
Here you can enter the Hour, Minutes, and Time (AM/PM) to set an alarm. You can choose a
particular day or set of days, week days or weekends, or all days of the week for this alarm to
sound off.
When the clock's time matches any alarm's time, that alarm will sound off. The only way to turn
the alarm off it to navigate to it in the Alarms menu, and click the alarm that is sounding off.
This stops the alarm sound, removes the alarm from the menu, and the text area, and brings it
up in the panel. Here you can then update the time and click Set, simply click Set, or go back
to the clock panel of your choice. Returning to the clock panel without resetting the alarm 
deletes the alarm.

You can set a Timer. Click Features --> View Timer (or click Ctrl + T).
Here you can enter the Hours, Minutes, and Seconds of the Timer.
When the Timer is finished, a sound is triggered. It will play once and stop. Right now, only
one Timer can be created at a Time.

# History
v2.9 Updated to use my parent pom.

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