package net.rim.apps.internal.explorer.IndexService;

import net.rim.device.api.system.Application;

final class IndexServiceApp extends Application {
   IndexServiceApp() {
      this.invokeLater(new IndexServiceApp$1(this));
      this.enterEventDispatcher();
   }

   public static final void main(String[] args) {
      new IndexServiceApp();
   }
}
