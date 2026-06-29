package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.UiApplication;

class DialogWithBackgroundThread$2 implements Runnable {
   private final DialogWithBackgroundThread this$0;

   DialogWithBackgroundThread$2(DialogWithBackgroundThread _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      if (this.this$0._displayPopupScreen && this.this$0._popupScreen != null && this.this$0._popupScreen.getUiEngine() != null) {
         UiApplication.getUiApplication().popScreen(this.this$0._popupScreen);
      }
   }
}
