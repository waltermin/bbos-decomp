package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

class ForwardScreen$7 implements Runnable {
   private final ForwardScreen this$0;

   ForwardScreen$7(ForwardScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Status.show(EmailResources.getString(1003), Bitmap.getPredefinedBitmap(0), 1000);
   }
}
