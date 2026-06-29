package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailHeaderModel$AddLookupResultToAddressBookVerb extends Verb {
   private AddressCardModel _result;

   EmailHeaderModel$AddLookupResultToAddressBookVerb(AddressCardModel result) {
      super(16908560, EmailResources.getResourceBundle(), 1123);
      this._result = result;
   }

   @Override
   public final Object invoke(Object parameter) {
      Request.addLookupResultToAddressBook(this._result);
      return null;
   }
}
