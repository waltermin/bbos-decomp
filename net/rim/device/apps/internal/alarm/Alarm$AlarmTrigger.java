package net.rim.device.apps.internal.alarm;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.theme.ThemeManager;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.internal.profiles.AlertConsequence;
import net.rim.device.apps.internal.profiles.AlertEngine;
import net.rim.device.internal.system.AlertPlayer;
import net.rim.device.internal.ui.UiSettings;

final class Alarm$AlarmTrigger
   extends UiApplication
   implements Runnable,
   DialogClosedListener,
   HolsterListener,
   TrackwheelListener,
   StylusListener,
   KeyListener,
   SystemListener {
   private boolean _alarmCompleted;
   private AlertEngine _alertEngineForTrigger;
   private AlertPlayer _tune;
   private int _startVolume;
   private int _endVolume;
   private long _alarmTriggerStartTime;
   private Dialog _dialog;
   private boolean _alarmInterrupted = false;
   private static final int ALARM_SILENCED_DEFAULT_DELAY = 70000;
   private static final long ALARM_MAX_IDLE_DURATION = 3600000L;

   private Alarm$AlarmTrigger() {
      AlarmOptions options = AlarmOptions.getOptions();
      this._alertEngineForTrigger = AlertEngine.getInstance();
      if (options.getVolume() == 3) {
         this._startVolume = Alarm._lowVolume;
         this._endVolume = Alarm._highVolume;
      } else {
         this._startVolume = Alarm._volumeValues[options.getVolume() + 1];
         this._endVolume = this._startVolume;
      }

      ResourceBundle rb = ResourceBundle.getBundle(7243473093845420851L, "net.rim.device.apps.internal.resource.Alarm");
      Calendar alarmTime = DateTimeUtilities.getNextDate(options.getAlarmTime());
      String title = MessageFormat.format(rb.getString(12), new String[]{DateFormat.getInstance(6).format(alarmTime)});
      String[] choices = null;
      int[] values = null;
      this.addSystemListener(this);
      this.addHolsterListener(this);
      this.addTrackwheelListener(this);
      this.addStylusListener(this);
      this.addKeyListener(this);
      int snoozeIndex = options.getSnooze();
      if (snoozeIndex != 0) {
         String snoozeTime = null;
         if (snoozeIndex == 1) {
            snoozeTime = rb.getString(7);
         } else if (snoozeIndex == 2) {
            snoozeTime = rb.getString(8);
         } else {
            snoozeTime = rb.getString(9);
         }

         choices = new String[]{MessageFormat.format(rb.getString(31), new String[]{snoozeTime}), rb.getString(32)};
         values = new int[]{0, 1, -805044216, 67108864, 1953066601, -805044215, 1816200448, 7172705};
      } else {
         choices = new String[]{rb.getString(32)};
         values = new int[]{0, -805044216, 0, 0};
      }

      EncodedImage image = ThemeManager.getActiveTheme().getImage("net_rim_alarm_reminder");
      this._dialog = new Dialog(title, choices, values, 0, null, 0);
      this._dialog.setIcon(image);
      this._dialog.setDialogClosedListener(this);
      this.pushGlobalScreen(this._dialog, -2147483645, 2);
      new Thread(this).start();
      this._alarmTriggerStartTime = System.currentTimeMillis();
   }

   private final void updateTune() {
      AlarmOptions options = AlarmOptions.getOptions();
      if (TuneManager.isTuneManagerAvailable()) {
         TuneManager tuneManager = TuneManager.getTuneManager();
         this._tune = tuneManager.getTune(options.getTuneName());
         if (this._tune == null) {
            this._tune = tuneManager.getTune(tuneManager.getBuiltInTuneFileName(0));
         }
      }
   }

   @Override
   public final void run() {
      AlarmOptions options = AlarmOptions.getOptions();
      int alertRetriggerInterval = 70000;
      int snoozeTime = (int)this.getSnoozeTime();
      if (snoozeTime == 0) {
         snoozeTime = 70000;
      }

      synchronized (this) {
         EventLogger.logEvent(27450565375971827L, ("Alarm triggered at " + System.currentTimeMillis()).getBytes(), 0);

         while (!this._alarmCompleted) {
            Backlight.enable(true, UiSettings.getBacklightTimeout());
            if (!Phone.isSupported() || !Phone.getInstance().isActive() || RadioInfo.getNetworkType() == 5) {
               this.updateTune();
               this._alarmInterrupted = false;
               this._alertEngineForTrigger
                  .startNewAlert(
                     Alarm._alertValues[options.getAlertType()],
                     this._tune,
                     1,
                     Alarm._vibratesValues[options.getNumberOfVibrates()],
                     this._startVolume,
                     this._endVolume,
                     2469778178827742799L,
                     1,
                     99,
                     500,
                     -1,
                     2
                  );

               label164:
               try {
                  boolean proceed = false;

                  do {
                     this.wait(3000);
                     if (!this._alarmCompleted) {
                        if (this._alarmInterrupted) {
                           proceed = true;
                           alertRetriggerInterval = snoozeTime;
                        } else if (System.currentTimeMillis() - this._alarmTriggerStartTime >= 3600000) {
                           this._alarmCompleted = true;
                           UiApplication.getUiApplication().invokeLater(new Alarm$AlarmTrigger$1(this));
                        } else if (!this._alertEngineForTrigger.isPlayingForSource(2469778178827742799L)) {
                           proceed = true;
                           alertRetriggerInterval = 70000;
                        }
                     } else {
                        proceed = true;
                     }
                  } while (!proceed);
               } finally {
                  break label164;
               }
            } else if (Alarm._alertValues[options.getAlertType()] > 1 || this._startVolume > 0) {
               Alert.startAudio(Alarm.BLACKBERRY_DISCREET, AlertConsequence.getLowVolume(1));
            }

            if (!this._alarmCompleted) {
               AlarmManager.getInstance().setSavedAlarmTime(System.currentTimeMillis() + alertRetriggerInterval);
               long elapsedTime = 0;

               for (long startTime = System.currentTimeMillis();
                  !this._alarmCompleted && elapsedTime < alertRetriggerInterval;
                  elapsedTime = System.currentTimeMillis() - startTime
               ) {
                  try {
                     this.wait(alertRetriggerInterval - elapsedTime);
                  } finally {
                     continue;
                  }
               }
            }
         }
      }
   }

   @Override
   protected final boolean acceptsForeground() {
      return false;
   }

   private final long getSnoozeTime() {
      int snooze = AlarmOptions.getOptions().getSnooze();
      return snooze >= 0 && snooze < Alarm.SNOOZE_MAP.length ? Alarm.SNOOZE_MAP[snooze] : 0;
   }

   @Override
   public final void powerOff() {
      ContextObject context = new ContextObject();
      context.setFlag(39);
      this._alarmCompleted = true;
      NotificationsManager.cancelImmediateEvent(2469778178827742799L, 0, null, context);
   }

   @Override
   public final void batteryGood() {
   }

   @Override
   public final void batteryLow() {
   }

   @Override
   public final void batteryStatusChange(int status) {
   }

   @Override
   public final void powerUp() {
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (ApplicationManager.getApplicationManager().isSystemLocked()) {
         Backlight.setTimeout(5);
      }

      ContextObject context = new ContextObject();
      context.setFlag(39);
      this._alertEngineForTrigger.stopAlert(2469778178827742799L);
      if (AlarmOptions.getOptions().getSnooze() != 0 && choice == 0) {
         AlarmManager.setAlarmState(2);
         ApplicationManager.getApplicationManager().scheduleApplication(Alarm.getDescriptor(), -1, true);
         ApplicationManager.getApplicationManager().scheduleApplication(Alarm.getDescriptor(), System.currentTimeMillis() + this.getSnoozeTime(), true);
         AlarmManager.getInstance().setSavedAlarmTime(System.currentTimeMillis() + this.getSnoozeTime());
      } else {
         Alarm.setAlarm(false);
         EventLogger.logEvent(27450565375971827L, ("Alarm dismissed at " + System.currentTimeMillis()).getBytes(), 0);
      }

      synchronized (this) {
         this._alarmCompleted = true;
         this.notifyAll();
      }

      System.exit(0);
   }

   @Override
   public final void inHolster() {
      this.silenceAlarm();
   }

   @Override
   public final void outOfHolster() {
      this.silenceAlarm();
   }

   @Override
   public final boolean trackwheelClick(int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean trackwheelUnclick(int status, int time) {
      return false;
   }

   @Override
   public final boolean trackwheelRoll(int amount, int status, int time) {
      return false;
   }

   @Override
   public final boolean stylusDown(int x, int y, int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean stylusUp(int x, int y, int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean stylusDrag(int x, int y, int status, int time) {
      return false;
   }

   @Override
   public final boolean stylusTap(int x, int y, int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean stylusDoubleTap(int x, int y, int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean stylusTapHold(int x, int y, int status, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean keyChar(char key, int time, int status) {
      return false;
   }

   @Override
   public final boolean keyDown(int keycode, int time) {
      this.silenceAlarm();
      return false;
   }

   @Override
   public final boolean keyUp(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyRepeat(int keycode, int time) {
      return false;
   }

   @Override
   public final boolean keyStatus(int keycode, int time) {
      this.silenceAlarm();
      return false;
   }

   private final void silenceAlarm() {
      if (!this._alarmInterrupted) {
         if (this._alertEngineForTrigger.isPlayingForSource(2469778178827742799L) && this._alertEngineForTrigger.isInProgress()) {
            this._alertEngineForTrigger.stopAlert(2469778178827742799L);
         }

         this._alarmInterrupted = true;
      }
   }

   Alarm$AlarmTrigger(Alarm$1 x0) {
      this();
   }
}
