package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.ContextObjectWR;
import net.rim.device.apps.api.framework.model.DefaultProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.internal.proxy.Proxy;

final class UpdateLastUsedEntryVerb extends WrapperVerb implements Runnable {
   private AddressCardModel _addressCard;
   Object _entry;
   private static ContextObjectWR _contextObjectWR = (ContextObjectWR)(new Object(36));

   UpdateLastUsedEntryVerb(AddressCardModel addressCard, Object entry, Verb verb) {
      super(verb, null, verb.getOrdering());
      this._addressCard = addressCard;
      this._entry = entry;
   }

   @Override
   public final Object invoke(Object parameter) {
      Object result = super._innerVerb.invoke(parameter);
      if (this._addressCard instanceof Object) {
         DefaultProvider defaultProvider = (DefaultProvider)this._addressCard;
         ContextObject contextObject = _contextObjectWR.getContextObject();
         synchronized (contextObject) {
            contextObject.put(6609423255094033855L, new Object(super._innerVerb.getVerbGroupId()));
            defaultProvider.getDefault(null, contextObject);
         }

         Proxy.getInstance().submitRunnable(this);
      }

      return result;
   }

   @Override
   public final void run() {
      DefaultProvider defaultProvider = (DefaultProvider)this._addressCard;
      ContextObject contextObject = _contextObjectWR.getContextObject();
      synchronized (contextObject) {
         contextObject.put(6609423255094033855L, new Object(super._innerVerb.getVerbGroupId()));
         defaultProvider.updateDefault(this._entry, contextObject);
      }
   }

   @Override
   public final RIMModel getRIMModel() {
      return (RIMModel)this._entry;
   }
}
