package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.IntHashtable;

final class NewContactRequest extends PeerRequest {
   private EmailInvitation _invite;

   NewContactRequest(String replyTo, String body, EmailInvitation invite) {
      super(2, replyTo, body, invite.getPersistentData());
      this._invite = invite;
   }

   NewContactRequest(IntHashtable initialData) {
      super(initialData);
      this._invite = EmailInvitation.makeInbound();
      this._invite.initialize((IntHashtable)initialData.get(7));
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
   public final void accept() {
      this._invite.acceptInvitation();
   }

   @Override
   public final void decline() {
      this._invite.declineInvitation();
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof String ? super._hashId == ((String)obj).toUpperCase().hashCode() : super.equals(obj);
   }
}
