package net.rim.device.apps.internal.addressbook.groupaddresscard;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class GroupComposeAdapter extends Verb {
   private Object _gacm;
   private Verb _composeVerb;
   private ContextObject _context;
   private String _verbDescription;

   GroupComposeAdapter(Object gacm, Verb composeVerb, int ordering, Object context) {
      super(ordering);
      this._gacm = gacm;
      this._composeVerb = composeVerb;
      this._context = ContextObject.clone(context);
      this._context.put(254, gacm);
      this._verbDescription = composeVerb.toString(this._context);
   }

   @Override
   public final String toString() {
      return this._verbDescription;
   }

   @Override
   public final String toString(Object context) {
      Object oldValue = ContextObject.get(context, 254);
      ContextObject.put(context, 254, this._gacm);
      String result = this._composeVerb.toString(context);
      if (oldValue != null) {
         ContextObject.put(context, 254, oldValue);
         return result;
      } else {
         ContextObject.remove(context, 254);
         return result;
      }
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject contextObject = (ContextObject)(new Object());
      contextObject.put(254, this._gacm);
      return this._composeVerb.invoke(contextObject);
   }

   @Override
   public final int getVerbGroupId() {
      return this._composeVerb.getVerbGroupId();
   }
}
