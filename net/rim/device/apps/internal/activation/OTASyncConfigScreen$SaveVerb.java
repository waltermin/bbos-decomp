package net.rim.device.apps.internal.activation;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class OTASyncConfigScreen$SaveVerb extends Verb {
   private final OTASyncConfigScreen this$0;

   OTASyncConfigScreen$SaveVerb(OTASyncConfigScreen _1) {
      super(598336, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object anObject) {
      this.this$0.onSave();
      this.this$0.close();
      return null;
   }
}
