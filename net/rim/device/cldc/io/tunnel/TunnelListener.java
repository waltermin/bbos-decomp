package net.rim.device.cldc.io.tunnel;

public interface TunnelListener {
   int STATUS_NONE = 0;
   int STATUS_PENDING = 1;
   int STATUS_ATTEMPTING = 2;
   int STATUS_ACTIVE = 3;
   int STATUS_FAILED = 4;
   int STATUS_INACTIVE = 5;
   int STATUS_RESETTING = 6;
   int STATUS_DORMANT = 7;
   int CODE_SUCCESS = 0;
   int CODE_ERROR_MAX_ATTEMPTS = 1;
   int CODE_ERROR_CRITICAL = 2;

   void statusChanged(int var1, int var2);
}
