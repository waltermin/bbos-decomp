package net.rim.device.apps.internal.implus;

import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;

final class ViewAddressVerb extends Verb {
   private Object _addressToView;

   public ViewAddressVerb(Object addressToView) {
      super(16908576);
      this._addressToView = addressToView;
   }

   @Override
   public final String toString() {
      return IMPlusResources.getString(2);
   }

   @Override
   public final Object invoke(Object parameter) {
      Verb[] verbs = new Object[0];
      if (this._addressToView instanceof Object) {
         VerbProvider verbProvider = (VerbProvider)this._addressToView;
         verbProvider.getVerbs(new Object(45, 18), verbs);
      }

      if (verbs.length > 0) {
         verbs[0].invoke(parameter);
      }

      return null;
   }
}
