package net.rim.device.apps.internal.ribbon.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.cldc.util.TimeService;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.timesync.TimeSync;
import net.rim.vm.PersistentInteger;
import net.rim.vm.Process;

public final class DateTimeFreshness implements Runnable, RealtimeClockListener, GlobalEventListener {
   private int _baseTimeId = PersistentInteger.getId(-1652972997206671730L, 0);
   private int _clockTickerId = PersistentInteger.getId(-7343770044348515223L, 0);
   private int _refMinutes = this.getReferenceMinutes();
   private int _elapsedMinutes = this.getElapsedMinutes();
   private boolean _gotDateEvent;
   private static final long RESET_DATE_UPDATED_FLAG = 6486915521057235885L;
   public static final long VALIDATE_DATE_TIME_GUID = 4169543157268029527L;
   private static final long BASE_TIME = -1652972997206671730L;
   private static final long CLOCK_TICKER = -7343770044348515223L;
   private static final int BITS_TO_STORE_ONE_MINUTE = 16;
   private static final long HALF_MINUTE = 32768L;
   private static final int ELAPSED_MINUTES_MAX = 32;
   private static final long DEFAULT_TIME = 1141171200000L;

   public final void update() {
      long curTime = System.currentTimeMillis();
      int curMinutes = (int)(curTime + 32768 >> 16);
      int elapsedMinutes = curMinutes - this._refMinutes;
      if (elapsedMinutes >= 0 && elapsedMinutes <= 32) {
         if (elapsedMinutes != this._elapsedMinutes) {
            this._elapsedMinutes = elapsedMinutes;
            this.saveElapsedMinutes(this._elapsedMinutes);
            return;
         }
      } else {
         this._refMinutes = curMinutes;
         this._elapsedMinutes = 0;
         this.saveReferenceMinutes(this._refMinutes);
         this.saveElapsedMinutes(this._elapsedMinutes);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6486915521057235885L) {
         TimeSync timeSync = TimeSync.getInstance();
         if (timeSync.isEnabled()) {
            timeSync.synchronize(false);
         }

         this._gotDateEvent = false;
      } else {
         if (guid == 8877632280522743328L) {
            this._gotDateEvent = true;
         }
      }
   }

   @Override
   public final void clockUpdated() {
      this.update();
   }

   @Override
   public final void run() {
      TimeService timeService = TimeService.getTimeService();
      Application.getApplication().removeGlobalEventListener(this);
      if (!timeService.isTimeZoneConfigured() || !this._gotDateEvent) {
         RIMGlobalMessagePoster.postGlobalEvent(4169543157268029527L);
      }
   }

   public DateTimeFreshness() {
      boolean dateTimeValid = true;
      ApplicationManager appManager = ApplicationManager.getApplicationManager();
      if (!appManager.wasDeviceTimeValidOnStartup() || !InternalServices.isDateTimeValid()) {
         dateTimeValid = false;
         if (this._refMinutes != 0) {
            DeviceInternal.setDateTime(this.getSavedTime() + 32768);
         } else {
            DeviceInternal.setDateTime(1141171200000L);
         }

         RIMGlobalMessagePoster.postGlobalEvent(((Process)Process.currentProcess()).getProcessId(), 6486915521057235885L, 0, 0, null, null);
      }

      if (dateTimeValid && System.currentTimeMillis() < 1141257600000L) {
         dateTimeValid = false;
      }

      if (!dateTimeValid) {
         TimeSync timeSync = TimeSync.getInstance();
         if (timeSync.isEnabled()) {
            Application.getApplication().addGlobalEventListener(this);
            timeSync.synchronize(false);
            Application.getApplication().invokeLater(this, 90000, false);
         } else {
            this.run();
         }
      }

      UiApplication.getUiApplication().addRealtimeClockListener(this);
   }

   private final long getSavedTime() {
      return (long)(this._refMinutes + this._elapsedMinutes) << 16;
   }

   private final void saveReferenceMinutes(int minutes) {
      PersistentInteger.set(this._baseTimeId, minutes);
   }

   private final int getReferenceMinutes() {
      return PersistentInteger.get(this._baseTimeId);
   }

   private final void saveElapsedMinutes(int mins) {
      if (mins < 32) {
         PersistentInteger.set(this._clockTickerId, -1 >>> mins);
      } else {
         PersistentInteger.set(this._clockTickerId, 0);
      }
   }

   private final int getElapsedMinutes() {
      int bits = PersistentInteger.get(this._clockTickerId);
      int minutes = 32;

      while (bits != 0) {
         minutes--;
         bits >>>= 1;
      }

      return minutes;
   }
}
