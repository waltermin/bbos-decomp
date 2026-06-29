package net.rim.device.cldc.io.sync.command;

public final class Get extends Record {
   public Get() {
      this.setTag(5);
   }

   @Override
   public final boolean isValid() {
      return this.all() || super.isValid();
   }

   public final boolean all() {
      return this.getRecordUID() == 0;
   }
}
