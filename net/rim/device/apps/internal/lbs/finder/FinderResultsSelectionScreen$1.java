package net.rim.device.apps.internal.lbs.finder;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.UiApplication;

final class FinderResultsSelectionScreen$1 implements Runnable {
   private final FinderResultsSelectionScreen this$0;

   FinderResultsSelectionScreen$1(FinderResultsSelectionScreen this$0) {
      this.this$0 = this$0;
   }

   @Override
   public final void run() {
      synchronized (Application.getEventLock()) {
         UiApplication.getUiApplication().pushModalScreen(this.this$0);
      }
   }
}
