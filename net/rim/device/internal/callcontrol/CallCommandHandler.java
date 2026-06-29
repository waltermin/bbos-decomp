package net.rim.device.internal.callcontrol;

public class CallCommandHandler extends AbstractCallCommandHandler {
   protected CallCommandHandler(int order) {
      super(order);
   }

   @Override
   public boolean canInvokeCallTransferAction(int callId, int action) {
      return this.getNext().canInvokeCallTransferAction(callId, action);
   }

   @Override
   public boolean canHold(int callId) {
      return this.getNext().canHold(callId);
   }

   @Override
   public boolean canSwap(int callId) {
      return this.getNext().canSwap(callId);
   }

   @Override
   public boolean canJoin(int callId) {
      return this.getNext().canJoin(callId);
   }

   @Override
   public boolean canPark(int callId) {
      return this.getNext().canPark(callId);
   }

   @Override
   public boolean canSendToVoicemail(int callId) {
      return this.getNext().canSendToVoicemail(callId);
   }

   @Override
   public String getAlternateLineLabel(int line) {
      return this.getNext().getAlternateLineLabel(line);
   }

   @Override
   public boolean isAlternateLineAvailable(int line) {
      return this.getNext().isAlternateLineAvailable(line);
   }

   @Override
   public void setAlternateLineLabel(int line, String description) {
      this.getNext().setAlternateLineLabel(line, description);
   }

   @Override
   public String getAlternateLineNumber(int line) {
      return this.getNext().getAlternateLineNumber(line);
   }

   @Override
   public int[] getAlternateLines() {
      return this.getNext().getAlternateLines();
   }

   @Override
   public int getCallTransferState(int callId) {
      return this.getNext().getCallTransferState(callId);
   }

   @Override
   public String getVoiceMailNumber(int line) {
      return this.getNext().getVoiceMailNumber(line);
   }

   @Override
   public int getVoiceMailCount(int line) {
      return this.getNext().getVoiceMailCount(line);
   }

   @Override
   public int getWAFs(int line) {
      return this.getNext().getWAFs(line);
   }

   @Override
   public boolean invokeCallTransferAction(int callId, int action, Object param) {
      return this.getNext().invokeCallTransferAction(callId, action, param);
   }

   @Override
   public void parkCall(int callId) {
      this.getNext().parkCall(callId);
   }

   @Override
   public void sendToVoicemail(int callId) {
      this.getNext().sendToVoicemail(callId);
   }

   @Override
   public boolean supportsCorporateExtensions(int callId) {
      return this.getNext().supportsCorporateExtensions(callId);
   }

   @Override
   public void activateCallBarring(boolean activate, int type, String password) {
      this.getNext().activateCallBarring(activate, type, password);
   }

   @Override
   public void activateCallWaiting(boolean activate) {
      this.getNext().activateCallWaiting(activate);
   }

   @Override
   public void addCallToConference() {
      this.getNext().addCallToConference();
   }

   @Override
   public void answerCall(int callId) {
      this.getNext().answerCall(callId);
   }

   @Override
   public void deactivateCallForwarding() {
      this.getNext().deactivateCallForwarding();
   }

   @Override
   public void disableDTMFEcho(boolean disable) {
      this.getNext().disableDTMFEcho(disable);
   }

   @Override
   public boolean endEmergencyCallbackMode() {
      return this.getNext().endEmergencyCallbackMode();
   }

   @Override
   public void flash(String number) {
      this.getNext().flash(number);
   }

   @Override
   public int getActiveCallId() {
      return this.getNext().getActiveCallId();
   }

   @Override
   public int getAlternateLine(int callID) {
      return this.getNext().getAlternateLine(callID);
   }

   @Override
   public int getCallDuration(int callId) {
      return this.getNext().getCallDuration(callId);
   }

   @Override
   public String getCallForwardingNumber(int type) {
      return this.getNext().getCallForwardingNumber(type);
   }

   @Override
   public String getCallName(int callId, boolean original) {
      return this.getNext().getCallName(callId, original);
   }

   @Override
   public String getCallPhoneNumber(int callId, boolean original) {
      return this.getNext().getCallPhoneNumber(callId, original);
   }

