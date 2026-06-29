package net.rim.device.apps.internal.alarm;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DeviceInfo;

final class Alarm$AlarmScreen$1 implements Runnable {
   private final Alarm$AlarmScreen this$1;

   Alarm$AlarmScreen$1(Alarm$AlarmScreen _1) {
      this.this$1 = _1;
   }

   @Override
   public final void run() {
      AlarmOptions options = AlarmOptions.getOptions();
      synchronized (options) {
         long idleTime = DeviceInfo.getIdleTime();
         if (idleTime < 30) {
            Application.getApplication().invokeLater(this, (30 - idleTime) * 1000 + 250, false);
         } else {
            options.commit();
            this.this$1._backgroundCommitRunnable = null;
         }
      }
   }
}
