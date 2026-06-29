package net.rim.device.api.system;

public interface RadioPacketListener extends RadioListener {
   int TX_PENDING;
   int TX_TRANSMITTING;

   void packetSent(int var1, int var2);

   void packetNotSent(int var1, int var2);

   void packetStatus(int var1, int var2);
}
