package net.rim.device.apps.internal.ribbon;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

final class ApplicationMenuAction extends Action {
   private ResourceBundleFamily _rbf = ResourceBundle.getBundle(1137270090621229274L, "net.rim.device.apps.internal.resource.Ribbon");
   public static final String UNIQUE_ID;

   static final void register(RibbonLauncher ribbonLauncher, boolean b) {
      ribbonLauncher.unregisterAction("net_rim_application_menu");
      if (b) {
         ribbonLauncher.registerAction("net_rim_application_menu", new ApplicationMenuAction());
      }
   }

   ApplicationMenuAction() {
      super(null, "net_rim_application_menu", 300);
   }

   @Override
   protected final String getDescription() {
      return this._rbf.getString(141);
   }

   @Override
   public final void run() {
      ApplicationMenu.show();
   }

   @Override
   public final Boolean get(long propID, Boolean defaultReturned) {
      return propID == 11 ? Boolean.TRUE : defaultReturned;
   }
}
