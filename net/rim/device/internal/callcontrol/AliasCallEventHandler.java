package net.rim.device.internal.callcontrol;

public class AliasCallEventHandler extends AbstractCallEventHandler {
   protected AliasCallEventHandler(int order) {
      super(order);
   }

   protected int getAlias(int _1) {
      throw null;
   }

   @Override
   public void callAdded(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callAdded(callId);
   }

   @Override
   public void callConnected(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callConnected(callId);
   }

   @Override
   public void callDelivered(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callDelivered(callId);
   }

   @Override
   public void callDisconnected(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callDisconnected(callId);
   }

   @Override
   public void callDisplayUpdated(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callDisplayUpdated(callId);
   }

   @Override
   public void callFailed(int callId, int error) {
      callId = this.getAlias(callId);
      this.getNext().callFailed(callId, error);
   }

   @Override
   public void callHeld(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callHeld(callId);
   }

   @Override
   public void callIncoming(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callIncoming(callId);
   }

   @Override
   public void callInitiated(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callInitiated(callId);
   }

   @Override
   public void callManipulateFailed(int callId, int error) {
      callId = this.getAlias(callId);
      this.getNext().callManipulateFailed(callId, error);
   }

   @Override
   public void callOTAStatusUpdated(int callId, int status) {
      callId = this.getAlias(callId);
      this.getNext().callOTAStatusUpdated(callId, status);
   }

   @Override
   public void callRemoved(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callRemoved(callId);
   }

   @Override
   public void callResumed(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callResumed(callId);
   }

   @Override
   public void callTransferred(int status, int reason) {
      this.getNext().callTransferred(status, reason);
   }

   @Override
   public void callWaiting(int callId) {
      callId = this.getAlias(callId);
      this.getNext().callWaiting(callId);
   }

   @Override
   public void callVoicePrivacyUpdated(int callId, boolean on) {
      callId = this.getAlias(callId);
      this.getNext().callVoicePrivacyUpdated(callId, on);
   }

   @Override
   public void callTransferStateUpdated(int callId, int state) {
      callId = this.getAlias(callId);
      this.getNext().callTransferStateUpdated(callId, state);
   }

   @Override
   public void dtmfData(int dtmf) {
      this.getNext().dtmfData(dtmf);
   }

   @Override
   public void callTimerUpdated(int callId, int time) {
      callId = this.getAlias(callId);
      this.getNext().callTimerUpdated(callId, time);
   }

   @Override
   public void featureReady() {
      this.getNext().featureReady();
   }

   @Override
   public void responseEnableFDN(int status) {
      this.getNext().responseEnableFDN(status);
   }

   @Override
   public void ssNotification(int ssOption) {
      this.getNext().ssNotification(ssOption);
   }

   @Override
   public void ssPasswordRequested(int requestType) {
      this.getNext().ssPasswordRequested(requestType);
   }

   @Override
   public void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
      this.getNext().ssRequestFailed(reason, bearerService, isUSSDCmd);
   }

   @Override
   public void ssRequestInvalidPassword() {
      this.getNext().ssRequestInvalidPassword();
   }

   @Override
   public void ssRequestRejected(boolean isUSSDCmd) {
      this.getNext().ssRequestRejected(isUSSDCmd);
   }

   @Override
   public void ssRequestReleased(boolean isUSSDCmd) {
      this.getNext().ssRequestReleased(isUSSDCmd);
   }

   @Override
   public void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
      this.getNext().ssRequestSucceeded(ss, action, result, bearerService, isUSSDCmd, forwardingNumberAvailable);
   }

   @Override
   public void ssUpdated(int ssOption, int state) {
      this.getNext().ssUpdated(ssOption, state);
   }

   @Override
   public void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
      this.getNext().ssUssDisplay(data, messageCoding, collectInput);
   }

   @Override
   public void voiceLineChanged(int line) {
      this.getNext().voiceLineChanged(line);
   }

   @Override
   public void alternateLinesUpdated() {
      this.getNext().alternateLinesUpdated();
   }

   @Override
   public void voicemailCountUpdated(int line, int count) {
      this.getNext().voicemailCountUpdated(line, count);
   }
}
