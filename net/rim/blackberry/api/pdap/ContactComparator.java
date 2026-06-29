package net.rim.blackberry.api.pdap;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.microedition.pim.Contact;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;

final class ContactComparator implements Enumeration {
   Enumeration _addressCards;
   Object _nextAddress;
   ContactListImpl _contactList;
   ContactComparator$MatchProvider _matchProvider;
   private int _searchType;

   public ContactComparator(String matchString, Enumeration addressCards, ContactListImpl contactList, boolean nameComparator, int searchType) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._searchType = searchType;
      if (nameComparator) {
         this._matchProvider = new ContactComparator$StringNameMatchProvider(matchString);
      } else {
         this._matchProvider = new ContactComparator$StringMatchProvider(matchString);
      }
   }

   public ContactComparator(Contact contact, Enumeration addressCards, ContactListImpl contactList, boolean nameComparator) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._searchType = 1;
      if (nameComparator) {
         this._matchProvider = new ContactComparator$ContactNameMatchProvider(this, contact);
      } else {
         this._matchProvider = new ContactComparator$ContactMatchProvider(this, contact);
      }
   }

   public ContactComparator(String category, Enumeration addressCards, ContactListImpl contactList) {
      this._addressCards = addressCards;
      this._contactList = contactList;
      this._searchType = 1;
      this._matchProvider = new ContactComparator$CategoryMatchProvider(this, category);
   }

   @Override
   public final boolean hasMoreElements() {
      if (this._nextAddress != null) {
         return true;
      }

      while (this._addressCards.hasMoreElements()) {
         Object nextAddressCard = this._addressCards.nextElement();
         if (nextAddressCard instanceof AddressCardModel && (this._searchType & 1) != 0) {
            if (this._matchProvider instanceof ContactComparator$ContactMatchProvider) {
               ContactImpl nextAddress = new ContactImpl(nextAddressCard, this._contactList);
               if (this._matchProvider.matches(nextAddress)) {
                  this._nextAddress = nextAddress;
                  return true;
               }
            } else if (this._matchProvider.matches((AddressCardModel)nextAddressCard)) {
               this._nextAddress = new ContactImpl(nextAddressCard, this._contactList);
               return true;
            }
         } else if (nextAddressCard instanceof GroupAddressCardModel
            && (this._searchType & 2) != 0
            && this._matchProvider.comparesGroups()
            && this._matchProvider.matches((GroupAddressCardModel)nextAddressCard)) {
            this._nextAddress = new ContactGroupImpl(nextAddressCard, this._contactList);
            return true;
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
