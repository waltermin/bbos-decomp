package net.rim.device.apps.api.reminders;

import java.util.TimeZone;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.calendar.caldb.CalDB;
import net.rim.device.apps.api.calendar.caldb.CalendarServiceManager;
import net.rim.device.apps.api.calendar.modelcontrollerinterface.Event;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.api.ui.TimeChoiceField;
import net.rim.device.internal.proxy.Proxy;

public class ReminderManager implements GlobalEventListener {
   private PersistentObject _reminderLog = RIMPersistentStore.getPersistentObject(3627893931035587831L);
   protected ReminderRunnable _nextScheduledReminder;
   private boolean _systemLocked;
   private ReminderProvider[] _reminderProviders = new ReminderProvider[0];
   private boolean[] _reminderProviderEnabled = new boolean[0];
   private long[] _reminders = new long[0];
   private long _nextReminderID = Long.MAX_VALUE;
   private long _nextReminderTime = Long.MAX_VALUE;
   private long _nextReminderProviderID = Long.MAX_VALUE;
   protected TimeZone _tz = TimeZone.getDefault();
   private ReminderManager$ReminderScanner _reminderScanner;
   private ReminderManager$ReminderChangeNotificationThread _changeNotifier;
   private boolean _reminderScanningEnabled = true;
   private ContextObject _contextObject = (ContextObject)(new Object());
   static final long REMINDER_LOG = 3627893931035587831L;
   public static final long ID = -7539463754156969819L;
   private static final int ACTION_SCAN_ALL = 0;
   private static final int ACTION_ADD = 1;
   private static final int ACTION_REMOVE = 3;
   public static final int ERROR_REMINDER_DIALOG_FAILED_NULL = 1363562016;
   public static final int ERROR_REMINDER_DIALOG_FAILED_REMOVED = 1363561760;
   public static final int ERROR_REMINDER_DIALOG_QUEUE_FAILED_EXCEPTION = 1363559768;
   public static final int ERROR_REMINDER_ADD_TO_QUEUE_FAILED_1 = 1094993457;
   public static final int ERROR_REMINDER_ADD_TO_QUEUE_FAILED_2 = 1094993458;
   public static final int ERROR_REMINDER_REMOVED_BEFORE_PROCESSING = 1364218445;
   public static final int ERROR_REMINDER_DIALOG_PROCEED_EXCEPTION = 1347572302;
   public static final int ERROR_REMINDER_DIALOG_HIDE_EXCEPTION = 1212437070;
   public static final int ERROR_REMINDER_SCANNER_UHANDLED_EXCEPTION = 1381193029;
   public static final int DEBUG_REMINDER_DIALOG_FAILED_UPDATED = 1363563808;
   public static final int DEBUG_REMINDER_DIALOG_FAILED_DISMISSED = 1363563040;
   public static final int ERROR_REMINDER_EXTRAORDINARY_EXCEPTION = 1380273989;
   public static final int ERROR_IMMEDIATE_REMINDER_MISSING = 1230130509;
   public static final int ERROR_REMINDER_EVENT_LOCK_OUT_OF_ORDER = 1380273231;
   public static final int REMINDER_LOG_TIME_CHANGED = 1414090053;
   public static final int REMINDER_LOG_QUED_REMINDER = 1364542788;
   public static final int REMINDER_LOG_QUE_FAILED = 1364542790;
   public static final int REMINDER_LOG_REMINDER_PAST = 1346458452;
   public static final int REMINDER_LOG_NO_SUCH_REMINDER = 1313821261;
   public static final int REMINDER_LOG_REMINDER_ADDED = 1094992978;
   public static final int REMINDER_LOG_REMINDER_REMOVED = 1380273494;
   public static final int REMINDER_LOG_REMINDER_UPDATED = 1431323732;
   public static final int REMINDER_LOG_REMINDER_UPDATED_NEXT = 1431323726;
   public static final int REMINDER_LOG_REMINDER_IMMEDIATE = 1229800530;
   public static final int REMINDER_LOG_BAD_REMINDER = 1111573586;
   public static final int REMINDER_LOG_UNHANDLED_EXCEPTION = 1430607939;
   public static final int REMINDER_LOG_ALREADY_SCHEDULED = 1095975752;
   public static final int REMINDER_LOG_REMINDER_SCHEDULED = 1396918354;
   public static final int REMINDER_LOG_REMINDER_SCHEDULED_FAILED = 1396918342;
   public static final int REMINDER_LOG_REMINDER_DEEP_SCHEDULE_FAILED = 1145393222;
   public static final int REMINDER_LOG_DIALOG_STATUS_SCREEN_QUEUED = 1381192529;
   public static final int REMINDER_LOG_DIALOG_CLOSE_NOTIFY = 1380205390;
   public static final int REMINDER_LOG_DO_NOTIFICATION = 1380795471;
   public static final int REMINDER_LOG_FATAL_SCANNER_ERROR = 1178686540;
   public static final int REMINDER_LOG_SKIP_DUE_TO_CAL_OPTIONS = 1397442896;
   public static final int REMINDER_DIALOG_PRIORITY = 500;
   public static final long[] SNOOZE_CHOICES = new long[]{
      -1L,
      60000L,
      300000L,
      600000L,
      900000L,
      1800000L,
      7800949705523003648L,
      72185835958791049L,
      8377128769710008947L,
      72169106143838464L,
      3115244560543606L,
      5981906222247687425L,
      -1432310690569972725L,
      1444340379106739200L,
      7783909439788811776L,
      -6915840091691457278L,
      576535833751559783L,
      2884556060623243264L,
      7879935987840131355L,
      2884556201617216388L,
      1309148914905737176L,
      -1868383582041126426L,
      7594805134973283406L,
      8102047232259541374L,
      -1914793931828510003L,
      5315036393053947904L,
      8102047232267260003L,
      576510910670095053L,
      576587282230002753L,
      32533535643649089L,
      4830111143209091592L,
      1171789124622052880L,
      40043148332444278L,
      29400259244213000L,
      4830111096603689736L,
      7018408412169951164L,
      7378378365674482805L,
      8390047030745789793L,
      7575983769125480790L,
      4902168630071481441L,
      816123966658799205L,
      8766099007568609300L,
      4281873246391184462L,
      2324284517527221130L,
      -8486070029106781022L,
      -5889443451550149005L,
      32521862890651780L,
      4716573545935029512L
   };
   public static final int[] SNOOZE_CHOICE_STRINGS = new int[]{
      9061,
      9054,
      9056,
      9057,
      9058,
      9059,
      -805026930,
      1196314761,
      169478669,
      218103808,
      1380206665,
      -973078528,
      1207959552,
      1544,
      -1977504512,
      245,
      1497919497,
      184549491,
      184549395,
      -1711275757,
      6300,
      1130968577,
      1750093891,
      1936684143
   };
   public static final long EVENT_LOGGER_GUID = 3172337241449866705L;
   public static final String EVENT_LOGGER_NAME = "net.rim.reminders";

