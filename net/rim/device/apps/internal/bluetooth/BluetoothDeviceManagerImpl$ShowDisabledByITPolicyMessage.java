package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.component.Status;

final class BluetoothDeviceManagerImpl$ShowDisabledByITPolicyMessage implements Runnable {
   private final BluetoothDeviceManagerImpl this$0;

   BluetoothDeviceManagerImpl$ShowDisabledByITPolicyMessage(BluetoothDeviceManagerImpl _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      String msg = BluetoothMainScreen.getString(0);
      Object[] args = new Object[]{msg};
      String prompt = MessageFormat.format(BluetoothMainScreen.getString(44), args);
      Status.show(prompt, this.this$0.getDialogImage(), 3000, 33554432, true, false, -2147483646);
   }
}
