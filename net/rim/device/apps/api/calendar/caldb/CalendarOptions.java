package net.rim.device.apps.api.calendar.caldb;

import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.apps.api.calendar.ota.CICALConfiguration;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.apps.api.pim.TimeBasedCollection;
import net.rim.device.apps.api.service.ServiceIdentifier;
import net.rim.device.internal.i18n.InternalCalendarUtilities;
import net.rim.vm.PersistentInteger;

public final class CalendarOptions extends OptionsBase {
   private CalendarOptions$PersistedCalendarOptions _persistedCalendarOptions;
   private static final long CALENDAR_OPTIONS_SYNC_ITEM = 7371696380813287106L;
   private static final long PERSISTED_CALENDAR_OPTIONS = -8395920205769737713L;
   public static final long USER_CHANGED_OTA_CALENDAR_SETTING = 1103480146496078488L;
   public static final int DAY_VIEW = 0;
   public static final int WEEK_VIEW = 1;
   public static final int MONTH_VIEW = 2;
   public static final int AGENDA_VIEW = 3;
   public static final int LAST_VIEW = 4;
   public static int FIRST_DOW = 1;
   public static int START_OF_DAY = 2;
   public static int END_OF_DAY = 4;
   public static int OTA_ENABLED = 8;
   public static int SPELL_CHECK_BEFORE_COMMIT = 16;
   public static final long[] REMINDER_CHOICES = new long[]{
      -1L,
      0L,
      300000L,
      600000L,
      900000L,
      1800000L,
      2700000L,
      3600000L,
      7200000L,
      10800000L,
      14400000L,
      18000000L,
      21600000L,
      25200000L,
      28800000L,
      32400000L,
      36000000L,
      39600000L,
      43200000L,
      86400000L,
      172800000L,
      259200000L,
      345600000L,
      432000000L,
      518400000L,
      604800000L,
      7297109497210929408L,
      1991719971541780L,
      -1432310690566785791L,
      -1842975799976059069L,
      -5155376422031325696L,
      -8834652431036963956L,
      5000765295280110510L,
      -7032476345477554835L,
      2252678796114433035L,
      816871668621328585L,
      8376737122711240802L,
      36667013055514624L,
      8389650066648802824L,
      227440577466689129L,
      2884556495105957737L,
      1956822836116742153L,
      513721691243808545L,
      2956378993764511300L,
      -6958219271834435343L,
      463674058234825325L,
      4954748422864308224L,
      8293692527507370853L,
      5603266769205659648L,
      8102047232255837985L,
      576510910670095053L,
      8376985580022428225L,
      8376985579673752436L,
      4685995907895137140L,
      8404462807450612852L,
      5078197002815211520L,
      8522433653454694311L,
      4685996478809273226L,
      4685995906536526788L,
      576587836427031492L,
      8320788731639169858L,
      4758053467579570117L,
      -2035272954459298654L,
      5641951731326787L,
      4668296865190593032L,
      -5880838808332606337L,
      6730182954920996364L,
      1171789125878499010L,
      7278533073794394470L,
      277923971171813376L,
      -3954368246161644183L,
      4830111636130310976L,
      7590406462552197407L,
      4668296861503720409L,
      576535836492590918L,
      5265846316500066115L,
      5999015839763267652L,
      -3954368246161665013L,
      576587836427031360L,
      8088827036242091843L,
      4830111636130311024L,
      9106948411986331935L,
      -1981794078462638080L,
      -1981794078462638080L,
      -6827235898987904909L,
      7829016234787276800L,
      4668296861503762031L,
      4702682988150813631L,
      54798222426653516L,
      816079800669651720L,
      5983919751978567188L,
      4702964463127499787L,
      7494930013002238249L,
      913741476042902355L,
      -2780596653435947922L,
      32522163504807947L,
      -4365568793720896760L,
      8376737609673869312L,
      7236798038856173568L,
      1174812202927130624L,
      8458154160297607207L,
      816067190041310316L,
      8458154160297607188L,
      -1981794078462610324L,
      7815259820784879616L,
      7296966092676354932L,
      1444339865003319654L,
      38973589156665670L,
      8323290261959164936L,
      4974226624578062336L,
      4631222155383290732L,
      4928185135341594988L,
      8325532197348442242L,
      7262858560579897344L,
      -8906893633869315924L,
      -9060006125595262849L,
      2385545002777643008L,
      32521862890651655L,
      2566017643403691272L,
      576603982823171752L,
      576594206641163334L,
      -8544380732923638714L,
      8027781179126600811L,
      6081280366755210348L,
      -8617230640533388034L,
      5294290825864677376L,
      9107440939705594692L,
      2398175597859989057L,
      -120458695472480668L,
      7296533314813822976L,
      7215127277221998438L,
      -120457617433345410L,
      35862166804891648L,
      -3954367807972554744L,
      64577930101027648L,
      8102047680632802056L,
      -4418518921369510033L,
      576638216330545152L,
      -8553452795044735668L,
      5478629785999658859L,
      28477686301115017L,
      5104634498928561672L,
      7083889539237047584L,
      576694295501194090L,
      7113854749733455L,
      3341559306276616L,
      4274487794359357192L,
      576630920674403841L,
      5910975060243462994L,
      19220929114739083L,
      71587153679438344L,
      28477686503133960L,
      5983032745248117512L,
      5288191854827759883L,
      -5115118224928145340L,
      576533676687390979L,
      7587832523719142995L,
      7821468679777852100L,
      964742641033912L,
      2529727110490444552L,
      576674806025122645L,
      6134777239749227859L,
      8742340101361896307L,
      5983032360315741038L,
      576571993271529081L,
      2049994721046985043L,
      35856648175290368L,
      6055090538312192776L,
      7914605788493344889L,
      4920486106750527849L,
      2029269027575302144L,
      38973585934934262L,
      -712649108240313336L,
      55004590924973664L,
      5547964184320168968L,
      816275594349143824L,
      7494930954717757460L,
      7282420645836095700L,
      5288651320416536576L,
      576535834053312580L,
      816107512572505685L,
      825096649591507220L,
      6199205498387431665L,
      8314220326208692747L,
      4719317665231276032L,
      6199205384264970352L,
      4668296863489878426L,
      -4221168739803463565L,
      8021895033616011341L,
      913814043824818094L,
      -8860831745270478994L,
      -8860831834988392274L,
      -7779967920603835218L,
      8400348002977079305L,
      4701979147082100235L,
      32477423150593123L,
      2711175775824418056L,
      7929904932183633487L,
      248664933947408391L,
      -6843219112208467687L,
      49099158380347507L,
      47578069100701704L,
      -4217232630909586936L,
      8264677064073110871L,
      7306342476135089179L,
      7267604768200609643L,
      576671892903264836L,
      8460796985688745162L
   };
   private static final int DEFAULT_REMINDER_INDEX = 4;
   public static final short[] KEEP_APPOINTMENTS_DURATION_CHOICES = new short[]{15, 30, 60, 90, -1, 51, 0, 0, 8192, -12284};
   private static final int DEFAULT_KEEP_APPOINTMENTS_DURATION_INDEX = 2;
   private static final int FOREVER_KEEP_APPOINTMENTS_DURATION_INDEX = 4;
   private static final int FREE_TIME_BLOCK_THRESHOLD = 15;
   private static final int DEFAULT_CALENDAR_COLOR = 255;
   private static CalendarOptions _options;
   private static final long APPOINTMENT_COLOR_MODE = 623602917881886043L;
   private static final long PRIVATE_APPOINTMENTS_COLOUR = -2060260901660362511L;
   private static final int PRIVATE_APPOINTMENTS_COLOUR_ID = PersistentInteger.getId(-2060260901660362511L, -1);

