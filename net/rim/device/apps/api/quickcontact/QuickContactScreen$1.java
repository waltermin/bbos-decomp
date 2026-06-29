package net.rim.device.apps.api.quickcontact;

class QuickContactScreen$1 implements Runnable {
   private final int val$event;
   private final int val$idx;
   private final QuickContactScreen this$0;

   QuickContactScreen$1(QuickContactScreen _1, int _2, int _3) {
      this.this$0 = _1;
      this.val$event = _2;
      this.val$idx = _3;
   }

   @Override
   public void run() {
      switch (this.val$event) {
         case 0:
         case 2:
            this.this$0._listField.setSelectedIndex(this.val$idx);
            return;
         default:
            this.this$0._listField.invalidate(this.val$idx);
      }
   }
}
