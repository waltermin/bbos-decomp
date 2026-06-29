package net.rim.device.apps.api.addressbook;

public final class DuplicateAddressBookEntryException extends RuntimeException {
   private Object[] _duplicates;

   public DuplicateAddressBookEntryException(Object[] duplicates) {
      this._duplicates = duplicates;
   }

   public final Object[] GetDuplicates() {
      return this._duplicates;
   }

   public DuplicateAddressBookEntryException(String message) {
   }
}