   public boolean scheduleNextReminder(long _1, ReminderRunnable _3) {
      throw null;
   }

   public long getAdjustedCurrentTimeMillis() {
      throw null;
   }

   public void cancelNextReminder() {
      throw null;
   }

   public void cancelAllReminders() {
      throw null;
   }

   public void init(Application app) {
      if (this._reminderLog.getContents() == null) {
         this._reminderLog.setContents(new ReminderRollingLog(100), 51);
         this._reminderLog.commit();
      }

      EventLogger.register(3172337241449866705L, "net.rim.reminders", 2);
      ContextObject.put(this._contextObject, -442409970680484936L, new Object(-7539463754156969819L));
      app.addGlobalEventListener(this);
      this._reminderScanner = new ReminderManager$ReminderScanner(this);
      this._changeNotifier = new ReminderManager$ReminderChangeNotificationThread();
      this._reminderScanner.start();
      this._changeNotifier.start();
   }

   public TimeChoiceField getSnoozeOptionsField(long snoozeMillis) {
      return (TimeChoiceField)(new Object(CommonResources.getString(9062), SNOOZE_CHOICES, snoozeMillis));
   }

   void setNewAlarm(Reminder reminder, ReminderProvider reminderProvider) {
      long reminderProviderID = reminderProvider.getReminderProviderID();
      long reminderID = reminder.getReminderID();
      long reminderTime = reminder.getReminderTime(this._tz);
      reminderTime /= 1000;
      int seconds = (int)(reminderTime % 60);
      reminderTime = (reminderTime - seconds) * 1000;
      this.cancelNextReminder();
      this._nextScheduledReminder = new ReminderRunnable(reminderID, reminderProviderID, this);
      if (this.scheduleNextReminder(reminderTime, this._nextScheduledReminder)) {
         this._nextReminderID = reminderID;
         this._nextReminderTime = reminderTime;
         this._nextReminderProviderID = reminderProviderID;
         this.logReminderEvent(1396918354, reminderProvider, reminder);
      } else {
         this.logReminderEvent(1396918342, reminderProvider, reminder);
         this._nextScheduledReminder = null;
         this._nextReminderID = -1;
         this._nextReminderTime = Long.MAX_VALUE;
         this._nextReminderProviderID = -1;
      }
   }

