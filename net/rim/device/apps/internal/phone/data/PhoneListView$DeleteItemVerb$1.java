package net.rim.device.apps.internal.phone.data;

class PhoneListView$DeleteItemVerb$1 implements Runnable {
   private final PhoneListView$DeleteItemVerb this$1;

   PhoneListView$DeleteItemVerb$1(PhoneListView$DeleteItemVerb _1) {
      this.this$1 = _1;
   }

   @Override
   public void run() {
      this.this$1.this$0.deleteSelectedItem();
   }
}
