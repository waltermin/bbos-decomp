package net.rim.device.internal.callcontrol;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.system.SIMCardException;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.internal.system.PhoneFirmwareImpl;
import net.rim.device.internal.system.RadioInternal;

final class RadioCommandHandler extends AbstractCallCommandHandler {
   private RadioEventHandler _eventHandler;
   private static final int WORLD_PHONE_WAFS = 3;
   private static final boolean CDMA_GSM_WORLD_PHONE = (RadioInfo.getSupportedWAFs() & 3) == 3;
   static final int CELL_WAFS = !CDMA_GSM_WORLD_PHONE ? RadioInfo.getSupportedWAFs() & -5 : RadioInfo.getEnabledWAFs();

   public RadioCommandHandler() {
      super(10);
      AbstractCallCommandHandler.internalRegister(this);
      this._eventHandler = new RadioEventHandler();
   }

   public final void startListening(Application app) {
      this._eventHandler.startListening(app);
   }

   @Override
   public final boolean canInvokeCallTransferAction(int callId, int action) {
      return false;
   }

   @Override
   public final boolean canHold(int callId) {
      int networkFeatures = Phone.getInstance().getNetworkFeatures();
      return (networkFeatures & 4) != 0;
   }

   @Override
   public final boolean canSwap(int callId) {
      return this.canHold(callId);
   }

   @Override
   public final boolean canJoin(int callId) {
      return true;
   }

   @Override
   public final boolean canPark(int callId) {
      return false;
   }

   @Override
   public final boolean canSendToVoicemail(int callId) {
      return false;
   }

   @Override
   public final String getAlternateLineLabel(int line) {
      return null;
   }

   @Override
   public final boolean isAlternateLineAvailable(int line) {
      int callId = this.getActiveCallId();
      return callId == 0 ? true : line == this.getAlternateLine(callId);
   }

   @Override
   public final void setAlternateLineLabel(int line, String description) {
   }

   @Override
   public final String getAlternateLineNumber(int line) {
      switch (line) {
         case 0:
            return null;
         case 1:
         default:
            return this.getNumber(0);
         case 2:
            return this.getNumber(1);
      }
   }

   @Override
   public final int[] getAlternateLines() {
      int features = this.getNetworkFeatures();
      return (features & 2097152) != 0 ? new int[]{1, 2, -805044223, 3, -805044192, -1728051673, 561916211, 1903397046} : new int[]{1, -804651006, 1, 2};
   }

   @Override
   public final int getCallTransferState(int callId) {
      return 1;
   }

   @Override
   public final String getVoiceMailNumber(int line) {
      if (RadioInfo.getNetworkType() == 4) {
         try {
            int stringID = line == 2 ? 7 : 1;
            String number = RadioInternal.readNVString(stringID);
            if (number != null && number.length() > 0) {
               return number;
            }
         } catch (RadioException var7) {
         }

         try {
            String number = this.getAlternateLineNumber(line);
            if (number != null && number.length() > 0) {
               return number;
            }
         } catch (RadioException var6) {
         }
      }

      if (SIMCard.isSupported()) {
         try {
            String number = null;
            switch (line) {
               case 0:
                  break;
               case 1:
               default:
                  number = SIMCard.getVoiceMailNumber(0);
                  break;
               case 2:
                  number = SIMCard.getVoiceMailNumber(1);
            }

            if (number != null && number.length() > 0) {
               return number;
            }
         } catch (SIMCardException var4) {
            return null;
         } catch (UnsupportedOperationException var5) {
         }
      }

      return null;
   }

   @Override
   public final int getVoiceMailCount(int line) {
      return 0;
   }

   @Override
   public final int getWAFs(int line) {
      return CELL_WAFS;
   }

   @Override
   public final boolean invokeCallTransferAction(int callId, int action, Object param) {
      return false;
   }

   @Override
   public final void parkCall(int callId) {
      this._eventHandler.callManipulateFailed(callId, 0);
   }

   @Override
   public final void sendToVoicemail(int callId) {
      this._eventHandler.callManipulateFailed(callId, 0);
   }

   @Override
   public final boolean supportsCorporateExtensions(int callId) {
      return false;
   }

   @Override
   public final void activateCallBarring(boolean activate, int type, String password) {
      PhoneFirmwareImpl.activateCallBarring(activate, type, password);
   }

   @Override
   public final void activateCallWaiting(boolean activate) {
      PhoneFirmwareImpl.activateCallWaiting(activate);
   }

   @Override
   public final void addCallToConference() {
      PhoneFirmwareImpl.addCallToConference();
   }

   @Override
   public final void answerCall(int callId) {
      PhoneFirmwareImpl.answerCall(callId);
   }

   @Override
   public final void deactivateCallForwarding() {
      PhoneFirmwareImpl.deactivateCallForwarding();
   }

   @Override
   public final void disableDTMFEcho(boolean disable) {
      PhoneFirmwareImpl.disableDTMFEcho(disable);
   }

   @Override
   public final boolean endEmergencyCallbackMode() {
      return PhoneFirmwareImpl.endEmergencyCallbackMode();
   }

   @Override
   public final void flash(String number) {
      PhoneFirmwareImpl.flash(number);
   }

   @Override
   public final int getActiveCallId() {
      return PhoneFirmwareImpl.getActiveCallId();
   }

   @Override
   public final int getAlternateLine(int callID) {
      return (2097152 & this.getNetworkFeatures()) != 0 ? PhoneFirmwareImpl.getAlternateLine(callID) : 1;
   }

   @Override
   public final int getCallDuration(int callId) {
      return PhoneFirmwareImpl.getCallDuration(callId);
   }

