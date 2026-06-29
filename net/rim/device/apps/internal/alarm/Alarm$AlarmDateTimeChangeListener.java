package net.rim.device.apps.internal.alarm;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.ui.component.Status;

final class Alarm$AlarmDateTimeChangeListener implements GlobalEventListener {
   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L || guid == 3596208183088439728L) {
         AlarmManager alarmManager = AlarmManager.getInstance();
         if (alarmManager != null && alarmManager.getAlarmState() == 2) {
            Status.show(Alarm._rb.getString(38), Bitmap.getPredefinedBitmap(0), 6000, 33554432, true, false, 50);
         }

         Alarm.setAlarm(false);
      }
   }
}
