package net.rim.device.internal.system;

import java.io.UnsupportedEncodingException;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;

public final class PhoneFirmwareImpl {
   public static final long GUID_CDMA_FLASH;

   private PhoneFirmwareImpl() {
   }

   public static final native void checkSupported();

   public static final native String getNumber(int var0);

   public static final native int startCall(String var0, int var1);

   public static final native void answerCall(int var0);

   public static final native void stopCall(int var0);

   public static final native void stopAllCalls(boolean var0);

   public static final native void rejectCall(int var0);

   public static final native void holdCall();

   public static final native void resumeCall();

   public static final native void swapCalls();

   public static final native void transferCall();

   public static final native void addCallToConference();

   public static final native void removeCallFromConference(int var0);

   public static final native void startDTMF(int var0, byte var1);

   public static final native void stopDTMF(int var0);

   public static final native void disableDTMFEcho(boolean var0);

   public static final native int getMaxConferenceMembers();

   public static final native int getActiveCallId();

   public static final native int getHeldCallId();

   public static final native int getIncomingCallId();

   public static final native int getCallState(int var0);

   public static final native int getCallDuration(int var0);

   public static final native boolean isCallRedirected(int var0);

   public static final native int getCLIPDisplayMode(int var0);

   public static final native String getCallPhoneNumber(int var0, boolean var1);

   public static final String getCallName(int callId, boolean original) {
      try {
         byte[] data = getCallName0(callId, original);
         return (RadioInfo.getActiveWAFs() & 1) != 0 ? new String(data, "SMS") : new String(data);
      } catch (UnsupportedEncodingException ex) {
         return null;
      }
   }

   private static final native byte[] getCallName0(int var0, boolean var1);

   public static final native void querySSOption(int var0);

   public static final native int querySSOptionResult(int var0, int var1);

   public static final native void setCallForwardingNumber(int var0, String var1);

   public static final native String getCallForwardingNumber(int var0);

   public static final native boolean isCallForwardUnconditionalActive(int var0);

   public static final native void deactivateCallForwarding();

   public static final native void activateCallBarring(boolean var0, int var1, String var2);

   public static final native void setCallBarringPassword(String var0, String var1);

   public static final void flash(String number) {
      flash0(number);
      RIMGlobalMessagePoster.postGlobalEvent(5606159505026478103L, 0, 0, number, null);
   }

   public static final native void flash0(String var0);

   public static final native void activateCallWaiting(boolean var0);

   public static final native int getNetworkFeatures();

   public static final native void setUSSDResponse(byte[] var0);

   public static final native String getForwardingNumber();

   public static final native String getForwardingNumberForService(int var0, int var1);

   public static final native void requestEnableFDN(boolean var0);

   public static final native boolean isFDNAvailable();

   public static final native boolean isFDNEnabled();

   public static final native boolean inCallDTMFDigitsEntered(String var0);

   public static final native boolean endEmergencyCallbackMode();

   public static final native boolean isEmergencyNumber(String var0);

   public static final native String getEmergencyNumber();

   public static final native void setSSBasicService(int var0);

   public static final native void sendSSPasswordResponse(String var0);

   public static final native boolean setAlternateLine(int var0);

   public static final native int getAlternateLine(int var0);
}
