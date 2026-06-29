package net.rim.device.api.system;

import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.callcontrol.CallControlSystem;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.system.Security;

public class Phone {
   private int _defaultCLIR = 0;
   public static final long GUID_PHONE_INITIATE_CALL;
   public static final int CALL_STATE_NOT_CONNECTED;
   public static final int CALL_STATE_OUTGOING;
   public static final int CALL_STATE_INCOMING;
   public static final int CALL_STATE_ACTIVE;
   public static final int CALL_STATE_ON_HOLD;
   public static final int CALL_STATE_IN_CONFERENCE;
   public static final int CALL_STATE_CONFERENCE_HOLD;
   public static final int SS_CALL_FORWARD_UNCONDITIONAL;
   public static final int SS_CALL_FORWARD_BUSY;
   public static final int SS_CALL_FORWARD_NO_REPLY;
   public static final int SS_CALL_FORWARD_NOT_REACHABLE;
   public static final int SS_CALL_BARRING_OUTGOING_DEACTIVATE_ALL;
   public static final int SS_CALL_BARRING_OUTGOING;
   public static final int SS_CALL_BARRING_OUTGOING_INTL;
   public static final int SS_CALL_BARRING_OUTGOING_INTL_PLMN;
   public static final int SS_CALL_BARRING_INCOMING_DEACTIVATE_ALL;
   public static final int SS_CALL_BARRING_INCOMING;
   public static final int SS_CALL_BARRING_INCOMING_WHEN_ROAMING;
   public static final int SS_CLIP;
   public static final int SS_CLIR;
   public static final int SS_COLP;
   public static final int SS_COLR;
   public static final int SS_ALL_FORWARDING;
   public static final int SS_ALL_COND_FORWARDING;
   public static final int SS_ALL_CALL_RESTRICTION;
   public static final int SS_CALL_WAITING;
   public static final int CLIR_OFF;
   public static final int CLIR_INVOCATION;
   public static final int CLIR_SUPPRESSION;
   public static final int SS_OPTION_PROVISIONED;
   public static final int SS_OPTION_ACTIVE;
   public static final int SS_OPTION_REGISTERED;
   public static final int SS_OPTION_QUIESCENT;
   public static final int FEATURE_REJECT;
   public static final int FEATURE_FLASH;
   public static final int FEATURE_SINGLE_FLASH_3WC;
   public static final int FEATURE_HOLD;
   public static final int FEATURE_SPLIT;
   public static final int FEATURE_ADD;
   public static final int FEATURE_REMOVE;
   public static final int FEATURE_DIRECT_CONNECT;
   public static final int FEATURE_TTY;
   public static final int FEATURE_ECT;
   public static final int FEATURE_E911CB_EXIT;
   public static final int FEATURE_ALS;
   public static final int FEATURE_SS_CALL_BARRING;
   public static final int FEATURE_SS_CALL_FORWARD_UNCONDITIONAL;
   public static final int FEATURE_SS_CALL_FORWARD_BUSY;
   public static final int FEATURE_SS_CALL_FORWARD_NO_REPLY;
   public static final int FEATURE_SS_CALL_FORWARD_NOT_REACHABLE;
   public static final int FEATURE_SS_CALL_WAITING;
   public static final int FEATURE_SS_CLIR;
   public static final int FEATURE_HAC;
   public static final int CLIP_DISPLAY_MODE_ALLOWED;
   public static final int CLIP_DISPLAY_MODE_PRIVATE;
   public static final int CLIP_DISPLAY_MODE_UNKNOWN;
   public static final int INVALID_CALL_ID;
   public static final int CALL_IN_CONFERENCE;
   public static final int SS_CALL_ID;
   public static final int SS_BEARER_SERVICE_NONE;
   public static final int SS_BEARER_SERVICE_ALL_TELESERVICES;
   public static final int SS_BEARER_SERVICE_TELEPHONY;
   public static final int SS_BEARER_SERVICE_FACSIMILE;
   public static final int SS_BEARER_SERVICE_ALL_SMS;
   public static final int SS_BEARER_SERVICE_ALL_EXCEPT_SMS;
   public static final int SS_BEARER_SERVICE_ALL;
   public static final int SS_BEARER_SERVICE_ASYNC_SERVICES;
   public static final int SS_BEARER_SERVICE_SYNC_SERVICES;
   public static final int SS_BEARER_SERVICE_PLMN_TELESERVICE;
   public static final int SS_BEARER_SERVICE_PLMN;
   public static final int SS_BEARER_SERVICE_AUX_TELEPHONY;
   public static final int SS_BEARER_SERVICE_INVALID;
   public static final int CALL_TRANSFER_ACTION_BEGIN;
   public static final int CALL_TRANSFER_ACTION_SWAP;
   public static final int CALL_TRANSFER_ACTION_JOIN;
   public static final int CALL_TRANSFER_ACTION_COMPLETE;
   public static final int CALL_TRANSFER_ACTION_CANCEL;
   public static final int CALL_TRANSFER_ACTION_COUNT;
   public static final int ALTERNATE_LINE_1;
   public static final int ALTERNATE_LINE_2;

   public static boolean isSupported() {
      return true;
   }

   public static Phone getInstance() {
      return CallControlSystem.getCommandHandler();
   }

   public static int initiateCall(String number, int clir) {
      ApplicationControl.assertPhonePermitted(true, CommonResource.getBundle(), 10045);
      if (ApplicationManager.getApplicationManager().isSystemLocked() && !Security.getInstance().getAllowOutgoingCallWhileLocked()) {
         return 0;
      }

      int callId = getInstance().startCall(number, clir);
      RIMGlobalMessagePoster.postGlobalEvent(-5324686711008477091L, callId, 0, number, null);
      return callId;
   }

