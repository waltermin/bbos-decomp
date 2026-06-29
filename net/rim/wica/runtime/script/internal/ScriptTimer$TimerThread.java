package net.rim.wica.runtime.script.internal;

final class ScriptTimer$TimerThread extends Thread {
   private final ScriptTimer this$0;

   ScriptTimer$TimerThread(ScriptTimer this$0) {
      this.this$0 = this$0;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var24 = false /* VF: Semaphore variable */;

      try {
         var24 = true;
         long timeout = 0;

         label226:
         while (true) {
            ScriptTimer$TimerElement element = null;
            boolean repeat = true;
            long currentTime = System.currentTimeMillis();
            long shortestTimeout = Long.MAX_VALUE;

            while (repeat) {
               shortestTimeout = Long.MAX_VALUE;
               element = null;
               synchronized (this.this$0._queue) {
                  currentTime = System.currentTimeMillis();
                  int elementCount = this.this$0._queue.size();
                  if (this.this$0._shutdown) {
                     var24 = false;
                     break label226;
                  }

                  repeat = false;

                  for (int i = 0; i < elementCount && elementCount > 0; i++) {
                     element = (ScriptTimer$TimerElement)this.this$0._queue.elementAt(i);
                     if (element._timeToExecuteAt <= currentTime) {
                        if (!element._repeat) {
                           this.this$0._queue.removeElementAt(i);
                        }

                        repeat = true;
                        break;
                     }

                     if (shortestTimeout > element._timeToExecuteAt) {
                        shortestTimeout = element._timeToExecuteAt;
                     }

                     element = null;
                  }
               }

               if (element != null) {
                  this.this$0._engine.getRuntime().enqueueRunnable(element);
                  if (element._repeat) {
                     element._timeToExecuteAt = System.currentTimeMillis() + element._repeatTime;
                  }
               }
            }

            if (shortestTimeout == Long.MAX_VALUE) {
               timeout = 30000;
            } else {
               timeout = shortestTimeout - currentTime;
            }

            synchronized (this.this$0._queue) {
               if (!this.this$0._shutdown && timeout > 0) {
                  try {
                     this.this$0._queue.wait(timeout);
                     if (this.this$0._queue.size() == 0) {
                        this.this$0._shutdown = true;
                     }
                  } finally {
                     continue;
                  }
               }
            }
         }
      } finally {
         if (var24) {
            synchronized (this.this$0._queue) {
               this.this$0._timerThread = null;
               this.this$0._shutdown = true;
            }
         }
      }

      synchronized (this.this$0._queue) {
         this.this$0._timerThread = null;
         this.this$0._shutdown = true;
      }
   }
}
