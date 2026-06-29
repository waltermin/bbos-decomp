package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;

public class WLANInfo {
   public static final int WLAN_BAND_A = 2;
   public static final int WLAN_BAND_B = 4;
   public static final int WLAN_BAND_BG = 1;
   public static final int WLAN_STATE_CONNECTED = 4620;
   public static final int WLAN_STATE_DISCONNECTED = 4621;

   private WLANInfo() {
   }

   public static void addListener(WLANListener listener) {
      if (listener == null) {
         throw new NullPointerException();
      }

      if (WLAN.isSupported()) {
         ApplicationControl.assertWiFiPermitted(true, CommonResource.getBundle(), 10165);
         WLANInfoImpl.getInstance().addListener(listener);
      }
   }

   public static void removeListener(WLANListener listener) {
      if (WLAN.isSupported()) {
         WLANInfoImpl.getInstance().removeListener(listener);
      }
   }

   public static int getWLANState() {
      return WLAN.isSupported() && WLANInfoImpl.getInstance().isConnected() ? 4620 : 4621;
   }

   public static WLANInfo$WLANAPInfo getAPInfo() {
      if (WLAN.isSupported()) {
         ApplicationControl.assertWiFiPermitted(true, CommonResource.getBundle(), 10165);
         return WLANInfoImpl.getInstance().getAPInfo();
      } else {
         return null;
      }
   }
}
