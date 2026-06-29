package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.phone.api.WeakPhoneEventListener;
import net.rim.device.apps.internal.phone.api.ui.PhoneStatusDialog;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;

class SSRequest implements PhoneEventListener, Runnable {
   private WeakPhoneEventListener _weakRefPhoneListener;
   protected boolean _aborted;
   protected boolean _showStatus;
   protected PhoneStatusDialog _statusDialog;
   protected boolean _gotNetworkResponse;
   private String[] _errors;
   private int _numProgressUpdates;
   protected static long MAX_WAIT_FOR_NETWORK;

   protected synchronized void done() {
      this.closeStatus();
   }

   protected void runTask() {
      throw null;
   }

   protected void setNumProgressUpdates(int num) {
      this._numProgressUpdates = num;
   }

   protected void updateProgress() {
      if (this._numProgressUpdates > 0 && this._statusDialog != null) {
         this._statusDialog.updateProgress();
      }
   }

   protected String getSSErrorString(int eventId, int reason) {
      switch (eventId) {
         case 5003:
            return PhoneResources.getString(189);
         default:
            return PhoneResources.getString(6055);
      }
   }

   protected void onSSError(int errorEventId, int reason) {
      String errorString = this.getSSErrorString(errorEventId, reason);
      if (errorString != null && errorString.length() > 0) {
         this.queueError(errorString);
      }
   }

   public void start() {
      if (this._showStatus) {
         this._statusDialog = new PhoneStatusDialog(CommonResources.getString(9156), this._numProgressUpdates);
         PhoneOptionsScreen.setSSRequestInProgress(true);
         Ui.getUiEngine().pushGlobalScreen(this._statusDialog, 10, 2);
      }

      new Thread(this).start();
   }

   protected synchronized void waitForNetworkResponse() {
      this._gotNetworkResponse = false;

      try {
         this.wait(MAX_WAIT_FOR_NETWORK);
         if (!this._gotNetworkResponse) {
            this.onTimeout();
            return;
         }
      } finally {
         ;
      }
   }

   protected synchronized void onTimeout() {
      this.abort();
      Application.getApplication().invokeLater(new SSRequest$3(this));
   }

   protected synchronized void abort() {
      this._aborted = true;
      this.closeStatus();
      this.notify();
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      boolean var3 = false /* VF: Semaphore variable */;

      try {
         var3 = true;
         SSManager.suppressMessageDialogs(true);
         this.runTask();
         this.showErrors();
         var3 = false;
      } finally {
         if (var3) {
            SSManager.suppressMessageDialogs(false);
            this.closeStatus();
            if (this._weakRefPhoneListener.get() != null) {
               VoiceServices.removePhoneEventListener(this._weakRefPhoneListener);
            }
         }
      }

      SSManager.suppressMessageDialogs(false);
      this.closeStatus();
      if (this._weakRefPhoneListener.get() != null) {
         VoiceServices.removePhoneEventListener(this._weakRefPhoneListener);
      }
   }

   @Override
   public synchronized void phoneEventNotify(int eventId, int callId, Object context) {
      this._gotNetworkResponse = true;
      switch (eventId) {
         case 5000:
            this.notify();
         case 4999:
            return;
         case 5001:
         case 5002:
         case 5003:
         case 5004:
         default:
            int reason = this.getErrorReasonCode(eventId, callId, context);
            this.onSSError(eventId, reason);
            this.notify();
      }
   }

   private boolean isDuplicateError(String error) {
      for (int idx = this._errors.length - 1; idx >= 0; idx--) {
         if (error.equals(this._errors[idx])) {
            return true;
         }
      }

      return false;
   }

   SSRequest(boolean showStatus) {
      this._showStatus = showStatus;
      this._weakRefPhoneListener = new WeakPhoneEventListener(this);
      VoiceServices.addPhoneEventListener(this._weakRefPhoneListener);
   }

   private void showErrors() {
      if (this._errors != null && this._errors.length != 0) {
         String[] errors = this._errors;
         Application.getApplication().invokeLater(new SSRequest$1(this, errors));
      }
   }

   private void queueError(String error) {
      if (error != null && error.length() != 0) {
         if (this._errors == null) {
            this._errors = new String[]{error};
         } else {
            if (!this.isDuplicateError(error)) {
               Array.resize(this._errors, this._errors.length + 1);
               this._errors[this._errors.length - 1] = error;
            }
         }
      }
   }

   SSRequest() {
      this(true);
   }

   private int getErrorReasonCode(int eventId, int callId, Object context) {
      int reason = -1;
      if (context instanceof int[]) {
         int[] params = (int[])context;
         if (params.length > 0) {
            reason = params[0];
         }
      }

      if (reason == -1) {
         reason = callId;
      }

      return reason;
   }

   private void closeStatus() {
      if (this._statusDialog != null) {
         Screen screen = this._statusDialog;
         this._statusDialog = null;
         Application.getApplication().invokeLater(new SSRequest$2(this, screen));
      }
   }

   static {
      switch (RadioInfo.getNetworkType()) {
         case 4:
            MAX_WAIT_FOR_NETWORK = 16000;
            return;
         default:
            MAX_WAIT_FOR_NETWORK = 10000;
      }
   }
}