   @Override
   public int getCallState(int callId) {
      return this.getNext().getCallState(callId);
   }

   @Override
   public int getCLIPDisplayMode(int callId) {
      return this.getNext().getCLIPDisplayMode(callId);
   }

   @Override
   public String getEmergencyNumber() {
      return this.getNext().getEmergencyNumber();
   }

   @Override
   public String getForwardingNumber() {
      return this.getNext().getForwardingNumber();
   }

   @Override
   public String getForwardingNumberForService(int ssOption, int bearerService) {
      return this.getNext().getForwardingNumberForService(ssOption, bearerService);
   }

   @Override
   public int getHeldCallId() {
      return this.getNext().getHeldCallId();
   }

   @Override
   public int getIncomingCallId() {
      return this.getNext().getIncomingCallId();
   }

   @Override
   public int getMaxConferenceMembers() {
      return this.getNext().getMaxConferenceMembers();
   }

   @Override
   public int getNetworkFeatures() {
      return this.getNext().getNetworkFeatures();
   }

   @Override
   public String getNumber(int index) {
      return this.getNext().getNumber(index);
   }

   @Override
   public void holdCall() {
      this.getNext().holdCall();
   }

   @Override
   public boolean inCallDTMFDigitsEntered(String digits) {
      return this.getNext().inCallDTMFDigitsEntered(digits);
   }

   @Override
   public boolean isActive() {
      return this.getNext().isActive();
   }

   @Override
   public boolean isCallForwardUnconditionalActive(int line) {
      return this.getNext().isCallForwardUnconditionalActive(line);
   }

   @Override
   public boolean isCallRedirected(int callId) {
      return this.getNext().isCallRedirected(callId);
   }

   @Override
   public boolean isEmergencyNumber(String number) {
      return this.getNext().isEmergencyNumber(number);
   }

   @Override
   public boolean isFDNAvailable() {
      return this.getNext().isFDNAvailable();
   }

   @Override
   public boolean isFDNEnabled() {
      return this.getNext().isFDNEnabled();
   }

   @Override
   public void querySSOption(int ssOption) {
      this.getNext().querySSOption(ssOption);
   }

   @Override
   public int querySSOptionResult(int ssOption, int bearerService) {
      return this.getNext().querySSOptionResult(ssOption, bearerService);
   }

   @Override
   public void removeCallFromConference(int callId) {
      this.getNext().removeCallFromConference(callId);
   }

   @Override
   public void rejectCall(int callId) {
      this.getNext().rejectCall(callId);
   }

   @Override
   public void requestEnableFDN(boolean enable) {
      this.getNext().requestEnableFDN(enable);
   }

   @Override
   public void resumeCall() {
      this.getNext().resumeCall();
   }

   @Override
   public void sendSSPasswordResponse(String password) {
      this.getNext().sendSSPasswordResponse(password);
   }

   @Override
   public boolean setAlternateLine(int line) {
      return this.getNext().setAlternateLine(line);
   }

   @Override
   public void setCallBarringPassword(String oldPassword, String newPassword) {
      this.getNext().setCallBarringPassword(oldPassword, newPassword);
   }

   @Override
   public void setCallForwardingNumber(int type, String number) {
      this.getNext().setCallForwardingNumber(type, number);
   }

   @Override
   public void setSSBasicService(int bearerService) {
      this.getNext().setSSBasicService(bearerService);
   }

   @Override
   public void setUSSDResponse(byte[] data) {
      this.getNext().setUSSDResponse(data);
   }

   @Override
   public int startCall(String number, int clir) {
      return this.getNext().startCall(number, clir);
   }

   @Override
   public void startDTMF(int callId, byte character) {
      this.getNext().startDTMF(callId, character);
   }

   @Override
   public void stopAllCalls(boolean unconditional) {
      this.getNext().stopAllCalls(unconditional);
   }

   @Override
   public void stopCall(int callId) {
      this.getNext().stopCall(callId);
   }

   @Override
   public void stopDTMF(int callId) {
      this.getNext().stopDTMF(callId);
   }

   @Override
   public void swapCalls() {
      this.getNext().swapCalls();
   }

   @Override
   public void transferCall() {
      this.getNext().transferCall();
   }
}
