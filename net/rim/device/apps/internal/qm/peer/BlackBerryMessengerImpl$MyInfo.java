package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;
import net.rim.blackberry.api.blackberrymessenger.Session;
import net.rim.blackberry.api.pdap.BlackBerryContact;

final class BlackBerryMessengerImpl$MyInfo implements MessengerContact {
   @Override
   public final int getContactId() {
      PeerApplication.getInstance();
      return PeerApplication.getSession().getMyContactId().hashCode();
   }

   @Override
   public final String getDisplayName() {
      PeerApplication.getInstance();
      return PeerApplication.getSession().getDisplayName();
   }

   @Override
   public final BlackBerryContact getBlackBerryContact() {
      return null;
   }

   @Override
   public final Session getSession() {
      return null;
   }
}
