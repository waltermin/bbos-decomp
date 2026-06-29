package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.ApplicationControlInformation;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class InstalledModulesOptionsItem$PermissionsVerb extends Verb {
   private int _handle;

   public InstalledModulesOptionsItem$PermissionsVerb(int handle) {
      super(598288, OptionsResources.getResourceBundle(), handle == 0 ? 1960 : 1959);
      this._handle = handle;
   }

   @Override
   public final Object invoke(Object parameter) {
      ApplicationControlInformation aci = (ApplicationControlInformation)(new Object(this._handle));
      aci.open();
      return null;
   }
}
