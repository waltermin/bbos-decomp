package net.rim.device.internal.synchronization.ota.session;

import java.util.Vector;
import net.rim.device.internal.synchronization.ota.util.Event;

class TriggerSyncEvent extends Event {
   private SessionManager _sessionManager;
   private boolean _progressiveSync;

   TriggerSyncEvent(SessionManager aSessionManager, boolean aProgressiveSync) {
      this._sessionManager = aSessionManager;
      this._progressiveSync = aProgressiveSync;
   }

   @Override
   public void onExecute() {
      this._sessionManager.triggerSync0(this._progressiveSync);
   }

   @Override
   public boolean onBeforeAddingEvent(Vector queue) {
      if (queue.isEmpty()) {
         return true;
      }

      Object anObject = queue.lastElement();
      if (this == anObject) {
         return false;
      }

      if (anObject instanceof TriggerSyncEvent) {
         TriggerSyncEvent xTriggerSyncEvent = (TriggerSyncEvent)anObject;
         if (this._progressiveSync == xTriggerSyncEvent._progressiveSync || !this._progressiveSync && xTriggerSyncEvent._progressiveSync) {
            return false;
         }
      }

      return true;
   }
}
