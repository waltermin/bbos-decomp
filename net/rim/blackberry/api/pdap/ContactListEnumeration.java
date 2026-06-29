package net.rim.blackberry.api.pdap;

import java.util.Enumeration;

final class ContactListEnumeration implements Enumeration {
   Enumeration _addressCards;
   ContactListImpl _contactList;
   Object _nextAddress;
   int _searchType;

   public ContactListEnumeration(Enumeration addressCards, ContactListImpl contactList, int searchType) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._nextAddress = null;
      this._searchType = searchType;
   }

   private final Object getNextAddress() {
      while (this._addressCards.hasMoreElements()) {
         Object next = this._addressCards.nextElement();
         if ((this._searchType & 1) != 0 && next instanceof Object) {
            return next;
         }

         if ((this._searchType & 2) != 0 && next instanceof Object) {
            return next;
         }
      }

      return null;
   }

   @Override
   public final boolean hasMoreElements() {
      if (this._nextAddress != null) {
         return true;
      }

      this._nextAddress = this.getNextAddress();
      return this._nextAddress != null;
   }

   @Override
   public final Object nextElement() {
      if (this._nextAddress == null) {
         this._nextAddress = this.getNextAddress();
         if (this._nextAddress == null) {
            throw new Object();
         }
      }

      Object contact;
      if (this._nextAddress instanceof Object) {
         contact = new ContactImpl(this._nextAddress, this._contactList);
      } else {
         contact = new ContactGroupImpl(this._nextAddress, this._contactList);
      }

      this._nextAddress = null;
      return contact;
   }
}
