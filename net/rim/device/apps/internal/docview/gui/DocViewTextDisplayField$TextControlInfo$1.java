package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$TextControlInfo$1 implements Runnable {
   private final DocViewTextDisplayField$TextControlInfo this$1;

   DocViewTextDisplayField$TextControlInfo$1(DocViewTextDisplayField$TextControlInfo _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0._duringLayoutActivity = true;
      this.this$1.processLinks(false);
      this.this$1.this$0._duringLayoutActivity = false;
      if (this.this$1.this$0._incLayoutRequested) {
         DocViewTextDisplayField.access$4100(this.this$1.this$0);
         this.this$1.this$0._incLayoutRequested = false;
      }
   }
}
