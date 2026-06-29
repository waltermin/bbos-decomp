package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.Image;

class ForwardScreen$2 implements Runnable {
   private final Image val$laterIcon;
   private final ForwardScreen this$0;

   ForwardScreen$2(ForwardScreen _1, Image _2) {
      this.this$0 = _1;
      this.val$laterIcon = _2;
   }

   @Override
   public void run() {
      this.this$0._iconField.setImage(this.val$laterIcon);
      if (this.this$0._spacerField.getIndex() == -1) {
         this.this$0._titleManager.add(this.this$0._spacerField);
      }

      if (this.this$0._iconField.getIndex() == -1) {
         this.this$0._titleManager.add(this.this$0._iconField);
      }
   }
}
