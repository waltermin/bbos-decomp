package net.rim.device.apps.internal.bluetooth;

import java.util.Vector;
import net.rim.device.apps.api.options.OptionsProviderRegistration$OptionsProvider;

class BluetoothMainScreen$1 implements OptionsProviderRegistration$OptionsProvider {
   @Override
   public Vector getOptionsItems() {
      Vector v = (Vector)(new Object(1));
      v.addElement(new BluetoothMainScreen());
      return v;
   }
}
