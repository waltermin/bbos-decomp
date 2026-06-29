package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Contact;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;

class ContactComparator$ContactMatchProvider extends ContactComparator$MatchProvider {
   private Contact _baseContact;
   private final ContactComparator this$0;

   public ContactComparator$ContactMatchProvider(ContactComparator _1, Contact contact) {
      this.this$0 = _1;
      this._baseContact = contact;
   }

   @Override
   public boolean comparesGroups() {
      return false;
   }

   @Override
   public boolean matches(ContactImpl contact) {
      int[] fields = this._baseContact.getFields();

      for (int i = 0; i < fields.length; i++) {
         int field = fields[i];
         if (this.this$0._contactList.isSupportedField(field)) {
            int count = this._baseContact.countValues(field);
            if (field != 106 && field != 100) {
               for (int j = 0; j < count; j++) {
                  String value = this._baseContact.getString(field, j);
                  int count2 = contact.countValues(field);
                  boolean found = false;

                  for (int k = 0; k < count2; k++) {
                     if (this.match(value, contact.getString(field, k))) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }
            } else {
               for (int j = 0; j < count; j++) {
                  String[] values = this._baseContact.getStringArray(field, j);
                  int count2 = contact.countValues(field);
                  boolean found = false;

                  for (int k = 0; k < count2; k++) {
                     String[] values2 = contact.getStringArray(field, k);
                     int numvals = values2.length;
                     boolean allmatch = true;

                     for (int m = 0; m < numvals; m++) {
                        if (values[m] != null && !this.match(values[m], values2[m])) {
                           allmatch = false;
                           break;
                        }
                     }

                     if (allmatch) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   @Override
   public boolean matches(GroupAddressCardModel groupAddressCard) {
      return false;
   }

   @Override
   public boolean matches(AddressCardModel addressCard) {
      return false;
   }
}
