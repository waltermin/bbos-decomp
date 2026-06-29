package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class CallForwardingScreen$CancelVerb extends Verb {
   private final CallForwardingScreen this$0;

   CallForwardingScreen$CancelVerb(CallForwardingScreen _1) {
      super(268501008, CommonResource.getBundle(), 9);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this.this$0.isDirty()) {
         switch (Dialog.ask(1, CommonResource.getString(10003))) {
            case 0:
               return null;
            case 1:
            default:
               this.this$0.save();
            case 2:
         }
      }

      this.this$0.close();
      return null;
   }
}
