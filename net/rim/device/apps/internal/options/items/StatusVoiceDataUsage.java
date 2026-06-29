package net.rim.device.apps.internal.options.items;

import net.rim.device.apps.internal.options.resources.OptionsResources;
import net.rim.device.internal.system.VoiceDataUsage;

final class StatusVoiceDataUsage extends StatusListItem {
   private boolean _isData;

   StatusVoiceDataUsage(boolean isData) {
      super(isData ? 1938 : 1939);
      this._isData = isData;
   }

   @Override
   public final String getDisplayValue() {
      return this._isData ? this.dataString() : this.voiceString();
   }

   private final String dataString() {
      if (VoiceDataUsage.itPolicyEnabled()) {
         return this.itPolicyEnabledString();
      }

      if (VoiceDataUsage.exceededDataLimit()) {
         return this.exceededString();
      }

      int bytes = VoiceDataUsage.getDataBytes();
      int deciK = bytes * 10 / 1024;
      return "" + deciK / 10 + "." + deciK % 10 + "K";
   }

   private final String voiceString() {
      if (VoiceDataUsage.itPolicyEnabled()) {
         return this.itPolicyEnabledString();
      }

      if (VoiceDataUsage.exceededVoiceLimit()) {
         return this.exceededString();
      }

      int seconds = VoiceDataUsage.getVoiceSeconds();
      int deciMins = seconds * 10 / 60;
      String minutesString = OptionsResources.getString(1942);
      return "" + deciMins / 10 + "." + deciMins % 10 + " " + minutesString;
   }

   private final String itPolicyEnabledString() {
      return OptionsResources.getString(1947);
   }

   private final String exceededString() {
      String exceededString = OptionsResources.getString(1941);
      String limitString;
      if (this._isData) {
         limitString = "250K";
      } else {
         String minutesString = OptionsResources.getString(1942);
         limitString = "60 " + minutesString;
      }

      return exceededString + " " + limitString;
   }
}
