package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.system.RIMGlobalMessagePoster;

final class ConvenienceKeyOptionsImpl$InvokeTaskSwitcherRunnable implements Runnable {
   private int _convenienceKey;

   final void setConvenienceKey(int convenienceKey) {
      this._convenienceKey = convenienceKey;
   }

   @Override
   public final void run() {
      RIMGlobalMessagePoster.postGlobalEvent(7563637690172082503L, 0, this._convenienceKey, null, null);
   }
}
