package net.rim.device.apps.api.addressbook;

class AddressBookComboField$1 implements Runnable {
   private final AddressBookComboField this$0;

   AddressBookComboField$1(AddressBookComboField _1) {
      this.this$0 = _1;
   }

   @Override
   public void run() {
      ((AddressBookComboField$AddressBookCallback)this.this$0.getList().getCallback()).update();
   }
}
