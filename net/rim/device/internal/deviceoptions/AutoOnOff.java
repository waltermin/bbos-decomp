package net.rim.device.internal.deviceoptions;

import java.util.Calendar;
import java.util.Date;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.cldc.util.CalendarExtensions;

public final class AutoOnOff {
   public static final long GUID_AUTOONOFF_OPTIONS_CHANGED;
   public static final int IMMEDIATE_AUTO_OFF;
   public static final int AUTO_OFF_WHEN_IDLE;
   public static final int ABORT_AUTO_OFF;
   private static final long AUTOONOFF_DATA_KEY;
   private static PersistentObject _persistentAutoOnOffData = RIMPersistentStore.getPersistentObject(4271995000954929308L);
   private static AutoOnOff$AutoOnOffData _autoOnOffData;

   private AutoOnOff() {
   }

   public static final void init() {
   }

   private static final void commit(boolean notifyOfChanges) {
      _persistentAutoOnOffData.commit();
      if (notifyOfChanges) {
         RIMGlobalMessagePoster.postGlobalEvent(-2918606221006897090L);
      }

      setApplicationManagerPowerOnBehaviour();
   }

   private static final void setApplicationManagerPowerOnBehaviour() {
      ApplicationManager appmanager = ApplicationManager.getApplicationManager();
      if (!isWeekendAutoOnOffEnabled() && !isWeekdayAutoOnOffEnabled()) {
         appmanager.setCurrentPowerOnBehavior(1);
      } else {
         appmanager.setCurrentPowerOnBehavior(2);
      }
   }

   public static final int getWeekdayOnTime() {
      return _autoOnOffData._weekdayOn;
   }

