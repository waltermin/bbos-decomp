package net.rim.device.api.gps;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.internal.ui.component.IPEditField;

public class GPSSettings {
   private GPSSettings() {
   }

   public static boolean setPDEInfo(String ip, int port) {
      if ((RadioInfo.getActiveWAFs() & 2) != 2) {
         throw new UnsupportedOperationException();
      }

      if (port >= 0 && port <= 65535) {
         String ipaddr = null;
         String appId = null;
         String password = null;
         int index = ip.indexOf(59);
         if (index != -1) {
            ipaddr = ip.substring(0, index).trim();
            String temp = ip.substring(index + 1);
            index = temp.indexOf(59);
            if (index == -1) {
               throw new IllegalArgumentException("Invalid ip string");
            }

            appId = temp.substring(0, index);
            password = temp.substring(index + 1);
         } else {
            ipaddr = ip;
         }

         int ipAddr = 0;
         if (ipaddr != null && !ipaddr.equals("")) {
            ipAddr = IPEditField.parseIpAddr(ipaddr);
         }

         GPS$AppCredential cred = null;
         if (appId != null && password != null) {
            cred = new GPS$AppCredential(Integer.parseInt(appId), password);
         }

         GPS$GPSPDEInfo pdeInfo = new GPS$GPSPDEInfo(ipAddr, port, cred);
         boolean result = GPSRegistry.getInstance().addPDEInfo(pdeInfo);
         if (!GPSRegistry.isVerizon()) {
            GPS.startLocationUpdate(3, 1, 0, null);

            try {
               Thread.sleep(100);
            } catch (Exception var11) {
            }

            GPS.stopLocationUpdate(1);
         }

         return result;
      } else {
         throw new IllegalArgumentException("Illegal port value");
      }
   }
}
