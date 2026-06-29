package net.rim.device.apps.internal.addressbook.ui;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;

final class AddressBookListUI$LastAddressWrapper implements PersistentContentListener {
   Object _object;

   AddressBookListUI$LastAddressWrapper() {
      PersistentContent.addWeakListener(this);
   }

   final void set(Object object) {
      this._object = object;
   }

   final Object get() {
      return this._object;
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      this._object = null;
   }
}
