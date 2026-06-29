package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.framework.verb.Verb;

final class BluetoothDevice$ConnectVerb extends Verb {
   private final BluetoothDevice this$0;

   BluetoothDevice$ConnectVerb(BluetoothDevice _1) {
      super(1507328, BluetoothMainScreen.getResourceBundle(), 22);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0.connect(true);
      return null;
   }
}
