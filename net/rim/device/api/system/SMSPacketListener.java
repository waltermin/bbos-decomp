package net.rim.device.api.system;

public interface SMSPacketListener extends RadioPacketListener {
   void packetReceived(SMSPacketHeader var1, byte[] var2);

   void packetDelivered(int var1, int var2, int var3);
}
