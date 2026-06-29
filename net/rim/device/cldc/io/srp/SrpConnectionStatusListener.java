package net.rim.device.cldc.io.srp;

public interface SrpConnectionStatusListener {
   int CONNECTION_OPENED = 1;
   int CONNECTION_CLOSED_NORMALLY = 2;
   int CONNECTION_CLOSED_ABNORMALLY = 3;
   int CONNECTION_OPEN_FAILURE = 4;

   void connectionStatusChanged(Object var1, int var2);
}
