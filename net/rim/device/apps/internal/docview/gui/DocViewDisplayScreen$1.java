package net.rim.device.apps.internal.docview.gui;

import net.rim.device.internal.ui.component.SimpleInputDialog;

class DocViewDisplayScreen$1 implements Runnable {
   private final SimpleInputDialog val$pwdDlg;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$1(DocViewDisplayScreen _1, SimpleInputDialog _2) {
      this.this$0 = _1;
      this.val$pwdDlg = _2;
   }

   @Override
   public void run() {
      this.val$pwdDlg.show();
   }
}