   Object getLock() {
      return this._reminderProviders;
   }

   void notifyReminderProvider(long reminderProviderID) {
      this._reminderScanner.notifyReminderProvider(reminderProviderID);
   }

   void doNotification(long reminderID, long reminderProviderID) {
      ReminderProvider reminderProvider = null;
      synchronized (this._reminderProviders) {
         if (reminderProviderID == this._nextReminderProviderID && this._nextReminderID == reminderID) {
            this._nextReminderID = Long.MAX_VALUE;
            this._nextReminderProviderID = Long.MAX_VALUE;
            this._nextReminderTime = Long.MAX_VALUE;
         }

         reminderProvider = this.findReminderProvider(reminderProviderID);
      }

      if (reminderProvider != null) {
         Reminder reminder = reminderProvider.getReminder(reminderID);
         if (reminder != null) {
            ReminderModel rm = reminder.getReminderData();
            if (rm != null && rm.hasReminder()) {
               CalDB calDB = (CalDB)CalendarServiceManager.getInstance().findCalendarDatabase(reminderID);
               if (calDB != null) {
                  Event event = (Event)calDB.get(reminderID);
                  if (event != null && !event.isDisplayReminder()) {
                     this.logReminderEvent(1397442896, reminderProvider, reminder);
                     reminder = reminderProvider.updateReminderState(reminderID, 3, reminder.getReminderTime(TimeZone.getDefault()));
                     return;
                  }
               }

               int state = reminder.getReminderState();
               this.logReminderEvent(1364542788, reminderProvider, reminder);
               if (state != 5 && state != 3) {
                  ReminderElement reminderElement = new ReminderElement(reminderID, reminderProvider);
                  reminder = reminderProvider.updateReminderState(reminderID, 5, reminder.getReminderTime(TimeZone.getDefault()));
                  this.logReminderEvent(1380795471, reminderProvider, reminder);
                  NotificationsManager.triggerImmediateEvent(reminderProvider.getProfileID(reminderID), reminderID, reminderElement, null);
                  NotificationsManager.negotiateDeferredEvent(reminderProvider.getProfileID(reminderID), reminderID, reminderElement, 0, 1, this._contextObject);
                  return;
               }

               this.logReminderEvent(1364542790, reminderProvider, reminder);
               return;
            }

            return;
         }

         this.logReminderEvent(1313821261, reminderProvider, null, -1, null);
         this.notifyReminderProvider(reminderProvider.getReminderProviderID());
      }
   }

   public void registerReminderProvider(ReminderProvider rmProvider) {
      Proxy proxy = Proxy.getInstance();
      proxy.invokeLater(new ReminderManager$RegsiterReminderProvider(this, rmProvider));
   }

   public void unregisterReminderProvider(ReminderProvider rmProvider) {
      Proxy proxy = Proxy.getInstance();
      proxy.invokeLater(new ReminderManager$UnregsiterReminderProvider(this, rmProvider));
   }

   public void addReminder(ReminderProvider reminderProvider, Reminder reminder) {
      int state = reminder.getReminderState();
      this.logReminderEvent(1094992978, reminderProvider, reminder);
      if (state != 5 && state != 6 && state != 2) {
         this._reminderScanner.notifyReminderProviderThreaded(reminderProvider.getReminderProviderID(), reminder, 1);
      }
   }

   public void removeReminder(ReminderProvider reminderProvider, Reminder reminder) {
      int state = reminder.getReminderState();
      this.logReminderEvent(1380273494, reminderProvider, reminder);
      if (state != 5 && state != 6 && state != 2) {
         this._reminderScanner.notifyReminderProviderThreaded(reminderProvider.getReminderProviderID(), reminder, 3);
      }
   }

   public void updateReminder(ReminderProvider reminderProvider, Reminder updatedReminder, Reminder oldReminder) {
      int state = updatedReminder.getReminderState();
      this.logReminderEvent(1431323732, reminderProvider, updatedReminder);
      if (state != 5 && state != 6 && state != 2) {
         if (oldReminder.getReminderID() == this._nextReminderID) {
            this.logReminderEvent(1431323726, reminderProvider, updatedReminder);
            this._reminderScanner.notifyReminderProviderThreaded(reminderProvider.getReminderProviderID(), updatedReminder, 0);
            return;
         }

         this._reminderScanner.notifyReminderProviderThreaded(reminderProvider.getReminderProviderID(), updatedReminder, 1);
      }
   }

