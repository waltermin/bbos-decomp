package net.rim.device.apps.internal.phone.api.verbs;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class ThreeWayCallVerb extends Verb {
   private LiveCall _call;

   public ThreeWayCallVerb(LiveCall call) {
      super(70928);
      this._call = call;
   }

   @Override
   public final Object invoke(Object parameter) {
      this.startThreeWayCall();
      return null;
   }

   @Override
   public final String toString() {
      return PhoneResources.getString(465);
   }

   private final void startThreeWayCall() {
      Object connectionParams = PhoneUtilities.getCallConnectionFromAddressBook(null, null, false, true);
      if (connectionParams != null) {
         ContextObject context = new ContextObject();
         String phoneNumber = (String)ContextObject.get(connectionParams, 6486659828352467672L);
         DialVerb.startThreeWayCall(this._call, phoneNumber, context, (ContextObject)connectionParams);
      }
   }
}
