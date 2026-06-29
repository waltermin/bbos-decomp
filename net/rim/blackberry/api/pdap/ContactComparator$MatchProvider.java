package net.rim.blackberry.api.pdap;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;

class ContactComparator$MatchProvider {
   public boolean matches(ContactImpl _1) {
      throw null;
   }

   public boolean matches(AddressCardModel _1) {
      throw null;
   }

   public boolean matches(GroupAddressCardModel _1) {
      throw null;
   }

   public boolean comparesGroups() {
      throw null;
   }

   protected boolean match(String pre, String val) {
      if (val == null) {
         return false;
      }

      String prefix = pre.toLowerCase();
      String value = val.toLowerCase();
      int index = value.indexOf(prefix);
      return index >= 0;
   }
}
