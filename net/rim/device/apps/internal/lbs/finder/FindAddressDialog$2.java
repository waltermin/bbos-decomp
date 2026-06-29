package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.ui.UiApplication;

final class FindAddressDialog$2 implements Runnable {
   private final FindAddressDialog this$0;

   FindAddressDialog$2(FindAddressDialog this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushModalScreen(this.this$0);
   }
}
