package net.rim.device.apps.api.messaging.messagelist;

import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.messaging.resources.MessageResources;

class MessageListUI$PickVerb extends Verb {
   RIMModel _selectedModel;
   private final MessageListUI this$0;

   MessageListUI$PickVerb(MessageListUI _1) {
      super(612352);
      this.this$0 = _1;
   }

   void setParameters(RIMModel selectedModel) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   void resetParameters() {
      this._selectedModel = null;
   }

   @Override
   public Object invoke(Object context) {
      this.this$0._leaveScreenVerb.invoke(this.this$0._context);
      return this._selectedModel;
   }

   @Override
   public String toString() {
      return MessageResources.getString(4);
   }
}
