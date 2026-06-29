package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class DeleteOverrideVerb extends Verb {
   private Override _override;

   DeleteOverrideVerb(Override override) {
      super(626752, CommonResource.getBundle(), 17);
      this._override = override;
   }

   @Override
   public Object invoke(Object context) {
      ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
      int retVal = Dialog.ask(2, resources.getString(243), -1);
      if (retVal == 3) {
         Overrides.getInstance().delete(this._override);
      }

      return null;
   }
}
