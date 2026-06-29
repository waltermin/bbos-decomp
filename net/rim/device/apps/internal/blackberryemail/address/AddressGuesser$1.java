package net.rim.device.apps.internal.blackberryemail.address;

class AddressGuesser$1 extends Thread {
   private final AddressGuesser this$0;

   AddressGuesser$1(AddressGuesser _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.buildLearningDatabase0();
   }
}
