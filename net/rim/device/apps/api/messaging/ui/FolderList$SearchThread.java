package net.rim.device.apps.api.messaging.ui;

final class FolderList$SearchThread extends Thread {
   private boolean stop;
   private final FolderList this$0;

   public FolderList$SearchThread(FolderList _1) {
      this.this$0 = _1;
      this.stop = false;
   }

   public final void interruptSearch() {
      this.this$0._interrupted = true;
      synchronized (this.this$0._lockObj) {
         if (this.this$0._searchCount > 0) {
            FolderList.access$1010(this.this$0);
         }
      }
   }

   public final synchronized void stop() {
      this.stop = true;
      this.notify();
   }

   public final synchronized void wakeup() {
      this.notify();
   }

   @Override
   public final void run() {
      throw new RuntimeException("cod2jar: invokevirtual: slot out of range");
   }
}
