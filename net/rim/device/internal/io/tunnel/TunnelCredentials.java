package net.rim.device.internal.io.tunnel;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.api.util.Persistable;

final class TunnelCredentials implements Persistable {
   String apn;
   String apnUsername;
   String apnPassword;
   boolean editingOptionsAllowed;
   boolean outgoingSocketsAllowed;
   boolean incomingSocketsAllowed;

   TunnelCredentials(boolean loadNVS, boolean loadPRV, boolean loadITP) {
      switch (RadioInfo.getSupportedWAFs()) {
         case 2:
         case 8:
            this.apn = "";
            break;
         case 3:
            if ((RadioInfo.getActiveWAFs() & 1) != 0) {
               byte[] iif_apn = Branding.getData(13824);
               if (iif_apn != null) {
                  this.apn = new String(iif_apn);
               } else {
                  this.apn = "";
               }
            } else {
               this.apn = "";
            }
            break;
         case 4:
            this.apn = WLAN.WLAN_PSEUDO_APN;
            break;
         default:
            this.apn = !DeviceInfo.isSimulator() ? "" : "rim.net.gprs";
      }

      this.editingOptionsAllowed = true;
      this.outgoingSocketsAllowed = true;
      this.incomingSocketsAllowed = true;
      if (loadNVS) {
         this.loadValuesFromBranding();
      }

      if (loadITP) {
         this.loadValuesFromITPolicy();
      }
   }

   final void loadValuesFromBranding() {
      byte[] data = Branding.getData(13568);
      if (data != null) {
         this.apn = new String(data);
         data = Branding.getData(13569);
         this.apnUsername = data != null ? new String(data) : null;
         data = Branding.getData(13570);
         this.apnPassword = data != null ? new String(data) : null;
      }
   }

   final void loadValuesFromITPolicy() {
      String data = ITPolicy.getString(32, 4);
      if (data != null) {
         this.apn = data;
         this.apnUsername = ITPolicy.getString(32, 5);
         this.apnPassword = ITPolicy.getString(32, 6);
      }

      this.editingOptionsAllowed = ITPolicy.getBoolean(32, 3, this.editingOptionsAllowed);
      this.outgoingSocketsAllowed = ITPolicy.getBoolean(32, 7, this.outgoingSocketsAllowed);
      this.incomingSocketsAllowed = ITPolicy.getBoolean(32, 8, this.incomingSocketsAllowed);
   }
}
