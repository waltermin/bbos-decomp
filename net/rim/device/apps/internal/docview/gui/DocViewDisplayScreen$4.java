package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$4 extends Thread {
   private final Object val$scr;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$4(DocViewDisplayScreen _1, Object _2) {
      this.this$0 = _1;
      this.val$scr = _2;
   }

   @Override
   public void run() {
      synchronized (this.val$scr) {
         if (this.this$0._idleSyncInProgress || this.this$0._displayingData) {
            return;
         }

         this.this$0._idleSyncInProgress = true;
      }

      label49:
      try {
         this.this$0.executeIdleTimeMore();
      } finally {
         break label49;
      }

      synchronized (this.val$scr) {
         this.this$0._idleSyncInProgress = false;
      }
   }
}
