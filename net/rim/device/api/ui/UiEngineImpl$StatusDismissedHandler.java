package net.rim.device.api.ui;

import net.rim.device.api.system.EventLogger;
import net.rim.tid.awt.im.InputContext;

final class UiEngineImpl$StatusDismissedHandler implements Runnable {
   private Screen _screen;
   private final UiEngineImpl this$0;

   UiEngineImpl$StatusDismissedHandler(UiEngineImpl _1, Screen screen) {
      this.this$0 = _1;
      this._screen = screen;
   }

   @Override
   public final void run() {
      synchronized (InputContext.getInstance()) {
         synchronized (GlobalScreenManager.getLock()) {
            if (!this._screen.isUiEngineAttached()) {
               return;
            }

            this._screen.doPaintabilityWalk(false);
            this._screen.doVisibilityWalk(false);
            this.this$0.globalScreenEventCommon(2, this._screen.getExtent(), null);
            String trace = "GS/-D " + Long.toString(4294967295L & this._screen.hashCode(), 16);
            EventLogger.logEvent(-4685663286194863677L, trace.getBytes(), 0);
            this._screen.callOnUiEngineAttached(false);
         }
      }
   }
}
