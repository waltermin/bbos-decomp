package net.rim.device.apps.internal.sms.message;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;
import net.rim.device.apps.api.utility.general.Copyable;
import net.rim.device.apps.api.utility.general.SetParameter;
import net.rim.device.apps.internal.sms.resources.SMSResources;

final class UseOnceSMSButtonVerb extends WrapperVerb implements SetParameter, Copyable {
   Object _phoneNumber = null;

   public UseOnceSMSButtonVerb(Verb innerVerb) {
      super(innerVerb, null, 1266944);
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject ctx;
      if (this._phoneNumber != null) {
         ctx = ContextObject.clone(parameter);
         ContextObject.put(ctx, 247, this._phoneNumber.toString());
      } else {
         ctx = (ContextObject)parameter;
      }

      return super._innerVerb.invoke(ctx);
   }

   @Override
   public final String toString() {
      return this._phoneNumber != null
         ? ((StringBuffer)(new Object())).append(SMSResources.getString(379)).append(' ').append(this._phoneNumber.toString()).toString()
         : SMSResources.getString(379);
   }

   @Override
   public final Object copy() {
      return new UseOnceSMSButtonVerb(super._innerVerb);
   }

   @Override
   public final void setParameter(Object parameter) {
      if (parameter instanceof Object) {
         ContextObject contextObject = (ContextObject)parameter;
         Object phoneNumber = contextObject.get(247);
         if (phoneNumber != null) {
            this._phoneNumber = phoneNumber;
         }
      }
   }
}
