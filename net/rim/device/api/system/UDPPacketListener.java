package net.rim.device.api.system;

public interface UDPPacketListener extends RadioPacketListener {
   int ERROR_PACKET_NOT_SENT_RETRY = 249;
   int ERROR_PACKET_NOT_SENT_CONTEXT = 250;
   int ERROR_PACKET_NOT_SENT = 251;
   int ERROR_PACKET_NOT_SENT_COVER = 252;
   int ERROR_PACKET_INVALID_ADDRESS = 255;

   void packetReceived(UDPPacketHeader var1, byte[] var2);
}
