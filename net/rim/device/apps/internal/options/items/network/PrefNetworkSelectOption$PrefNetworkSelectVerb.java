package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class PrefNetworkSelectOption$PrefNetworkSelectVerb extends Verb {
   private final PrefNetworkSelectOption this$0;

   PrefNetworkSelectOption$PrefNetworkSelectVerb(PrefNetworkSelectOption _1) {
      super(598288, OptionsResources.getResourceBundle(), 911);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this.this$0.selectNetwork()) {
         UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
      }

      return null;
   }
}
