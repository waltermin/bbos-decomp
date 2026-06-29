package net.rim.device.cldc.io.sync.command;

public final class Delete extends Record {
   public Delete() {
      this.setTag(2);
   }

   @Override
   public final boolean isValid() {
      return true;
   }

   public final boolean deleteAll() {
      return this.getRecordUID() == 0;
   }
}
