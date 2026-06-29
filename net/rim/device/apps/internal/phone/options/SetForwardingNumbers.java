package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneLogger;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class SetForwardingNumbers extends SSRequest {
   private int[] _types;
   private String[] _numbers;
   private boolean _disableFirst;
   private int _currentFriendlyType;
   private boolean _disabling;
   private String _currentFwdingNumber;

   SetForwardingNumbers(int[] types, String[] numbers, boolean disableFirst, boolean showStatus) {
      super(showStatus);
      this._types = types;
      this._numbers = numbers;
      this._disableFirst = disableFirst;
      int numUpdates = this._disableFirst ? 1 : 0;
      numUpdates += this._types.length;
      if (numUpdates > 1) {
         this.setNumProgressUpdates(numUpdates);
      }
   }

   @Override
   public final synchronized void runTask() {
      if (this._disableFirst) {
         this._disabling = true;
         this._disabling = true;
         PhoneLogger.log("CFO disable forwarding");
         VoiceServices.deactivateCallForwarding();
         this.waitForNetworkResponse();
         this.updateProgress();
      }

      this._disabling = false;

      for (int i = 0; i < this._types.length; i++) {
         if (super._aborted) {
            return;
         }

         if (this._numbers[i] != null) {
            this._currentFriendlyType = this._types[i];
            this._currentFwdingNumber = this._numbers[i];

            try {
               int nativeType = CallForwardingOption.mapForwardingType(this._currentFriendlyType);
               Phone.getInstance().setCallForwardingNumber(nativeType, this._numbers[i]);
            } finally {
               ;
            }

            this.waitForNetworkResponse();
            this.updateProgress();
         }
      }
   }

   @Override
   protected final String getSSErrorString(int eventId, int reason) {
      String fwdingNumber = this._currentFwdingNumber;
      int fwdingType = this._currentFriendlyType;
      String errorString = null;
      if (eventId == 5001 && reason == 13) {
         errorString = PhoneResources.getString(10);
      } else {
         String typeString = PhoneResources.getString(PhoneOptionsConstants.FWDING_TYPE_STRINGS[fwdingType]);
         if (PhoneUtilities.cdmaWAFActive() && fwdingNumber != null && fwdingNumber.length() == 0) {
            errorString = MessageFormat.format(PhoneResources.getString(6281), new Object[]{typeString});
         } else {
            errorString = MessageFormat.format(PhoneResources.getString(148), new Object[]{fwdingNumber, typeString});
         }
      }

      return errorString;
   }

   @Override
   protected final void onSSError(int eventId, int reason) {
      if (this._disabling) {
         this._disabling = false;
      } else {
         super.onSSError(eventId, reason);
      }
   }
}
