package net.rim.device.apps.internal.phone.model;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

final class AbstractPhoneNumberModel$ViewAddressVerb extends Verb {
   private RIMModel _address;

   public AbstractPhoneNumberModel$ViewAddressVerb(RIMModel address) {
      super(16908576);
      this._address = address;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(9158);
   }

   @Override
   public final Object invoke(Object context) {
      ContextObject viewCtx = new ContextObject(18, 45);
      Verb[] verbs = new Verb[0];
      if (this._address instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)this._address;
         verbProvider.getVerbs(viewCtx, verbs);
      }

      return verbs.length > 0 ? verbs[0].invoke(context) : null;
   }
}
