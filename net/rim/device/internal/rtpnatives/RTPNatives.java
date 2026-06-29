package net.rim.device.internal.rtpnatives;

import net.rim.device.api.rtp.RtpAmrAttributes;
import net.rim.device.api.rtp.RtpModify;
import net.rim.device.api.rtp.RtpParameters;

public class RTPNatives {
   public static final int RTP_DEVICE;
   public static final int NETWORK_ERROR;
   public static final int NETWORK_EVENT;
   public static final int REMOTE_DISCONNECTED;
   public static final int DTMF_RTP_2833;
   public static final int DTMF_RTP_AUDIO;
   public static final int DTMF_SIP_INFO;

   private RTPNatives() {
   }

   public static native int register();

   public static native int deregister(int var0);

   public static native int allocateRtpHandle(int var0);

   public static native int createStream(int var0, RtpParameters var1);

   public static native int set711Codec(int var0, int var1);

   public static native int set729Codec(int var0, int var1);

   public static native int setAmrCodec(int var0, int var1, RtpAmrAttributes var2);

   public static native int startStream(int var0, RtpParameters var1);

   public static native int stopStream(int var0, RtpParameters var1);

   public static native int release(int var0, int var1);

   public static native int setRtpDtmfMode(int var0, RtpParameters var1);

   public static native int startRtpDtmf(int var0, RtpParameters var1);

   public static native int stopRtpDtmf(int var0, int var1);

   public static native int modifyStream(int var0, RtpModify var1);

   public static native int createBothStreams(int var0, RtpParameters var1);

   public static native int startBothStreams(int var0, int var1);

   public static native int stopBothStreams(int var0, int var1);

   public static native int mute(int var0, int var1, byte var2);

   public static native int setRtcpInterval(int var0, RtpParameters var1);

   public static native int configureRtcpReport(int var0, RtpParameters var1);
}
