package net.rim.device.api.bluetooth;

import net.rim.device.internal.system.EventDispatcher;
import net.rim.vm.Message;

final class BluetoothSerialEventDispatcher extends EventDispatcher {
   @Override
   public final void dispatch(Message message, Object listener) {
      BluetoothSerialPort serialPort = (BluetoothSerialPort)listener;
      serialPort.dispatch(message);
   }
}
