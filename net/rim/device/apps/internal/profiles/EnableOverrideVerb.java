package net.rim.device.apps.internal.profiles;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class EnableOverrideVerb extends Verb {
   private Override _override;

   EnableOverrideVerb(Override override) {
      super(626736);
      this._override = override;
   }

   @Override
   public String toString() {
      return CommonResources.getString(900);
   }

   @Override
   public Object invoke(Object context) {
      this._override.setEnabled(!this._override.isEnabled());
      Overrides.getInstance().statusToggled(this._override);
      return null;
   }
}
