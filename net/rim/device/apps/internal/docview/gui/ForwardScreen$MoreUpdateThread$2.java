package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Manager;

class ForwardScreen$MoreUpdateThread$2 implements Runnable {
   private final int val$node;
   private final ForwardScreen$MoreUpdateThread this$1;

   ForwardScreen$MoreUpdateThread$2(ForwardScreen$MoreUpdateThread _1, int _2) {
      this.this$1 = _1;
      this.val$node = _2;
   }

   @Override
   public void run() {
      if (this.this$1.this$0._pendingParts == 0 && this.this$1.this$0.isFwdScreenTop()) {
         ModalDisplayDlg errorDlg = new ModalDisplayDlg(
            (Manager)(new Object(281474976710656L)),
            this.this$1.this$0._attachmentsTree.getCookie(this.val$node).toString(),
            AttachmentViewerFactory.getErrorString(this.this$1._response._errorCode),
            null,
            null,
            null,
            null,
            null,
            null,
            -1,
            (byte)17
         );
         errorDlg.setModal(true);
         errorDlg.show();
         Object var2 = null;
      }
   }
}
