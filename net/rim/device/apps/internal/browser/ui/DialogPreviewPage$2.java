package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.MenuItem;

class DialogPreviewPage$2 extends MenuItem {
   private final DialogPreviewPage this$0;

   DialogPreviewPage$2(DialogPreviewPage _1, String x0, int x1, int x2) {
      super(x0, x1, x2);
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._previewMgr.zoomOut();
      this.this$0.reLayout();
   }
}
