package net.rim.device.apps.internal.addressbook.ui;

final class AddressBookListField$ResetListWhileHoldingALPManagerLock implements Runnable {
   private boolean _finished;
   private final AddressBookListField this$0;

   private AddressBookListField$ResetListWhileHoldingALPManagerLock(AddressBookListField _1) {
      this.this$0 = _1;
      this._finished = true;
   }

   private final boolean initiateReset() {
      synchronized (this.this$0._alpManager) {
         boolean finished = this._finished;
         this._finished = false;
         return finished;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void run() {
      synchronized (this.this$0._alpManager) {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            this.this$0.updateExtraRowCount();
            var6 = false;
         } finally {
            if (var6) {
               this._finished = true;
            }
         }

         this._finished = true;
      }
   }

   AddressBookListField$ResetListWhileHoldingALPManagerLock(AddressBookListField x0, AddressBookListField$1 x1) {
      this(x0);
   }
}
