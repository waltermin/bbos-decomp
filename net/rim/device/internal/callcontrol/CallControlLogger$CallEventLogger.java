package net.rim.device.internal.callcontrol;

class CallControlLogger$CallEventLogger extends AbstractCallEventHandler {
   private final CallControlLogger this$0;

   public CallControlLogger$CallEventLogger(CallControlLogger _1, int order) {
      super(order);
      this.this$0 = _1;
   }

   @Override
   public void callAdded(int callId) {
      this.this$0.logEvent(0, CallControlLogger.ADDED, callId);
      this.getNext().callAdded(callId);
   }

   @Override
   public void callConnected(int callId) {
      this.this$0.logEvent(0, CallControlLogger.CONNECTED, callId);
      this.getNext().callConnected(callId);
   }

   @Override
   public void callDelivered(int callId) {
      this.this$0.logEvent(0, CallControlLogger.DELIVERED, callId);
      this.getNext().callDelivered(callId);
   }

   @Override
   public void callDisconnected(int callId) {
      this.this$0.logEvent(0, CallControlLogger.DISCONNECTED, callId);
      this.getNext().callDisconnected(callId);
   }

   @Override
   public void callDisplayUpdated(int callId) {
      this.this$0.logEvent(0, CallControlLogger.UPDATED, callId);
      this.getNext().callDisplayUpdated(callId);
   }

   @Override
   public void callFailed(int callId, int error) {
      this.this$0.logEvent(0, CallControlLogger.FAILED, callId, error);
      this.getNext().callFailed(callId, error);
   }

   @Override
   public void callHeld(int callId) {
      this.this$0.logEvent(0, CallControlLogger.HELD, callId);
      this.getNext().callHeld(callId);
   }

   @Override
   public void callIncoming(int callId) {
      this.this$0.logEvent(0, CallControlLogger.INCOMING, callId);
      this.getNext().callIncoming(callId);
   }

   @Override
   public void callInitiated(int callId) {
      this.this$0.logEvent(0, CallControlLogger.INITIATED, callId);
      this.getNext().callInitiated(callId);
   }

   @Override
   public void callManipulateFailed(int callId, int error) {
      this.this$0.logEvent(0, CallControlLogger.MANIPULATEFAILED, callId, error);
      this.getNext().callManipulateFailed(callId, error);
   }

   @Override
   public void callOTAStatusUpdated(int callId, int status) {
      this.this$0.logEvent(0, CallControlLogger.OTAUPDATED, callId, status);
      this.getNext().callOTAStatusUpdated(callId, status);
   }

   @Override
   public void callRemoved(int callId) {
      this.this$0.logEvent(0, CallControlLogger.REMOVED, callId);
      this.getNext().callRemoved(callId);
   }

   @Override
   public void callResumed(int callId) {
      this.this$0.logEvent(0, CallControlLogger.RESUMED, callId);
      this.getNext().callResumed(callId);
   }

   @Override
   public void callTransferred(int status, int reason) {
      this.this$0.logEvent(0, CallControlLogger.XFER, status, reason);
      this.getNext().callTransferred(status, reason);
   }

   @Override
   public void callWaiting(int callId) {
      this.this$0.logEvent(0, CallControlLogger.WAITING, callId);
      this.getNext().callWaiting(callId);
   }

   @Override
   public void callVoicePrivacyUpdated(int callId, boolean on) {
      this.this$0.logEvent(0, CallControlLogger.PRIVACY, callId, on);
      this.getNext().callVoicePrivacyUpdated(callId, on);
   }

   @Override
   public void callTransferStateUpdated(int callId, int state) {
      this.this$0.logEvent(0, CallControlLogger.XFERUPDATED, callId, state);
      this.getNext().callTransferStateUpdated(callId, state);
   }

   @Override
   public void callTimerUpdated(int callId, int time) {
      this.this$0.logEvent(5, CallControlLogger.TIMER, callId, time);
      this.getNext().callTimerUpdated(callId, time);
   }

   @Override
   public void featureReady() {
      this.this$0.logEvent(0, CallControlLogger.FEATUREREADY);
      this.getNext().featureReady();
   }

   @Override
   public void responseEnableFDN(int status) {
      this.this$0.logEvent(0, CallControlLogger.ENABLEFDN, status);
      this.getNext().responseEnableFDN(status);
   }

   @Override
   public void ssNotification(int ssOption) {
      this.this$0.logEvent(0, CallControlLogger.SSNOTIFY, ssOption);
      this.getNext().ssNotification(ssOption);
   }

   @Override
   public void ssPasswordRequested(int requestType) {
      this.this$0.logEvent(0, CallControlLogger.SSPWDREQUESTED, requestType);
      this.getNext().ssPasswordRequested(requestType);
   }

   @Override
   public void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
      this.this$0.logEvent(0, CallControlLogger.SSRQFAIL, reason, bearerService, isUSSDCmd);
      this.getNext().ssRequestFailed(reason, bearerService, isUSSDCmd);
   }

   @Override
   public void ssRequestInvalidPassword() {
      this.this$0.logEvent(0, CallControlLogger.SSRQINVALIDPWD);
      this.getNext().ssRequestInvalidPassword();
   }

   @Override
   public void ssRequestRejected(boolean isUSSDCmd) {
      this.this$0.logEvent(0, CallControlLogger.SSRQREJECTED, isUSSDCmd);
      this.getNext().ssRequestRejected(isUSSDCmd);
   }

   @Override
   public void ssRequestReleased(boolean isUSSDCmd) {
      this.this$0.logEvent(0, CallControlLogger.SSRQRELEASED, isUSSDCmd);
      this.getNext().ssRequestReleased(isUSSDCmd);
   }

   @Override
   public void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
      this.this$0.logEvent(0, CallControlLogger.SSRQSUCCESS, ss, action, result, bearerService, isUSSDCmd, forwardingNumberAvailable);
      this.getNext().ssRequestSucceeded(ss, action, result, bearerService, isUSSDCmd, forwardingNumberAvailable);
   }

   @Override
   public void ssUpdated(int ssOption, int state) {
      this.this$0.logEvent(0, CallControlLogger.SSUPDATED, ssOption, state);
      this.getNext().ssUpdated(ssOption, state);
   }

   @Override
   public void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
      this.this$0.logEvent(0, CallControlLogger.SSUSSD, data, messageCoding, collectInput);
      this.getNext().ssUssDisplay(data, messageCoding, collectInput);
   }

   @Override
   public void voiceLineChanged(int line) {
      this.this$0.logEvent(0, CallControlLogger.LINECHANGED, line);
      this.getNext().voiceLineChanged(line);
   }

   @Override
   public void alternateLinesUpdated() {
      this.this$0.logEvent(0, CallControlLogger.LINESUPDATED);
      this.getNext().alternateLinesUpdated();
   }

   @Override
   public void voicemailCountUpdated(int line, int count) {
      this.this$0.logEvent(0, CallControlLogger.VMCOUNTUPDATED, line, count);
      this.getNext().voicemailCountUpdated(line, count);
   }

   @Override
   public void dtmfData(int dtmf) {
      this.this$0.logEvent(4, CallControlLogger.DTMFDATA, dtmf);
      this.getNext().dtmfData(dtmf);
   }
}
