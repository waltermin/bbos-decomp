package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.ui.UiApplication;

final class BluetoothSetupApplication extends UiApplication implements Runnable {
   BluetoothSetupApplication() {
      this.invokeLater(this);
   }

   @Override
   public final void run() {
      new BluetoothMainScreen(true).perform(6099736323056465049L, null);
   }
}
