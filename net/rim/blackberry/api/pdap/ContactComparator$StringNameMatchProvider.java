package net.rim.blackberry.api.pdap;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;

class ContactComparator$StringNameMatchProvider extends ContactComparator$MatchProvider {
   private String _matchString;

   public ContactComparator$StringNameMatchProvider(String matchString) {
      this._matchString = matchString;
   }

   @Override
   public boolean comparesGroups() {
      return true;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      return false;
   }

   @Override
   public boolean matches(GroupAddressCardModel groupAddressCard) {
      return this.match(this._matchString, groupAddressCard.getName());
   }

   @Override
   public boolean matches(AddressCardModel addressCard) {
      PersonNameModel personalModel = addressCard.getName();
      if (personalModel != null) {
         if (this.match(this._matchString, personalModel.getFirstName())) {
            return true;
         }

         if (this.match(this._matchString, personalModel.getLastName())) {
            return true;
         }
      }

      return false;
   }
}