   public final int startCall(String number) {
      return this.startCall(number, this.getCLIR());
   }

   public int startCall(String _1, int _2) {
      throw null;
   }

   public void answerCall(int _1) {
      throw null;
   }

   public String getNumber(int _1) {
      throw null;
   }

   public void stopCall(int _1) {
      throw null;
   }

   public void stopAllCalls(boolean _1) {
      throw null;
   }

   public void rejectCall(int _1) {
      throw null;
   }

   public void holdCall() {
      throw null;
   }

   public void resumeCall() {
      throw null;
   }

   public void swapCalls() {
      throw null;
   }

   public void transferCall() {
      throw null;
   }

   public void addCallToConference() {
      throw null;
   }

   public void removeCallFromConference(int _1) {
      throw null;
   }

   public void startDTMF(int _1, byte _2) {
      throw null;
   }

   public void stopDTMF(int _1) {
      throw null;
   }

   public int getMaxConferenceMembers() {
      throw null;
   }

   public int getActiveCallId() {
      throw null;
   }

   public int getHeldCallId() {
      throw null;
   }

   public int getIncomingCallId() {
      throw null;
   }

   public int getCallState(int _1) {
      throw null;
   }

   public int getCallDuration(int _1) {
      throw null;
   }

   public boolean isCallRedirected(int _1) {
      throw null;
   }

   public int getCLIPDisplayMode(int _1) {
      throw null;
   }

   public final int getCLIR() {
      return this._defaultCLIR;
   }

   public final void setCLIR(int clir) {
      this._defaultCLIR = clir;
   }

   public String getCallPhoneNumber(int _1) {
      throw null;
   }

   public String getCallPhoneNumber(int _1, boolean _2) {
      throw null;
   }

   public String getCallName(int _1) {
      throw null;
   }

   public String getCallName(int _1, boolean _2) {
      throw null;
   }

   public void querySSOption(int _1) {
      throw null;
   }

   public int querySSOptionResult(int _1, int _2) {
      throw null;
   }

   public void setCallForwardingNumber(int _1, String _2) {
      throw null;
   }

   public String getCallForwardingNumber(int _1) {
      throw null;
   }

   public boolean isCallForwardUnconditionalActive(int _1) {
      throw null;
   }

   public void deactivateCallForwarding() {
      throw null;
   }

   public void activateCallBarring(boolean _1, int _2, String _3) {
      throw null;
   }

   public void setCallBarringPassword(String _1, String _2) {
      throw null;
   }

   public void flash(String _1) {
      throw null;
   }

   public void activateCallWaiting(boolean _1) {
      throw null;
   }

   public int getNetworkFeatures() {
      throw null;
   }

   public void setUSSDResponse(byte[] _1) {
      throw null;
   }

   public String getForwardingNumber() {
      throw null;
   }

   public String getForwardingNumberForService(int _1, int _2) {
      throw null;
   }

   public void requestEnableFDN(boolean _1) {
      throw null;
   }

   public boolean isFDNAvailable() {
      throw null;
   }

   public boolean isFDNEnabled() {
      throw null;
   }

   public boolean inCallDTMFDigitsEntered(String _1) {
      throw null;
   }

   public boolean endEmergencyCallbackMode() {
      throw null;
   }

   public boolean isActive() {
      throw null;
   }

   public static boolean isPhoneActive() {
      return getInstance().isActive();
   }

   public void disableDTMFEcho(boolean _1) {
      throw null;
   }

   public boolean isEmergencyNumber(String _1) {
      throw null;
   }

   public String getEmergencyNumber() {
      throw null;
   }

   public static void setDTMFMode(boolean silent) {
      getInstance().disableDTMFEcho(silent);
   }

   public void setSSBasicService(int _1) {
      throw null;
   }

   public void sendSSPasswordResponse(String _1) {
      throw null;
   }

   public boolean setAlternateLine(int _1) {
      throw null;
   }

   public int getAlternateLine(int _1) {
      throw null;
   }

   public boolean canInvokeCallTransferAction(int _1, int _2) {
      throw null;
   }

   public boolean canHold(int _1) {
      throw null;
   }

   public boolean canSwap(int _1) {
      throw null;
   }

   public boolean canJoin(int _1) {
      throw null;
   }

   public boolean canPark(int _1) {
      throw null;
   }

   public boolean canSendToVoicemail(int _1) {
      throw null;
   }

   public String getAlternateLineLabel(int _1) {
      throw null;
   }

   public boolean isAlternateLineAvailable(int _1) {
      throw null;
   }

   public void setAlternateLineLabel(int _1, String _2) {
      throw null;
   }

   public String getAlternateLineNumber(int _1) {
      throw null;
   }

   public int[] getAlternateLines() {
      throw null;
   }

   public int getCallTransferState(int _1) {
      throw null;
   }

   public String getVoiceMailNumber(int _1) {
      throw null;
   }

   public int getVoiceMailCount(int _1) {
      throw null;
   }

   public int getWAFs(int _1) {
      throw null;
   }

   public boolean invokeCallTransferAction(int _1, int _2, Object _3) {
      throw null;
   }

   public void parkCall(int _1) {
      throw null;
   }

   public void sendToVoicemail(int _1) {
      throw null;
   }

   public boolean supportsCorporateExtensions(int _1) {
      throw null;
   }
}
