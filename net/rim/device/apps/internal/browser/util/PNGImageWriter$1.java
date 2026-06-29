package net.rim.device.apps.internal.browser.util;

import net.rim.device.internal.ui.component.ProgressDialog;

class PNGImageWriter$1 implements Runnable {
   private final ProgressDialog val$pg;
   private final PNGImageWriter this$0;

   PNGImageWriter$1(PNGImageWriter _1, ProgressDialog _2) {
      this.this$0 = _1;
      this.val$pg = _2;
   }

   @Override
   public void run() {
      this.val$pg.show();
   }
}
