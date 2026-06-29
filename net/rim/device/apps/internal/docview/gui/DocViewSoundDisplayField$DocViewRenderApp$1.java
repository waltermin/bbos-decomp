package net.rim.device.apps.internal.docview.gui;

class DocViewSoundDisplayField$DocViewRenderApp$1 extends Thread {
   private final Runnable val$runnable;
   private final DocViewSoundDisplayField$DocViewRenderApp this$1;

   DocViewSoundDisplayField$DocViewRenderApp$1(DocViewSoundDisplayField$DocViewRenderApp _1, Runnable _2) {
      this.this$1 = _1;
      this.val$runnable = _2;
   }

   @Override
   public void run() {
      this.val$runnable.run();
   }
}
