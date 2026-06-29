package net.rim.device.apps.internal.alarm;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.profiles.TuneChoiceField;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.ribbon.RibbonBanner;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.profiles.AlertConsequence;
import net.rim.device.apps.internal.profiles.AlertEngine;
import net.rim.device.cldc.util.CalendarExtensions;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.AlertPlayer;

final class Alarm extends UiApplication implements FieldChangeListener {
   private Alarm$AlarmScreen _mainScreen;
   private ObjectChoiceField _enabledField;
   private DateField _alarmTime;
   private ObjectChoiceField _snoozeField;
   private ObjectChoiceField _alertTypeField;
   private TuneChoiceField _tuneField;
   private ObjectChoiceField _volumeField;
   private ObjectChoiceField _numberOfVibratesField;
   private AlertEngine _alertEngine;
   private static final long ALARM_APP_DESCRIPTOR_ID = 27450565375971827L;
   private static final long ADJUSTMENT_TIME_AMOUNT = 30000L;
   private static ResourceBundle _rb = ResourceBundle.getBundle(7243473093845420851L, "net.rim.device.apps.internal.resource.Alarm");
   private static ResourceBundle _rbProfiles = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
   private static final long DATE_TIME_OPTIONS_SCREEN_KEY = 6173420044896290124L;
   private static Calendar _calendar = Calendar.getInstance();
   private static CalendarExtensions _calendarEx = (CalendarExtensions)_calendar;
   private static final String[] _alarmFiringArgs = new String[]{"alarm"};
   private static final int ENABLED = 0;
   private static final int TIME = 1;
   private static final int SNOOZE = 2;
   public static final int ALERT_SETTING = 4;
   public static final int VOLUME_SETTING = 5;
   public static final int NUMBER_OF_VIBRATES_SETTING = 6;
   public static final int ALERT_FIELD_INDEX = 4;
   public static final int TUNE_FIELD_INDEX = 5;
   public static final int VOLUME_FIELD_INDEX = 6;
   public static final int NUMBER_OF_VIBRATES_FIELD_INDEX = 7;
   private static final int ESCALATING_VOLUME_INDEX = 3;
   public static final int TYPE_TONE = 1;
   public static final int TYPE_VIBRATE = 2;
   public static final int TYPE_VIBRATE_THEN_TONE = 4;
   public static final int[] _alertValues = new int[]{1, 2, 4, -805044223, 2, -805044219, 1718183726, 10, -804651007, 51, -805044157, 944130375};
   public static final int[] _volumeValues = AlertConsequence.getVolumeLevels(0);
   private static final int[] _vibratesValues = new int[]{1, 2, 3, -804651005, 1, 2, 4, -805044223, 2, -805044219, 1718183726, 10};
   private static final int _lowVolume = AlertConsequence.getLowVolume(0);
   private static final int _highVolume = AlertConsequence.getHighVolume(0);
   private static final short[] BLACKBERRY_DISCREET = new short[]{
      900, 100, 0, 25, 900, 100, 0, 25, 900, 100, 2, -12284, 150, 256, 28488, 7020, 11, 30209, 27503, 19557
   };
   private static final long[] SNOOZE_MAP = new long[]{
      0L,
      60000L,
      300000L,
      600000L,
      3490316290L,
      -3457638579553959935L,
      8388357178271989760L,
      7800521530630471689L,
      7172705L,
      3329295923628212235L,
      14691885598649907L,
      7300923238178357273L,
      5269326340353516915L,
      7957695015391404142L,
      199253838880L,
      7308826527758811183L,
      7306015542957190772L,
      8102082581536926070L,
      7958535042260086387L,
      8462109104396987489L,
      8241987820271788914L,
      -3457638605323763603L,
      -3457637913833995520L,
      4055009086419279616L,
      36028917279908153L,
      2449958192994582528L,
      3170535237180523769L,
      7881419606982656L,
      -2253020754566970880L,
      -5645428890258891104L,
      -9111478820781170868L,
      5654780920932891063L
   };
   private static Alarm$AlarmDateTimeChangeListener _dateTimeListener;

   public static final void main(String[] args) {
      if (args != null && args.length > 0) {
         if (args[0].equals("init")) {
            ApplicationRegistry.getApplicationRegistry().put(27450565375971827L, ApplicationDescriptor.currentApplicationDescriptor());
            AlarmOptions.getOptions().enableSynchronization();
            AlarmManager.init();
            setAlarm(false);
            _dateTimeListener = new Alarm$AlarmDateTimeChangeListener();
            Proxy.getInstance().addGlobalEventListener(_dateTimeListener);
            EventLogger.register(27450565375971827L, "alarm", 2);
            return;
         }

         if (args[0].equals(_alarmFiringArgs[0])) {
            long now = System.currentTimeMillis();
            long savedAlarmTime = AlarmManager.getInstance().getSavedAlarmTime();
            boolean isStale = now - savedAlarmTime > 60000;
            if (isStale) {
               EventLogger.logEvent(27450565375971827L, "stale alarm".getBytes(), 3);
               setAlarm(false);
               return;
            }

            Alarm$AlarmTrigger alarmTrigger = new Alarm$AlarmTrigger(null);
            alarmTrigger.enterEventDispatcher();
            return;
         }
      } else {
         new Alarm().enterEventDispatcher();
      }
   }

