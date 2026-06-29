package net.rim.device.api.system;

public interface RadioPacketListener extends RadioListener {
   int TX_PENDING = 1;
   int TX_TRANSMITTING = 2;

   void packetSent(int var1, int var2);

   void packetNotSent(int var1, int var2);

   void packetStatus(int var1, int var2);
}
