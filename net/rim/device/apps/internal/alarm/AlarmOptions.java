package net.rim.device.apps.internal.alarm;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.apps.api.options.OptionsBase;

final class AlarmOptions extends OptionsBase {
   private AlarmOptions$PersistedAlarmOptions _persistedAlarmOptions = (AlarmOptions$PersistedAlarmOptions)this.getPersistentObject().getContents();
   private static final long ALARM_OPTIONS_SYNC_ITEM;
   private static final long PERSISTED_ALARM_OPTIONS;
   private static AlarmOptions _options;

   private AlarmOptions() {
   }

   public static final AlarmOptions getOptions() {
      if (_options == null) {
         _options = new AlarmOptions();
      }

      return _options;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(-1560511511382083433L);
      synchronized (persistentObject) {
         Object persistedAlarmOptions = persistentObject.getContents();
         if (persistedAlarmOptions == null) {
            persistedAlarmOptions = new AlarmOptions$PersistedAlarmOptions();
            persistentObject.setContents(persistedAlarmOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(5627370694302260787L);
         if (syncItem == null) {
            syncItem = new AlarmOptions$AlarmOptionsSyncItem();
            ar.put(5627370694302260787L, syncItem);
         }

         return syncItem;
      }
   }

   final boolean getAlarmEnabled() {
      return this._persistedAlarmOptions._alarmEnabled;
   }

   final void setAlarmEnabled(boolean isEnabled) {
      this._persistedAlarmOptions._alarmEnabled = isEnabled;
   }

   final boolean getActiveWeekends() {
      return this._persistedAlarmOptions._activeWeekends;
   }

   final void setActiveWeekends(boolean isActive) {
      this._persistedAlarmOptions._activeWeekends = isActive;
   }

   final byte getSnooze() {
      return this._persistedAlarmOptions._snooze;
   }

   final void setSnooze(byte snoozeIndex) {
      this._persistedAlarmOptions._snooze = snoozeIndex;
   }

   final byte getAlertType() {
      return this._persistedAlarmOptions._alertType;
   }

   final void setAlertType(byte alertIndex) {
      this._persistedAlarmOptions._alertType = alertIndex;
   }

   final byte getVolume() {
      return this._persistedAlarmOptions._volume;
   }

   final void setVolume(byte volumeIndex) {
      this._persistedAlarmOptions._volume = volumeIndex;
   }

   final byte getNumberOfVibrates() {
      return this._persistedAlarmOptions._numberOfVibrates;
   }

   final void setNumberOfVibrates(byte vibrateIndex) {
      this._persistedAlarmOptions._numberOfVibrates = vibrateIndex;
   }

   final int getAlarmTime() {
      return this._persistedAlarmOptions._alarmTime;
   }

   final void setAlarmTime(int alarmTime) {
      this._persistedAlarmOptions._alarmTime = alarmTime;
   }

   final String getTuneName() {
      return this._persistedAlarmOptions._tuneName;
   }

   final void setTuneName(String newTune) {
      if (newTune != null) {
         this._persistedAlarmOptions._tuneName = newTune;
      }
   }
}
