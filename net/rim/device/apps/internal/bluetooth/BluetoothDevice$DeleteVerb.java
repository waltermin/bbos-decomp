package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.i18n.CommonResource;

final class BluetoothDevice$DeleteVerb extends Verb {
   private final BluetoothDevice this$0;

   BluetoothDevice$DeleteVerb(BluetoothDevice _1) {
      super(1572866, BluetoothMainScreen.getResourceBundle(), 10);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      String prompt = CommonResource.format(10025, this.this$0.getFriendlyName());
      if (Dialog.ask(2, prompt, -1) != 3) {
         return null;
      }

      this.this$0._btManager.deletePairedDevice(this.this$0);
      return null;
   }
}
