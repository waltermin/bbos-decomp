package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.ui.UiApplication;

final class ServiceConnection$3 implements Runnable {
   private final ServiceConnection this$0;

   ServiceConnection$3(ServiceConnection _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(this.this$0._popupScreen);
   }
}
