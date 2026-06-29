package net.rim.device.apps.internal.docview.gui;

class DocViewDisplayField$3 extends Thread {
   private final DocViewDisplayField this$0;

   DocViewDisplayField$3(DocViewDisplayField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0._docData.resumeParsing();
      this.this$0._parsingData.waitForData();
      this.this$0.notifyDataParsed(0);
      synchronized (this.this$0._syncObject) {
         this.this$0._duringPausedAutoMore = false;
      }
   }
}
