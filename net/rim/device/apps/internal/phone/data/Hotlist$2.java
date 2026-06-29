package net.rim.device.apps.internal.phone.data;

class Hotlist$2 extends Thread {
   private final Hotlist this$0;

   Hotlist$2(Hotlist _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.doAddressBookSanityCheck(true);
   }
}
