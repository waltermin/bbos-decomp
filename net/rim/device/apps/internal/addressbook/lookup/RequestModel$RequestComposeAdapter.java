package net.rim.device.apps.internal.addressbook.lookup;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

public class RequestModel$RequestComposeAdapter extends Verb {
   RequestModel _request;
   Verb _composeVerb;
   String _verbDescription;

   RequestModel$RequestComposeAdapter(RequestModel request, Verb composeVerb, int ordering, ContextObject context) {
      super(ordering);
      this._request = request;
      this._composeVerb = composeVerb;
      context.put(254, this._request);
      this._verbDescription = composeVerb.toString(context);
   }

   @Override
   public String toString() {
      return this._verbDescription;
   }

   @Override
   public Object invoke(Object parameter) {
      ContextObject contextObject = new ContextObject();
      contextObject.put(254, this._request);
      return this._composeVerb.invoke(contextObject);
   }
}
