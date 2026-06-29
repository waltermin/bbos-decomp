package net.rim.device.api.rtp;

public interface RTPSystem {
   int UNSUPPORTED_CODEC;
   int UNKNOWN_HANDLE;
   int INSUFFICIENT_MEMORY;
   int INVALID_PARAMETER;
   int INVALID_PORT;
   int INVALID_SESSION_STATE;
   int INVALID_DTMF_MODE;
   int INBOUND;
   int OUTBOUND;
   int BIDIRECTION;
   int RTP_DEFAULT_LOCAL_PORT;
   int RTP_DEFAULT_CLOCK_RATE;

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
