package net.rim.device.apps.api.utility.options;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class OptionsEditorScreen$SaveOptionsVerb extends Verb {
   private final OptionsEditorScreen this$0;

   OptionsEditorScreen$SaveOptionsVerb(OptionsEditorScreen _1) {
      super(268435456);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object o) {
      if (this.this$0.onSave()) {
         UiApplication.getUiApplication().popScreen(this.this$0);
      }

      return null;
   }

   @Override
   public final String toString() {
      return CommonResource.getString(18);
   }
}
