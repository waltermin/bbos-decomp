package net.rim.wica.runtime.management.internal;

import java.util.TimerTask;
import net.rim.wica.runtime.logging.Logger;

final class ManagementServiceImpl$KeyRefreshTask extends TimerTask {
   private KeyRefreshTaskParams _params;
   private final ManagementServiceImpl this$0;

   public ManagementServiceImpl$KeyRefreshTask(ManagementServiceImpl this$0, KeyRefreshTaskParams params) {
      this.this$0 = this$0;
      this._params = params;
   }

   @Override
   public final void run() {
      Logger.log(this.toString(), "Key Refresh; agID: " + this._params._agID + (this.isRetryTimer() ? "; retryTimer" : "; regularTimer"), 4);
      this.this$0.performKeyRefresh(this._params._agID);
   }

   public final boolean isTimeToStopRetrying() {
      long timePassed = System.currentTimeMillis() - this._params._timeLastSuccess;
      return timePassed > 2592000000L;
   }

   public final boolean isRetryTimer() {
      return this._params._retryTimer;
   }

   public final void successful() {
      this._params._timeLastSuccess = System.currentTimeMillis();
   }
}
