package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class ShowAddressBookVerb extends Verb {
   public ShowAddressBookVerb(int ordering) {
      super(ordering, PhoneResources.getResourceBundle(), 448);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context = ContextObject.castOrCreate(parameter);
      ContextObject.setPrivateFlag(context, 4936088360624690805L, 1);
      AddressBookServices.showAddressBook(context);
      return null;
   }
}
