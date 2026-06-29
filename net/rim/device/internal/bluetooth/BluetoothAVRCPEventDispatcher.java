package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothAVRCPEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      BluetoothAVRCPListener l = (BluetoothAVRCPListener)listener;
      int handle = message.getSubMessage();
      switch (message.getEvent()) {
         case 6401:
         default:
            l.avrcpIncomingConnection(handle, (byte[])message.getObject0());
            return;
         case 6402:
            l.avrcpConnected(handle, (byte[])message.getObject0());
            return;
         case 6403:
            l.avrcpDisconnected(handle);
            return;
         case 6404:
            l.avrcpPanelPress(handle, message.getData0());
            return;
         case 6405:
            l.avrcpPanelHold(handle, message.getData0());
            return;
         case 6406:
            l.avrcpPanelRelease(handle, message.getData0());
            return;
         case 6407:
            l.avrcpPanelResponse(handle, message.getData0(), message.getData1() != 0, message.getDataLength());
         case 6400:
      }
   }
}
