package net.rim.device.apps.internal.browser.javascript;

final class JavaScriptTimer$ExecuteThread extends Thread {
   private final JavaScriptTimer this$0;

   public JavaScriptTimer$ExecuteThread(JavaScriptTimer _1) {
      this.this$0 = _1;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      boolean var25 = false /* VF: Semaphore variable */;

      try {
         var25 = true;
         long timeout = 0;

         label306:
         while (true) {
            JavaScriptTimer$QueueElement element = null;
            boolean repeat = true;
            long currentTime = System.currentTimeMillis();
            long shortestTimeout = Long.MAX_VALUE;

            while (repeat) {
               shortestTimeout = Long.MAX_VALUE;
               element = null;
               synchronized (this.this$0._waitQueue) {
                  currentTime = System.currentTimeMillis();
                  int elementCount = this.this$0._waitQueue.size();
                  if (this.this$0._shutdown) {
                     var25 = false;
                     break label306;
                  }

                  repeat = false;

                  for (int i = 0; i < elementCount && elementCount > 0; i++) {
                     element = (JavaScriptTimer$QueueElement)this.this$0._waitQueue.elementAt(i);
                     if (element._timeToExecuteAt <= currentTime) {
                        if (!element._repeat) {
                           this.this$0._waitQueue.removeElementAt(i);
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
                  boolean var40 = false /* VF: Semaphore variable */;

                  label286:
                  try {
                     var40 = true;
                     this.this$0._scriptEngine.executeMethod(null, element._action, null, false);
                     var40 = false;
                  } finally {
                     if (var40) {
                        element._repeat = false;
                        break label286;
                     }
                  }

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

            synchronized (this.this$0._waitQueue) {
               if (!this.this$0._shutdown && timeout > 0) {
                  try {
                     this.this$0._waitQueue.wait(timeout);
                     if (this.this$0._waitQueue.size() == 0) {
                        this.this$0._shutdown = true;
                     }
                  } finally {
                     continue;
                  }
               }
            }
         }
      } finally {
         if (var25) {
            synchronized (this.this$0._waitQueue) {
               this.this$0._currentThread = null;
               this.this$0._shutdown = true;
            }
         }
      }

      synchronized (this.this$0._waitQueue) {
         this.this$0._currentThread = null;
         this.this$0._shutdown = true;
      }
   }
}
