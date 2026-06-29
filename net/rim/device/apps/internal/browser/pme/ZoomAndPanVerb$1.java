package net.rim.device.apps.internal.browser.pme;

import net.rim.device.api.ui.UiApplication;

final class ZoomAndPanVerb$1 implements Runnable {
   private final ZoomAndPanVerb this$0;

   ZoomAndPanVerb$1(ZoomAndPanVerb _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      UiApplication.getUiApplication().pushScreen(ZoomAndPanVerb._zpScreen);
   }
}
