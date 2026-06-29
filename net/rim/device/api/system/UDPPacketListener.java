package net.rim.device.api.system;

public interface UDPPacketListener extends RadioPacketListener {
   int ERROR_PACKET_NOT_SENT_RETRY;
   int ERROR_PACKET_NOT_SENT_CONTEXT;
   int ERROR_PACKET_NOT_SENT;
   int ERROR_PACKET_NOT_SENT_COVER;
   int ERROR_PACKET_INVALID_ADDRESS;

   void packetReceived(UDPPacketHeader var1, byte[] var2);
}
