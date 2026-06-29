package net.rim.device.apps.internal.qm.peer;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class PeerConversation$ConversationMarkVerb extends Verb {
   private final PeerConversation this$0;

   PeerConversation$ConversationMarkVerb(PeerConversation _1) {
      super(_1.isUnread() ? 602448 : 602450);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.perform(this.this$0.isUnread() ? 5803508244060051872L : -8629311385729242560L, null);
      return null;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(this.this$0.isUnread() ? 1325 : 1350);
   }
}
