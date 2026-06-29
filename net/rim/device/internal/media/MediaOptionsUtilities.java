package net.rim.device.internal.media;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.StringUtilities;

public class MediaOptionsUtilities {
   private static final long GUID;
   private static final int SIM_CARD_NOT_PRESENT_IMSI_VALUE;

   public static MediaOptionsUtilities getInstance() {
      MediaOptionsUtilities instance = (MediaOptionsUtilities)ApplicationRegistry.getApplicationRegistry().get(8285784959406531370L);
      if (instance == null) {
         instance = new MediaOptionsUtilities();
         ApplicationRegistry.getApplicationRegistry().replace(8285784959406531370L, instance);
      }

      return instance;
   }

   private MediaOptionsUtilities() {
   }

   private static int computeCurrentImsiValue() {
      if (SIMCard.isSupported()) {
         try {
            String currentImsi = SIMCard.imsiToString(SIMCard.getIMSI());
            return StringUtilities.hashCodeIgnoreCase(currentImsi);
         } catch (SIMCardException var1) {
         }
      }

      return -1;
   }

   public static boolean isVolumeBoostKeyExpired() {
      return !SIMCard.isSupported() ? false : computeCurrentImsiValue() != MediaOptionsRegistry.getInstance().getInt(-811168513825316359L);
   }

   public void showBoostVolumeWarning() {
      UiApplication app = UiApplication.getUiApplication();
      if (!app.isForeground()) {
         app.requestForeground();
      }

      Screen activeScreen = app.getActiveScreen();
      if (!(activeScreen instanceof MediaOptionsUtilities$VolumeBoostDialog)
         && !(activeScreen instanceof MediaOptionsUtilities$VolumeBoostMoreInformationScreen)) {
         MediaOptionsUtilities$VolumeBoostDialog volumeBoostDialog = new MediaOptionsUtilities$VolumeBoostDialog();
         synchronized (Application.getEventLock()) {
            volumeBoostDialog.show();
         }
      }
   }
}
