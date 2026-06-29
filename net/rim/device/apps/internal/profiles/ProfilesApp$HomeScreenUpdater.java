package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.apps.api.ribbon.ApplicationEntryPoint;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.internal.ui.UiSettings;

final class ProfilesApp$HomeScreenUpdater implements GlobalEventListener {
   private Profiles _profileManager;
   private ApplicationDescriptor _baseDescriptor;
   private static final String PROFILE_APP_IDENTIFIER = "net.rim.ProfileHomeScreenApp";

   final void updateHomeScreen(Profile activeProfile) {
      ApplicationDescriptor descriptor = new ApplicationDescriptor(
         this._baseDescriptor,
         activeProfile.getIconBaseName(),
         null,
         null,
         this._baseDescriptor.getPosition(),
         "net.rim.device.apps.internal.resource.Profiles",
         0,
         this._baseDescriptor.getFlags()
      );
      ApplicationEntryPoint entryPoint = new ApplicationEntryPoint(descriptor);
      entryPoint.set(9, "net_rim_bb_profiles_app.Profiles");
      entryPoint.set(3, this.getBaseTitle() + " (" + activeProfile.getName() + ')');
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.registerAction("net.rim.ProfileHomeScreenApp", entryPoint);
      }
   }

   @Override
   public final void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid != 6679759753682678305L && guid != -7464003439710973532L) {
         if (guid == 6869208671291562587L) {
            Profiles profiles = Profiles.getInstance();
            if (profiles != null) {
               Profile offProfile = profiles.getByUID(6);
               if (offProfile != null && UiSettings.getOffProfileEnabled()) {
                  profiles.enable(offProfile);
               }
            }
         }
      } else {
         this.updateHomeScreen(this._profileManager.getEnabled());
      }
   }

   private final String getBaseTitle() {
      return ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles").getString(0);
   }

   ProfilesApp$HomeScreenUpdater(Profiles profileManager) {
      this._profileManager = profileManager;
      ApplicationDescriptor base = ApplicationDescriptor.currentApplicationDescriptor();
      this._baseDescriptor = new ApplicationDescriptor(base, base.getName(), null, null, base.getPosition(), null, 0, base.getFlags());
      ApplicationEntryPoint entryPoint = new ApplicationEntryPoint(this._baseDescriptor);
      entryPoint.set(9, "net_rim_bb_profiles_app.Profiles");
      RibbonLauncher launcher = RibbonLauncher.getInstance();
      if (launcher != null) {
         launcher.registerAction("net.rim.ProfileHomeScreenApp", entryPoint);
      }
   }
}
