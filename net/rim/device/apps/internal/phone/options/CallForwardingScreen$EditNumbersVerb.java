package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CallForwardingScreen$EditNumbersVerb extends Verb {
   private final CallForwardingScreen this$0;

   CallForwardingScreen$EditNumbersVerb(CallForwardingScreen _1) {
      super(120001);
      this.this$0 = _1;
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(6241);
   }

   @Override
   public final Object invoke(Object parameter) {
      this.this$0._forwardingNumbersOutOfDate = false;
      UiApplication.getUiApplication().pushScreen(new EditForwardingNumbersScreen(this.this$0, this.this$0.getActiveForwardingNumbers()));
      return null;
   }
}
