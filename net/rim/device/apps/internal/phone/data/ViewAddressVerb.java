package net.rim.device.apps.internal.phone.data;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.verbs.DialVerb;
import net.rim.device.apps.internal.phone.resource.PhoneContexts;

final class ViewAddressVerb extends Verb {
   private Object _addressToView;

   public ViewAddressVerb(Object addressToView) {
      super(16908576, CommonResources.getResourceBundle(), 9158);
      this._addressToView = addressToView;
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context = PhoneContexts.GET_VERBS_CONTEXT_WR.getContextObject();
      context.reset();
      Verb[] verbs = new Object[0];
      context.setFlag(45, 18);
      context.put(-409744358660961448L, DialVerb.getRecognizer());
      PhoneUtilities.setPrivateFlag(context, 1);
      if (this._addressToView instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)this._addressToView;
         verbProvider.getVerbs(context, verbs);
      }

      if (verbs.length > 0) {
         verbs[0].invoke(parameter);
      }

      return null;
   }
}
