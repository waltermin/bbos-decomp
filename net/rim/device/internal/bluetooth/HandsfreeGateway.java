package net.rim.device.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.internal.system.EventDispatchManager;

public final class HandsfreeGateway {
   public static final int INDICATOR_CALL = 2;
   public static final int INDICATOR_SERVICE = 1;
   public static final int INDICATOR_CALL_SETUP = 3;
   public static final int INDICATOR_BATTERY_LEVEL = 4;
   public static final int INDICATOR_RSSI = 5;
   public static final int INDICATOR_LOW_BATTERY = 6;
   public static final int INDICATOR_CHARGING = 7;
   public static final int INDICATOR_CALL_HELD = 8;
   public static final int INDICATOR_ROAMING = 9;

   public static final boolean isEnabled() {
      return !ITPolicy.getBoolean(34, 4, false);
   }

   public static final native int connect(byte[] var0, int var1);

   public static final native int disconnect();

   public static final native int enableAudio(boolean var0);

   public static final native int sendRing();

   public static final native int sendIndicatorUpdate(int var0, int var1);

   public static final native int sendCallWaiting(String var0, int var1);

   public static final native int sendCallerId(String var0, int var1);

   public static final native int sendSpeakerVolume(int var0);

   public static final native int sendRawData(byte[] var0);

   public static final native int sendRawData(String var0);

   public static final native boolean isNRECSupported();

   public static final native int sendHoldInfo(String var0);

   public static final void addListener(Application app, HandsfreeGatewayListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(40) == null) {
            dispatchManager.setDispatcher(40, new HandsfreeGatewayEventDispatcher());
         }
      }

      app.addListener(40, listener);
   }

   public static final void removeListener(Application app, HandsfreeGatewayListener listener) {
      app.removeListener(40, listener);
   }
}