   public static final void setWeekdayOnTime(int msPastMidnight) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekdayOn = msPastMidnight;
         commit(true);
      }
   }

   public static final int getWeekdayOffTime() {
      return _autoOnOffData._weekdayOff;
   }

   public static final void setWeekdayOffTime(int msPastMidnight) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekdayOff = msPastMidnight;
         commit(true);
      }
   }

   public static final boolean isWeekdayAutoOnOffEnabled() {
      return _autoOnOffData._weekdayAutoOnOffEnabled;
   }

   public static final void enableWeekdayAutoOnOff(boolean enable) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekdayAutoOnOffEnabled = enable;
         commit(true);
      }
   }

   public static final int getWeekendOnTime() {
      return _autoOnOffData._weekendOn;
   }

   public static final void setWeekendOnTime(int msPastMidnight) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekendOn = msPastMidnight;
         commit(true);
      }
   }

   public static final int getWeekendOffTime() {
      return _autoOnOffData._weekendOff;
   }

   public static final void setWeekendOffTime(int msPastMidnight) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekendOff = msPastMidnight;
         commit(true);
      }
   }

   public static final boolean isWeekendAutoOnOffEnabled() {
      return _autoOnOffData._weekendAutoOnOffEnabled;
   }

   public static final void enableWeekendAutoOnOff(boolean enable) {
      synchronized (_autoOnOffData) {
         _autoOnOffData._weekendAutoOnOffEnabled = enable;
         commit(true);
      }
   }

   public static final void resetToDefaults() {
      synchronized (_autoOnOffData) {
         _autoOnOffData.reset();
         commit(true);
      }
   }

   public static final long getNextAutoOnTime() {
      synchronized (_autoOnOffData) {
         long nextOnTime = Long.MIN_VALUE;
         if (_autoOnOffData._weekdayAutoOnOffEnabled) {
            Calendar nextWeekday = DateTimeUtilities.getNextDate(getWeekdayOnTime());

            while (DateTimeUtilities.isWeekend(nextWeekday)) {
               ((CalendarExtensions)nextWeekday).add(5, 1);
            }

            nextOnTime = ((CalendarExtensions)nextWeekday).getTimeLong();
         }

         if (_autoOnOffData._weekendAutoOnOffEnabled) {
            Calendar nextWeekend = DateTimeUtilities.getNextDate(getWeekendOnTime());

            while (!DateTimeUtilities.isWeekend(nextWeekend)) {
               ((CalendarExtensions)nextWeekend).add(5, 1);
            }

            if (nextOnTime == Long.MIN_VALUE || nextOnTime > ((CalendarExtensions)nextWeekend).getTimeLong()) {
               nextOnTime = ((CalendarExtensions)nextWeekend).getTimeLong();
            }
         }

         return nextOnTime;
      }
   }

   public static final long getNextAutoOffTime(boolean backward) {
      synchronized (_autoOnOffData) {
         Calendar autoOnCal = Calendar.getInstance();
         autoOnCal.setTime(new Date(getNextAutoOnTime()));
         long autoOnLong = ((CalendarExtensions)autoOnCal).getTimeLong();
         if (!_autoOnOffData._weekendAutoOnOffEnabled && !_autoOnOffData._weekdayAutoOnOffEnabled) {
            return Long.MIN_VALUE;
         }

         if (!backward) {
            return constructAutoOffTime(autoOnLong, DateTimeUtilities.isWeekend(autoOnLong) ? getWeekendOffTime() : getWeekdayOffTime());
         }

         long currentTime = System.currentTimeMillis();

         while (autoOnLong > currentTime) {
            autoOnLong -= 86400000;
            if (DateTimeUtilities.isWeekend(autoOnLong) && _autoOnOffData._weekendAutoOnOffEnabled) {
               autoOnCal.setTime(new Date(autoOnLong));
               DateTimeUtilities.zeroCalendarTime(autoOnCal);
               autoOnLong = ((CalendarExtensions)autoOnCal).getTimeLong() + getWeekendOnTime();
               return constructAutoOffTime(autoOnLong, getWeekendOffTime());
            }

            if (!DateTimeUtilities.isWeekend(autoOnLong) && _autoOnOffData._weekdayAutoOnOffEnabled) {
               autoOnCal.setTime(new Date(autoOnLong));
               DateTimeUtilities.zeroCalendarTime(autoOnCal);
               autoOnLong = ((CalendarExtensions)autoOnCal).getTimeLong() + getWeekdayOnTime();
               return constructAutoOffTime(autoOnLong, getWeekdayOffTime());
            }
         }

         return Long.MIN_VALUE;
      }
   }

   private static final long constructAutoOffTime(long autoOnTimeLong, int autoOffTime) {
      Calendar autoOnCal = Calendar.getInstance();
      autoOnCal.setTime(new Date(autoOnTimeLong));
      int offHoursSinceMidnight = autoOffTime / 3600000;
      int offMinutesSinceMidnight = (autoOffTime - offHoursSinceMidnight * 3600000) / 60000;
      autoOnCal.set(11, offHoursSinceMidnight);
      autoOnCal.set(12, offMinutesSinceMidnight);
      autoOnCal.set(13, 0);
      autoOnCal.set(14, 0);
      long autoOffLong = ((CalendarExtensions)autoOnCal).getTimeLong();
      if (autoOffLong < autoOnTimeLong) {
         autoOffLong += 86400000;
      }

      return autoOffLong < System.currentTimeMillis() ? Long.MIN_VALUE : autoOffLong;
   }

   public static final int determineAutoOffBehavior() {
      int behavior = 2;
      Calendar cal = Calendar.getInstance();
      int currentTime = cal.get(11) * 3600000 + cal.get(12) * 60000 + cal.get(13) * 1000 + cal.get(14);
      if (DateTimeUtilities.isWeekend(cal) && _autoOnOffData._weekendAutoOnOffEnabled) {
         if (!insideTimeFrame(_autoOnOffData._weekendOn, _autoOnOffData._weekendOff, currentTime, cal.get(7) == 7)) {
            behavior = 1;
            if (turnOffImmediately(_autoOnOffData._weekendOff, currentTime)) {
               behavior = 0;
            }
         }

         if (cal.get(7) == 7
            && behavior != 0
            && _autoOnOffData._weekdayAutoOnOffEnabled
            && _autoOnOffData._weekdayOn > _autoOnOffData._weekdayOff
            && behavior == 1
            && currentTime < _autoOnOffData._weekendOn) {
            if (insideTimeFrame(0, _autoOnOffData._weekdayOff, currentTime)) {
               return 2;
            }

            if (turnOffImmediately(_autoOnOffData._weekdayOff, currentTime)) {
               return 0;
            }
         }
      } else if (!DateTimeUtilities.isWeekend(cal) && _autoOnOffData._weekdayAutoOnOffEnabled) {
         if (!insideTimeFrame(_autoOnOffData._weekdayOn, _autoOnOffData._weekdayOff, currentTime, cal.get(7) == 2)) {
            behavior = 1;
            if (turnOffImmediately(_autoOnOffData._weekdayOff, currentTime)) {
               behavior = 0;
            }
         }

         if (cal.get(7) == 2
            && behavior != 0
            && _autoOnOffData._weekendAutoOnOffEnabled
            && _autoOnOffData._weekendOn > _autoOnOffData._weekendOff
            && behavior == 1
            && currentTime < _autoOnOffData._weekdayOn) {
            if (insideTimeFrame(0, _autoOnOffData._weekendOff, currentTime)) {
               return 2;
            }

            if (turnOffImmediately(_autoOnOffData._weekendOff, currentTime)) {
               behavior = 0;
            }
         }
      }

      return behavior;
   }

   private static final boolean insideTimeFrame(int startTime, int endTime, int currentTime) {
      return insideTimeFrame(startTime, endTime, currentTime, false);
   }

   private static final boolean insideTimeFrame(int startTime, int endTime, int currentTime, boolean isMondayOrSaturday) {
      boolean validTime;
      if (endTime < startTime) {
         validTime = !insideTimeFrame(endTime, startTime, currentTime);
         if (isMondayOrSaturday) {
            validTime = validTime && currentTime >= startTime;
         }
      } else {
         validTime = currentTime >= startTime - 60000 && currentTime <= endTime - 60000;
      }

      return validTime;
   }

   private static final boolean turnOffImmediately(int endTime, int currentTime) {
      int diffTime = currentTime - endTime;
      return diffTime >= 0 && diffTime <= 60000;
   }

   static {
      synchronized (_persistentAutoOnOffData) {
         _autoOnOffData = (AutoOnOff$AutoOnOffData)_persistentAutoOnOffData.getContents();
         if (_autoOnOffData == null) {
            _autoOnOffData = new AutoOnOff$AutoOnOffData();
            _persistentAutoOnOffData.setContents(_autoOnOffData, 51, false);
            commit(false);
         } else {
            setApplicationManagerPowerOnBehaviour();
         }
      }
   }
}
