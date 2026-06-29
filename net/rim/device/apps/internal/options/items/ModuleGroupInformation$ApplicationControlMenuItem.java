package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.ui.ApplicationControlInformation;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ModuleGroupInformation$ApplicationControlMenuItem extends MenuItem {
   private int[] _moduleHandles;
   private String _name;

   public ModuleGroupInformation$ApplicationControlMenuItem(int[] moduleHandles, String name) {
      super(OptionsResources.getString(1959), 3000, 100);
      this._moduleHandles = moduleHandles;
      this._name = name;
   }

   @Override
   public final void run() {
      ApplicationControlInformation aci = (ApplicationControlInformation)(new Object(this._moduleHandles, this._name));
      aci.open();
   }
}
