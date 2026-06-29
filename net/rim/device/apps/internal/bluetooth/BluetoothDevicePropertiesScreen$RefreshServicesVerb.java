package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.apps.api.framework.verb.Verb;

final class BluetoothDevicePropertiesScreen$RefreshServicesVerb extends Verb {
   private final BluetoothDevicePropertiesScreen this$0;

   BluetoothDevicePropertiesScreen$RefreshServicesVerb(BluetoothDevicePropertiesScreen _1) {
      super(1572867, ResourceBundle.getBundle(2718987090951705092L, "net.rim.device.apps.internal.resource.Bluetooth"), 35);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      this.this$0._device.startServiceDiscovery(true);
      return null;
   }
}
