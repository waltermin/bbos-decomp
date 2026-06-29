package net.rim.device.apps.internal.phone;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.messaging.MessageLookups;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneCallInitialData;
import net.rim.device.apps.internal.phone.api.PhoneCallModel;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.api.livecall.LiveCall;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.data.PhoneCallModelImpl;
import net.rim.device.apps.internal.phone.data.PhoneFolders;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.options.PhoneOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class CDMACall extends StandardCall {
   private int _ota_flag;
   private CallerIDInfo _displayCallerIDInfo;
   private boolean _receivedCallWaiting;

   public CDMACall(PhoneCallInitialData data, Object context) {
      super(data, context);
      this.setFlag(1024);
      Object callInitiationContext = data._context;
      if (PhoneUtilities.getPrivateFlag(callInitiationContext, 57)) {
         this.setFlag(4096);
         super._redirectedNumber = (String)ContextObject.get(callInitiationContext, 9190530831625408279L);
      }
   }

   private final void setOTAFlag(int flag) {
      throw new RuntimeException("cod2jar: stack: underflow");
   }

   private final int getOTAFlag() {
      return this._ota_flag;
   }

   @Override
   public final boolean equals(Object o) {
      return o == this;
   }

   @Override
   protected final boolean logRequired() {
      return this.getFlag(512) ? false : super.logRequired();
   }

   @Override
   public final void phoneEventNotify(int eventId, int callId, Object context) {
      switch (eventId) {
         case 1005:
            if (this.matchCallId(callId)) {
               this._receivedCallWaiting = true;
               return;
            }
            break;
         case 2200:
            if (this.matchCallId(callId)) {
               String cidNumber = null;
               CallerIDInfo cidProvider = null;
               if (this._displayCallerIDInfo != null) {
                  cidProvider = this._displayCallerIDInfo;
               } else if (super._callerIDInfo != null) {
                  cidProvider = super._callerIDInfo;
               }

               if (cidProvider != null) {
                  Object number = cidProvider.getNumber();
                  if (number instanceof Object) {
                     cidNumber = ((AbstractPhoneNumberModel)number).getValue();
                  }
               }

               if (this.getFlag(1024)) {
                  this.clearFlag(1024);
                  if (cidNumber != null) {
                     if (cidNumber.equals(VoiceServices.getCallPhoneNumber(callId, false))) {
                        String callerId = VoiceServices.getCallName(callId, false);
                        if (callerId != null && callerId.length() > 0) {
                           cidProvider.setFriendlyName(callerId);
                        }
                     } else if (cidNumber.equals(VoiceServices.getCallPhoneNumber(callId, true))) {
                        String callerId = VoiceServices.getCallName(callId, true);
                        if (callerId != null && callerId.length() > 0) {
                           cidProvider.setFriendlyName(callerId);
                        }
                     }
                  }
               }

               if (this.isOutgoing() && !this._receivedCallWaiting) {
                  String redirectedNumber = VoiceServices.getCallPhoneNumber(callId);
                  if (cidNumber != null && !cidNumber.equals(redirectedNumber)) {
                     this.setFlag(4096);
                     super._redirectedNumber = redirectedNumber;
                     return;
                  }
               }
            }
            break;
         case 150070:
            if (this.matchCallId(callId)) {
               PhoneUtilities.setLastNumberDialed("");
               this.setFlag(512);
               if (context instanceof Object) {
                  int status = context;
                  System.out.println(((StringBuffer)(new Object("EV_OTA_STATUS_CHANGE("))).append(callId).append(", ").append(status).append(')').toString());
                  this.setOTAFlag(status);
                  return;
               }
            }
            break;
         default:
            super.phoneEventNotify(eventId, callId, context);
      }
   }

   @Override
   protected final void onFlashByUser(Object context) {
      super.onFlashByUser(context);
      String additionalNumbers = (String)ContextObject.get(context, 7528018505720453076L);
      if (additionalNumbers != null) {
         new AfterDialToneHandler(this, additionalNumbers, UiApplication.getUiApplication()).start(false);
      }

      LiveCall call = (LiveCall)VoiceServices.getVoiceApplication().getCurrentCall();
      if (call.isMuted()) {
         call.mute();
      }

      if (context instanceof Object) {
         Object info = ContextObject.get(context, 5898398779440734986L);
         if (info instanceof Object) {
            CallerIDInfo callerIDInfo = (CallerIDInfo)info;
            this._displayCallerIDInfo = callerIDInfo;
            this.clearFlag(4096);
            this.clearUserDialedPhoneNumber();
            if (callerIDInfo.isIncomingCall()) {
               boolean hasConnected = this.getFlag(2);
               if (!hasConnected && PhoneOptions.getOptions().getBooleanOption(8)) {
                  PhoneCallInitialData data = (PhoneCallInitialData)(new Object(0, (byte)0, 8, callerIDInfo, null));
                  PhoneCallModel phoneCall = (PhoneCallModel)PhoneUtilities.createPhoneCallModel(data);
                  if (phoneCall != null) {
                     MessageLookups.put(-7579072715623987642L, phoneCall.getRefId(), phoneCall);
                     PhoneFolders.addItem(phoneCall);
                     return;
                  }
               }
            } else {
               PhoneCallModel phoneCall = PhoneFolders.getWritableCallLog((PhoneCallModelImpl)this.getPhoneCall());
               phoneCall.addCallerIDInfo(callerIDInfo);
               phoneCall.setType((byte)4);
            }
         }
      }
   }

   @Override
   public final CallerIDInfo getDisplayCallerIDInfo() {
      return this._displayCallerIDInfo != null ? this._displayCallerIDInfo : super._callerIDInfo;
   }

   private final String getOTAStatusString() {
      String msg = null;
      switch (this.getOTAFlag()) {
         case 0:
         default:
            return PhoneResources.getString(184);
         case 1:
            msg = PhoneResources.getString(6272);
         case -1:
         case 2:
         case 3:
         case 4:
            return msg;
         case 5:
            return PhoneResources.getString(6262);
         case 6:
            return PhoneResources.getString(6263);
         case 7:
            return PhoneResources.getString(6264);
         case 8:
            return PhoneResources.getString(6265);
         case 9:
            return PhoneResources.getString(6266);
         case 10:
            return PhoneResources.getString(6267);
         case 11:
            return PhoneResources.getString(6268);
         case 12:
            return PhoneResources.getString(6269);
         case 13:
            return PhoneResources.getString(6270);
         case 14:
            return PhoneResources.getString(6271);
         case 15:
            return PhoneResources.getString(6273);
      }
   }

   @Override
   public final String getStatusString() {
      if (this.getFlag(512)) {
         return !this.getFlag(2) ? PhoneResources.getString(3) : this.getOTAStatusString();
      } else {
         return super.getStatusString();
      }
   }
}