   private Alarm() {
      AlarmOptions options = AlarmOptions.getOptions();
      this._mainScreen = new Alarm$AlarmScreen(this);
      this._mainScreen.getDelegate().getField(0).setTag(null);
      RibbonBanner ribbonBar = RibbonBanner.getInstance();
      Field statusBanner = ribbonBar.getStatusBanner(null, 1);
      this._mainScreen.setTitle(statusBanner);
      statusBanner.getManager().setTag(null);
      String[] onOffChoices = new Object[]{CommonResources.getString(107), CommonResources.getString(106), _rb.getString(40)};
      int alarmSelection = 0;
      if (options.getAlarmEnabled()) {
         if (!options.getActiveWeekends()) {
            alarmSelection = 2;
         } else {
            alarmSelection = 1;
         }
      }

      this._enabledField = (ObjectChoiceField)(new Object(_rb.getString(35), onOffChoices, alarmSelection, 134217728));
      this._enabledField.setChangeListener(this._mainScreen);
      this._mainScreen.add(this._enabledField);
      this._alarmTime = (DateField)(new Object(_rb.getString(34), 0, 32));
      this._alarmTime.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      this._alarmTime.setDate(options.getAlarmTime());
      this._alarmTime.setChangeListener(this._mainScreen);
      this._mainScreen.add(this._alarmTime);
      String[] snoozeChoices = new Object[]{CommonResources.getString(107), _rb.getString(7), _rb.getString(8), _rb.getString(9)};
      this._snoozeField = (ObjectChoiceField)(new Object(_rb.getString(10), snoozeChoices, options.getSnooze(), 134217728));
      this._snoozeField.setChangeListener(this._mainScreen);
      this._mainScreen.add(this._snoozeField);
      this._mainScreen.add((Field)(new Object()));
      this._alertTypeField = (ObjectChoiceField)(new Object(_rb.getString(37), _rb.getStringArray(33), options.getAlertType()));
      this._alertTypeField.setChangeListener(this);
      this._alertTypeField.setChangeListener(new Alarm$CascadeFieldChangeListener(this._alertTypeField, this._mainScreen));
      this._mainScreen.add(this._alertTypeField);
      TuneManager tuneManager = null;
      tuneManager = TuneManager.getTuneManager();
      this._tuneField = tuneManager.getTuneChoiceField(_rbProfiles.getString(310), options.getTuneName(), tuneManager.getBuiltInTuneFileName(0), false);
      this._tuneField.setVolumeIndex(options.getVolume() + 1);
      this._tuneField.setChangeListener(new Alarm$CascadeFieldChangeListener(this._tuneField, this._mainScreen));
      this._mainScreen.add(this._tuneField);
      this._volumeField = (ObjectChoiceField)(new Object(_rbProfiles.getString(320), _rb.getStringArray(36), options.getVolume(), 134217728));
      this._volumeField.setChangeListener(this);
      this._volumeField.setChangeListener(new Alarm$CascadeFieldChangeListener(this._volumeField, this._mainScreen));
      this._mainScreen.add(this._volumeField);
      this._numberOfVibratesField = (ObjectChoiceField)(new Object(
         _rbProfiles.getString(350), _rbProfiles.getStringArray(351), options.getNumberOfVibrates(), 134217728
      ));
      this._numberOfVibratesField.setChangeListener(this._mainScreen);
      int alertType = _alertValues[options.getAlertType()];
      if (alertType == 2 || alertType == 4) {
         this._mainScreen.add(this._numberOfVibratesField);
      }

      this.addGlobalEventListener(new Alarm$1(this));
      this._alertEngine = AlertEngine.getInstance();
      this.pushScreen(this._mainScreen);
   }

