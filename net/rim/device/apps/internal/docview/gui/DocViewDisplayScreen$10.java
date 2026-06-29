package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayScreen$10 implements Runnable {
   private final ForwardScreen val$fScreen;
   private final boolean val$showTrackChanges;
   private final DocViewDisplayScreen this$0;

   DocViewDisplayScreen$10(DocViewDisplayScreen _1, ForwardScreen _2, boolean _3) {
      this.this$0 = _1;
      this.val$fScreen = _2;
      this.val$showTrackChanges = _3;
   }

   @Override
   public void run() {
      this.val$fScreen.displayElement(this.this$0._morePartID, this.this$0._archiveIndicator, this.this$0._partIndex, this.val$showTrackChanges);
   }
}
