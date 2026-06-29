package net.rim.blackberry.api.pim;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import net.rim.device.apps.api.addressbook.AddressCardModel;

final class ContactComparator implements Enumeration {
   Enumeration _addressCards;
   Contact _nextAddress;
   ContactListImpl _contactList;
   ContactComparator$MatchProvider _matchProvider;

   public ContactComparator(Contact contact, Enumeration addressCards, ContactListImpl contactList) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._matchProvider = new ContactComparator$ContactMatchProvider(this, contact);
   }

   public ContactComparator(String matchString, Enumeration addressCards, ContactListImpl contactList) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._matchProvider = new ContactComparator$StringMatchProvider(matchString);
   }

   @Override
   public final boolean hasMoreElements() {
      if (this._nextAddress != null) {
         return true;
      }

      while (this._addressCards.hasMoreElements()) {
         Object nextAddressCard = this._addressCards.nextElement();
         if (nextAddressCard instanceof AddressCardModel) {
            ContactImpl nextAddress = new ContactImpl(nextAddressCard, this._contactList);
            if (this._matchProvider.matches(nextAddress)) {
               this._nextAddress = nextAddress;
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public final Object nextElement() {
      if (this._nextAddress != null) {
         Object obj = this._nextAddress;
         this._nextAddress = null;
         return obj;
      } else if (this.hasMoreElements()) {
         Object obj = this._nextAddress;
         this._nextAddress = null;
         return obj;
      } else {
         throw new NoSuchElementException();
      }
   }
}
