package net.rim.device.apps.internal.phone;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.PhoneEventListener;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.DTMFEcho;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.ui.component.PopupDialog;
import net.rim.device.internal.ui.component.PopupDialogClosedListener;

final class AfterDialToneHandler implements PopupDialogClosedListener, PhoneEventListener, Runnable {
   private boolean _useCtiRules;
   private AfterDialDialog _afterDialDialog;
   private AfterDialToneHandler$DTMFPauseStatus _dtmfPauseStatus;
   private String _tones;
   private LiveCall _call;
   private int _firstToneIndex;
   private int _lastToneIndex;
   private int _timeToPause;
   private boolean _wait;
   private UiApplication _app;
   private String _currentSequence;
   private boolean _started;
   private boolean _aborted;
   private boolean _finished;
   private boolean _continueOnCallConnected;
   private boolean _simPhoneBookCall;
   private static final int CTI_PAUSE_LENGTH = 1;
   private static final int STANDARD_PAUSE_LENGTH = 2;
   private static final int GSM_11_11_PAUSE_LENGTH = 3;

   public AfterDialToneHandler(LiveCall call, String dtmfTones, UiApplication app) {
      this(call, dtmfTones, app, null);
   }

   public AfterDialToneHandler(LiveCall call, String dtmfTones, UiApplication app, Object context) {
      this._app = app;
      this._call = call;
      this._tones = dtmfTones;
      if (ContextObject.getFlag(context, 123)) {
         this._simPhoneBookCall = true;
      } else if (isImodeNumber(call)) {
         this._useCtiRules = isImodeNumber(call);
      }

      VoiceServices.addPhoneEventListener(this);
      this._firstToneIndex = 0;
      this._lastToneIndex = 0;
      this._finished = false;
      this._aborted = false;
   }

   public final void start(boolean waitForCallConnected) {
      synchronized (this) {
         if (this._started || this._aborted) {
            return;
         }

         this._started = true;
      }

      if (waitForCallConnected) {
         char ch = this._tones.charAt(0);
         if (ch != '!') {
            this._continueOnCallConnected = true;
            this._currentSequence = null;
            synchronized (Application.getEventLock()) {
               this.getAfterDialDialog().show(null);
               return;
            }
         }
      }

      this.processNextTones();
   }

   private final void abort() {
      this._aborted = true;
      if (this._dtmfPauseStatus != null && this._dtmfPauseStatus.isDisplaying()) {
         this._dtmfPauseStatus.dismiss();
      } else if (this._afterDialDialog != null && this._afterDialDialog.isDisplaying()) {
         this._afterDialDialog.dismiss();
      }

      VoiceServices.removePhoneEventListener(this);
   }

   private final boolean isDTMFTone(char c) {
      return Character.isDigit(c) || c == '*' || c == '#' || Character.isDigit(PhoneNumberServices.getMnemonicKeyMapping(c));
   }

   private static final boolean isImodeNumber(LiveCall liveCall) {
      CallerIDInfo callerIDInfo = liveCall.getCallerIDInfo();
      if (callerIDInfo != null) {
         AbstractPhoneNumberModel phoneNumber = (AbstractPhoneNumberModel)callerIDInfo.getNumber();
         if (phoneNumber != null && phoneNumber.getType() == 12) {
            return true;
         }
      }

      return false;
   }

