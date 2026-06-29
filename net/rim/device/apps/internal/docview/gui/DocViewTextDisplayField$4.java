package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$4 implements Runnable {
   private final int val$currentBlockIndex;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$4(DocViewTextDisplayField _1, int _2) {
      this.this$0 = _1;
      this.val$currentBlockIndex = _2;
   }

   @Override
   public void run() {
      try {
         this.this$0.processNewDataImpl(this.val$currentBlockIndex);
      } finally {
         return;
      }
   }
}
