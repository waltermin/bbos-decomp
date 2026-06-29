package net.rim.blackberry.api.phone;

import net.rim.device.apps.internal.phone.api.livecall.LiveCall;

public class PhoneCall {
   private LiveCall _livecall;
   public static final int STATUS_CONNECTED = 1;
   public static final int STATUS_CONNECTED_MUTED = 2;
   public static final int STATUS_CONNECTING = 0;
   public static final int STATUS_DISCONNECTED = 4;
   public static final int STATUS_HELD = 3;

   PhoneCall(LiveCall lc) {
      this._livecall = lc;
   }

   public int getCallId() {
      return this._livecall.getCallId();
   }

   public String getDisplayPhoneNumber() {
      return this._livecall.getDisplayPhoneNumber();
   }

   public String getDTMFTones() {
      return null;
   }

   public int getElapsedTime() {
      return this._livecall.getElapsedTime();
   }

   public int getStatus() {
      return this._livecall.getStatus();
   }

   public String getStatusString() {
      return this._livecall.getStatusString();
   }

   public boolean isOutgoing() {
      return this._livecall.isOutgoing();
   }

   public boolean sendDTMFTone(char tone) {
      return this._livecall.sendDTMFTone(tone);
   }

   public boolean sendDTMFTones(String tones) {
      return this._livecall.sendDTMFTones(tones, false);
   }
}
