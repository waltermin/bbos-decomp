package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Contact;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;

class ContactComparator$ContactNameMatchProvider extends ContactComparator$MatchProvider {
   String[] names;
   private final ContactComparator this$0;

   public ContactComparator$ContactNameMatchProvider(ContactComparator _1, Contact contact) {
      this.this$0 = _1;
      this.names = contact.getStringArray(106, 0);
   }

   @Override
   public boolean comparesGroups() {
      return false;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      return false;
   }

   @Override
   public boolean matches(GroupAddressCardModel groupAddressCard) {
      return false;
   }

   @Override
   public boolean matches(AddressCardModel addressCard) {
      PersonNameModel personalModel = addressCard.getName();
      if (personalModel != null && (this.names[0] != null || this.names[1] != null)) {
         return this.names[0] != null && !this.match(this.names[0], personalModel.getLastName())
            ? false
            : this.names[1] == null || this.match(this.names[1], personalModel.getFirstName());
      } else {
         return false;
      }
   }
}
