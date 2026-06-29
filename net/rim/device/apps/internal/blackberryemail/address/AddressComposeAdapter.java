package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;

final class AddressComposeAdapter extends Verb {
   private Object _address;
   private Verb _composeVerb;
   private Object _addressCard;
   private String _verbDescription;

   AddressComposeAdapter(Object address, Verb composeVerb, int order, Object context) {
      super(order);
      this._address = address;
      this._composeVerb = composeVerb;
      this._addressCard = ContextObject.get(context, -4055106280780392421L);
      ContextObject contextClone = ContextObject.clone(context);
      contextClone.put(254, address);
      this._verbDescription = composeVerb.toString(contextClone);
   }

   @Override
   public final String toString() {
      return this._verbDescription;
   }

   @Override
   public final String toString(Object context) {
      ContextObject contextClone = ContextObject.clone(context);
      contextClone.put(254, this._address);
      if (this._addressCard != null) {
         contextClone.put(-4055106280780392421L, this._addressCard);
      }

      return this._composeVerb.toString(contextClone);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject contextObject;
      if (parameter instanceof Object) {
         contextObject = ContextObject.clone(parameter);
      } else {
         contextObject = (ContextObject)(new Object());
      }

      contextObject.put(254, this._address);
      if (this._addressCard != null) {
         contextObject.put(-4055106280780392421L, this._addressCard);
      }

      return this._composeVerb.invoke(contextObject);
   }

   @Override
   public final int getVerbGroupId() {
      return this._composeVerb.getVerbGroupId();
   }
}
