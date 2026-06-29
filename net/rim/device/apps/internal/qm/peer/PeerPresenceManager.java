package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.util.IntHashtable;

final class PeerPresenceManager extends ContactStatus implements RealtimeClockListener {
   long _lastTimePresenceStatusUpdated = -1;
   public static final long AVAILABILITY_SETTINGS_CHANGED_GUID = 5802930574313301195L;
   private static final long AVAILABLE_TIMEOUT = 1500000L;
   private static final long BUSY_TIME_OUT = 120000L;

   public final void dispatchStatus(PeerContact contactToNotify) {
      PeerSession session = PeerApplication.getSession();
      if (session != null) {
         session.setUserStatus(super._contactStatus & 32543, this.getCustomStatusMessage(), contactToNotify);
      }
   }

   final void dispatchStatus() {
      PeerSession session = PeerApplication.getSession();
      if (session != null) {
         session.setUserStatus(super._contactStatus & 32543, this.getCustomStatusMessage());
         this._lastTimePresenceStatusUpdated = System.currentTimeMillis();
      }
   }

   @Override
   public final void clockUpdated() {
   }

   @Override
   final void contactStatusChanged() {
      super.contactStatusChanged();
      PeerData.setUserStatus(super._persistentData);
      this.dispatchStatus();
   }

   PeerPresenceManager(IntHashtable userStatusData) {
      super(userStatusData);
      if (userStatusData == null) {
         this.setPresenceStatus(16384);
      }
   }

   @Override
   protected final void timer(boolean restart) {
   }

   @Override
   protected final void commit() {
   }
}
