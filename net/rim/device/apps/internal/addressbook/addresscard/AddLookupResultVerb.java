package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.addressbook.lookup.ALPConfiguration;
import net.rim.device.apps.internal.addressbook.lookup.Request;
import net.rim.device.apps.internal.addressbook.lookup.Result;

final class AddLookupResultVerb extends Verb {
   private Request _request;

   AddLookupResultVerb(Object request) {
      super(0);
      this._request = (Request)request;
   }

   @Override
   public final Object invoke(Object newModel) {
      if (!(newModel instanceof Object)) {
         throw new Object();
      }

      Request.addLookupResultToAddressBook(newModel);
      if (this._request != null) {
         Result result = this._request.getResult();
         if (result != null && result.getIncludedMatches() == 1) {
            ALPConfiguration.getManager().deleteRequest(this._request);
         }
      }

      return newModel;
   }
}
