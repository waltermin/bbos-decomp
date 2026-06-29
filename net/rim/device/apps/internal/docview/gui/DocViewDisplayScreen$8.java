package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.Status;

class DocViewDisplayScreen$8 implements Runnable {
   private final Screen val$myScreen;
   private final ServerResponse val$response;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$8(DocViewDisplayScreen _1, Screen _2, ServerResponse _3) {
      this.this$0 = _1;
      this.val$myScreen = _2;
      this.val$response = _3;
   }

   @Override
   public void run() {
      try {
         if (DocViewDisplayScreen.access$500(this.this$0).isForeground() && this.val$myScreen.isDisplayed()
            || this.this$0._displayField != null && this.this$0._displayField.getScreen() != null && this.this$0._displayField.getScreen().isDisplayed()) {
            Status.show(AttachmentViewerFactory.getErrorString(this.val$response._errorCode), Bitmap.getPredefinedBitmap(0), 1500);
            return;
         }
      } finally {
         return;
      }
   }
}
