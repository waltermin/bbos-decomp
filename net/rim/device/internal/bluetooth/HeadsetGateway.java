package net.rim.device.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.internal.system.EventDispatchManager;

public final class HeadsetGateway {
   public static final boolean isEnabled() {
      return !ITPolicy.getBoolean(34, 3, false);
   }

   public static final native int connect(byte[] var0, int var1);

   public static final native int disconnect();

   public static final native int enableAudio(boolean var0);

   public static final native int sendOK();

   public static final native int sendError();

   public static final native int sendRing();

   public static final native int sendSpeakerVolume(int var0);

   public static final void addListener(Application app, HeadsetGatewayListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(41) == null) {
            dispatchManager.setDispatcher(41, new HeadsetGatewayEventDispatcher());
         }
      }

      app.addListener(41, listener);
   }

   public static final void removeListener(Application app, HeadsetGatewayListener listener) {
      app.removeListener(41, listener);
   }
}
