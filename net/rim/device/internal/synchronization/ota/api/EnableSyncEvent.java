package net.rim.device.internal.synchronization.ota.api;

import java.util.Vector;
import net.rim.device.internal.synchronization.ota.OTASyncDaemon;
import net.rim.device.internal.synchronization.ota.service.Configuration;
import net.rim.device.internal.synchronization.ota.service.ServicesConfigurationManager;
import net.rim.device.internal.synchronization.ota.session.SessionManager;
import net.rim.device.internal.synchronization.ota.util.Event;

class EnableSyncEvent extends Event {
   private boolean _enabled;
   private SyncAgent _syncAgent;

   EnableSyncEvent(boolean enabled, SyncAgent aSyncAgent) {
      this._enabled = enabled;
      this._syncAgent = aSyncAgent;
   }

   @Override
   public void onExecute() {
      long sid = this._syncAgent.getDefaultSid();
      SessionManager xSessionManager = SessionManager.getSessionManagerFor(sid);
      if (xSessionManager != null) {
         Configuration xConfiguration = ServicesConfigurationManager.getSingletonInstance().getConfiguration(sid);
         xConfiguration.setUserPreferenceToSync(this._enabled);
         xConfiguration.setUserEnabled(this._enabled);
         if (this._enabled) {
            xSessionManager.fetchConfiguration();
            xSessionManager.sendResume();
            return;
         }

         xSessionManager.abortAllSessions();
         SyncAgentConnections.closeConnections(sid, null, null);
         xSessionManager.sendSuspend();
         this._syncAgent.notifyListenersWith(2, OTASyncDaemon.getSingletonInstance().getServiceIdentifierForSid(sid));
      }
   }

   @Override
   public boolean onBeforeAddingEvent(Vector queue) {
      if (queue.isEmpty()) {
         return true;
      }

      int xLastElementIndex = queue.size() - 1;
      Object anObject = queue.elementAt(xLastElementIndex);
      if (this == anObject) {
         return false;
      }

      if (anObject instanceof EnableSyncEvent) {
         queue.removeElementAt(xLastElementIndex);
      }

      return true;
   }
}
