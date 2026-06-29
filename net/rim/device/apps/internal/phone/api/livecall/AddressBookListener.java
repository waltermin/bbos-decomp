package net.rim.device.apps.internal.phone.api.livecall;

import net.rim.device.api.collection.Collection;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.system.Application;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.phone.api.AddressBookDependentObject;

class AddressBookListener implements RIMModel, CollectionListener, AddressBookDependentObject {
   protected boolean _outOfSyncWithAddressBook = false;
   protected int _lastAddressBookUpdateType;
   protected Object _lastAddressBookUpdateObject;
   private Application _app = Application.getApplication();
   private WeakCollectionListener _weakListener;
   private AddressBookListener$DeferUpdate _deferUpdate = new AddressBookListener$DeferUpdate(this);

   protected void startListeningForAddressBookUpdates() {
      if (this._weakListener == null) {
         this._weakListener = new WeakCollectionListener(this);
         this._weakListener.startListening();
      }
   }

   protected void stopListeningForAddressBookUpdates() {
      if (this._weakListener != null) {
         this._weakListener.stopListening();
         this._weakListener = null;
      }
   }

   protected void addressBookUpdated() {
      throw null;
   }

   protected Object getLastAddressBookUpdateObject() {
      return this._lastAddressBookUpdateObject;
   }

   protected void setIsOutOfSyncWithAddressBook(boolean outOfSyncWithAddressBook) {
      this._outOfSyncWithAddressBook = outOfSyncWithAddressBook;
   }

   protected int getLastAddressBookUpdateType() {
      return this._lastAddressBookUpdateType;
   }

   @Override
   public boolean addressBookUpdated(int updateType, Object o) {
      return false;
   }

   @Override
   public boolean isOutOfSyncWithAddressBook() {
      return this._outOfSyncWithAddressBook;
   }

   @Override
   public void reset(Collection collection) {
      this.scheduleDeferUpdate(0, null);
   }

   @Override
   public void elementAdded(Collection collection, Object element) {
      this.scheduleDeferUpdate(1, element);
   }

   @Override
   public void elementUpdated(Collection collection, Object oldElement, Object newElement) {
      this.scheduleDeferUpdate(3, newElement);
   }

   @Override
   public void elementRemoved(Collection collection, Object element) {
      this.scheduleDeferUpdate(2, element);
   }

   protected AddressBookListener(boolean startListening, Object context) {
      if (startListening) {
         this.startListeningForAddressBookUpdates();
      }
   }

   private void scheduleDeferUpdate(int updateType, Object updateObject) {
      AddressBookListener$DeferUpdate du = this._deferUpdate;
      boolean finished;
      synchronized (du) {
         finished = du._finished;
         if (finished) {
            this._lastAddressBookUpdateObject = updateObject;
            this._lastAddressBookUpdateType = updateType;
         } else {
            this._lastAddressBookUpdateObject = null;
            this._lastAddressBookUpdateType = 0;
         }

         this._outOfSyncWithAddressBook = true;
         du._finished = false;
      }

      if (finished) {
         this._app.invokeLater(du);
      }
   }
}
