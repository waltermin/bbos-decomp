package net.rim.device.apps.internal.options.items;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.internal.ui.component.IPEditField;

public final class StatusIP extends StatusListItem {
   private int _requestedWAF;

   public StatusIP(int WAF) {
      super(924);
      this._requestedWAF = WAF;
   }

   @Override
   public final String getDisplayValue() {
      String apnName = null;
      switch (this._requestedWAF) {
         case 4:
            apnName = WLAN.WLAN_PSEUDO_APN;
            break;
         case 8:
            apnName = "";
      }

      int apn = -1;
      if (apnName != null) {
         try {
            apn = RadioInfo.getAccessPointNumber(apnName);
         } finally {
            ;
         }
      }

      byte[] ip = null;
      if (apn >= 0) {
         ip = RadioInfo.getIPAddress(apn);
      }

      StringBuffer sb = (StringBuffer)(new Object());
      IPEditField.appendIpAddr(sb, ip);
      return sb.toString();
   }
}
