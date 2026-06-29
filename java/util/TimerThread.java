package java.util;

class TimerThread extends Thread {
   boolean newTasksMayBeScheduled = true;
   private TaskQueue queue;
   private static final long THREAD_TIMEOUT = 10000L;

   TimerThread(TaskQueue queue) {
      this.queue = queue;
   }

   @Override
   public void run() {
      try {
         this.mainLoop();
      } catch (Throwable t) {
         synchronized (this.queue) {
            this.newTasksMayBeScheduled = false;
            this.queue.clear();
         }
      }
   }

   private void mainLoop() {
      while (true) {
         try {
            TimerTask task;
            boolean taskFired;
            synchronized (this.queue) {
               while (this.queue.isEmpty() && this.newTasksMayBeScheduled) {
                  this.queue.wait(10000);
                  if (this.queue.isEmpty()) {
                     break;
                  }
               }

               if (this.queue.isEmpty()) {
                  return;
               }

               task = this.queue.getMin();
               long currentTime;
               long executionTime;
               synchronized (task.lock) {
                  if (task.state == 3) {
                     this.queue.removeMin();
                     continue;
                  }

                  currentTime = System.currentTimeMillis();
                  executionTime = task.nextExecutionTime;
                  if (taskFired = executionTime <= currentTime) {
                     if (task.period == 0) {
                        this.queue.removeMin();
                        task.state = 2;
                     } else {
                        this.queue.rescheduleMin(task.period < 0 ? currentTime - task.period : executionTime + task.period);
                     }
                  }
               }

               if (!taskFired) {
                  this.queue.wait(executionTime - currentTime);
               }
            }

            if (taskFired) {
               try {
                  task.run();
               } catch (Throwable e) {
                  task.cancel();
                  System.err.println(e);
                  System.err.println("Timer died: " + this);
               }
            }
         } catch (InterruptedException var14) {
         }
      }
   }
}
