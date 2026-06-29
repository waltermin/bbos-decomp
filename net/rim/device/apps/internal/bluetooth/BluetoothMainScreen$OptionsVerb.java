package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.framework.verb.Verb;

final class BluetoothMainScreen$OptionsVerb extends Verb {
   BluetoothMainScreen$OptionsVerb() {
      super(16986368, BluetoothMainScreen._rb, 19);
   }

   @Override
   public final Object invoke(Object context) {
      BluetoothOptionsScreen.show();
      return null;
   }
}
