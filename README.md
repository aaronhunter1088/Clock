# clock.v4

This is a simple GUI project which displays the time and allows the user to set alarms. 

In v2, You can show the time in military time or not and expand to see the full date or not.

In v2.1, you can now show the partial date.

In v3, you can now set alarms. Click Features --> View Alarms --> Set Alarms (or click Ctrl + A).
Here you can enter the Hour, Minutes, and Time (AM/PM) to set an alarm.
When the clock's time matches the alarm's time, the alarm will sound off. The only way to turn
the alarm off it to navigate through the Alarms menu, and click the alarm that is sounding off.
This stops the alarm sound, removes the alarm from the menu, and the text area, and brings it 
up in the panel. Here you can then update the time and click Set, simply click Set, or go back
to the ClockFace. Returning to the clock face without resetting the alarm deletes the alarm.

In v4, you can now set a Timer. Click Features --> View Timer (or click Ctrl + T).
Here you can enter the Hours, Minutes, and Seconds of the Timer.
When the Timer is finished, it will reset to the default values. You can go back to the main
Clock view if so desired. When the Timer is finished, it will change to the Timer panel to indicate
it has finished.

Coming Soon: v5

Enhancement:
Using the new Time API
Enhancement:
Restructure the code to be as concise as possible and remove any deprecated methods
