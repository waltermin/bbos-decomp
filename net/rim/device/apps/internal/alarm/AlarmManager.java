package net.rim.device.apps.internal.alarm;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Factory;
import net.rim.device.apps.api.ribbon.FactoryRepository;
import net.rim.device.apps.api.ribbon.GlobalFactoryRepository;
import net.rim.device.apps.api.ribbon.indicators.TestPoint;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

final class AlarmManager implements Factory, TestPoint {
   private int _alarmState;
   private WeakReference[] _alarmStateListeners = new Object[0];
   private long _savedAlarmTime = 0;
   static final int STATE_NONE;
   static final int STATE_ALARM;
   static final int STATE_SNOOZE;
   private static final long GUID;

   final int getAlarmState() {
      return this._alarmState;
   }

   final void setSavedAlarmTime(long savedAlarmTime) {
      this._savedAlarmTime = savedAlarmTime;
   }

   final long getSavedAlarmTime() {
      return this._savedAlarmTime;
   }

   @Override
   public final Object createInstance(Object context) {
      AlarmStateField asf = new AlarmStateField(this);
      this.registerAlarmStateListener(asf);
      return asf;
   }

   @Override
   public final void test(Object id, Object value) {
      if (value instanceof Object) {
         setAlarmState(value);
      }
   }

   public static final AlarmManager getInstance() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      return ar != null ? (AlarmManager)ar.get(-7118281301835656932L) : null;
   }

   private final void notifyListeners() {
      WeakReference[] alarmStateListeners = this._alarmStateListeners;
      synchronized (alarmStateListeners) {
         int count = alarmStateListeners.length;

         for (int i = 0; i < count; i++) {
            AlarmStateField alarmStateListener = (AlarmStateField)alarmStateListeners[i].get();
            if (alarmStateListener != null) {
               alarmStateListener.dataChanged();
            }
         }
      }

      this.cleanupListeners();
   }

   private final void registerAlarmStateListener(AlarmStateField alarmStateListener) {
      synchronized (this._alarmStateListeners) {
         Array.resize(this._alarmStateListeners, this._alarmStateListeners.length + 1);
         this._alarmStateListeners[this._alarmStateListeners.length - 1] = (WeakReference)(new Object(alarmStateListener));
      }

      this.cleanupListeners();
   }

   static final void setAlarmState(int alarmState) {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      AlarmManager alarmManager = (AlarmManager)appReg.waitFor(-7118281301835656932L);
      alarmManager._alarmState = alarmState;
      alarmManager.notifyListeners();
   }

   static final void init() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      AlarmManager alarmManager = new AlarmManager();
      appReg.put(-7118281301835656932L, alarmManager);
      FactoryRepository repos = GlobalFactoryRepository.getFactoryRepository(-4018062520840731194L);
      repos.addFactory("AlarmIndicator", alarmManager);
   }

   private final void cleanupListeners() {
      WeakReference[] alarmStateListeners = this._alarmStateListeners;
      synchronized (alarmStateListeners) {
         int count = alarmStateListeners.length;

         for (int i = 0; i < count; i++) {
            Object alarmStateListener = alarmStateListeners[i].get();
            if (alarmStateListener == null) {
               System.arraycopy(alarmStateListeners, i + 1, alarmStateListeners, i, count - i - 1);
               count--;
            }
         }

         if (count != alarmStateListeners.length) {
            Array.resize(alarmStateListeners, count);
         }
      }
   }
}
