package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.internal.provisioning.ActivationService;

public final class SecurityOptionsItem$ReKeyVerb extends Verb {
   private String _uid;

   SecurityOptionsItem$ReKeyVerb(String uid) {
      super(200977, CommonResources.getResourceBundle(), 9113);
      this._uid = uid;
   }

   @Override
   public final Object invoke(Object parameter) {
      ActivationService activationService = ActivationService.getInstance();
      activationService.regenerateKey(this._uid, true);
      return null;
   }
}
