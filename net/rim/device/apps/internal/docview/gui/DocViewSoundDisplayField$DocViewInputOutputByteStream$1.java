package net.rim.device.apps.internal.docview.gui;

class DocViewSoundDisplayField$DocViewInputOutputByteStream$1 implements Runnable {
   private final DocViewSoundDisplayField$DocViewInputOutputByteStream this$1;

   DocViewSoundDisplayField$DocViewInputOutputByteStream$1(DocViewSoundDisplayField$DocViewInputOutputByteStream _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0.executeMore(null, true, true);
   }
}