   @Override
   public final String getCallForwardingNumber(int type) {
      return PhoneFirmwareImpl.getCallForwardingNumber(type);
   }

   @Override
   public final String getCallName(int callId, boolean original) {
      return PhoneFirmwareImpl.getCallName(callId, original);
   }

   @Override
   public final String getCallPhoneNumber(int callId, boolean original) {
      return PhoneFirmwareImpl.getCallPhoneNumber(callId, original);
   }

   @Override
   public final int getCallState(int callId) {
      return PhoneFirmwareImpl.getCallState(callId);
   }

   @Override
   public final int getCLIPDisplayMode(int callId) {
      return PhoneFirmwareImpl.getCLIPDisplayMode(callId);
   }

   @Override
   public final String getEmergencyNumber() {
      return PhoneFirmwareImpl.getEmergencyNumber();
   }

   @Override
   public final String getForwardingNumber() {
      return PhoneFirmwareImpl.getForwardingNumber();
   }

   @Override
   public final String getForwardingNumberForService(int ssOption, int bearerService) {
      return PhoneFirmwareImpl.getForwardingNumberForService(ssOption, bearerService);
   }

   @Override
   public final int getHeldCallId() {
      return PhoneFirmwareImpl.getHeldCallId();
   }

   @Override
   public final int getIncomingCallId() {
      return PhoneFirmwareImpl.getIncomingCallId();
   }

   @Override
   public final int getMaxConferenceMembers() {
      return PhoneFirmwareImpl.getMaxConferenceMembers();
   }

   @Override
   public final int getNetworkFeatures() {
      return PhoneFirmwareImpl.getNetworkFeatures();
   }

   @Override
   public final String getNumber(int index) {
      return PhoneFirmwareImpl.getNumber(index);
   }

   @Override
   public final void holdCall() {
      PhoneFirmwareImpl.holdCall();
   }

   @Override
   public final boolean inCallDTMFDigitsEntered(String digits) {
      return PhoneFirmwareImpl.inCallDTMFDigitsEntered(digits);
   }

   @Override
   public final boolean isActive() {
      try {
         if (this.getActiveCallId() != 0 || this.getHeldCallId() != 0 || this.getIncomingCallId() != 0) {
            return true;
         }

         if (DirectConnect.isSupported() && DirectConnect.getActiveCallType() != 0) {
            return true;
         }
      } catch (RadioException var2) {
      }

      return false;
   }

   @Override
   public final boolean isCallForwardUnconditionalActive(int line) {
      return PhoneFirmwareImpl.isCallForwardUnconditionalActive(line);
   }

   @Override
   public final boolean isCallRedirected(int callId) {
      return PhoneFirmwareImpl.isCallRedirected(callId);
   }

   @Override
   public final boolean isEmergencyNumber(String number) {
      return PhoneFirmwareImpl.isEmergencyNumber(number);
   }

   @Override
   public final boolean isFDNAvailable() {
      return PhoneFirmwareImpl.isFDNAvailable();
   }

   @Override
   public final boolean isFDNEnabled() {
      return PhoneFirmwareImpl.isFDNEnabled();
   }

   @Override
   public final void querySSOption(int ssOption) {
      PhoneFirmwareImpl.querySSOption(ssOption);
   }

   @Override
   public final int querySSOptionResult(int ssOption, int bearerService) {
      return PhoneFirmwareImpl.querySSOptionResult(ssOption, bearerService);
   }

   @Override
   public final void removeCallFromConference(int callId) {
      PhoneFirmwareImpl.removeCallFromConference(callId);
   }

   @Override
   public final void rejectCall(int callId) {
      PhoneFirmwareImpl.rejectCall(callId);
   }

   @Override
   public final void requestEnableFDN(boolean enable) {
      PhoneFirmwareImpl.requestEnableFDN(enable);
   }

   @Override
   public final void resumeCall() {
      PhoneFirmwareImpl.resumeCall();
   }

   @Override
   public final void sendSSPasswordResponse(String password) {
      PhoneFirmwareImpl.sendSSPasswordResponse(password);
   }

   @Override
   public final boolean setAlternateLine(int line) {
      if (RadioInfo.getNetworkType() == 3) {
         PhoneFirmwareImpl.setSSBasicService(2);
      }

      return PhoneFirmwareImpl.setAlternateLine(line);
   }

   @Override
   public final void setCallBarringPassword(String oldPassword, String newPassword) {
      PhoneFirmwareImpl.setCallBarringPassword(oldPassword, newPassword);
   }

   @Override
   public final void setCallForwardingNumber(int type, String number) {
      PhoneFirmwareImpl.setCallForwardingNumber(type, number);
   }

   @Override
   public final void setSSBasicService(int bearerService) {
      PhoneFirmwareImpl.setSSBasicService(bearerService);
   }

   @Override
   public final void setUSSDResponse(byte[] data) {
      PhoneFirmwareImpl.setUSSDResponse(data);
   }

   @Override
   public final int startCall(String number, int clir) {
      return PhoneFirmwareImpl.startCall(number, clir);
   }

   @Override
   public final void startDTMF(int callId, byte character) {
      PhoneFirmwareImpl.startDTMF(callId, character);
   }

   @Override
   public final void stopAllCalls(boolean unconditional) {
      PhoneFirmwareImpl.stopAllCalls(unconditional);
   }

   @Override
   public final void stopCall(int callId) {
      PhoneFirmwareImpl.stopCall(callId);
   }

   @Override
   public final void stopDTMF(int callId) {
      PhoneFirmwareImpl.stopDTMF(callId);
   }

   @Override
   public final void swapCalls() {
      PhoneFirmwareImpl.swapCalls();
   }

   @Override
   public final void transferCall() {
      PhoneFirmwareImpl.transferCall();
   }
}
