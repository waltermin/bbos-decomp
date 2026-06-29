package net.rim.device.internal.system;

import net.rim.device.api.system.RadioPacketListener;

public interface TCPPacketListener extends RadioPacketListener {
   int ERROR_PACKET_NOT_SENT_RETRY;
   int ERROR_PACKET_NOT_SENT_CONTEXT;
   int ERROR_PACKET_NOT_SENT;
   int ERROR_PACKET_NOT_SENT_COVER;
   int ERROR_PACKET_INVALID_ADDRESS;

   void packetReceived(TCPPacketHeader var1, byte[] var2);
}
