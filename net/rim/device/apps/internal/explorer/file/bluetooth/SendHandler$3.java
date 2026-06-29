package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.ui.UiApplication;

final class SendHandler$3 implements Runnable {
   private final SendHandler this$0;

   SendHandler$3(SendHandler _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(this.this$0._popupScreen);
   }
}
