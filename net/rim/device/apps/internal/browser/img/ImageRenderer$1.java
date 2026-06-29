package net.rim.device.apps.internal.browser.img;

import net.rim.device.api.ui.Field;

class ImageRenderer$1 implements Runnable {
   private final Field val$fieldToAdd;
   private final ImageRenderer this$0;

   ImageRenderer$1(ImageRenderer _1, Field _2) {
      this.this$0 = _1;
      this.val$fieldToAdd = _2;
   }

   @Override
   public void run() {
      this.this$0._manager.add(this.val$fieldToAdd);
   }
}
