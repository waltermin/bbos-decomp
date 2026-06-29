package net.rim.device.api.system;

public interface IOPortListener {
   int ERROR_PARITY = 2;
   int ERROR_FRAMING = 3;
   int ERROR_OVERRUN = 4;

   void connected();

   void disconnected();

   void receiveError(int var1);

   void dataReceived(int var1);

   void dataSent();

   void patternReceived(byte[] var1);
}
