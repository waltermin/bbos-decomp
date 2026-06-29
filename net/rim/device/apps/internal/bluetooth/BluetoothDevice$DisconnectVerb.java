package net.rim.device.apps.internal.bluetooth;

import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.internal.bluetooth.BluetoothME;

final class BluetoothDevice$DisconnectVerb extends Verb {
   private final BluetoothDevice this$0;

   BluetoothDevice$DisconnectVerb(BluetoothDevice _1) {
      super(1507329, BluetoothMainScreen.getResourceBundle(), 23);
      this.this$0 = _1;
   }

   @Override
   public final Object invoke(Object context) {
      boolean force = true;
      this.this$0.notifyOperationStart();
      BluetoothProfileManager[] profileManagers = this.this$0._btManager.getProfileManagers();

      for (int i = profileManagers.length - 1; i >= 0; i--) {
         if (profileManagers[i].isConnected(this.this$0)) {
            profileManagers[i].disconnect(this.this$0);
            force = false;
         }
      }

      if (this.this$0._btManager.disconnectPBAPClient(this.this$0.getAddress())) {
         force = false;
         this.this$0.displayDisconnectMessage(91);
      }

      if (force) {
         this.this$0._waitingForConnection = 3;
         BluetoothME.forceDisconnectDevice(this.this$0.getAddress());
         this.this$0.displayDisconnectMessage(91);
      }

      return null;
   }
}
