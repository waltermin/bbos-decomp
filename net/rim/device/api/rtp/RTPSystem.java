package net.rim.device.api.rtp;

public interface RTPSystem {
   int UNSUPPORTED_CODEC = -1;
   int UNKNOWN_HANDLE = -2;
   int INSUFFICIENT_MEMORY = -4;
   int INVALID_PARAMETER = -3;
   int INVALID_PORT = -5;
   int INVALID_SESSION_STATE = -6;
   int INVALID_DTMF_MODE = -7;
   int INBOUND = 1;
   int OUTBOUND = 2;
   int BIDIRECTION = 3;
   int RTP_DEFAULT_LOCAL_PORT = 1100;
   int RTP_DEFAULT_CLOCK_RATE = 8000;

   void setListener(int var1, RTPListener var2);

   int register();

   int deregister();

   int allocateRtpHandle();

   int createStream(int var1, int var2, byte var3, int var4, int var5, int var6, byte[] var7, int var8, int var9);

   int set711Codec(int var1);

   int set729Codec(int var1);

   int setAmrCodec(int var1, byte var2, byte var3, byte var4, byte var5, byte var6, byte[] var7, byte[] var8, byte var9);

   int startStream(int var1, int var2);

   int stopStream(int var1, int var2);

   int release(int var1);

   int setRtpDtmfMode(int var1, int var2, int var3);

   int startRtpDtmf(int var1, byte var2);

   int stopRtpDtmf(int var1);

   int modifyStream(int var1, byte var2, byte var3, byte[] var4, int var5, int var6, int var7);

   int createBothStreams(int var1, byte var2, int var3, int var4, int var5, byte[] var6, int var7, int var8, byte[] var9, int var10, int var11);

   int startBothStreams(int var1);

   int stopBothStreams(int var1);

   int mute(int var1, byte var2);

   int setRtcpInterval(int var1, int var2);

   int configureRtcpReport(int var1, byte var2, int var3);
}
