package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.apps.internal.addressbook.resources.AddressBookResources;

final class AddressBookOptionsScreen$SortOrder {
   private String _name;
   private long _key;
   private static final AddressBookOptionsScreen$SortOrder[] _sortOrders = new AddressBookOptionsScreen$SortOrder[]{
      new AddressBookOptionsScreen$SortOrder(1232448844688687736L, 1400),
      new AddressBookOptionsScreen$SortOrder(-227891759293611117L, 1401),
      new AddressBookOptionsScreen$SortOrder(-4388042602796535003L, 1402)
   };

   private AddressBookOptionsScreen$SortOrder(long key, int resourceID) {
      this._key = key;
      this._name = AddressBookResources.getString(resourceID);
   }

   @Override
   public final String toString() {
      return this._name;
   }

   static final long getKey(int index) {
      return _sortOrders[index]._key;
   }

   static final Object[] getSortOrders() {
      return _sortOrders;
   }

   static final int getSortOrder(long key) {
      int n = _sortOrders.length;

      for (int i = 0; i < n; i++) {
         if (_sortOrders[i]._key == key) {
            return i;
         }
      }

      return 0;
   }
}
