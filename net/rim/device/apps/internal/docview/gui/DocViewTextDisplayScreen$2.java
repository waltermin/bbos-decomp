package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

class DocViewTextDisplayScreen$2 implements Runnable {
   private final short val$errorCode;
   private final String val$pattern;
   private final DocViewTextDisplayScreen this$0;

   DocViewTextDisplayScreen$2(DocViewTextDisplayScreen _1, short _2, String _3) {
      this.this$0 = _1;
      this.val$errorCode = _2;
      this.val$pattern = _3;
   }

   @Override
   public void run() {
      ActiveDisplayedPart activeRetrievedPart = DocViewDisplayScreenInstance.getActivePartInstance(this.this$0._messageID, this.this$0._applicationID);
      if (activeRetrievedPart != null) {
         if (this.val$errorCode == 248) {
            String errorString = DocViewDisplayScreen._resources.getString(70);
            if (this.val$pattern != null) {
               errorString = ((StringBuffer)(new Object()))
                  .append(errorString)
                  .append(' ')
                  .append(DocViewDisplayScreen._resources.getString(91))
                  .append(' ')
                  .append(this.val$pattern)
                  .toString();
            }

            Dialog.alert(errorString);
            return;
         }

         Status.show(AttachmentViewerFactory.getErrorString(this.val$errorCode), Bitmap.getPredefinedBitmap(0), 1500);
      }
   }
}
