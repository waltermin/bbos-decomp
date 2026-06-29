package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.MenuItem;

class ForwardScreen$11 extends MenuItem {
   private final ForwardScreen this$0;

   ForwardScreen$11(ForwardScreen _1, ResourceBundle x0, int x1, int x2, int x3) {
      super(x0, x1, x2, x3);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.doActionOnSelectedNode(false);
   }
}
