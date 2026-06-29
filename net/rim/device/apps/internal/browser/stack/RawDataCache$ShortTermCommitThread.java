package net.rim.device.apps.internal.browser.stack;

final class RawDataCache$ShortTermCommitThread extends Thread {
   private boolean _stopped;
   private int _pauseDelay;
   private final RawDataCache this$0;

   RawDataCache$ShortTermCommitThread(RawDataCache _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      while (!this._stopped) {
         int lastSize = this.this$0._shortTermCache.persistNextItem();
         if (lastSize == -1) {
            return;
         }

         label65:
         try {
            Thread.sleep(this._pauseDelay);
         } finally {
            break label65;
         }

         if (this.this$0._shortTermPersistentObject == null) {
            return;
         }

         if (lastSize == 0) {
            this.this$0._shortTermPersistentObject.commit();

            try {
               Thread.sleep(this._pauseDelay);
            } finally {
               continue;
            }
         }
      }
   }

   public final void pauseCommit() {
      this._pauseDelay = 10000;
   }

   public final void unPauseCommit() {
      this._pauseDelay = 2000;
   }

   public final void shutdown() {
      this._stopped = true;
   }
}
