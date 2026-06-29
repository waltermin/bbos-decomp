package net.rim.device.apps.internal.bluetooth;

import net.rim.device.api.system.Application;
import net.rim.device.apps.api.ui.SecurityDialog;

class BluetoothDeviceManagerImpl$PasswordPromptApplication extends Application implements Runnable {
   private String _prompt;
   private final BluetoothDeviceManagerImpl this$0;

   public BluetoothDeviceManagerImpl$PasswordPromptApplication(BluetoothDeviceManagerImpl _1, String prompt) {
      this.this$0 = _1;
      this._prompt = prompt;
   }

   @Override
   protected boolean acceptsForeground() {
      return true;
   }

   @Override
   public void run() {
      if (SecurityDialog.challengeUser(this._prompt, false, true, '\u0000', true)) {
         this.this$0.setPowerOn(true);
      }

      System.exit(0);
   }
}
