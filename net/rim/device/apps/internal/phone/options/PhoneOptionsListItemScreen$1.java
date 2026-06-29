package net.rim.device.apps.internal.phone.options;

class PhoneOptionsListItemScreen$1 implements Runnable {
   private final PhoneOptionsListItemScreen this$0;

   PhoneOptionsListItemScreen$1(PhoneOptionsListItemScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      this.this$0.optionsUpdated();
   }
}
