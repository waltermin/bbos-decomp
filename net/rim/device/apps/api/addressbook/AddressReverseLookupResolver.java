package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.Recognizer;

public interface AddressReverseLookupResolver {
   Object reverseLookup(Object var1);

   Object[] reverseLookup(Object var1, Recognizer var2);

   Object getAddressCard(long var1);
}
