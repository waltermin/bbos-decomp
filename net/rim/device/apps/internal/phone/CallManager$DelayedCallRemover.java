package net.rim.device.apps.internal.phone;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Ui;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.Out;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.vm.WeakReference;

final class CallManager$DelayedCallRemover implements Runnable {
   private WeakReference _callRef;
   private int _callId;
   private int _doneEventID;
   private CallManager$CallFailedAlertDialog _alertDialog;
   private Object _context;
   private boolean _removed;
   private boolean _displayed;
   private Application _app;
   private int _timeout;
   private int _failureReason;
   private final CallManager this$0;

   public CallManager$DelayedCallRemover(CallManager _1, int callId, int failureReason, int doneEventID) {
      this.this$0 = _1;
      this._callRef = new WeakReference(_1.findCallByCallId(callId));
      this._callId = callId;
      this._doneEventID = doneEventID;
      this._failureReason = failureReason;
   }

   public final void setContext(Object context) {
      this._context = context;
   }

   public final void removeLater(Application app, int timeout) {
      if (this._alertDialog != null && !this._displayed) {
         this._app = app;
         this._timeout = timeout;
      } else {
         app.invokeLater(this, timeout, false);
      }
   }

   public final synchronized void removeNow() {
      if (!this._removed) {
         System.out.println("CallManager removing " + this._callId);
         LiveCall call = (LiveCall)this._callRef.get();
         if (call != null) {
            LiveCall foundCall = this.this$0.findCallByCallId(this._callId);
            if (call.equals(foundCall)) {
               int originalCount = this.this$0.getNumCalls();
               VoiceServices.stopCall(this._callId);
               this.this$0.removeCall(call);
               this.this$0.currentCallSanityCheck(this._callId);
               int flags = 4;
               if (this._failureReason == 28) {
                  flags |= 8;
               }

               if (originalCount == 1) {
                  this.this$0.notifyListeners(10, call, flags);
               } else {
                  this.this$0.notifyListeners(30, call, flags);
               }
            } else {
               System.out.println("call not found");
            }
         } else {
            System.out.println("weak ref empty");
         }

         if (this._doneEventID != 0) {
            VoiceServices.broadcastEvent(this._doneEventID, this._callId, this._context);
         }

         this._removed = true;
      }
   }

   final void alert(String message, int callId, boolean specialFailureReason) {
      if (this._alertDialog == null) {
         Out.p("PHONE: queue failure status");
         this._alertDialog = new CallManager$CallFailedAlertDialog(message, callId);
         this._alertDialog.setCallRemover(this);
         Ui.getUiEngine().pushGlobalScreen(this._alertDialog, -2147483645, 2);
      }
   }

   final void onAlertDialogDisplayed() {
      if (!this._displayed && this._timeout > 0) {
         this._app.invokeLater(this, this._timeout, false);
      }

      this._displayed = true;
   }

   final void onAlertDialogClosed() {
      this.removeNow();
   }

   @Override
   public final void run() {
      if (this._alertDialog != null) {
         this._alertDialog.close();
      }

      this.removeNow();
   }
}
