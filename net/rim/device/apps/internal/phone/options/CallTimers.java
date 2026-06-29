package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.apps.api.options.OptionsBase;

public final class CallTimers extends OptionsBase {
   private int[] _timers;
   private static final long PERSISTED_CALL_TIMERS = 4349576476285167631L;
   public static final int LAST_CALL_TIME = 0;
   public static final int TOTAL_CALL_TIME = 1;
   public static final int DC_LAST_CALL_TIME = 2;
   public static final int DC_TOTAL_CALL_TIME = 3;
   private static final int TIMER_COUNT = 4;
   private static CallTimers _instance;

   private CallTimers() {
   }

   public static final CallTimers getCallTimers() {
      if (_instance == null) {
         _instance = new CallTimers();
      }

      return _instance;
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(4349576476285167631L);
      synchronized (persistentObject) {
         this._timers = (int[])persistentObject.getContents();
         if (this._timers == null) {
            this._timers = new int[4];
            persistentObject.setContents(this._timers, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      return (SyncItem)ApplicationRegistry.getApplicationRegistry().waitFor(9065083732853317491L);
   }

   @Override
   public final void enableSynchronization() {
   }

   public final int getTimer(int index) {
      return this._timers[index];
   }

   public final void updateTimers(int setIndex, int addIndex, int time) {
      if (time > 0) {
         this._timers[setIndex] = time;
         this._timers[addIndex] = this._timers[addIndex] + time;
         this.commit();
      }
   }

   public final void resetTimer(int index) {
      this._timers[index] = 0;
      this.commit();
   }

   public final void resetTimers() {
      Arrays.fill(this._timers, 0);
      this.commit();
   }

   final int[] getTimers() {
      return this._timers;
   }

   final void setTimers(int[] timers) {
      Arrays.fill(this._timers, 0);
      if (timers != null) {
         int numTimers = timers.length;
         if (numTimers > 4) {
            numTimers = 4;
         }

         for (int i = 0; i < numTimers; i++) {
            this._timers[i] = timers[i];
         }
      }

      this.commit();
   }
}
