package net.rim.device.api.system;

public interface IOPortListener {
   int ERROR_PARITY;
   int ERROR_FRAMING;
   int ERROR_OVERRUN;

   void connected();

   void disconnected();

   void receiveError(int var1);

   void dataReceived(int var1);

   void dataSent();

   void patternReceived(byte[] var1);
}
