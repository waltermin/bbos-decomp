package net.rim.device.apps.internal.manageconnections;

import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.bluetooth.BluetoothME;

final class ConnectionsPopupScreen$1 implements Runnable {
   private final ConnectionsPopupScreen this$0;

   ConnectionsPopupScreen$1(ConnectionsPopupScreen _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      this.this$0._app.removeRadioListener(this.this$0._wirelessListener);
      if (this.this$0._wifiEnabled) {
         this.this$0._app.removeRadioListener(this.this$0._wifiListener);
      }

      BluetoothME.removeListener(this.this$0._app, this.this$0._bluetoothListener);
      ServiceRouting sr = ServiceRouting.getInstance();
      if (sr != null) {
         sr.removeListener(this.this$0._srListener);
      }

      VoiceServices.removePhoneEventListener(this.this$0);
   }
}
