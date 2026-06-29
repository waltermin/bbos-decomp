package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntHashtable;

final class WrongPasscodeNotification extends PeerRequest {
   WrongPasscodeNotification(String recipient, String message) {
      super(3, recipient, message, null);
   }

   WrongPasscodeNotification(IntHashtable initialData) {
      super(initialData);
   }

   @Override
   public final String getBody() {
      try {
         return PersistentContent.decodeString(super._body);
      } finally {
         ;
      }
   }

   @Override
   public final int getIconId() {
      return 7;
   }

   @Override
   public final boolean isRead() {
      return true;
   }

   @Override
   public final void accept() {
   }

   @Override
   public final void decline() {
   }
}
