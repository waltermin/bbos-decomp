package net.rim.device.internal.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothL2CAPEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      BluetoothL2CAPListener l = (BluetoothL2CAPListener)listener;
      int psmHandle = message.getSubMessage();
      int cid = message.getData0();
      switch (message.getEvent()) {
         case 3073:
         default:
            l.l2capIncomingConnection(psmHandle, cid, (byte[])message.getObject0());
            return;
         case 3074:
            l.l2capConnected(cid);
            return;
         case 3075:
            l.l2capDisconnected(cid);
            return;
         case 3076:
            l.l2capDataReceived(cid, (byte[])message.getObject0());
            return;
         case 3077:
            l.l2capDataSent(cid);
         case 3072:
      }
   }
}
