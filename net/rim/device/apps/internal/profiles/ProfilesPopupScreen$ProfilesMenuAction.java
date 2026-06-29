package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.apps.api.ribbon.Action;
import net.rim.device.apps.api.ribbon.RibbonLauncher;

class ProfilesPopupScreen$ProfilesMenuAction extends Action {
   private ResourceBundleFamily _res = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
   private static final String UNIQUE_ID = "net_rim_profiles_menu";

   static void register(boolean b) {
      RibbonLauncher ribbonLauncher = RibbonLauncher.getInstance();
      if (ribbonLauncher != null) {
         ribbonLauncher.unregisterAction("net_rim_profiles_menu");
         if (b) {
            ribbonLauncher.registerAction("net_rim_profiles_menu", new ProfilesPopupScreen$ProfilesMenuAction());
         }
      }
   }

   ProfilesPopupScreen$ProfilesMenuAction() {
      super(null, "net_rim_profiles_menu", 300);
   }

   @Override
   protected String getDescription() {
      return this._res.getString(0);
   }

   @Override
   public void run() {
      ProfilesPopupScreen.show(false, false);
   }

   @Override
   public Boolean get(long propID, Boolean defaultReturned) {
      return propID == 11 ? Boolean.TRUE : defaultReturned;
   }
}
