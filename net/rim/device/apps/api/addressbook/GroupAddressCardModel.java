package net.rim.device.apps.api.addressbook;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.collection.WritableList;
import net.rim.device.api.collection.WritableSet;
import net.rim.device.apps.api.framework.model.RIMModel;

public interface GroupAddressCardModel extends AddressCardElement, ReadableList, WritableSet, WritableList, MemberComparator {
   byte EMAIL;
   byte PIN;
   byte PHONE;

   String getName();

   void setName(String var1);

   boolean isValid();

   boolean isEditable();

   Object getAddressCardModelAt(int var1);

   RIMModel getAddressModelAt(int var1);

   byte getAddressModelTypeAt(int var1);

   void warnUserSomeAddressesCannotReceive(String var1);
}
