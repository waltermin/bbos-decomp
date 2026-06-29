package net.rim.device.cldc.io.waphttp;

import net.rim.device.internal.proxy.Proxy;

class WAPConnectionImpl$ShutdownConnection implements Runnable {
   private boolean _force;
   private final WAPConnectionImpl this$0;

   public WAPConnectionImpl$ShutdownConnection(WAPConnectionImpl _1, boolean force) {
      this.this$0 = _1;
      this._force = force;
   }

   @Override
   public void run() {
      try {
         if (!this._force) {
            synchronized (this.this$0._syncObject) {
               if (this.this$0._params != null && this.this$0._currentConfig != null && this.this$0._currentConfig._bearer != null) {
                  long timeDiff = System.currentTimeMillis() - this.this$0._currentConfig._bearer.getLastPacketTime();
                  if (this.this$0._params._timeout > 1000 && timeDiff > 0 && timeDiff < this.this$0._params._timeout - 1000) {
                     Proxy proxyInstance = Proxy.getInstance();
                     this.this$0._currentConfig._currentTimer = proxyInstance.invokeLater(
                        new WAPConnectionImpl$ShutdownConnection(this.this$0, false),
                        Math.min(this.this$0._params._timeout, this.this$0._params._timeout - timeDiff),
                        false
                     );
                     return;
                  }
               }
            }
         }

         this.this$0.close(this._force);
      } finally {
         return;
      }
   }
}
