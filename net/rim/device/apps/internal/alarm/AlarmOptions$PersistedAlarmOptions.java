package net.rim.device.apps.internal.alarm;

import net.rim.device.api.system.Alert;
import net.rim.vm.Persistable;

final class AlarmOptions$PersistedAlarmOptions implements Persistable {
   boolean _alarmEnabled = false;
   boolean _activeWeekends = false;
   byte _snooze = 0;
   byte _alertType = 0;
   byte _volume = 1;
   byte _numberOfVibrates = 1;
   int _alarmTime = 0;
   String _tuneName;

   AlarmOptions$PersistedAlarmOptions() {
      if (Alert.isBuzzerSupported()) {
         this._tuneName = "BlackBerry 1";
      } else {
         this._tuneName = "Alarm_Antelope.mid";
      }
   }
}
