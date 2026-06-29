package net.rim.device.cldc.io.tunnel;

public interface TunnelListener {
   int STATUS_NONE;
   int STATUS_PENDING;
   int STATUS_ATTEMPTING;
   int STATUS_ACTIVE;
   int STATUS_FAILED;
   int STATUS_INACTIVE;
   int STATUS_RESETTING;
   int STATUS_DORMANT;
   int CODE_SUCCESS;
   int CODE_ERROR_MAX_ATTEMPTS;
   int CODE_ERROR_CRITICAL;

   void statusChanged(int var1, int var2);
}
