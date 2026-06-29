package net.rim.device.apps.internal.blackberryemail.address;

import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class ViewAddressVerb extends Verb {
   private Object _addressToView;

   public ViewAddressVerb(Object addressToView) {
      super(16908576, EmailResources.getResourceBundle(), 1110);
      this._addressToView = addressToView;
   }

   @Override
   public final Object invoke(Object parameter) {
      ContextObject context = new ContextObject();
      Verb[] verbs = new Verb[0];
      context.setFlag(45, 18);
      if (this._addressToView instanceof VerbProvider) {
         VerbProvider verbProvider = (VerbProvider)this._addressToView;
         verbProvider.getVerbs(context, verbs);
      }

      if (verbs.length > 0) {
         verbs[0].invoke(parameter);
      }

      return null;
   }
}
