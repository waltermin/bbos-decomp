package net.rim.device.apps.internal.browser.util;

import net.rim.device.apps.api.framework.profiles.TuneManager;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.profiles.AlertConfiguration;
import net.rim.device.apps.internal.profiles.Profile;
import net.rim.device.apps.internal.profiles.Profiles;

public final class MediaUtilities {
   private MediaUtilities() {
   }

   public static final boolean setAsHomeScreenBackground(String fullFilename) {
      RibbonLauncher ribbon = RibbonLauncher.getInstance();
      if (ribbon != null) {
         ribbon.setBackgroundImage(fullFilename, null);
         return true;
      } else {
         return false;
      }
   }

   public static final boolean setAsRingtone(String tuneName) {
      TuneManager tuneManager = TuneManager.getTuneManager();
      if (!tuneManager.isTuneAvailable(tuneName)) {
         return false;
      }

      Profiles profiles = Profiles.getInstance();
      int numProfiles = profiles.size();

      for (int i = 0; i < numProfiles; i++) {
         Profile profile = (Profile)profiles.getAt(i);
         AlertConfiguration config = (AlertConfiguration)profile.getConfiguration(
            -2870941457036655797L, 2868625504212929964L + PhoneUtilities.getCurrentLineId()
         );
         config.setTuneName(tuneName, true);
         config.setTuneName(tuneName, false);
         profiles.commitChanges(profile, true);
      }

      return true;
   }
}