   public void immediateReminder(ReminderProvider reminderProvider, Reminder reminder) {
      this.logReminderEvent(1229800530, reminderProvider, reminder, reminder.getReminderState(), null);
      Proxy proxy = Proxy.getInstance();
      proxy.invokeLater(new ReminderRunnable(reminder.getReminderID(), reminderProvider.getReminderProviderID(), this));
   }

   public ReminderProvider findReminderProvider(long reminderProviderID) {
      synchronized (this._reminderProviders) {
         for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
            if (this._reminderProviders[i].getReminderProviderID() == reminderProviderID) {
               return this._reminderProviders[i];
            }
         }

         return null;
      }
   }

   public ReminderProvider findActiveReminderProvider(long reminderProviderID) {
      synchronized (this._reminderProviders) {
         for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
            if (this._reminderProviders[i].getReminderProviderID() == reminderProviderID) {
               if (this._reminderProviderEnabled[i]) {
                  return this._reminderProviders[i];
               }

               return null;
            }
         }

         return null;
      }
   }

   public void enableAllReminders(boolean enable) {
      this._reminderScanningEnabled = enable;
      if (enable) {
         this._reminderScanner.notifyReminderProviderThreaded(Long.MAX_VALUE, null, 0);
      } else {
         this.cancelNextReminder();
         this._nextReminderID = Long.MAX_VALUE;
         this._nextReminderTime = Long.MAX_VALUE;
         this._nextReminderProviderID = Long.MAX_VALUE;
         this._nextScheduledReminder = null;
      }
   }

   public void enableReminders(long reminderProviderID, boolean enable) {
      synchronized (this._reminderProviders) {
         for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
            if (this._reminderProviders[i].getReminderProviderID() == reminderProviderID) {
               this._reminderProviderEnabled[i] = enable;
            }
         }
      }
   }

   public String[] getReminderLog() {
      ReminderRollingLog log = (ReminderRollingLog)this._reminderLog.getContents();
      return log.getEvents();
   }

   public void logReminderEvent(int code, ReminderProvider rp, Reminder reminder, long firedFor, String extraInfo) {
      ReminderRollingLog log = (ReminderRollingLog)this._reminderLog.getContents();
      long providerID = rp != null ? rp.getReminderProviderID() : -1;
      long reminderTime = reminder != null ? reminder.getReminderTime(this._tz) : -1;
      long reminderID = reminder != null ? reminder.getReminderID() : -1;
      int state = reminder != null ? reminder.getReminderState() : -1;
      String name = rp != null ? rp.getName() : "";
      log.addEvent(code, providerID, name, reminderTime, firedFor, reminderID, state, extraInfo);
      this._reminderLog.commit();
   }

   public void logReminderEvent(int code, ReminderProvider rp, Reminder reminder) {
      this.logReminderEvent(code, rp, reminder, -1, null);
   }

   public void logReminderEvent(int code) {
      this.logReminderEvent(code, null, null, -1, null);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 8877632280522743328L || guid == 3596208183088439728L) {
         this.logReminderEvent(1414090053);
         this.cancelAllReminders();
         synchronized (this._reminderProviders) {
            for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
               this._reminders[i] = Long.MAX_VALUE;
            }

            this._nextReminderID = Long.MAX_VALUE;
            this._nextReminderProviderID = Long.MAX_VALUE;
            this._nextReminderTime = Long.MAX_VALUE;
         }

         this._reminderScanner.notifyAllReminderProviders();
      } else if (guid != -8246942408779309615L && guid != -7131874474196788121L) {
         if (guid == 6946990846586544061L || guid == 6345609069135580235L) {
            if (guid == 6345609069135580235L) {
               this._systemLocked = false;
            }

            if (!this._systemLocked) {
               synchronized (this._reminderProviders) {
                  for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
                     this._reminderProviders[i].displayReminderIndicator(false);
                  }

                  return;
               }
            }
         }
      } else {
         if (guid == -7131874474196788121L) {
            this._systemLocked = true;
         }

         synchronized (this._reminderProviders) {
            for (int i = this._reminderProviders.length - 1; i >= 0; i--) {
               this._reminderProviders[i].displayReminderIndicator(true);
            }
         }
      }
   }

   public static ReminderManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return (ReminderManager)ar.get(-7539463754156969819L);
   }

   public static void logEvent(String text, int level) {
      if (text != null) {
         EventLogger.logEvent(3172337241449866705L, text.getBytes(), level);
      }
   }

   public static void logEvent(int code, int level) {
      EventLogger.logEvent(3172337241449866705L, code, level);
   }
}
