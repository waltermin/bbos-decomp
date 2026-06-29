package net.rim.device.apps.internal.phone.api.livecall;

import java.lang.ref.WeakReference;
import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.apps.api.addressbook.AddressBook;
import net.rim.device.apps.api.addressbook.AddressBookServices;

final class WeakCollectionListener extends WeakReference implements CollectionListener {
   public WeakCollectionListener(CollectionListener listener) {
   }

   private final CollectionListener getListener() {
      Object listenerObj = this.get();
      if (listenerObj == null) {
         this.stopListening();
      }

      return (CollectionListener)listenerObj;
   }

   public final void startListening() {
      AddressBook ab = AddressBookServices.getAddressBook(false);
      if (ab != null) {
         ab.addCollectionListener(this);
      }
   }

   public final void stopListening() {
      AddressBook ab = AddressBookServices.getAddressBook(false);
      if (ab != null) {
         ab.removeCollectionListener(this);
      }
   }

   @Override
   public final void reset(Collection collection) {
      CollectionListener listener = this.getListener();
      if (listener != null) {
         listener.reset(collection);
      }
   }

   @Override
   public final void elementAdded(Collection collection, Object element) {
      CollectionListener listener = this.getListener();
      if (listener != null) {
         listener.elementAdded(collection, element);
      }
   }

   @Override
   public final void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      CollectionListener listener = this.getListener();
      if (listener != null) {
         listener.elementUpdated(collection, oldElement, newElement);
      }
   }

   @Override
   public final void elementRemoved(Collection collection, Object element) {
      CollectionListener listener = this.getListener();
      if (listener != null) {
         listener.elementRemoved(collection, element);
      }
   }
}
