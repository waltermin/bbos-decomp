package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

class EditOverrideVerb extends Verb {
   private Override _override;

   EditOverrideVerb(Override override) {
      super(override == null ? 626709 : 626720, CommonResource.getBundle(), override == null ? 13 : 16);
      this._override = override;
   }

   @Override
   public String toString() {
      if (this._override == null) {
         ResourceBundle resources = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         return resources.getString(232);
      } else {
         return super.toString();
      }
   }

   @Override
   public Object invoke(Object context) {
      boolean newOverride = false;
      if (this._override == null) {
         newOverride = true;
         this._override = Overrides.getInstance().createNewOverride(null);
      }

      UiApplication.getUiApplication().pushScreen(new OverrideEditScreen(this._override, newOverride));
      return null;
   }
}
