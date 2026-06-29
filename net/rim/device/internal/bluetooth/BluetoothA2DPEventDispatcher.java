package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothA2DPEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      BluetoothA2DPListener l = (BluetoothA2DPListener)listener;
      int handle = message.getSubMessage();
      int error = message.getDataLength();
      switch (message.getEvent()) {
         case 6145:
         default:
            l.a2dpIncomingConnection(handle, error, (byte[])message.getObject0(), message.getData1(), (byte[])message.getObject1());
            return;
         case 6146:
            l.a2dpConnected(handle, error, (byte[])message.getObject0());
            return;
         case 6147:
            l.a2dpDisconnected(handle, error);
            return;
         case 6148:
            l.a2dpIdle(handle, error);
            return;
         case 6149:
            l.a2dpStarted(handle, error);
            return;
         case 6150:
            l.a2dpSuspended(handle, error);
            return;
         case 6151:
            l.a2dpAborted(handle, error);
            return;
         case 6152:
            l.a2dpCodecInfo(handle, error, message.getData0(), (byte[])message.getObject0());
            return;
         case 6153:
            l.a2dpConfigRequired(handle, error);
            return;
         case 6154:
            l.a2dpReconfigRequired(handle, error, message.getData0(), (byte[])message.getObject0());
         case 6144:
            return;
         case 6155:
            l.a2dpStartRequested(handle, error);
      }
   }
}
