package net.rim.device.apps.internal.alarm;

import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.menu.MenuItemPrefab;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.apps.api.framework.registration.VerbRepository;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.AppsMainScreen;
import net.rim.device.apps.api.ui.SystemEnabledMenu;
import net.rim.device.cldc.util.CalendarExtensions;

final class Alarm$AlarmScreen extends AppsMainScreen implements FieldChangeListener {
   private Verb _dateTimeVerb;
   private Runnable _backgroundCommitRunnable;
   private final Alarm this$0;
   private static final long COMMIT_IDLE_TIMEOUT = 30L;

   public Alarm$AlarmScreen(Alarm _1) {
      super(17594333724672L);
      this.this$0 = _1;
      this._dateTimeVerb = (Verb)ApplicationRegistry.getApplicationRegistry().get(6173420044896290124L);
      this.setHelp("alarm");
   }

   @Override
   protected final boolean onSavePrompt() {
      Status.show(Alarm._rb.getString(41), 500);
      return true;
   }

   @Override
   protected final void makeMenu(SystemEnabledMenu menu, int instance) {
      menu.add(this._dateTimeVerb);
      VerbRepository vr = VerbRepository.getVerbRepository(7343976597688173750L);
      if (vr != null) {
         menu.add(vr.getVerbs(null));
      }

      if (this.isMuddy()) {
         menu.setDefault(MenuItemPrefab.get(14));
      }
   }

   @Override
   public final void close() {
      AlarmOptions options = AlarmOptions.getOptions();
      options.commit();
      Alarm.setAlarm(true);
      super.close();
   }

   @Override
   public final void fieldChanged(Field field, int context) {
      AlarmOptions options = AlarmOptions.getOptions();
      boolean dataChanged = true;
      boolean alarmChanged = false;
      if (field == this.this$0._alarmTime) {
         Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
         ((CalendarExtensions)cal).setTimeLong(this.this$0._alarmTime.getDate());
         options.setAlarmTime(cal.get(11) * 3600000 + cal.get(12) * 60000);
         alarmChanged = true;
      } else if (field == this.this$0._enabledField) {
         int enableField = this.this$0._enabledField.getSelectedIndex();
         if (enableField == 0) {
            options.setAlarmEnabled(false);
            options.setActiveWeekends(false);
         } else if (enableField == 1) {
            options.setAlarmEnabled(true);
            options.setActiveWeekends(true);
         } else if (enableField == 2) {
            options.setAlarmEnabled(true);
            options.setActiveWeekends(false);
         }

         alarmChanged = true;
      } else if (field == this.this$0._snoozeField) {
         options.setSnooze((byte)this.this$0._snoozeField.getSelectedIndex());
      } else if (field == this.this$0._alertTypeField) {
         options.setAlertType((byte)this.this$0._alertTypeField.getSelectedIndex());
      } else if (field == this.this$0._tuneField) {
         options.setTuneName(this.this$0._tuneField.getSelectedTuneName());
      } else if (field == this.this$0._volumeField) {
         options.setVolume((byte)this.this$0._volumeField.getSelectedIndex());
      } else if (field == this.this$0._numberOfVibratesField) {
         options.setNumberOfVibrates((byte)this.this$0._numberOfVibratesField.getSelectedIndex());
      } else {
         dataChanged = false;
      }

      if (dataChanged) {
         this.doBackgroundCommit();
      }

      if (alarmChanged) {
         Alarm.setAlarm(false);
      }
   }

   private final void doBackgroundCommit() {
      synchronized (AlarmOptions.getOptions()) {
         if (this._backgroundCommitRunnable == null) {
            this._backgroundCommitRunnable = new Alarm$AlarmScreen$1(this);
            Application.getApplication().invokeLater(this._backgroundCommitRunnable, 30250, false);
         }
      }
   }
}
