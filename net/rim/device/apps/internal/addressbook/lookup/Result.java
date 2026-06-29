package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.util.SortedReadableList;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.RIMModel;

public class Result implements CollectionEventSource {
   private int _fields;
   int _transactionId;
   int _action;
   SortedReadableList _matches = new SortedReadableList(
      AddressBookServices.getAddressBook().getComparator(null, AddressBookServices.getAddressBook().getAddressBookOptions().getSortOrder())
   );
   int _availableMatches;
   int _errorCode;
   String _errorString;
   private static final int FSM_ERROR_CODE = 1;
   private static final int FSM_ERROR_STRING = 2;
   private static final int FSM_NUMBER_AVAILABLE = 4;
   private static final int FSM_NULL = 0;

   Result(int transactionId, int action) {
      this._transactionId = transactionId;
      this._action = action;
   }

   public void addItem(Object addr) {
      this._matches.elementAdded(null, addr);
   }

   public void deleteItem(Object element) {
      this._matches.elementRemoved(null, element);
   }

   void setAvailableMatches(int v) {
      this._fields |= 4;
      this._availableMatches = v;
   }

   void setErrorString(byte[] str) {
      this._errorString = new String(str);
      this._fields |= 2;
   }

   void setErrorCode(int v) {
      this._fields |= 1;
      this._errorCode = v;
   }

   boolean isCoherent() {
      switch (this._action) {
         case 2:
            if ((this._fields & 3) != 0) {
               return false;
            }

            if ((this._fields & 4) == 0) {
               this.setAvailableMatches(this._matches.size());
               return true;
            }
            break;
         case 128:
         case 129:
            if ((this._fields & -4) != 0) {
               return false;
            }
            break;
         default:
            return false;
      }

      return true;
   }

   public int getIncludedMatches() {
      return this._matches.size();
   }

   RIMModel getAddress(int index) {
      return index < this._matches.size() ? (RIMModel)this._matches.getAt(index) : null;
   }

   boolean hasError() {
      return (this._fields & 3) != 0;
   }

   String getRawErrorMessage() {
      return this._errorString;
   }

   int getRawErrorCode() {
      return this._errorCode;
   }

   private void copyResultsFrom(Result old) {
      SortedReadableList newMatches = this._matches;
      this._matches = old._matches;
      int n = newMatches.size();

      for (int i = 0; i < n; i++) {
         this.addItem(newMatches.getAt(i));
      }

      if ((this._fields & 4) == 0 && (old._fields & 4) != 0) {
         this.setAvailableMatches(old._availableMatches);
      }
   }

   Result mergeMore(Result old) {
      if (old != null) {
         this.copyResultsFrom(old);
      }

      return this;
   }

   void resort(Object context, long sortOrder) {
      this._matches.setComparator(AddressBookServices.getAddressBook().getComparator(context, sortOrder));
   }

   @Override
   public void addCollectionListener(Object listener) {
      this._matches.addCollectionListener(listener);
   }

   @Override
   public void removeCollectionListener(Object listener) {
      this._matches.removeCollectionListener(listener);
   }
}
