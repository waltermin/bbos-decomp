package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.ApplicationManager;
import net.rim.device.internal.system.ApplicationManagerInternal;
import net.rim.vm.Message;

final class CallEventDispatcher extends AbstractCallEventHandler {
   public CallEventDispatcher() {
      super(2100);
   }

   @Override
   public final void callAdded(int callId) {
      this.postCallEvent(1560, callId);
   }

   @Override
   public final void callConnected(int callId) {
      this.postCallEvent(1555, callId);
   }

   @Override
   public final void callDelivered(int callId) {
      this.postCallEvent(1563, callId);
   }

   @Override
   public final void callDisconnected(int callId) {
      this.postCallEvent(1557, callId);
   }

   @Override
   public final void callDisplayUpdated(int callId) {
      this.postCallEvent(1574, callId);
   }

   @Override
   public final void callFailed(int callId, int error) {
      this.postCallEvent(1556, callId, error, 0);
   }

   @Override
   public final void callHeld(int callId) {
      this.postCallEvent(1558, callId);
   }

   @Override
   public final void callIncoming(int callId) {
      this.postCallEvent(1553, callId);
   }

   @Override
   public final void callInitiated(int callId) {
      this.postCallEvent(1564, callId);
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
      this.postCallEvent(1562, callId, error, 0);
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
      this.postCallEvent(1600, callId, status, 0);
   }

   @Override
   public final void callRemoved(int callId) {
      this.postCallEvent(1561, callId);
   }

   @Override
   public final void callResumed(int callId) {
      this.postCallEvent(1559, callId);
   }

   @Override
   public final void callTransferred(int status, int reason) {
      this.postCallEvent(1668, status, reason, 0);
   }

   @Override
   public final void callTransferStateUpdated(int callId, int state) {
      this.postCallEvent(5001, callId, state, 0);
   }

   @Override
   public final void callWaiting(int callId) {
      this.postCallEvent(1554, callId);
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
      this.postCallEvent(1605, callId, on ? 0 : 1, 0);
   }

   @Override
   public final void dtmfData(int dtmf) {
      this.postCallEvent(5003, dtmf);
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      this.postTimerEvent(1568, callId, time, 0);
   }

   @Override
   public final void featureReady() {
      this.postControlEvent(1589, 0);
   }

   @Override
   public final void responseEnableFDN(int status) {
      this.postControlEvent(status, 0);
   }

   @Override
   public final void ssNotification(int ssOption) {
      this.postControlEvent(1609, ssOption);
   }

   @Override
   public final void ssPasswordRequested(int requestType) {
      this.postControlEvent(1610, requestType);
   }

   @Override
   public final void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
      int data1 = bearerService | (isUSSDCmd ? 256 : 0);
      this.postControlEvent(1569, reason, 0, data1, null);
   }

   @Override
   public final void ssRequestInvalidPassword() {
      this.postControlEvent(1573, 0);
   }

   @Override
   public final void ssRequestRejected(boolean isUSSDCmd) {
      int data1 = isUSSDCmd ? 256 : 0;
      this.postControlEvent(1571, 0, 0, data1, null);
   }

   @Override
   public final void ssRequestReleased(boolean isUSSDCmd) {
      int data1 = isUSSDCmd ? 256 : 0;
      this.postControlEvent(1572, 0, 0, data1, null);
   }

   @Override
   public final void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
      int data0 = action | result << 16;
      int data1 = bearerService | (isUSSDCmd ? 256 : 0) | (forwardingNumberAvailable ? 65536 : 0);
      this.postControlEvent(1570, ss, data0, data1, null);
   }

   @Override
   public final void ssUpdated(int ssOption, int state) {
      this.postControlEvent(1606, 0, ssOption, state, null);
   }

   @Override
   public final void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
      int event = collectInput ? 1608 : 1607;
      this.postControlEvent(event, 0, messageCoding, 0, data);
   }

   @Override
   public final void voiceLineChanged(int line) {
      this.postControlEvent(1669, line);
   }

   @Override
   public final void alternateLinesUpdated() {
      this.postControlEvent(5000, 0);
   }

   @Override
   public final void voicemailCountUpdated(int line, int count) {
      this.postControlEvent(5002, line, count, 0, null);
   }

   private final void postCallEvent(int event, int subMessage) {
      this.postCallEvent(event, subMessage, 0, 0);
   }

   private final void postCallEvent(int event, int subMessage, int data0, int data1) {
      Message msg = new Message(52, event, subMessage, data0, data1);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }

   private final void postCallEvent(int event, int subMessage, int data0, int data1, Object obj0) {
      Message msg = new Message(52, event, subMessage, data0, data1);
      msg.setObject0(obj0);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }

   private final void postTimerEvent(int event, int subMessage, int data0, int data1) {
      Message msg = new Message(53, event, subMessage, data0, data1);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }

   private final void postControlEvent(int event, int subMessage) {
      this.postControlEvent(event, subMessage, 0, 0, null);
   }

   private final void postControlEvent(int event, int subMessage, int data0, int data1, Object obj0) {
      Message msg = new Message(54, event, subMessage, data0, data1);
      msg.setObject0(obj0);
      ApplicationManagerInternal appManager = (ApplicationManagerInternal)ApplicationManager.getApplicationManager();
      appManager.postMessage(msg);
   }
}
