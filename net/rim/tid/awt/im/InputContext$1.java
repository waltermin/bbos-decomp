package net.rim.tid.awt.im;

import net.rim.device.api.ui.Font;
import net.rim.tid.itie.IComponent;

class InputContext$1 implements Runnable {
   private final IComponent val$comp;
   private final Font val$newFont;
   private final InputContext this$0;

   InputContext$1(InputContext _1, IComponent _2, Font _3) {
      this.this$0 = _1;
      this.val$comp = _2;
      this.val$newFont = _3;
   }

   @Override
   public void run() {
      this.val$comp.setFont(this.val$newFont);
   }
}
