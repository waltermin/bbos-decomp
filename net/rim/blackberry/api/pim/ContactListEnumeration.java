package net.rim.blackberry.api.pim;

import java.util.Enumeration;

final class ContactListEnumeration implements Enumeration {
   Enumeration _addressCards;
   ContactListImpl _contactList;
   Object _nextAddress;

   public ContactListEnumeration(Enumeration addressCards, ContactListImpl contactList) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._nextAddress = null;
   }

   @Override
   public final boolean hasMoreElements() {
      if (this._nextAddress != null) {
         return true;
      }

      while (this._addressCards.hasMoreElements()) {
         this._nextAddress = this._addressCards.nextElement();
         if (this._nextAddress instanceof Object) {
            return true;
         }
      }

      return false;
   }

   @Override
   public final Object nextElement() {
      if (this._nextAddress == null) {
         Object na;
         do {
            na = this._addressCards.nextElement();
         } while (!(na instanceof Object));

         this._nextAddress = na;
      }

      Object contact = new ContactImpl(this._nextAddress, this._contactList);
      this._nextAddress = null;
      return contact;
   }
}
