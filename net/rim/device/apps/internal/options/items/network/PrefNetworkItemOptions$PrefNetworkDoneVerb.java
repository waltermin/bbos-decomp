package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class PrefNetworkItemOptions$PrefNetworkDoneVerb extends Verb {
   private final PrefNetworkItemOptions this$0;

   PrefNetworkItemOptions$PrefNetworkDoneVerb(PrefNetworkItemOptions _1) {
      super(268435456, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parm) {
      if (this.this$0.onSave()) {
         UiApplication app = UiApplication.getUiApplication();
         app.popScreen(app.getActiveScreen());
      }

      return null;
   }
}
