package net.rim.device.internal.bluetooth;

import net.rim.device.api.system.Application;
import net.rim.device.internal.system.EventDispatchManager;

public final class BluetoothL2CAP {
   public static final native int registerPSM(int var0, int var1, int var2, boolean var3, int var4);

   public static final native void deregisterPSM(int var0);

   public static final native int connectRequest(int var0, int var1, int var2);

   public static final native void connectResponse(int var0, int var1);

   public static final native void disconnectRequest(int var0);

   public static final native int getPSM(int var0);

   public static final native void sendData(int var0, int var1, byte[] var2);

   public static final native int getTransmitMTU(int var0);

   public static final void addListener(Application app, BluetoothL2CAPListener listener) {
      EventDispatchManager dispatchManager = EventDispatchManager.getInstance();
      synchronized (dispatchManager) {
         if (dispatchManager.getDispatcher(12) == null) {
            dispatchManager.setDispatcher(12, new BluetoothL2CAPEventDispatcher());
         }
      }

      app.addListener(12, listener);
   }

   public static final void removeListener(Application app, BluetoothL2CAPListener listener) {
      app.removeListener(12, listener);
   }
}
