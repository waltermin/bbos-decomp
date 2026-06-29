package net.rim.device.api.ui;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.tid.awt.im.InputContext;

final class UiEngineImpl$StatusDisplayedHandler implements Runnable {
   private Screen _screen;
   private boolean _redisplay;
   private XYRect _revokedInvalid;
   private final UiEngineImpl this$0;

   UiEngineImpl$StatusDisplayedHandler(UiEngineImpl _1, Screen screen, boolean inputRequired, boolean redisplay, XYRect revokedInvalid) {
      this.this$0 = _1;
      this._screen = screen;
      this._redisplay = redisplay;
      this._revokedInvalid = revokedInvalid;
   }

   @Override
   public final void run() {
      synchronized (InputContext.getInstance()) {
         synchronized (GlobalScreenManager.getLock()) {
            if (this._screen.getUiEngine() != this.this$0) {
               return;
            }

            if (!this._screen.isGlobal()) {
               throw new IllegalStateException("Screen not global.");
            }

            if (!this._redisplay) {
               this._screen.callOnUiEngineAttached(true);
               String trace = "GS/+D " + Long.toString(4294967295L & this._screen.hashCode(), 16);
               EventLogger.logEvent(-4685663286194863677L, trace.getBytes(), 0);
            }

            this._screen.doLayout();
            this._screen.invalidate();
            if (this.this$0.equals(GlobalScreenManager.getPaintControlEngine())) {
               this._screen.doPaintabilityWalk(true);
            }

            this.this$0.globalScreenEventCommon(1, this._revokedInvalid, null);
            XYRect invalid = this._revokedInvalid != null ? new XYRect(this._revokedInvalid) : null;
            RIMGlobalMessagePoster.postGlobalEvent(5961289116197897667L, 1, 0, invalid, null);
         }
      }
   }
}
