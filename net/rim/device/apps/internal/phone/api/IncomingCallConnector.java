package net.rim.device.apps.internal.phone.api;

import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.phone.VoiceServices;

public final class IncomingCallConnector extends CallConnector {
   protected int _callIdToAnswer;
   protected int _callIdToWaitFor;
   protected int _answeringOption;
   protected boolean _finished;
   protected Object _connectionContext;
   protected boolean _callWaiting;
   protected boolean _conferenceCallInProgress;
   protected static final long TIMEOUT;

   public static final void connect(int callId, int answeringOption, Object context) {
      new IncomingCallConnector(callId, answeringOption, context).connect();
   }

   public IncomingCallConnector(int callId, int answeringOption, Object context) {
      this(callId, answeringOption, 5000, context);
   }

   public IncomingCallConnector(int callId, int answeringOption, long timeout, Object context) {
      super(timeout);
      this._callIdToAnswer = callId;
      this._finished = false;
      this._answeringOption = answeringOption;
      this._callWaiting = PhoneUtilities.getPrivateFlag(context, 23);
      this._connectionContext = ContextObject.clone(context);
      this._conferenceCallInProgress = ContextObject.getFlag(context, 80);
      this.setPreferredLine(PhoneUtilities.getCurrentLineId(callId));
   }

   @Override
   protected final boolean conditionsSatisfied(int lastEvent, int callId, Object context) {
      byte state = VoiceServices.getPhoneState();
      switch (this._answeringOption) {
         case 0:
            return false;
         case 1:
            switch (state) {
               case 1:
               case 5:
                  return true;
               default:
                  return false;
            }
         case 2:
         default:
            switch (state) {
               case 1:
               case 5:
                  return true;
               default:
                  return false;
            }
      }
   }

   @Override
   protected final void startConnection() {
      super.startConnection();
      this.answer();
   }

   private final void answer() {
      if (PhoneUtilities.cdmaTypeNetwork()) {
         this.answerCDMA();
      } else {
         this.answerGSM();
      }
   }

   protected final void answerDropCurrent() {
      byte state = VoiceServices.getPhoneState();
      switch (state) {
         case 2:
         case 4:
         case 8:
            this.waitForEvent(1002);
            PhoneUtilities.setPrivateFlag(this._connectionContext, 56);
            VoiceServices.stopCurrentCall(this._connectionContext);
            return;
         default:
            this.answerCall();
      }
   }

   protected final void answerHoldCurrent() {
      byte state = VoiceServices.getPhoneState();
      if (RadioInfo.getNetworkType() == 5) {
         this.answerCall();
      } else {
         switch (state) {
            case 2:
               this.waitForEvent(1003);
               VoiceServices.holdCall();
               return;
            case 4:
               this.waitForEvent(1003);
               VoiceServices.holdCall();
               return;
            default:
               this.answerCall();
         }
      }
   }

   protected final void answerDropAll() {
   }

   protected final void answerGSM() {
      if (this._callWaiting) {
         this._callIdToWaitFor = Phone.getInstance().getActiveCallId();
         switch (this._answeringOption) {
            case 0:
               this.answerCall();
               return;
            case 1:
            default:
               this.answerHoldCurrent();
               return;
            case 2:
               this.answerDropCurrent();
               return;
            case 3:
               this.answerDropAll();
         }
      } else {
         this.answerCall();
      }
   }

   private final void answerCDMA() {
      this.stopListening();
      if (this._callWaiting) {
         VoiceServices.flash(null);
         VoiceServices.broadcastEvent(3006, this._callIdToAnswer, this._connectionContext);
      } else {
         this.answerCall();
      }
   }

   protected final void answerCall() {
      this.stopListening();
      boolean success = VoiceServices.answerCall(this._callIdToAnswer);
      if (success) {
         VoiceServices.broadcastEvent(1110, this._callIdToAnswer, this._connectionContext);
      }
   }

   @Override
   protected final void onTimeout() {
      super.onTimeout();
      Out.p("ICC-ontimeout");
      switch (this._answeringOption) {
         case 1:
         case 2:
         case 3:
         default:
            this.answerCall();
         case 0:
      }
   }

   @Override
   protected final void completeConnection() {
      super.completeConnection();
      if (this._answeringOption == 1 || this._answeringOption == 2) {
         this.answerCall();
      }
   }
}
