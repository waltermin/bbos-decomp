package net.rim.device.apps.internal.vad;

import net.rim.device.api.system.AudioRouter;

final class VADEngineManager$RemoveSourceRunnable implements Runnable {
   private final VADEngineManager this$0;

   VADEngineManager$RemoveSourceRunnable(VADEngineManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      AudioRouter.getInstance().removeSource(1);
      this.this$0._app.uiExit();
   }
}
