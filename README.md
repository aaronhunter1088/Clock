# Clock v2.6

This is a simple GUI project which displays the time and allows the user to set alarms.

The clock starts up in Digital Mode. You can switch to this mode at any time by clicking
on Features --> View Digital Clock (or clicking Ctrl + D); This mode has 3 specific 
settings you can enable.
You can show the time in military time, or not, and expand to show the full date, or not.
You can also show the partial date, which displays less text on the screen while still
allowing the user to tell the time and date. These features are also present when viewing
the alarm and timer panels. Enabling a setting will still work but you must navigate back
to the clock to see the results.

You can set alarms. Click Features --> View Alarms --> Set Alarms (or click Ctrl + A).
Here you can enter the Hour, Minutes, and Time (AM/PM) to set an alarm.
When the clock's time matches any alarm's time, that alarm will sound off. The only way to turn
the alarm off it to navigate through the Alarms menu, and click the alarm that is sounding off.
This stops the alarm sound, removes the alarm from the menu, and the text area, and brings it 
up in the panel. Here you can then update the time and click Set, simply click Set, or go back
to the ClockPanel. Returning to the clock face without resetting the alarm deletes the alarm.

You can set a Timer. Click Features --> View Timer (or click Ctrl + T).
Here you can enter the Hours, Minutes, and Seconds of the Timer.
When the Timer is finished, a sound is triggered. It will play once and stop. Right now, only
one Timer can be created at a Time.

v2.6
You can now view the time in Analogue mode. Click Features --> View Analogue Clock 
(or click Ctrl + C). This takes the current time of the Clock instance and displays
it in Analogue mode. This feature has its own setting which lets you decide if you
still want the digital time displayed. Date is not supported. 