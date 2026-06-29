package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.apps.api.ui.ApplicationControlInformation;
import net.rim.device.apps.internal.options.resources.OptionsResources;

final class ModuleInformation$ApplicationControlMenuItem extends MenuItem {
   private int _moduleHandle;
   private final ModuleInformation this$0;

   public ModuleInformation$ApplicationControlMenuItem(ModuleInformation _1, int moduleHandle) {
      super(OptionsResources.getString(1959), 3000, 100);
      this.this$0 = _1;
      this._moduleHandle = moduleHandle;
   }

   @Override
   public final void run() {
      ApplicationControlInformation aci = new ApplicationControlInformation(this._moduleHandle);
      aci.open();
      this.this$0._mainScreen.deleteAll();
      this.this$0._mainScreen.removeAllMenuItems();
      this.this$0.createModuleInformationItems();
   }
}
