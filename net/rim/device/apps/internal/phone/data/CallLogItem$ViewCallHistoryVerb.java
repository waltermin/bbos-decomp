package net.rim.device.apps.internal.phone.data;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallLogItem$ViewCallHistoryVerb extends Verb {
   private CallLogItem _cli;

   CallLogItem$ViewCallHistoryVerb(CallLogItem cli) {
      super(1332234, PhoneResources.getResourceBundle(), 6283);
      this._cli = cli;
   }

   @Override
   public final Object invoke(Object parameter) {
      CallLogItemScreen.view(this._cli);
      return null;
   }
}
