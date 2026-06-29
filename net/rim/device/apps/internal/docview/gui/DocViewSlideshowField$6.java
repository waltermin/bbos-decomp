package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.EncodedImage;

class DocViewSlideshowField$6 implements Runnable {
   private final EncodedImage val$img;
   private final DocViewSlideshowField this$0;

   DocViewSlideshowField$6(DocViewSlideshowField _1, EncodedImage _2) {
      this.this$0 = _1;
      this.val$img = _2;
   }

   @Override
   public void run() {
      this.this$0._imgFld.setImage(this.val$img);
      if (this.this$0._imgFld.getIndex() == -1) {
         this.this$0.deleteAll();
         this.this$0.add(this.this$0._imgFld);
      }
   }
}
