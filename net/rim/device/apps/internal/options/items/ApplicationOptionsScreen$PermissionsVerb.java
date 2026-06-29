package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ApplicationControlInformation;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ApplicationOptionsScreen$PermissionsVerb extends Verb {
   int[] _handles;
   String _name;

   public ApplicationOptionsScreen$PermissionsVerb() {
      super(16986368, OptionsResources.getResourceBundle(), 1960);
   }

   public ApplicationOptionsScreen$PermissionsVerb(int[] handles, String name) {
      super(598288, OptionsResources.getResourceBundle(), 1959);
      this._handles = handles;
      this._name = name;
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationControlInformation aci;
      if (this._handles == null) {
         aci = new ApplicationControlInformation(0);
      } else {
         aci = new ApplicationControlInformation(this._handles, this._name);
      }

      aci.open();
      return null;
   }
}
