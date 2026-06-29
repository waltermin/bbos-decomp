package net.rim.device.api.system;

public interface CBPacketListener extends RadioPacketListener {
   void packetReceived(CBPacketHeader var1, byte[] var2);
}
