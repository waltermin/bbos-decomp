package net.rim.device.internal.bluetooth;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.AudioRouter;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.internal.system.EventDispatchManager;

public final class BluetoothA2DP {
   public static final boolean isSupported() {
      return DeviceInfo.isSimulator() ? true : AudioRouter.isAudioModeSupported(36);
   }

   public static final boolean isEnabled() {
      return !ITPolicy.getBoolean(34, 18, false);
   }

   public static final native int registerStream(int var0, int var1, byte[] var2);

   public static final native int deregisterStream(int var0);

   public static final native int openStream(int var0, byte[] var1);

   public static final native int openStreamResponse(int var0, int var1, int var2);

   public static final native int closeStream(int var0);

   public static final native int configStream(int var0, int var1, byte[] var2);

   public static final native int reconfigStreamResponse(int var0, int var1, int var2);

   public static final native int startStream(int var0);

   public static final native int startStreamResponse(int var0, int var1);

   public static final native int suspendStream(int var0);

   public static final void addListener(Application app, BluetoothA2DPListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(24) == null) {
            dispatchManager.setDispatcher(24, new BluetoothA2DPEventDispatcher());
         }
      }

      app.addListener(24, listener);
   }

   public static final void removeListener(Application app, BluetoothA2DPListener listener) {
      app.removeListener(24, listener);
   }
}
