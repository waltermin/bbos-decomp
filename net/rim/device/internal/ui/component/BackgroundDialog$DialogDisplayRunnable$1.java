package net.rim.device.internal.ui.component;

import net.rim.device.internal.proxy.Proxy;

class BackgroundDialog$DialogDisplayRunnable$1 implements Runnable {
   private final BackgroundDialog$DialogDisplayRunnable this$0;

   BackgroundDialog$DialogDisplayRunnable$1(BackgroundDialog$DialogDisplayRunnable _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      Proxy.getInstance().invokeAndWait(this.this$0);
   }
}
