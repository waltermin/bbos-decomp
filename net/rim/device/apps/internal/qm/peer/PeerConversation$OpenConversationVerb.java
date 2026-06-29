package net.rim.device.apps.internal.qm.peer;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class PeerConversation$OpenConversationVerb extends Verb implements Runnable {
   private ContextObject _context;
   private final PeerConversation this$0;

   PeerConversation$OpenConversationVerb(PeerConversation _1) {
      super(590080);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(15);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.setParameter(parameter);
      Application.getApplication().invokeLater(this);
      return null;
   }

   final void setParameter(Object parameter) {
      this.setParameter(parameter, false);
   }

   final void setParameter(Object parameter, boolean focus) {
      this._context = ContextObject.clone(parameter instanceof ContextObject ? parameter : null);
      if (!focus) {
         this._context.setFlag(26);
      }
   }

   @Override
   public final void run() {
      PeerApplication.getInstance().openConversationTriggeredByUnHolster(this.this$0, this._context);
   }
}
