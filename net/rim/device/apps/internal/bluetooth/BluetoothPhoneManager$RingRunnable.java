package net.rim.device.apps.internal.bluetooth;

import net.rim.device.internal.bluetooth.HandsfreeGateway;
import net.rim.device.internal.bluetooth.HeadsetGateway;

final class BluetoothPhoneManager$RingRunnable implements Runnable {
   private final BluetoothPhoneManager this$0;

   BluetoothPhoneManager$RingRunnable(BluetoothPhoneManager _1) {
      this.this$0 = _1;
   }

   @Override
   public final void run() {
      int callId = this.this$0.getCallID(48);
      if (callId != 0 && this.this$0._state == 2) {
         if (this.this$0._isHandsfree) {
            HandsfreeGateway.sendRing();
            if (this.this$0._callerIDReporting) {
               label34:
               try {
                  String id = this.this$0._phone.getCallPhoneNumber(callId);
                  if (id != null && id.length() != 0) {
                     HandsfreeGateway.sendCallerId(id, this.this$0.getNumberType(id));
                  }
               } finally {
                  break label34;
               }
            }
         } else {
            HeadsetGateway.sendRing();
         }

         this.this$0._btManager.invokeLater(this, 5000, false);
      }
   }
}
