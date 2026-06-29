package net.rim.device.api.system;

import net.rim.device.internal.crypto.CryptoBlock;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.USBPasswordRedirectManager;
import net.rim.vm.OTAUpgrade;
import net.rim.vm.TraceBack;

public final class Radio {
   private static final long ID = 6796807463572612615L;
   private static Radio _instance;

   private Radio() {
   }

   public static final boolean activateWAFs(int WAFs) {
      RadioInternal.assertWAFAccessPermission(WAFs);
      if (WAFs == 0) {
         return true;
      }

      if (!CryptoBlock.areMasterKeysAvailable()) {
         return false;
      }

      if (!USBPasswordRedirectManager.getInstance().isRadioAllowedOn()) {
         return false;
      }

      if (!OTAUpgrade.isRadioAllowedOn()) {
         return false;
      }

      if ((WAFs & 4) != 0 && !WLAN.isWLANAllowed()) {
         if (WAFs == 4) {
            return false;
         }

         WAFs &= -5;
      }

      int enabled3GPPRats = RadioInternal.get3GPPEnabledRats();
      if ((WAFs & 1) != 0) {
         if (RadioInternal.getGANPreference() == 2) {
            return false;
         }

         enabled3GPPRats |= RadioInternal.get3GPPSupportedRats() & -5;
      }

      if ((WAFs & 4) != 0 && GAN.isGANAllowed()) {
         enabled3GPPRats |= RadioInternal.get3GPPSupportedRats() & 4;
      }

      RadioInternal.set3GPPEnabledRats(enabled3GPPRats);
      int networkType = RadioInfo.getNetworkType();
      if (networkType != 7 && networkType != 4) {
         int savedMode = RadioInternal.getNetworkSelectionMode();
         if ((1 << savedMode & RadioInternal.getAvailableNetworkSelectionModes()) == 0) {
            RadioInternal.setNetworkSelectionMode(0);
         } else {
            RadioInternal.setNetworkSelectionMode(savedMode);
         }
      }

      RadioInternal.set3GPPRatConfig(RadioInternal.get3GPPActiveRats(), RadioInternal.get3GPPRATPreference(RadioInternal.getGANPreference()));
      if (RadioInfo.areWAFsSupported(1)) {
         if (RadioInternal.get3GPPActiveRats() != 0 && (RadioInfo.getEnabledWAFs() & 1) != 0) {
            WAFs |= 1;
         } else {
            WAFs &= -2;
         }
      }

      RadioInternal.activateWAFsInternal(WAFs);
      return true;
   }

   public static final void deactivateWAFs(int WAFs) {
      RadioInternal.assertWAFAccessPermission(WAFs);
      int enabled3GPPRats = RadioInternal.get3GPPEnabledRats();
      if ((WAFs & 1) != 0) {
         enabled3GPPRats &= ~(RadioInternal.get3GPPSupportedRats() & -5);
      }

      if ((WAFs & 4) != 0) {
         enabled3GPPRats &= ~(RadioInternal.get3GPPSupportedRats() & 4);
      }

      RadioInternal.set3GPPEnabledRats(enabled3GPPRats);
      RadioInternal.set3GPPRatConfig(RadioInternal.get3GPPActiveRats(), RadioInternal.get3GPPRATPreference(RadioInternal.getGANPreference()));
      if (RadioInfo.areWAFsSupported(1)) {
         if (RadioInternal.get3GPPActiveRats() == 0) {
            WAFs |= 1;
         } else {
            WAFs &= -2;
            if (WAFs == 0) {
               return;
            }
         }
      }

      RadioInternal.deactivateWAFsInternal(WAFs);
   }

   public static final boolean setEnabledWAFs(int WAFs) {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return setEnabledWAFsInternal(WAFs);
   }

   private static final native boolean setEnabledWAFsInternal(int var0);

   public static final boolean powerOn() {
      return RadioInternal.activateRadios(RadioInternal.getEnabledRadios());
   }

   public static final void requestPowerOn() {
      powerOn();
   }

   public static final void requestPowerOff() {
      RadioInternal.deactivateRadios(RadioInternal.getSupportedRadios());
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _instance = (Radio)ar.getOrWaitFor(6796807463572612615L);
      if (_instance == null) {
         _instance = new Radio();
         ar.put(6796807463572612615L, _instance);
      }
   }
}
