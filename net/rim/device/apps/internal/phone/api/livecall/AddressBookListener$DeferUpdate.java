package net.rim.device.apps.internal.phone.api.livecall;

class AddressBookListener$DeferUpdate implements Runnable {
   private boolean _finished;
   private final AddressBookListener this$0;

   AddressBookListener$DeferUpdate(AddressBookListener _1) {
      this.this$0 = _1;
      this._finished = true;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         this.this$0.addressBookUpdated();
         var3 = false;
      } finally {
         if (var3) {
            this._finished = true;
         }
      }

      this._finished = true;
   }
}
