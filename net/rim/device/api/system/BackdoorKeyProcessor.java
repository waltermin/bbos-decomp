package net.rim.device.api.system;

import net.rim.device.api.ui.Keypad;
import net.rim.device.internal.system.InternalServices;

public class BackdoorKeyProcessor {
   private int _keys;
   private int _altStatus;
   private BackdoorKeyListener _listener;
   private static boolean _developmentDevice = !InternalServices.isDeviceSecure();
   private static boolean _rimBrandedDevice = Branding.getVendorId() == 1;

   public BackdoorKeyProcessor(boolean altStatus, BackdoorKeyListener listener) {
      this._listener = listener;
      this.setAltStatus(altStatus);
   }

   public void setAltStatus(boolean altStatus) {
      this._altStatus = altStatus ? 1 : 0;
   }

   public static boolean isDevelopmentDevice() {
      return _developmentDevice || _rimBrandedDevice && !PersistentContent.isEncryptionEnabled();
   }

   public boolean keyDown(int keycode) {
      return this.processKey(Keypad.key(keycode), Keypad.status(keycode));
   }

   public boolean keyChar(char key, int status, int time) {
      return this.processKey(key, status);
   }

   private static int nextKey(int in) {
      if (!InternalServices.isReducedFormFactor()) {
         return in;
      }

      switch (in) {
         case 65:
            return 83;
         case 66:
            return 78;
         case 67:
            return 86;
         case 68:
            return 70;
         case 69:
            return 82;
         case 71:
            return 72;
         case 74:
            return 75;
         case 79:
            return 80;
         case 81:
            return 87;
         case 84:
            return 89;
         case 85:
            return 73;
         case 90:
            return 88;
         default:
            return in;
      }
   }

   public static int appendKeyDetectingMultitap(int keys, int key) {
      key &= 255;
      int nextKey;
      return key == (keys & 0xFF) && (nextKey = nextKey(key)) != key ? keys & -256 | nextKey : keys << 8 | key;
   }

   private boolean processKey(int key, int status) {
      if ((status & 1) != this._altStatus) {
         this._keys = 0;
         return false;
      } else {
         this._keys = appendKeyDetectingMultitap(this._keys, key);
         if (this._listener.openProductionBackdoor(this._keys)) {
            this._keys = 0;
            return true;
         } else if (isDevelopmentDevice() && this._listener.openDevelopmentBackdoor(this._keys)) {
            this._keys = 0;
            return true;
         } else {
            return false;
         }
      }
   }
}
