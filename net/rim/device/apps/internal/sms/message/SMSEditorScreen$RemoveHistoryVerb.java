package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.sms.resources.SMSResources;

class SMSEditorScreen$RemoveHistoryVerb extends Verb {
   private final SMSEditorScreen this$0;

   SMSEditorScreen$RemoveHistoryVerb(SMSEditorScreen _1) {
      super(16859472, SMSResources.getResourceBundle(), 404);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object parameter) {
      this.this$0.removeHistory();
      return null;
   }
}
