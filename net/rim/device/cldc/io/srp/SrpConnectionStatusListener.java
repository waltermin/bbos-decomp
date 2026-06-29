package net.rim.device.cldc.io.srp;

public interface SrpConnectionStatusListener {
   int CONNECTION_OPENED;
   int CONNECTION_CLOSED_NORMALLY;
   int CONNECTION_CLOSED_ABNORMALLY;
   int CONNECTION_OPEN_FAILURE;

   void connectionStatusChanged(Object var1, int var2);
}
