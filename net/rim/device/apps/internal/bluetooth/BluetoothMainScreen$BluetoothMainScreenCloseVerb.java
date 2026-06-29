package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.ui.ExitVerb;
import net.rim.device.internal.bluetooth.BluetoothME;
import net.rim.device.internal.i18n.CommonResource;

final class BluetoothMainScreen$BluetoothMainScreenCloseVerb extends ExitVerb {
   private final BluetoothMainScreen this$0;

   BluetoothMainScreen$BluetoothMainScreenCloseVerb(BluetoothMainScreen _1) {
      super(268501008, CommonResource.getBundle(), 9, _1._setupMode ? 1 : 0, null);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object parameter) {
      if (this.this$0._turnPowerOffOnExit && !BluetoothME.isAnyDeviceConnected()) {
         this.this$0._btManager.setPowerOn(false);
      }

      return super.invoke(parameter);
   }
}