   private final void calculateNextSequence() {
      this._timeToPause = 0;
      this._wait = false;
      this._currentSequence = null;
      if (this._useCtiRules) {
         for (; this._firstToneIndex < this._tones.length(); this._firstToneIndex++) {
            char c = this._tones.charAt(this._firstToneIndex);
            if (c == ',') {
               if (this._wait) {
                  break;
               }

               this._timeToPause++;
            } else if (c == '!') {
               if (this._timeToPause > 0) {
                  break;
               }

               this._wait = true;
            } else if (this.isDTMFTone(c)) {
               break;
            }
         }
      } else {
         int pauseLength = 2;
         if (this._simPhoneBookCall || this._firstToneIndex == 0) {
            pauseLength = 3;
         }

         for (; this._firstToneIndex < this._tones.length(); this._firstToneIndex++) {
            char c = this._tones.charAt(this._firstToneIndex);
            if (c == ',') {
               this._timeToPause += pauseLength;
               if (!this._simPhoneBookCall) {
                  pauseLength = 2;
               }
            } else if (c == '!') {
               this._wait = true;
            } else if (this.isDTMFTone(c)) {
               break;
            }
         }

         if (this._firstToneIndex == this._tones.length()) {
            this._wait = false;
            this._timeToPause = 0;
            this._finished = true;
            return;
         }
      }

      for (this._lastToneIndex = this._firstToneIndex; this._lastToneIndex < this._tones.length(); this._lastToneIndex++) {
         char c = this._tones.charAt(this._lastToneIndex);
         if (!this.isDTMFTone(c)) {
            break;
         }
      }

      if (this._lastToneIndex == this._tones.length()) {
         this._finished = true;
      }
   }

   private final boolean finished() {
      return this._finished;
   }

   private final synchronized void processNextTones() {
      if (this.finished()) {
         VoiceServices.removePhoneEventListener(this);
      } else {
         this.calculateNextSequence();
         if (this._timeToPause > 0) {
            this._currentSequence = this._tones.substring(this._firstToneIndex, this._lastToneIndex);
            this._app.invokeLater(this);
         } else if (this._wait) {
            this._currentSequence = this._tones.substring(this._firstToneIndex, this._lastToneIndex);
            synchronized (Application.getEventLock()) {
               this.getAfterDialDialog().show(this._currentSequence);
            }
         }

         if (!this.finished()) {
            this._firstToneIndex = this._lastToneIndex;
         }
      }
   }

   private final AfterDialDialog getAfterDialDialog() {
      this._afterDialDialog = new AfterDialDialog(this);
      return this._afterDialDialog;
   }

   private final AfterDialToneHandler$DTMFPauseStatus getPauseStatusDialog() {
      if (this._dtmfPauseStatus == null) {
         this._dtmfPauseStatus = new AfterDialToneHandler$DTMFPauseStatus(this, this);
      }

      return this._dtmfPauseStatus;
   }

   @Override
   public final void run() {
      int seconds = this._timeToPause;
      String statusString = MessageFormat.format(PhoneResources.getString(128), new String[]{"" + seconds});
      this.getPauseStatusDialog().display(statusString, seconds * 1000);
   }

   private final void sendTones(String tones) {
      if (tones != null) {
         if (!ApplicationManager.getApplicationManager().isSystemLocked()) {
            DTMFEcho.echoTones(tones);
         }

         this._call.sendDTMFTones(tones, true);
      } else {
         this.processNextTones();
      }
   }

   @Override
   public final void dialogClosed(PopupDialog dialog, int closeReason) {
      if (!this._aborted) {
         this._afterDialDialog = null;
         this._continueOnCallConnected = false;
         if (dialog instanceof AfterDialDialog) {
            AfterDialDialog afterDialDialog = (AfterDialDialog)dialog;
            switch (afterDialDialog.getUserResponse()) {
               case -1:
                  break;
               case 0:
               default:
                  this.sendTones(this._currentSequence);
                  return;
               case 1:
                  this.processNextTones();
                  return;
               case 2:
                  VoiceServices.stopCurrentCall(null);
            }
         }
      }
   }

   @Override
   public final void phoneEventNotify(int eventId, int param1, Object param2) {
      switch (eventId) {
         case 1001:
            if (param1 == this._call.getCallId() && this._continueOnCallConnected && this._afterDialDialog != null) {
               this._afterDialDialog.dismissContinue();
               this._continueOnCallConnected = false;
               return;
            }
            break;
         case 1002:
         case 1006:
            int callId = param1;
            if (callId == this._call.getCallId()) {
               this.abort();
               return;
            }
            break;
         case 170000:
            if (this._started) {
               this.processNextTones();
            }
      }
   }

   private final void pauseStatusDismissed() {
      if (!this._aborted) {
         this.sendTones(this._currentSequence);
      }
   }
}
