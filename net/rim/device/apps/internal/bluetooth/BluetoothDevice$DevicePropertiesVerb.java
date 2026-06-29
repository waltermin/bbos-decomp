package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.framework.verb.Verb;

final class BluetoothDevice$DevicePropertiesVerb extends Verb {
   private final BluetoothDevice this$0;

   BluetoothDevice$DevicePropertiesVerb(BluetoothDevice _1) {
      super(1572867, BluetoothMainScreen.getResourceBundle(), 20);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      BluetoothDevicePropertiesScreen.show(this.this$0);
      return null;
   }
}
