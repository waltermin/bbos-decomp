package net.rim.device.apps.internal.docview.gui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.ui.AppsMainScreen;

class DocViewParserObj$DocViewParseNotifyImpl$1 implements Runnable {
   private final Field val$displayField;
   private final DocViewParserObj$DocViewParseNotifyImpl this$1;

   DocViewParserObj$DocViewParseNotifyImpl$1(DocViewParserObj$DocViewParseNotifyImpl _1, Field _2) {
      this.this$1 = _1;
      this.val$displayField = _2;
   }

   @Override
   public void run() {
      if (this.this$1.this$0._applicationID != 0
         && DocViewDisplayScreenInstance.getForwardScreenInstance(this.this$1.this$0._messageID, this.this$1.this$0._applicationID) == null) {
         if (this.this$1.this$0._viewer != null) {
            this.this$1.this$0._viewer.releaseRefs();
            this.this$1.this$0._viewer = null;
         }
      } else {
         if (this.this$1.this$0._viewer != null) {
            this.this$1.this$0._viewer.go(true);
         } else if (this.val$displayField != null
            && this.this$1.this$0._displayImageAsSlideshow
            && this.this$1.this$0._coreData.getParsingData().getDocumentType() == 2) {
            AppsMainScreen displayScreen = new AppsMainScreen(2814751914590208L);
            displayScreen.setHelp("messages_index");
            displayScreen.add(this.val$displayField);
            UiApplication.getUiApplication().pushModalScreen(displayScreen);
         }

         try {
            DocViewDisplayScreenInstance.getForwardScreenInstance(this.this$1.this$0._messageID, this.this$1.this$0._applicationID).onDocDisplayEnd();
         } finally {
            return;
         }
      }
   }
}