   private CalendarOptions() {
   }

   public static final CalendarOptions getOptions() {
      if (_options == null) {
         _options = new CalendarOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-8395920205769737713L);
      synchronized (persistentObject) {
         this._persistedCalendarOptions = (CICALConfiguration)persistentObject.getContents();
         if (this._persistedCalendarOptions == null) {
            this._persistedCalendarOptions = new CICALConfiguration();
            persistentObject.setContents(this._persistedCalendarOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(7371696380813287106L);
         if (syncItem == null) {
            syncItem = new CalendarOptions$CalendarOptionsSyncItem();
            ar.put(7371696380813287106L, syncItem);
         }

         return syncItem;
      }
   }

   public final boolean getConfirmDelete() {
      return this._persistedCalendarOptions._confirmDelete;
   }

   public final void setConfirmDelete(boolean confirmDelete) {
      if (confirmDelete != this._persistedCalendarOptions._confirmDelete) {
         this._persistedCalendarOptions._confirmDelete = confirmDelete;
      }
   }

   public final long getReminderMillis() {
      return this._persistedCalendarOptions._reminderMillis;
   }

   public final void setReminderMillis(long reminderMillis) {
      boolean reminderValid = false;

      for (int i = 0; i < REMINDER_CHOICES.length; i++) {
         if (reminderMillis == REMINDER_CHOICES[i]) {
            reminderValid = true;
            break;
         }
      }

      if (!reminderValid) {
         reminderMillis = REMINDER_CHOICES[4];
      }

      if (reminderMillis != this._persistedCalendarOptions._reminderMillis) {
         this._persistedCalendarOptions._reminderMillis = reminderMillis;
      }
   }

   public final long getSnoozeMillis() {
      return this._persistedCalendarOptions._snoozeMillis;
   }

   public final void setSnoozeMillis(long snoozeMillis) {
      if (snoozeMillis != this._persistedCalendarOptions._snoozeMillis) {
         this._persistedCalendarOptions._snoozeMillis = snoozeMillis;
      }
   }

   public final int getDayStart() {
      return this._persistedCalendarOptions._dayStartMillis;
   }

   public final int getDayEnd() {
      return this._persistedCalendarOptions._dayEndMillis;
   }

   public final void setDayStartAndEnd(int startMillis, int endMillis) {
      int changedOptions = 0;
      if (startMillis != this._persistedCalendarOptions._dayStartMillis) {
         this._persistedCalendarOptions._dayStartMillis = startMillis;
         changedOptions |= START_OF_DAY;
      }

      if (endMillis != this._persistedCalendarOptions._dayEndMillis) {
         this._persistedCalendarOptions._dayEndMillis = endMillis;
         changedOptions |= END_OF_DAY;
      }

      if (changedOptions != 0) {
         this.fireOptionsChanged(changedOptions);
      }
   }

   public final int getFirstDayOfWeekOption() {
      return this._persistedCalendarOptions._firstDayOfWeek;
   }

   public final int getFirstDayOfWeek() {
      int fdow = this.getFirstDayOfWeekOption();
      return fdow == 0 ? InternalCalendarUtilities.getFirstDayOfWeek() : fdow;
   }

   public final void setFirstDayOfWeek(int firstDOW) {
      if (firstDOW != this._persistedCalendarOptions._firstDayOfWeek) {
         if (firstDOW >= 1 && firstDOW <= 7) {
            this._persistedCalendarOptions._firstDayOfWeek = firstDOW;
         } else {
            this._persistedCalendarOptions._firstDayOfWeek = InternalCalendarUtilities.getFirstDayOfWeek();
         }

         this.fireOptionsChanged(FIRST_DOW);
      }
   }

   public final int getInitialView() {
      return this._persistedCalendarOptions._initialView;
   }

   public final void setInitialView(int initialView) {
      if (initialView != this._persistedCalendarOptions._initialView) {
         this._persistedCalendarOptions._initialView = initialView;
      }
   }

   public final boolean isQuickInputTriggeredByKeystrokes() {
      return this._persistedCalendarOptions._keystrokesTriggerQuickInput;
   }

   public final void setQuickInputTriggeredByKeystrokes(boolean keystrokesTriggerQuickInput) {
      if (keystrokesTriggerQuickInput != this._persistedCalendarOptions._keystrokesTriggerQuickInput) {
         this._persistedCalendarOptions._keystrokesTriggerQuickInput = keystrokesTriggerQuickInput;
      }
   }

   public final void setSpellCheckCommit(boolean doSpellCheck) {
      this._persistedCalendarOptions._doSpellCheck = doSpellCheck;
   }

   public final boolean getSpellCheckCommit() {
      return this._persistedCalendarOptions._doSpellCheck;
   }

   public final short getKeepAppointmentsDuration() {
      return this._persistedCalendarOptions._keepAppointmentsDuration;
   }

   public final boolean setKeepAppointmentsDuration(short keepAppointmentsDuration) {
      for (int i = KEEP_APPOINTMENTS_DURATION_CHOICES.length - 1; i >= 0; i--) {
         if (keepAppointmentsDuration == KEEP_APPOINTMENTS_DURATION_CHOICES[i]) {
            this._persistedCalendarOptions._keepAppointmentsDuration = keepAppointmentsDuration;
            return true;
         }
      }

      return false;
   }

   public final boolean isTasksIntegratedIntoViews() {
      return this._persistedCalendarOptions._tasksInView;
   }

   public final void integrateTasksIntoViews(boolean integrate) {
      boolean oldSetting = this._persistedCalendarOptions._tasksInView;
      this._persistedCalendarOptions._tasksInView = integrate;
      TimeBasedCollection.getInstance().activateProvider(-5718599435502913979L, integrate);
      if (oldSetting != integrate) {
         RIMGlobalMessagePoster.postGlobalEvent(5483692278053761660L, 0, 0, null, null);
      }
   }

   public final void setUseLegacyAgendaView(boolean useLegacyAgendaView) {
      this._persistedCalendarOptions._useLegacyAgendaView = useLegacyAgendaView;
   }

   public final boolean useLegacyAgendaView() {
      return this._persistedCalendarOptions._useLegacyAgendaView;
   }

   public final void setShowEndTime(boolean showEndTime) {
      this._persistedCalendarOptions._showEndTime = showEndTime;
   }

   public final boolean showEndTime() {
      return this._persistedCalendarOptions._showEndTime;
   }

   public final void setFreeTimeBlockThreshhold(int timeToShowFreeBlocks) {
      this._persistedCalendarOptions._timeToShowFreeBlocks = this.validateFreeTimeBlockThreshold(timeToShowFreeBlocks);
   }

   public final int getFreeTimeBlockThreshhold() {
      return this.validateFreeTimeBlockThreshold(this._persistedCalendarOptions._timeToShowFreeBlocks);
   }

   private final int validateFreeTimeBlockThreshold(int timeToShowFreeBlocks) {
      return timeToShowFreeBlocks >= 5 && timeToShowFreeBlocks <= 60 ? timeToShowFreeBlocks : 15;
   }

   private final Hashtable getCalendarPropertyTable() {
      return this._persistedCalendarOptions._calendarPropertyTable;
   }

   private final CalendarOptions$CalendarPropertyValue getCalendarValue(CalendarKey key) {
      Hashtable table = this._persistedCalendarOptions._calendarPropertyTable;
      CalendarOptions$CalendarPropertyValue value = (CalendarService)table.get(key);
      if (value == null) {
         value = new CalendarService();
         value._calendarServiceID = key.getCalendarServiceID();
         value._calendarFolderID = key.getCalendarFolderID();
      }

      return value;
   }

   public final void setAllowWirelessSync(long calendarServiceID, boolean allow, boolean broadcastChange) {
      if (this.isAllowWirelessSync(calendarServiceID) != allow) {
         CalendarKey key = new ServiceIdentifier(calendarServiceID, 0);
         CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
         value._allowWirelessSync = allow;
         this._persistedCalendarOptions._calendarPropertyTable.put(key, value);
         if (allow) {
            EventLogger.logEvent(-256469206327664059L, 1465077582, 0);
         } else {
            EventLogger.logEvent(-256469206327664059L, 1465077574, 0);
         }

         if (broadcastChange) {
            CalendarKey[] keys = new ServiceIdentifier[]{key};
            RIMGlobalMessagePoster.postGlobalEvent(1103480146496078488L, allow ? 1 : 0, 0, keys, null);
         }
      }
   }

   public final boolean isAllowWirelessSync(long calendarServiceID) {
      CalendarKey key = new ServiceIdentifier(calendarServiceID, 0);
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      return value._allowWirelessSync;
   }

   public final void setCalendarColour(CalendarKey key, int colour) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      value._calendarColour = colour;
      this._persistedCalendarOptions._calendarPropertyTable.put(key, value);
   }

   public final int getCalendarColour(CalendarKey key) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      return value._calendarColour;
   }

