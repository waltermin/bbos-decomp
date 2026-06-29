package net.rim.device.apps.internal.docview.gui;

class DocViewTextDisplayField$5 implements Runnable {
   private final Runnable[] val$appUpdates;
   private final DocViewTextDisplayField this$0;

   DocViewTextDisplayField$5(DocViewTextDisplayField _1, Runnable[] _2) {
      this.this$0 = _1;
      this.val$appUpdates = _2;
   }

   @Override
   public void run() {
      this.this$0._duringLayoutActivity = true;
      this.this$0.executeUpdates(this.val$appUpdates);
      this.this$0._duringLayoutActivity = false;
      if (this.this$0._incLayoutRequested) {
         DocViewTextDisplayField.access$500(this.this$0);
         this.this$0._incLayoutRequested = false;
      }
   }
}
