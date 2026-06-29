package net.rim.device.apps.internal.deviceselftest;

import java.util.Timer;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.PhoneListener;

final class TestRFAntenna$MyPhoneListener implements PhoneListener {
   private final TestRFAntenna this$0;

   TestRFAntenna$MyPhoneListener(TestRFAntenna _1) {
      this.this$0 = _1;
   }

   @Override
   public final void callIncoming(int callId) {
   }

   @Override
   public final void callConnected(int callId) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }

      this.this$0.taskScreen.insertReport("Call Connected @ ");
      this.this$0.timer = new Timer();
      this.this$0.timeoutAction = new TimeoutAction(this.this$0.taskScreen);
      this.this$0.timer.schedule(this.this$0.timeoutAction, 0, 5000);
   }

   @Override
   public final void callDisconnected(int callId) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }

      if (this.this$0.timer != null) {
         this.this$0.timer.cancel();
      }

      this.this$0.taskScreen.insertReport("Call Disconnected @ ");
   }

   @Override
   public final void callFailed(int callId, int code) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }

      if (this.this$0.timer != null) {
         this.this$0.timer.cancel();
      }

      String cause;
      switch (code) {
         case 1:
            cause = "Subscriber busy, ";
            break;
         case 5:
            cause = "Authorization failure, ";
            break;
         case 6:
            cause = "Emergency call only, ";
            break;
         case 11:
            cause = "Service not available, ";
            break;
         case 12:
            cause = "Out of coverage, ";
            break;
         case 16:
            cause = "Connection denied by network, ";
            break;
         case 24:
            cause = "User not available, ";
            break;
         case 25:
            cause = "User unknown, ";
            break;
         case 26:
            cause = "User not reachable, ";
            break;
         default:
            cause = "";
      }

      this.this$0.taskScreen.insertReport(cause + " Call Failed @ ");
   }

   @Override
   public final void callInitiated(int callId) {
      this.this$0.taskScreen.insertReport("Call Initialized @ ");
   }

   @Override
   public final void callDelivered(int callId) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }
   }

   @Override
   public final void callAdded(int callId) {
   }

   @Override
   public final void callDisplayUpdated(int callId) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }
   }

   @Override
   public final void callHeld(int callId) {
   }

   @Override
   public final void callManipulateFailed(int callId, int error) {
   }

   @Override
   public final void callOTAStatusUpdated(int callId, int status) {
   }

   @Override
   public final void callRemoved(int callId) {
   }

   @Override
   public final void callTransferred(int status, int reason) {
   }

   @Override
   public final void callResumed(int callId) {
   }

   @Override
   public final void callTimerUpdated(int callId, int time) {
      if (ApplicationManager.getApplicationManager().getForegroundProcessId() != this.this$0.processId) {
         ApplicationManager.getApplicationManager().requestForeground(this.this$0.processId);
      }
   }

   @Override
   public final void callVoicePrivacyUpdated(int callId, boolean on) {
   }

   @Override
   public final void callWaiting(int callId) {
   }

   @Override
   public final void ssRequestFailed(int reason, int bearerService, boolean isUSSDCmd) {
   }

   @Override
   public final void ssRequestInvalidPassword() {
   }

   @Override
   public final void ssPasswordRequested(int requestType) {
   }

   @Override
   public final void ssRequestRejected(boolean isUSSDCmd) {
   }

   @Override
   public final void ssRequestReleased(boolean isUSSDCmd) {
   }

   @Override
   public final void ssRequestSucceeded(int ss, int action, int result, int bearerService, boolean isUSSDCmd, boolean forwardingNumberAvailable) {
   }

   @Override
   public final void ssUpdated(int ssOption, int state) {
   }

   @Override
   public final void ssNotification(int ssOption) {
   }

   @Override
   public final void ssUssDisplay(byte[] data, int messageCoding, boolean collectInput) {
   }

   @Override
   public final void featureReady() {
   }

   @Override
   public final void responseEnableFDN(int status) {
   }

   @Override
   public final void callTransferStateUpdated(int parameter1, int parameter2) {
   }

   @Override
   public final void alternateLinesUpdated() {
   }

   @Override
   public final void voiceLineChanged(int parameter1) {
   }

   @Override
   public final void voicemailCountUpdated(int p1, int p2) {
   }

   @Override
   public final void dtmfData(int dtmf) {
   }
}
