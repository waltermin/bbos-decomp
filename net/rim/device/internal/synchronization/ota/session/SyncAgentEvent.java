package net.rim.device.internal.synchronization.ota.session;

import java.util.Vector;
import net.rim.device.internal.synchronization.ota.api.SyncAgent;
import net.rim.device.internal.synchronization.ota.util.Event;

class SyncAgentEvent extends Event {
   private int _event;
   private Object _msg;

   SyncAgentEvent(int event, Object aMsg) {
      this._event = event;
      this._msg = aMsg;
   }

   @Override
   public boolean onBeforeAddingEvent(Vector queue) {
      return true;
   }

   @Override
   public void onExecute() {
      SyncAgent.getSingletonInstance().notifyListenersWith(this._event, this._msg);
   }
}