   @Override
   public final void fieldChanged(Field aField, int context) {
      ChoiceField originalField = (ChoiceField)aField.getOriginal();
      Manager manager = originalField.getManager();
      int fieldIndex = manager.getFieldWithFocusIndex();
      if (fieldIndex == 4 || fieldIndex == 6) {
         ChoiceField fieldWithChanges = (ChoiceField)aField;
         int alertIndex = fieldIndex == 4 ? fieldWithChanges.getSelectedIndex() : 0;
         int volumeIndex = 0;
         int startVolume = _lowVolume;
         int vibratesIndex = 0;
         AlertPlayer tune = null;
         String tuneName = this._tuneField.getSelectedTuneName();
         if (TuneManager.isTuneManagerAvailable()) {
            TuneManager tuneManager = TuneManager.getTuneManager();
            tune = tuneManager.getTune(tuneName);
         }

         if (fieldIndex == 6) {
            volumeIndex = fieldWithChanges.getSelectedIndex();
            this._tuneField.setVolumeIndex(volumeIndex + 1);
         } else {
            volumeIndex = ((ChoiceField)manager.getField(6)).getSelectedIndex();
         }

         int endVolume;
         if (volumeIndex == 3) {
            startVolume = _lowVolume;
            endVolume = _highVolume;
         } else {
            startVolume = _volumeValues[volumeIndex + 1];
            endVolume = startVolume;
         }

         if (this._numberOfVibratesField.isVisible()) {
            vibratesIndex = _vibratesValues[((ChoiceField)manager.getField(7)).getSelectedIndex()];
         } else {
            vibratesIndex = _vibratesValues[AlarmOptions.getOptions().getNumberOfVibrates()];
         }

         if ((context & 2) == 0 && tune != null) {
            this._alertEngine.startNewAlertLater(_alertValues[alertIndex], tune, 1, vibratesIndex, startVolume, endVolume, 0, -1, 1, -1, -1);
         }

         if (fieldIndex == 4) {
            if (_alertValues[fieldWithChanges.getSelectedIndex()] == 1) {
               if (this._numberOfVibratesField.isVisible()) {
                  this._mainScreen.delete(this._numberOfVibratesField);
                  return;
               }
            } else if (!this._numberOfVibratesField.isVisible()) {
               this._mainScreen.add(this._numberOfVibratesField);
            }
         }
      }
   }

   static final void setAlarm(boolean displayUI) {
      AlarmOptions options = AlarmOptions.getOptions();
      ApplicationManager.getApplicationManager().scheduleApplication(getDescriptor(), -1, true);
      if (!options.getAlarmEnabled()) {
         AlarmManager.getInstance().setSavedAlarmTime(0);
         AlarmManager.setAlarmState(0);
      } else {
         long timeToSchedule = getNextAlarmTime(options.getAlarmTime());
         AlarmManager.getInstance().setSavedAlarmTime(timeToSchedule);
         ApplicationManager.getApplicationManager().scheduleApplication(getDescriptor(), timeToSchedule, true);
         AlarmManager.setAlarmState(1);
         if (displayUI) {
            long currentTime = System.currentTimeMillis();
            long diff = timeToSchedule - currentTime;
            if (diff > 86340000 && diff < 86430000) {
               ResourceBundle rb = ResourceBundle.getBundle(7243473093845420851L, "net.rim.device.apps.internal.resource.Alarm");
               DateFormat dateFormatter = DateFormat.getInstance(63);
               String[] data = new Object[]{dateFormatter.format(_calendar, (StringBuffer)(new Object()), null).toString()};
               String fireNextAt = MessageFormat.format(rb.getString(39), data);

               try {
                  Dialog.alert(fireNextAt);
                  return;
               } finally {
                  return;
               }
            }
         }
      }
   }

   private static final long getNextAlarmTime(int sinceMidnight) {
      _calendar.setTimeZone(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      _calendarEx.setTimeLong(sinceMidnight);
      int hour = _calendar.get(11);
      int minute = _calendar.get(12);
      long currentTime = System.currentTimeMillis() + 30000;
      _calendar.setTimeZone(TimeZone.getDefault());
      _calendarEx.setTimeLong(currentTime);
      _calendar.set(11, hour);
      _calendar.set(12, minute);
      _calendar.set(13, 0);
      _calendar.set(14, 0);
      long nextAlarmTime = _calendarEx.getTimeLong();
      if (nextAlarmTime < currentTime) {
         _calendar.set(5, _calendar.get(5) + 1);
         nextAlarmTime = _calendarEx.getTimeLong();
      }

      if (!AlarmOptions.getOptions().getActiveWeekends()) {
         _calendarEx.setTimeLong(nextAlarmTime);

         for (int day = _calendar.get(7); day == 7 || day == 1; day = _calendar.get(7)) {
            _calendar.set(5, _calendar.get(5) + 1);
         }

         nextAlarmTime = _calendarEx.getTimeLong();
      }

      return nextAlarmTime;
   }

   private static final ApplicationDescriptor getDescriptor() {
      ApplicationDescriptor alarmApp = (ApplicationDescriptor)ApplicationRegistry.getApplicationRegistry().get(27450565375971827L);
      alarmApp = (ApplicationDescriptor)(new Object(alarmApp, _alarmFiringArgs));
      alarmApp.setPowerOnBehavior(3);
      return alarmApp;
   }
}
