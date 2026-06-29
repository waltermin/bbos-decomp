package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.ui.UiApplication;

final class SendHandler$2 implements Runnable {
   private final SendHandler this$0;

   SendHandler$2(SendHandler _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._popupScreen.isDisplayed()) {
         UiApplication.getUiApplication().popScreen(this.this$0._popupScreen);
      }
   }
}