   public final void setDisplayReminders(CalendarKey key, boolean display) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      value._displayReminders = display;
      this._persistedCalendarOptions._calendarPropertyTable.put(key, value);
   }

   public final boolean isDisplayReminders(CalendarKey key) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      return value._displayReminders;
   }

   public final void setShowAppointments(CalendarKey key, boolean show) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      value._showAppointments = show;
      this._persistedCalendarOptions._calendarPropertyTable.put(key, value);
   }

   public final boolean isShowAppointments(CalendarKey key) {
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      return value._showAppointments;
   }

   public final void resetCalendarServiceFilter() {
      Hashtable table = this._persistedCalendarOptions._calendarPropertyTable;
      Enumeration i = table.elements();

      while (i.hasMoreElements()) {
         CalendarOptions$CalendarPropertyValue value = (CalendarService)i.nextElement();
         value._showAppointments = true;
      }
   }

   public final CalendarService getDefaultServiceForNewEvent() {
      CalendarKey calendarKey = null;
      CalendarService defaultCalendarService = (CalendarService)CalendarServiceManager.getInstance().getDefaultCalendarService();
      CalendarKey defaultCalendarKey = new ServiceIdentifier(defaultCalendarService.getUniqueServiceID(), defaultCalendarService.getPrimaryCalendarFolderID());
      CalendarKey[] visibleCalendarKeys = getOptions().getVisibleCalendars();
      Persistable var6;
      if (visibleCalendarKeys.length == 1) {
         var6 = visibleCalendarKeys[0];
      } else {
         var6 = defaultCalendarKey;
      }

      CalendarService calendarService = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(var6.getCalendarServiceID());
      if (calendarService == null || calendarService.isSystemDefault()) {
         calendarService = defaultCalendarService;
      }

      return calendarService;
   }

   public final void initializeCalendarFolder(long serviceID, long folderID) {
      CalendarKey key = new ServiceIdentifier(serviceID, folderID);
      CalendarOptions$CalendarPropertyValue value = this.getCalendarValue(key);
      Hashtable propertyTable = this.getCalendarPropertyTable();
      propertyTable.put(key, value);
   }

   public final CalendarKey[] getVisibleCalendars() {
      return this.getCalendars(true);
   }

   public final CalendarKey[] getHiddenCalendars() {
      return this.getCalendars(false);
   }

   private final CalendarKey[] getCalendars(boolean isVisible) {
      CalendarKey[] calendars = new ServiceIdentifier[0];
      Hashtable propertyTable = this.getCalendarPropertyTable();
      Enumeration e = propertyTable.keys();

      while (e.hasMoreElements()) {
         CalendarKey key = (ServiceIdentifier)e.nextElement();
         CalendarService service = (CalendarService)CalendarServiceManager.getInstance().findCalendarService(key.getCalendarServiceID());
         if (key.getCalendarFolderID() != 0 && service != null && this.isShowAppointments(key) == isVisible && !Arrays.contains(calendars, key)) {
            Arrays.add(calendars, key);
         }
      }

      return calendars;
   }

   public final int getAppointmentColorMode() {
      int id = PersistentInteger.getId(623602917881886043L, 6);
      return PersistentInteger.get(id);
   }

   public final void setAppointmentColorMode(int val) {
      int id = PersistentInteger.getId(623602917881886043L, 0);
      PersistentInteger.set(id, val);
   }

   public final int getPrivateAppointmentsColour() {
      return PersistentInteger.get(PRIVATE_APPOINTMENTS_COLOUR_ID);
   }

   public final void setPrivateAppointmentsColour(int colour) {
      PersistentInteger.set(PRIVATE_APPOINTMENTS_COLOUR_ID, colour);
   }

   @Override
   public final void commit() {
      throw new RuntimeException("cod2jar: tail call (jumpspecial)");
   }
}
