package net.rim.tid.OTAsync;

import net.rim.device.internal.proxy.Proxy;

class WordSyncCollection$Cleaner implements Runnable {
   private int _id;
   private final WordSyncCollection this$0;
   public static final int TIMEOUT = 1800000;

   WordSyncCollection$Cleaner(WordSyncCollection _1) {
      this.this$0 = _1;
      this._id = -1;
   }

   public void start() {
      if (this._id == -1) {
         if (this.this$0._debugOutputEnabled) {
            System.err.println("<<cleaner::start>>");
         }

         this._id = Proxy.getInstance().invokeLater(this, 1800000, false);
      }
   }

   public void cancel() {
      if (this._id != -1) {
         if (this.this$0._debugOutputEnabled) {
            System.err.println("<<cleaner::cancel>>");
         }

         Proxy.getInstance().cancelInvokeLater(this._id);
         this._id = -1;
      }
   }

   @Override
   public void run() {
      if (this._id != -1) {
         this._id = -1;
         if (this.this$0._debugOutputEnabled) {
            System.err.println("<<cleaner::run>>");
         }

         this.this$0.cleanCache();
      }
   }
}
