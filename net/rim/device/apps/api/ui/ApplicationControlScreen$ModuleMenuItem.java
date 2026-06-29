package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.MenuItem;

class ApplicationControlScreen$ModuleMenuItem extends MenuItem {
   int _handle;
   private final ApplicationControlScreen this$0;

   public ApplicationControlScreen$ModuleMenuItem(ApplicationControlScreen _1, int handle) {
      super(ApplicationControlScreen._rb.getString(743), 31000, 100);
      this.this$0 = _1;
      this._handle = handle;
   }

   @Override
   public void run() {
      this.this$0.editModulePermissions(this._handle);
   }
}
