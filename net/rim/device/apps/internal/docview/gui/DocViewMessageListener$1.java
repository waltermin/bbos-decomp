package net.rim.device.apps.internal.docview.gui;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;

class DocViewMessageListener$1 extends Thread {
   private final EmailMessageModel val$model;
   private final DocViewAttachmentViewerModel[] val$attachments;
   private final DocViewMessageListener this$0;

   DocViewMessageListener$1(DocViewMessageListener _1, EmailMessageModel _2, DocViewAttachmentViewerModel[] _3) {
      this.this$0 = _1;
      this.val$model = _2;
      this.val$attachments = _3;
   }

   @Override
   public void run() {
      ContextObject newContext = ContextObject.castOrCreate(null);
      ContextObject.put(newContext, 254, this.val$model);
      int remainingCount = 5;

      for (int i = 0; i < this.val$attachments.length && remainingCount > 0; i++) {
         remainingCount -= this.val$attachments[i]
            .requestFirstChunk(this.val$model, newContext, remainingCount, i == this.val$attachments.length - 1 ? remainingCount : 2);
      }

      ContextObject var4 = null;
   }
}
