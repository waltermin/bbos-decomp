package net.rim.device.api.crypto.tls;

import net.rim.device.api.util.DataBuffer;

public interface AlertProtocolMethods extends AlertProtocol {
   void processAlertMessage(DataBuffer var1);

   void sendAlertMessage(byte var1, byte var2);

   void sendCloseNotify(boolean var1);

   byte convertAlertDescription(byte var1);
}
