package net.rim.device.api.ui.component;

class ThreadPool$ThreadPoolRunner extends Thread {
   private final ThreadPool this$0;

   ThreadPool$ThreadPoolRunner(ThreadPool _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      while (true) {
         label26: {
            try {
               Runnable r = this.this$0.get();
               if (r != null) {
                  r.run();
                  break label26;
               }
            } catch (Throwable var3) {
               break label26;
            }

            ThreadPool.access$110(this.this$0);
            if (this.this$0._threadCount <= 0) {
               ThreadPool.access$202(null);
            }

            return;
         }

         Object var4 = null;
      }
   }
}
