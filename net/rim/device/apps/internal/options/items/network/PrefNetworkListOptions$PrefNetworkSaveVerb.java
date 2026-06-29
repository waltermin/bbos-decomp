package net.rim.device.apps.internal.options.items.network;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class PrefNetworkListOptions$PrefNetworkSaveVerb extends Verb {
   private final PrefNetworkListOptions this$0;

   PrefNetworkListOptions$PrefNetworkSaveVerb(PrefNetworkListOptions _1) {
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
