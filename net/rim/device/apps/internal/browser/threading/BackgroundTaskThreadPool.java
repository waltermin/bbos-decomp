package net.rim.device.apps.internal.browser.threading;

import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class BackgroundTaskThreadPool {
   private Worker[] _workers = new Worker[0];
   private Job[] _tasks = new Job[0];
   private Object _lock = new Object();

   public BackgroundTaskThreadPool() {
      this.setMaxWorkers(2);
   }

   public final void setMaxWorkers(int number) {
      if (number > 0 && number != this._workers.length) {
         if (number < this._workers.length) {
            for (int i = this._workers.length - 1; i >= number; i--) {
               this._workers[i].shutdownNow();
            }

            Array.resize(this._workers, number);
         } else {
            int oldSize = this._workers.length;
            Array.resize(this._workers, number);

            for (int i = oldSize; i < number; i++) {
               this._workers[i] = new Worker(this);
               this._workers[i].start();
            }
         }
      }
   }

   public final void cancelAllJobs() {
      Job[] tasksToCancel = null;
      synchronized (this._lock) {
         if (this._tasks.length == 0) {
            return;
         }

         tasksToCancel = this._tasks;
         this._tasks = new Job[0];
      }

      for (int i = tasksToCancel.length - 1; i >= 0; i--) {
         tasksToCancel[i].cancel();
      }

      for (int i = this._workers.length - 1; i >= 0; i--) {
         this._workers[i].cancelPendingJob();
      }
   }

   public final void cancelJob(Job j) {
      if (j != null) {
         synchronized (this._lock) {
            Arrays.remove(this._tasks, j);
         }

         j.cancel();
      }
   }

   public final void cancelJobs(Job[] jobs) {
      if (jobs != null) {
         synchronized (this._lock) {
            for (int i = jobs.length - 1; i >= 0; i--) {
               Arrays.remove(this._tasks, jobs[i]);
            }
         }

         for (int i = jobs.length - 1; i >= 0; i--) {
            Job job = jobs[i];
            if (job != null) {
               job.cancel();
            }
         }
      }
   }

   public final void scheduleJob(Job j) {
      if (j != null) {
         synchronized (this._lock) {
            Arrays.add(this._tasks, j);
            this._lock.notify();
         }
      }
   }

   public final void scheduleJobs(Job[] jobs) {
      if (jobs != null && jobs.length != 0) {
         synchronized (this._lock) {
            int pos = this._tasks.length;
            Array.resize(this._tasks, pos + jobs.length);
            System.arraycopy(jobs, 0, this._tasks, pos, jobs.length);
            this._lock.notifyAll();
         }
      }
   }

   final Job getNextJob(Worker w) {
      synchronized (this._lock) {
         while (this._tasks.length == 0) {
            if (w._shutdown) {
               return null;
            }

            try {
               this._lock.wait();
            } finally {
               continue;
            }
         }

         if (w._shutdown) {
            return null;
         }

         Job j = this._tasks[0];
         Arrays.removeAt(this._tasks, 0);
         return j;
      }
   }
}
