package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.SimpleInputDialog;

class AddNumberVerb$1 extends SimpleInputDialog {
   private final AddNumberVerb this$0;

   AddNumberVerb$1(AddNumberVerb _1, int x0, String x1) {
      super(x0, x1);
      this.this$0 = _1;
   }

   @Override
   protected boolean accept() {
      int actualLen = this.getEditField().getText().length();
      int minLen = this.getMinLength();
      if (!this.isNumberValid(this.getEditField().getText())) {
         synchronized (Application.getEventLock()) {
            Dialog.alert(PhoneResources.getString(3021));
            return false;
         }
      } else if (actualLen < minLen) {
         String fmtString = PhoneResources.getString(6135);
         String msg = MessageFormat.format(fmtString, new Object[]{((StringBuffer)(new Object(""))).append(minLen).toString()});
         synchronized (Application.getEventLock()) {
            Dialog.alert(msg);
            return false;
         }
      } else {
         this.close(0);
         return true;
      }
   }

   private boolean isNumberValid(String str) {
      return str.lastIndexOf(43, str.length()) <= 0;
   }
}
