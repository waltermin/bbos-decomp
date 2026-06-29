package net.rim.device.apps.internal.browser.threading;

final class Worker extends Thread {
   private Job _currentJob;
   BackgroundTaskThreadPool _pool;
   boolean _shutdown;

   public Worker(BackgroundTaskThreadPool pool) {
      this._pool = pool;
   }

   public final void shutdownNow() {
      this._shutdown = true;
   }

   @Override
   public final void run() {
      while (true) {
         Job j = this._pool.getNextJob(this);
         if (j == null) {
            return;
         }

         synchronized (this) {
            this._currentJob = j;
         }

         label45:
         try {
            j.run();
         } finally {
            break label45;
         }

         synchronized (this) {
            this._currentJob = null;
            Job var11 = null;
         }
      }
   }

   public final void cancelPendingJob() {
      synchronized (this) {
         if (this._currentJob != null) {
            this._currentJob.cancel();
         }
      }
   }
}
