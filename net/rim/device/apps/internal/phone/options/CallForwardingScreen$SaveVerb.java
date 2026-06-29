package net.rim.device.apps.internal.phone.options;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class CallForwardingScreen$SaveVerb extends Verb {
   private final CallForwardingScreen this$0;

   CallForwardingScreen$SaveVerb(CallForwardingScreen _1) {
      super(332288, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0.saveChanges();
      this.this$0.close();
      return null;
   }
}
