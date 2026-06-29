package net.rim.device.apps.internal.docview.gui;

import java.util.Timer;
import java.util.TimerTask;

class DocViewAttachmentViewerModel$1 extends TimerTask {
   private int _executionCount;
   private final String val$embeddedDomIDFinal;
   private final String val$pageDomID;
   private final int val$messageID;
   private final int val$partID;
   private final ServerResponse val$responseFinal;
   private final Object val$contextFinal;
   private final byte[] val$moreData;
   private final Timer val$timerFinal;
   private final DocViewAttachmentViewerModel this$0;
   private static final int NUM_EXECUTIONS_ALLOWED = 15;

   DocViewAttachmentViewerModel$1(DocViewAttachmentViewerModel _1, String _2, String _3, int _4, int _5, ServerResponse _6, Object _7, byte[] _8, Timer _9) {
      this.this$0 = _1;
      this.val$embeddedDomIDFinal = _2;
      this.val$pageDomID = _3;
      this.val$messageID = _4;
      this.val$partID = _5;
      this.val$responseFinal = _6;
      this.val$contextFinal = _7;
      this.val$moreData = _8;
      this.val$timerFinal = _9;
   }

   @Override
   public void run() {
      if (++this._executionCount == 15
         || this.this$0
            .isPageRenderingRetrieved(
               this.val$embeddedDomIDFinal, this.val$pageDomID, this.val$messageID, this.val$partID, this.val$responseFinal._archiveIndicator, false
            )) {
         SerialRunnableManager.post(
            new DocViewAttachmentViewerModel$ReceiveRunnableEx(
               this.this$0, this.val$contextFinal, this.val$responseFinal, this.val$moreData, this.val$messageID, this.val$partID, null
            )
         );
         this.cancel();
         this.val$timerFinal.cancel();
      }
   }
}
