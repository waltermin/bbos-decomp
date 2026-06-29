package net.rim.device.apps.internal.qm.peer;

import net.rim.blackberry.api.blackberrymessenger.MessengerContact;

final class TextMessageField$UnreachableTimer extends QMTimerTask {
   private final TextMessageField this$0;
   private static final int UNREACHABLE_TIMEOUT;

   private TextMessageField$UnreachableTimer(TextMessageField _1) {
      this.this$0 = _1;
   }

   final void schedule() {
      TextMessageField.access$100().schedule(this, 60000);
   }

   @Override
   public final void run() {
      if (TextMessageField.access$200(this.this$0).getState() != 11) {
         MessengerContact contact = TextMessageField.access$200(this.this$0).getRecipient();
         if (contact instanceof PeerContact) {
            if (TextMessageField.access$200(this.this$0).getState() == 10) {
               ((PeerContact)contact).setPresenceStatus(8);
            } else {
               ((PeerContact)contact).setPresenceStatus(16);
            }

            ((PeerContact)contact).setTyping(false);
         }
      }
   }

   TextMessageField$UnreachableTimer(TextMessageField x0, TextMessageField$1 x1) {
      this(x0);
   }
}
