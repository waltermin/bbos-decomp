package net.rim.device.apps.internal.phone.data;

import java.util.Calendar;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.vm.Process;

public final class CallLogCollectionCleaner implements GlobalEventListener, Runnable {
   private Application _app;
   private int _id = -1;
   private long _snooze;
   private static final long CLEAN_AT = 0L;

   private CallLogCollectionCleaner(Application app) {
      this._app = app;
      this._app.addGlobalEventListener(this);
   }

   public static final void initialize(Application app) {
      new CallLogCollectionCleaner(app).resetTimer();
   }

   @Override
   public final synchronized void run() {
      this._id = -1;
      if (Process.ensureMinimumIdleTime(30) <= 0) {
         this._snooze += 60000;
      } else {
         long startTime = System.currentTimeMillis();
         if (this.purgeOldMessages()) {
            this._snooze = 0;
         } else {
            long elapsedTime = System.currentTimeMillis() - startTime;
            this._snooze += elapsedTime + 60000;
         }
      }

      this.resetTimer();
   }

   private final synchronized void resetTimer() {
      if (this._id != -1) {
         this._app.cancelInvokeLater(this._id);
      }

      long currentTime = System.currentTimeMillis();
      long invokeAt = toMidnight(currentTime) + 0 + this._snooze;
      long timeToWait = (invokeAt - currentTime) % 86400000;
      if (timeToWait <= 0) {
         timeToWait += 86400000;
      }

      this._id = this._app.invokeLater(this, timeToWait, false);
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L || guid == 3596208183088439728L) {
         this.resetTimer();
      }
   }

   private final boolean purgeOldMessages() {
      long keepMessagesDuration = 30;
      long threshold = System.currentTimeMillis() - keepMessagesDuration * 86400000;
      threshold = toMidnight(threshold);
      return PhoneFolders.purgeOldCallLogItems(threshold);
   }

   private static final long toMidnight(long time) {
      Calendar cal = Calendar.getInstance();
      ((CalendarExtensions)cal).setTimeLong(time);
      cal.set(14, 0);
      cal.set(13, 0);
      cal.set(12, 0);
      cal.set(11, 0);
      return ((CalendarExtensions)cal).getTimeLong();
   }
}
