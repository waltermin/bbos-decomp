package net.rim.device.apps.internal.profiles;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Alert;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.Status;

public class ProfileQuickToggle {
   private static int _keyDownEventTime;
   private static boolean _toggledProfile;
   private static final int TWO_SECONDS = 2000;
   private static final int ONE_SECOND = 1000;
   private static final int ONE_QUARTER_SECOND = 250;
   private static final int AUDIO_VOLUME_LOW = 50;
   private static final short[] MUTE_OFF_TUNE = new short[]{1800, 150, 1500, 150, 1, -12278, 51, 0};

   public static boolean handleKeyDown(int time) {
      _keyDownEventTime = time;
      _toggledProfile = false;
      return false;
   }

   public static boolean handleKeyRepeat(int time) {
      if (_keyDownEventTime != 0 && time - _keyDownEventTime > 2000) {
         _keyDownEventTime = 0;
         _toggledProfile = true;
         toggleProfile();
      }

      return true;
   }

   public static boolean handleKeyUp() {
      return _toggledProfile;
   }

   private static void toggleProfile() {
      Profiles profilesInstance = Profiles.getInstance();
      int currentlyEnabledProfileUID = profilesInstance.getEnabled().getUID();
      int profileToSwitchToUID = profilesInstance.getPreviousProfileUID();
      Profile profileToSwitchTo = profilesInstance.getByUID(profileToSwitchToUID);
      if (profileToSwitchTo == null) {
         profileToSwitchTo = profilesInstance.getByUID(4);
         if (profileToSwitchTo != null) {
            profileToSwitchToUID = 4;
         }
      }

      if (profileToSwitchToUID != 2 && currentlyEnabledProfileUID != 2) {
         profileToSwitchTo = profilesInstance.getByUID(2);
         if (profileToSwitchTo != null) {
            profileToSwitchToUID = 2;
         }
      }

      if (currentlyEnabledProfileUID != profileToSwitchToUID && profileToSwitchTo != null) {
         profilesInstance.enable(profileToSwitchTo);
         if (profileToSwitchToUID != 3 && profileToSwitchToUID != 6) {
            if (profileToSwitchToUID == 2) {
               if (Alert.isVibrateSupported()) {
                  Alert.startVibrate(1000);
               }
            } else {
               Alert.startAudio(MUTE_OFF_TUNE, 50);
            }
         } else if (Alert.isVibrateSupported()) {
            Alert.startVibrate(250);
         }

         ResourceBundle rb = ResourceBundle.getBundle(2384708948246157241L, "net.rim.device.apps.internal.resource.Profiles");
         String[] args = new Object[]{profileToSwitchTo.getName()};
         Status.show(MessageFormat.format(rb.getString(251), args), Bitmap.getPredefinedBitmap(0), 2000, 33554432, true, true, 50);
      }
   }
}
