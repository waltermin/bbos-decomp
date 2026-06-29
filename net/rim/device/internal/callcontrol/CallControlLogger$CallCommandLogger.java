package net.rim.device.internal.callcontrol;

class CallControlLogger$CallCommandLogger extends AbstractCallCommandHandler {
   private final CallControlLogger this$0;

   public CallControlLogger$CallCommandLogger(CallControlLogger _1, int order) {
      super(order);
      this.this$0 = _1;
   }

   @Override
   public boolean canInvokeCallTransferAction(int callId, int action) {
      this.this$0.logEvent(5, CallControlLogger.CANINVOKEXFER, callId, action);
      boolean ret = this.getNext().canInvokeCallTransferAction(callId, action);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean canHold(int callId) {
      this.this$0.logEvent(5, CallControlLogger.CANHOLD, callId);
      boolean ret = this.getNext().canHold(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean canSwap(int callId) {
      this.this$0.logEvent(5, CallControlLogger.CANSWAP, callId);
      boolean ret = this.getNext().canSwap(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean canJoin(int callId) {
      this.this$0.logEvent(5, CallControlLogger.CANJOIN, callId);
      boolean ret = this.getNext().canJoin(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean canPark(int callId) {
      this.this$0.logEvent(5, CallControlLogger.CANPARK, callId);
      boolean ret = this.getNext().canPark(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean canSendToVoicemail(int callId) {
      this.this$0.logEvent(5, CallControlLogger.CANSENDTOVM, callId);
      boolean ret = this.getNext().canSendToVoicemail(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getAlternateLineLabel(int line) {
      this.this$0.logEvent(5, CallControlLogger.GETLINELABEL, line);
      String ret = this.getNext().getAlternateLineLabel(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isAlternateLineAvailable(int line) {
      this.this$0.logEvent(5, CallControlLogger.ISLINEAVAILABLE, line);
      boolean ret = this.getNext().isAlternateLineAvailable(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void setAlternateLineLabel(int line, String description) {
      this.this$0.logEvent(5, CallControlLogger.SETLINELABEL, line, description);
      this.getNext().setAlternateLineLabel(line, description);
   }

   @Override
   public String getAlternateLineNumber(int line) {
      this.this$0.logEvent(5, CallControlLogger.GETLINENUMBER, line);
      String ret = this.getNext().getAlternateLineNumber(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int[] getAlternateLines() {
      this.this$0.logEvent(5, CallControlLogger.GETLINES);
      int[] ret = this.getNext().getAlternateLines();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getCallTransferState(int callId) {
      this.this$0.logEvent(5, CallControlLogger.GETXFERSTATE, callId);
      int ret = this.getNext().getCallTransferState(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getVoiceMailNumber(int line) {
      this.this$0.logEvent(5, CallControlLogger.GETVMNUMBER, line);
      String ret = this.getNext().getVoiceMailNumber(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getVoiceMailCount(int line) {
      this.this$0.logEvent(5, CallControlLogger.GETVMCOUNT, line);
      int ret = this.getNext().getVoiceMailCount(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getWAFs(int line) {
      this.this$0.logEvent(5, CallControlLogger.GETWAFS, line);
      int ret = this.getNext().getWAFs(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean invokeCallTransferAction(int callId, int action, Object param) {
      this.this$0.logEvent(5, CallControlLogger.XFERACTION, callId, action, param);
      boolean ret = this.getNext().invokeCallTransferAction(callId, action, param);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void parkCall(int callId) {
      this.this$0.logEvent(0, CallControlLogger.PARK, callId);
      this.getNext().parkCall(callId);
   }

   @Override
   public void sendToVoicemail(int callId) {
      this.this$0.logEvent(0, CallControlLogger.SENDTOVM, callId);
      this.getNext().sendToVoicemail(callId);
   }

   @Override
   public boolean supportsCorporateExtensions(int callId) {
      this.this$0.logEvent(5, CallControlLogger.SUPPORTSEXT, callId);
      boolean ret = this.getNext().supportsCorporateExtensions(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void activateCallBarring(boolean activate, int type, String password) {
      this.this$0.logEvent(5, CallControlLogger.ACTIVATEBARRING, activate, type, password);
      this.getNext().activateCallBarring(activate, type, password);
   }

   @Override
   public void activateCallWaiting(boolean activate) {
      this.this$0.logEvent(5, CallControlLogger.ACTIVATEWAITING, activate);
      this.getNext().activateCallWaiting(activate);
   }

   @Override
   public void addCallToConference() {
      this.this$0.logEvent(0, CallControlLogger.ADDTOCONF);
      this.getNext().addCallToConference();
   }

   @Override
   public void answerCall(int callId) {
      this.this$0.logEvent(0, CallControlLogger.ANSWER, callId);
      this.getNext().answerCall(callId);
   }

   @Override
   public void deactivateCallForwarding() {
      this.this$0.logEvent(5, CallControlLogger.DEACTIVATECALLFWD);
      this.getNext().deactivateCallForwarding();
   }

   @Override
   public void disableDTMFEcho(boolean disable) {
      this.this$0.logEvent(5, CallControlLogger.DISABLEDTMFECHO, disable);
      this.getNext().disableDTMFEcho(disable);
   }

   @Override
   public boolean endEmergencyCallbackMode() {
      this.this$0.logEvent(0, CallControlLogger.END911CALLBACKMODE);
      boolean ret = this.getNext().endEmergencyCallbackMode();
      this.this$0.logEvent(0, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void flash(String number) {
      this.this$0.logEvent(0, CallControlLogger.FLASH, CallControlLogger.obfuscate(number));
      this.getNext().flash(number);
   }

   @Override
   public int getActiveCallId() {
      this.this$0.logEvent(5, CallControlLogger.GETACTIVECALL);
      int ret = this.getNext().getActiveCallId();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getAlternateLine(int callID) {
      this.this$0.logEvent(5, CallControlLogger.GETLINE, callID);
      int ret = this.getNext().getAlternateLine(callID);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getCallDuration(int callId) {
      this.this$0.logEvent(5, CallControlLogger.GETDURATION, callId);
      int ret = this.getNext().getCallDuration(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getCallForwardingNumber(int type) {
      this.this$0.logEvent(5, CallControlLogger.GETCALLFWDNUMBER, type);
      String ret = this.getNext().getCallForwardingNumber(type);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getCallName(int callId, boolean original) {
      this.this$0.logEvent(0, CallControlLogger.CALLNAME, callId, original);
      String ret = this.getNext().getCallName(callId, original);
      this.this$0.logEvent(0, CallControlLogger.RESULT, CallControlLogger.obfuscate(ret));
      return ret;
   }

   @Override
   public String getCallPhoneNumber(int callId, boolean original) {
      this.this$0.logEvent(0, CallControlLogger.GETCALLNUMBER, callId, original);
      String ret = this.getNext().getCallPhoneNumber(callId, original);
      this.this$0.logEvent(0, CallControlLogger.RESULT, CallControlLogger.obfuscate(ret));
      return ret;
   }

   @Override
   public int getCallState(int callId) {
      this.this$0.logEvent(5, CallControlLogger.GETCALLSTATE, callId);
      int ret = this.getNext().getCallState(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getCLIPDisplayMode(int callId) {
      this.this$0.logEvent(5, CallControlLogger.GETCLIP, callId);
      int ret = this.getNext().getCLIPDisplayMode(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getEmergencyNumber() {
      this.this$0.logEvent(5, CallControlLogger.GETEMERGENCYNUMBER);
      String ret = this.getNext().getEmergencyNumber();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getForwardingNumber() {
      this.this$0.logEvent(5, CallControlLogger.GETFWDNUMBER);
      String ret = this.getNext().getForwardingNumber();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getForwardingNumberForService(int ssOption, int bearerService) {
      this.this$0.logEvent(5, CallControlLogger.GETFWDNUMBER, ssOption, bearerService);
      String ret = this.getNext().getForwardingNumberForService(ssOption, bearerService);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getHeldCallId() {
      this.this$0.logEvent(5, CallControlLogger.GETHELD);
      int ret = this.getNext().getHeldCallId();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getIncomingCallId() {
      this.this$0.logEvent(5, CallControlLogger.GETINCOMING);
      int ret = this.getNext().getIncomingCallId();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getMaxConferenceMembers() {
      this.this$0.logEvent(5, CallControlLogger.GETMAXCONFMEMBERS);
      int ret = this.getNext().getMaxConferenceMembers();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public int getNetworkFeatures() {
      this.this$0.logEvent(5, CallControlLogger.GETNETWORKFEATURES);
      int ret = this.getNext().getNetworkFeatures();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public String getNumber(int index) {
      this.this$0.logEvent(5, CallControlLogger.GETNUMBER, index);
      String ret = this.getNext().getNumber(index);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void holdCall() {
      this.this$0.logEvent(0, CallControlLogger.HOLD);
      this.getNext().holdCall();
   }

   @Override
   public boolean inCallDTMFDigitsEntered(String digits) {
      this.this$0.logEvent(5, CallControlLogger.INCALLDTMF, digits);
      boolean ret = this.getNext().inCallDTMFDigitsEntered(digits);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isActive() {
      this.this$0.logEvent(5, CallControlLogger.ISACTIVE);
      boolean ret = this.getNext().isActive();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isCallForwardUnconditionalActive(int line) {
      this.this$0.logEvent(5, CallControlLogger.ISCFUACTIVE, line);
      boolean ret = this.getNext().isCallForwardUnconditionalActive(line);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isCallRedirected(int callId) {
      this.this$0.logEvent(5, CallControlLogger.ISREDIRECTED, callId);
      boolean ret = this.getNext().isCallRedirected(callId);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isEmergencyNumber(String number) {
      this.this$0.logEvent(5, CallControlLogger.ISEMERGENCYNUMBER, number);
      boolean ret = this.getNext().isEmergencyNumber(number);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isFDNAvailable() {
      this.this$0.logEvent(5, CallControlLogger.ISFDNAVAILABLE);
      boolean ret = this.getNext().isFDNAvailable();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public boolean isFDNEnabled() {
      this.this$0.logEvent(5, CallControlLogger.ISFDNENABLED);
      boolean ret = this.getNext().isFDNEnabled();
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void querySSOption(int ssOption) {
      this.this$0.logEvent(5, CallControlLogger.QUERYSSOPTION, ssOption);
      this.getNext().querySSOption(ssOption);
   }

   @Override
   public int querySSOptionResult(int ssOption, int bearerService) {
      this.this$0.logEvent(5, CallControlLogger.QUERYSSOPTIONRESULT, ssOption, bearerService);
      int ret = this.getNext().querySSOptionResult(ssOption, bearerService);
      this.this$0.logEvent(5, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void removeCallFromConference(int callId) {
      this.this$0.logEvent(5, CallControlLogger.REMOVECALL, callId);
      this.getNext().removeCallFromConference(callId);
   }

   @Override
   public void rejectCall(int callId) {
      this.this$0.logEvent(0, CallControlLogger.REJECTCALL, callId);
      this.getNext().rejectCall(callId);
   }

   @Override
   public void requestEnableFDN(boolean enable) {
      this.this$0.logEvent(5, CallControlLogger.REQUESTENABLEFDN);
      this.getNext().requestEnableFDN(enable);
   }

   @Override
   public void resumeCall() {
      this.this$0.logEvent(0, CallControlLogger.RESUMECALL);
      this.getNext().resumeCall();
   }

   @Override
   public void sendSSPasswordResponse(String password) {
      this.this$0.logEvent(5, CallControlLogger.SENDSSPWDRESPONSE, password);
      this.getNext().sendSSPasswordResponse(password);
   }

   @Override
   public boolean setAlternateLine(int line) {
      this.this$0.logEvent(0, CallControlLogger.SETLINE, line);
      boolean ret = this.getNext().setAlternateLine(line);
      this.this$0.logEvent(0, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void setCallBarringPassword(String oldPassword, String newPassword) {
      this.this$0.logEvent(5, CallControlLogger.SETBARRINGPWD, oldPassword, newPassword);
      this.getNext().setCallBarringPassword(oldPassword, newPassword);
   }

   @Override
   public void setCallForwardingNumber(int type, String number) {
      this.this$0.logEvent(5, CallControlLogger.SETFWDNUMBER, type, number);
      this.getNext().setCallForwardingNumber(type, number);
   }

   @Override
   public void setSSBasicService(int bearerService) {
      this.this$0.logEvent(5, CallControlLogger.SETSSBASICSERVICE, bearerService);
      this.getNext().setSSBasicService(bearerService);
   }

   @Override
   public void setUSSDResponse(byte[] data) {
      this.this$0.logEvent(5, CallControlLogger.SETUSSDRESPONSE, data);
      this.getNext().setUSSDResponse(data);
   }

   @Override
   public int startCall(String number, int clir) {
      this.this$0.logEvent(0, CallControlLogger.STARTCALL, CallControlLogger.obfuscate(number), clir);
      int ret = this.getNext().startCall(number, clir);
      this.this$0.logEvent(0, CallControlLogger.RESULT, ret);
      return ret;
   }

   @Override
   public void startDTMF(int callId, byte character) {
      this.this$0.logEvent(5, CallControlLogger.STARTDTMF, callId, character);
      this.getNext().startDTMF(callId, character);
   }

   @Override
   public void stopAllCalls(boolean unconditional) {
      this.this$0.logEvent(0, CallControlLogger.STOPALLCALLS, unconditional);
      this.getNext().stopAllCalls(unconditional);
   }

   @Override
   public void stopCall(int callId) {
      this.this$0.logEvent(0, CallControlLogger.STOPCALL, callId);
      this.getNext().stopCall(callId);
   }

   @Override
   public void stopDTMF(int callId) {
      this.this$0.logEvent(5, CallControlLogger.STOPDTMF, callId);
      this.getNext().stopDTMF(callId);
   }

   @Override
   public void swapCalls() {
      this.this$0.logEvent(0, CallControlLogger.SWAP);
      this.getNext().swapCalls();
   }

   @Override
   public void transferCall() {
      this.this$0.logEvent(0, CallControlLogger.XFER);
      this.getNext().transferCall();
   }
}
