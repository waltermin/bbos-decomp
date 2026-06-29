package com.fourthpass.wapstack.wtp;

final class WTPListenerThread extends Thread {
   private boolean _stopListener;
   private WTPLayer _wtpLayer;
   private Object _syncObject = new Object();

   WTPListenerThread(WTPLayer wtpLayer) {
      this._wtpLayer = wtpLayer;
      this.setPriority(2);
   }

   public final void shutdown() {
      synchronized (this._syncObject) {
         this._stopListener = true;
         this._wtpLayer = null;
      }
   }

   @Override
   public final void run() {
      while (true) {
         WTPLayer layer;
         synchronized (this._syncObject) {
            if (this._stopListener) {
               return;
            }

            layer = this._wtpLayer;
         }

         layer.invokeReceiver();
      }
   }
}
