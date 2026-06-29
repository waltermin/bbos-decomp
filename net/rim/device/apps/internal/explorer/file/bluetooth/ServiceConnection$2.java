package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.ui.UiApplication;

final class ServiceConnection$2 implements Runnable {
   private final ServiceConnection this$0;

   ServiceConnection$2(ServiceConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      if (this.this$0._popupScreen.isDisplayed()) {
         UiApplication.getUiApplication().popScreen(this.this$0._popupScreen);
      }
   }
}
