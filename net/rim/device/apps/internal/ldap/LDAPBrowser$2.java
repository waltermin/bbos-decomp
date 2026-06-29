package net.rim.device.apps.internal.ldap;

import net.rim.device.internal.ui.component.BackgroundDialog;

class LDAPBrowser$2 extends Thread {
   private final String val$tempOutput;
   private final LDAPBrowser this$0;

   LDAPBrowser$2(LDAPBrowser _1, String _2) {
      this.this$0 = _1;
      this.val$tempOutput = _2;
   }

   @Override
   public void run() {
      BackgroundDialog.showMessage(this.val$tempOutput);
   }
}
