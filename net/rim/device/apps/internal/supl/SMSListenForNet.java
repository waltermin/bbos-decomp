package net.rim.device.apps.internal.supl;

import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.apps.internal.sms.SMSService;
import net.rim.device.apps.internal.sms.SMSServiceListener;

final class SMSListenForNet implements SMSServiceListener {
   final int ULP_RX_PORT = 7275;

   SMSListenForNet() {
      SMSService.getInstance().addSMSServiceListener(this);
   }

   @Override
   public final boolean smsMessageReceived(SMSPacketHeader header, byte[] data, byte[] userDataHeader, int[] ports) {
      try {
         System.out.println("SUPL: Received SMS on port:" + ports[1]);
         if (ports[1] == ports[0] && ports[1] == 7275) {
            System.out.println("Recv'd PER encoded PDU of length:" + data.length);
            new SuplSessionManager(data);
            return true;
         }
      } finally {
         return false;
      }

      return false;
   }
}
