package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIMItem;

public interface BlackBerryContactGroup extends PIMItem {
   int ADDRESS_TYPE_EMAIL = 0;
   int ADDRESS_TYPE_PIN = 1;
   int ADDRESS_TYPE_PHONE = 2;
   int NAME = 100;
   int UID = 101;

   Contact getContact(int var1);

   String getAddress(int var1);

   int getAddressType(int var1);

   int numContacts();
}
