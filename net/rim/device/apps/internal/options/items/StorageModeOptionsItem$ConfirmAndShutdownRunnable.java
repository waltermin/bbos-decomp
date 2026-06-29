package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.DeviceInternal;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

final class StorageModeOptionsItem$ConfirmAndShutdownRunnable implements Runnable {
   private final StorageModeOptionsItem this$0;

   StorageModeOptionsItem$ConfirmAndShutdownRunnable(StorageModeOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      for (int i = 0; i < this.this$0._questions.length; i++) {
         if (Dialog.ask(3, this.this$0._questions[i]) == -1) {
            UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
            return;
         }
      }

      DeviceInternal.requestStorageMode();
      UiApplication.getUiApplication().popScreen(UiApplication.getUiApplication().getActiveScreen());
   }
}
