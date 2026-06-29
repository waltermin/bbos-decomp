package net.rim.wica.runtime.authentication.internal;

import net.rim.wica.runtime.ui.internal.AuthenticationDialog;

class AuthenticationService$1 implements Runnable {
   private final AuthenticationDialog val$dialog;
   private final AuthenticationService this$0;

   AuthenticationService$1(AuthenticationService this$0, AuthenticationDialog val$dialog) {
      this.this$0 = this$0;
      this.val$dialog = val$dialog;
   }

   @Override
   public void run() {
      this.val$dialog.show();
   }
}
