package net.rim.device.apps.internal.qm.peer;

final class PeerContactRecognizer {
   public static final int ALL_CONTACTS;
   public static final int SENDABLE_CONTACTS;
   public static final int PRESENCE_SUPPORT_CONTACTS;

   public static final boolean recognize(int id, PeerContact contact) {
      boolean result = false;
      switch (id) {
         case 0:
         default:
            return contact != null && !contact.isPending();
         case 1:
            return contact.isAvailable() && !contact.isPending();
         case 2:
            result = contact != null && contact.supportsPresence() && !contact.isPending() && contact.isAuthorized();
         case -1:
            return result;
      }
   }
}
