package net.rim.plazmic.internal.contentpreview.service;

final class LoopingThreadService$Loop implements Runnable {
   private final LoopingThreadService this$0;

   private LoopingThreadService$Loop(LoopingThreadService _1) {
      this.this$0 = _1;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      RuntimeException exception = null;
      synchronized (this.this$0._syncStartStop) {
         this.this$0._running = true;
         this.this$0._syncStartStop.notifyAll();
      }

      while (this.this$0.isEnabled()) {
         try {
            this.this$0._task.run();
         } catch (Throwable var18) {
            exception = rte;
            break;
         }

         if (this.this$0._loopWait) {
            synchronized (this.this$0._syncNext) {
               while (true) {
                  if (!this.this$0._next) {
                     label118:
                     try {
                        this.this$0._syncNext.wait();
                        continue;
                     } finally {
                        break label118;
                     }
                  }

                  this.this$0._next = false;
                  break;
               }
            }
         }
      }

      synchronized (this.this$0._syncStartStop) {
         this.this$0._running = false;
         this.this$0._syncStartStop.notifyAll();
      }

      if (exception != null) {
         throw exception;
      }
   }

   LoopingThreadService$Loop(LoopingThreadService x0, LoopingThreadService$1 x1) {
      this(x0);
   }
}
