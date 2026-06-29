package net.rim.device.apps.internal.browser.core;

import net.rim.device.api.util.DataBuffer;

class BSMRequest implements Runnable {
   private boolean _update;
   private DataBuffer _buffer;
   private BSMManager _bsmManager;

   public BSMRequest(BSMManager bsmManager, DataBuffer buffer) {
      this._update = true;
      this._buffer = buffer;
      this._bsmManager = bsmManager;
   }

   public BSMRequest(BSMManager bsmManager) {
      this._update = false;
      this._bsmManager = bsmManager;
   }

   @Override
   public void run() {
      if (this._bsmManager != null) {
         if (this._update) {
            this._bsmManager.update(this._buffer, null);
            return;
         }

         this._bsmManager.disconnect();
      }
   }
}
