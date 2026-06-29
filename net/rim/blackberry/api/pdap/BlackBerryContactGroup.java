package net.rim.blackberry.api.pdap;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIMItem;

public interface BlackBerryContactGroup extends PIMItem {
   int ADDRESS_TYPE_EMAIL;
   int ADDRESS_TYPE_PIN;
   int ADDRESS_TYPE_PHONE;
   int NAME;
   int UID;

   Contact getContact(int var1);

   String getAddress(int var1);

   int getAddressType(int var1);

   int numContacts();
}
