package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.internal.system.Security;
import net.rim.vm.Persistable;

final class PhoneOptions$PersistedPhoneOptions implements Persistable {
   int _booleanOptions;
   String _cachedSIMVoiceMailNumber;
   IntHashtable _voiceMailNumberEncoding;
   IntHashtable _voiceMailTonesEncoding;
   Object[] _forwardingNumbersEncoding;
   int _phonelistViewType;
   int _defaultCallVolume;
   int _defaultEnhanceCallAudio;
   int _previousEnhanceCallAudio;
   Object[] _savedFwdingNumbers;
   int _showCallLogsOption;
   int _HACMode;
   int _ringtoneLight;
   int _TTYMode;
   IntHashtable _lineDescriptions;
   private static final int NUM_FORWARDING_NUMBER_TYPES;
   private static final int DEFAULT_CALL_VOLUME;
   private static final int DEFAULT_ENHANCE_CALL_AUDIO;

   PhoneOptions$PersistedPhoneOptions() {
      this.reset();
   }

   final void reset() {
      this._booleanOptions = 21022;
      this._voiceMailNumberEncoding = (IntHashtable)(new Object());
      this._voiceMailTonesEncoding = (IntHashtable)(new Object());
      this._forwardingNumbersEncoding = new Object[4];
      this._cachedSIMVoiceMailNumber = "";
      this._phonelistViewType = 3;
      this._defaultCallVolume = 0;
      this._defaultEnhanceCallAudio = 0;
      this._previousEnhanceCallAudio = 0;
      this._savedFwdingNumbers = null;
      this.setShowCallLogsOption(this.getDefaultShowCallLogsOption(), true);
      this._HACMode = 0;
      this._ringtoneLight = 0;
      this._TTYMode = 3;
      this._lineDescriptions = (IntHashtable)(new Object());
   }

   private final int getDefaultShowCallLogsOption() {
      switch (RadioInfo.getNetworkType()) {
         case 3:
            return 2;
         case 4:
            return 0;
         case 5:
         default:
            return 3;
      }
   }

   final void setShowCallLogsOption(int newOption, boolean postOptionChangedGlobalEvent) {
      int oldOption = this._showCallLogsOption;
      if (oldOption != newOption) {
         this._showCallLogsOption = newOption;
         if (postOptionChangedGlobalEvent) {
            RIMGlobalMessagePoster.postGlobalEvent(5508103077902582983L, oldOption, newOption);
         }
      }
   }

   private final void reCrypt() {
      boolean encrypt = !Security.getInstance().getAllowOutgoingCallWhileLocked();
      IntEnumeration keys = this._voiceMailNumberEncoding.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         Object obj = PersistentContent.reEncode(this._voiceMailNumberEncoding.get(key), true, encrypt);
         this._voiceMailNumberEncoding.put(key, obj);
      }

      keys = this._voiceMailTonesEncoding.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         Object obj = PersistentContent.reEncode(this._voiceMailTonesEncoding.get(key), true, encrypt);
         this._voiceMailTonesEncoding.put(key, obj);
      }

      for (int i = 0; i < 4; i++) {
         this._forwardingNumbersEncoding[i] = PersistentContent.reEncode(this._forwardingNumbersEncoding[i]);
      }

      if (this._savedFwdingNumbers != null) {
         for (int i = 0; i < this._savedFwdingNumbers.length; i++) {
            this._savedFwdingNumbers[i] = PersistentContent.reEncode(this._savedFwdingNumbers[i]);
         }
      }
   }
}
