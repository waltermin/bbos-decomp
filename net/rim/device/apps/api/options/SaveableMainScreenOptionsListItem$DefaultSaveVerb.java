package net.rim.device.apps.api.options;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class SaveableMainScreenOptionsListItem$DefaultSaveVerb extends Verb {
   private final SaveableMainScreenOptionsListItem this$0;

   SaveableMainScreenOptionsListItem$DefaultSaveVerb(SaveableMainScreenOptionsListItem _1) {
      super(268435456, CommonResource.getBundle(), 18);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parm) {
      if (this.this$0.confirm(this, parm)) {
         UiApplication app = UiApplication.getUiApplication();
         app.popScreen(app.getActiveScreen());
      }

      return null;
   }
}
