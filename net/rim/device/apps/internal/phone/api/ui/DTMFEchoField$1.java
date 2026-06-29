package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.apps.internal.phone.api.verbs.DialVerb;

class DTMFEchoField$1 implements Runnable {
   private final String val$tonesToDial;
   private final DTMFEchoField this$0;

   DTMFEchoField$1(DTMFEchoField _1, String _2) {
      this.this$0 = _1;
      this.val$tonesToDial = _2;
   }

   @Override
   public void run() {
      new DialVerb(this.val$tonesToDial, null).invoke(null);
      this.this$0.clear(0);
   }
}
