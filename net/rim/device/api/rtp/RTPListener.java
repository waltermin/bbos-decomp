package net.rim.device.api.rtp;

public interface RTPListener {
   int ERROR_VAL_HIGH_PACKET_LOSS = 1;
   int ERROR_VAL_HOST_UNREACHABLE = 2;
   int ERROR_VAL_UNRECOVERABLE_ERROR = 3;

   void RTPError(int var1, int var2, int var3);

   void RTPDisconnected(int var1, int var2);
}
