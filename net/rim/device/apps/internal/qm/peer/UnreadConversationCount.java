package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.api.ribbon.indicators.UnreadCountManager;

final class UnreadConversationCount {
   Object _newest;

   final synchronized void decrement() {
      if (UnreadCountManager.getUnreadCount(10) != 0) {
         UnreadCountManager.decrementUnreadCount(10);
         PeerEntry.getInstance().update();
      }
   }

   final synchronized void increment(Object object) {
      PeerEntry.getInstance().setNew();
      UnreadCountManager.incrementUnreadCount(10);
      PeerEntry.getInstance().update();
      this._newest = object;
   }

   final synchronized void set(int newCount) {
      int count = UnreadCountManager.getUnreadCount(10);
      UnreadCountManager.modifyUnreadCount(10, newCount - count);
      PeerEntry.getInstance().update();
   }
}
