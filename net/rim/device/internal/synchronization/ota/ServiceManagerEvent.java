package net.rim.device.internal.synchronization.ota;

import java.util.Vector;
import net.rim.device.internal.synchronization.ota.api.Logger;
import net.rim.device.internal.synchronization.ota.util.Event;

class ServiceManagerEvent extends Event {
   private int _eventId;
   private OTASyncDaemon _otaSyncDaemon;
   public static final int LOAD_SERVICES_EVENT;

   public int getEventId() {
      return this._eventId;
   }

   ServiceManagerEvent(OTASyncDaemon otaSyncDaemon, int eventId) {
      this._eventId = eventId;
      this._otaSyncDaemon = otaSyncDaemon;
   }

   @Override
   public boolean onBeforeAddingEvent(Vector queue) {
      return true;
   }

   @Override
   public void onExecute() {
      try {
         this._otaSyncDaemon.onServiceManagerEvent(this);
         Logger.logSyncDaemonStarted();
      } finally {
         return;
      }
   }
}
