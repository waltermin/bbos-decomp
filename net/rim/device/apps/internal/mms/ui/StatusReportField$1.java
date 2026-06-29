package net.rim.device.apps.internal.mms.ui;

import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;

class StatusReportField$1 extends Verb {
   private final StatusReportField this$0;

   StatusReportField$1(StatusReportField _1, int x0, ResourceBundleFamily x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public Object invoke(Object param) {
      UiApplication.getUiApplication().pushScreen(new StatusReportField$StatusReportScreen(this.this$0, this.this$0._message, this.this$0._context));
      return null;
   }
}
