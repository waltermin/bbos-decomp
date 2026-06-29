package net.rim.device.apps.internal.phone.data;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.framework.verb.WrapperVerb;

final class PhoneCallModelImpl$DialMarkOpenWrapperVerb extends WrapperVerb {
   PhoneCallModelImpl _phoneCallModel;

   PhoneCallModelImpl$DialMarkOpenWrapperVerb(PhoneCallModelImpl phoneCallModel, Verb innerVerb) {
      super(innerVerb, null, innerVerb.getOrdering());
      this._phoneCallModel = phoneCallModel;
   }

   @Override
   public final Object invoke(Object parameter) {
      PhoneCallModelImpl.markOpened(this._phoneCallModel);
      return super.invoke(parameter);
   }
}
