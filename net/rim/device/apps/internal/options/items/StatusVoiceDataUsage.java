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
      return ((StringBuffer)(new Object(""))).append(deciK / 10).append(".").append(deciK % 10).append("K").toString();
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
      return ((StringBuffer)(new Object(""))).append(deciMins / 10).append(".").append(deciMins % 10).append(" ").append(minutesString).toString();
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
         limitString = ((StringBuffer)(new Object("60 "))).append(minutesString).toString();
      }

      return ((StringBuffer)(new Object())).append(exceededString).append(" ").append(limitString).toString();
   }
}
