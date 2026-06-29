package net.rim.device.apps.internal.addressbook;

import net.rim.device.api.util.IntComparator;

final class AddressBookCollection$CollatorComparator implements IntComparator {
   private AddressBookData _data;
   private AddressBookOrderHelper _helper;

   AddressBookCollection$CollatorComparator(AddressBookData data, long sortOrder) {
      this._data = data;
      this._helper = new AddressBookOrderHelper(null, sortOrder);
   }

   @Override
   public final int compare(int o1, int o2) {
      Object object1 = this._data.getElement(o1);
      Object object2 = this._data.getElement(o2);
      return this._helper.compare(object1, object2);
   }
}
