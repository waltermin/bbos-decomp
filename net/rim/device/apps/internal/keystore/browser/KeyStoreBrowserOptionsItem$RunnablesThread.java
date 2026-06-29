package net.rim.device.apps.internal.keystore.browser;

class KeyStoreBrowserOptionsItem$RunnablesThread extends Thread {
   private final KeyStoreBrowserOptionsItem this$0;

   private KeyStoreBrowserOptionsItem$RunnablesThread(KeyStoreBrowserOptionsItem _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      while (true) {
         long timeSinceLastAdd = Math.max(0, System.currentTimeMillis() - this.this$0._reloadCounterLastIncremented);
         if (timeSinceLastAdd < 200) {
            try {
               Thread.sleep(200 - timeSinceLastAdd);
            } finally {
               continue;
            }
         } else {
            synchronized (this.this$0._reloadCounterLock) {
               if (this.this$0._reloadCounter <= 0) {
                  this.this$0._runnablesThread = null;
                  return;
               }

               this.this$0._reloadCounter = 0;
            }

            this.this$0._app.invokeAndWait(new KeyStoreBrowserOptionsItem$RunnablesThread$1(this));
         }
      }
   }

   KeyStoreBrowserOptionsItem$RunnablesThread(KeyStoreBrowserOptionsItem x0, KeyStoreBrowserOptionsItem$1 x1) {
      this(x0);
   }
}
