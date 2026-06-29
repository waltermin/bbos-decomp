package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.sms.resources.SMSResources;

class SMSViewerScreen$RemoveHistoryVerb extends Verb {
   private final SMSViewerScreen this$0;

   SMSViewerScreen$RemoveHistoryVerb(SMSViewerScreen _1) {
      super(16859472, SMSResources.getResourceBundle(), 404);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0.removeHistory();
      return null;
   }
}
