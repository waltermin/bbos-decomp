package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.gps.GPS;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.OTASyncCapableSyncItem;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.IntEnumeration;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.internal.proxy.Proxy;
import net.rim.device.internal.system.AudioInternal;
import net.rim.device.internal.system.AudioTTYListener;
import net.rim.device.internal.system.TTY;

final class PhoneOptions$PhoneOptionsSyncItem extends OTASyncCapableSyncItem implements ForwardingTypes, AudioTTYListener {
   private static final int DB_VERSION;
   private static final int SMART_DIAL_COUNTRY_CODE_TAG;
   private static final int SMART_DIAL_AREA_CODE_TAG;
   private static final int SMART_DIAL_PHONE_NUMBER_TAG;
   private static final int SMART_DIAL_LOCAL_PHONE_NUMBER_LENGTH_TAG;
   private static final int VOICE_MAIL_TAG;
   private static final int BOOLEAN_OPTIONS_TAG;
   private static final int FORWARD_ALL_CALLS_TAG;
   private static final int FORWARD_BUSY_TAG;
   private static final int FORWARD_NO_REPLY_TAG;
   private static final int FORWARD_NOT_REACHABLE_TAG;
   private static final int SMART_DIAL_NATIONAL_PHONE_NUMBER_LENGTH_TAG;
   private static final int VOICE_MAIL_NUMBER_TAG;
   private static final int VOICE_MAIL_ADDITIONAL_TONES_TAG;
   private static final int CORPORATE_EXTENSIONS_ADDITIONAL_TONES_TAG;
   private static final int CORPORATE_EXTENSION_LENGTH_TAG;
   private static final int CALL_TIMERS_TAG;
   private static final int TTY_TAG;
   private static final int PHONE_LIST_VIEW_TYPE_TAG;
   private static final int DIRECT_CONNECT_OPTIONS_TAG;
   private static final int CORPORATE_EXTENSION_LENGTH_EXCLUSIONS_TAG;
   private static final int EXTENSIONS_ADDITIONAL_TONES_TAG;
   private static final int GPS_TAG;
   private static final int AVC_TAG;
   private static final int DIRECT_CONNECT_LOUD_AUDIO;
   private static final int DEFAULT_CALL_VOLUME_TAG;
   private static final int SAVED_FORWARDING_NUMBER_TAG;
   private static final int SHOW_CALL_LOGS_TAG;
   private static final int HAC_TAG;
   private static final int CORPORATE_EXTENSION_NETWORK_TAG;
   private static final int RINGTONE_LIGHT_TAG;
   private static final int VOICE_MAIL_LINE_TAG;
   private static final int DEFAULT_ENHANCED_CALL_AUDIO_TAG;
   private static final int PREVIOUS_ENHANCED_CALL_AUDIO_TAG;
   private static final int LINE_DESCRIPTION_LINE;
   private static final int LINE_DESCRIPTION_STRING;

   PhoneOptions$PhoneOptionsSyncItem() {
      Audio.addListener(Proxy.getInstance(), this);
   }

   @Override
   public final String getSyncName() {
      return "Phone Options";
   }

   @Override
   public final String getSyncName(Locale locale) {
      return null;
   }

   @Override
   public final int getSyncVersion() {
      return 0;
   }

   @Override
   public final boolean removeAllSyncObjects() {
      PhoneOptions.getOptions().resetOptions();
      DirectConnectOptions.getOptions().resetOptions();
      SmartDialingOptions.getOptions().resetOptions();
      RIMGlobalMessagePoster.postGlobalEvent(-3666745774872801074L);
      return true;
   }

   @Override
   public final boolean setSyncData(DataBuffer buffer, int version) {
      String smartAreaCode = null;
      int smartNationalPhoneNumberLength = -1;
      int smartLocalPhoneNumberLength = -1;
      PhoneOptions phoneOptions = PhoneOptions.getOptions();
      DirectConnectOptions directConnectOptions = DirectConnectOptions.getOptions();
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      int networkType = RadioInfo.getNetworkType();
      StringBuffer sb = (StringBuffer)(new Object());
      int lineId = 1;

      label147:
      try {
         while (buffer.available() > 0) {
            switch (ConverterUtilities.getType(buffer, true)) {
               case 0:
               case 12:
                  ConverterUtilities.skipField(buffer);
                  break;
               case 1:
               default:
                  smartDialingOptions.setCountryCode(this.readDigits(buffer, sb));
                  break;
               case 2:
                  smartAreaCode = this.readDigits(buffer, sb);
                  smartDialingOptions.setAreaCode(smartAreaCode);
                  break;
               case 3:
                  smartDialingOptions.setCorporatePhoneNumber(ConverterUtilities.readString(buffer));
                  break;
               case 4:
                  smartLocalPhoneNumberLength = ConverterUtilities.readInt(buffer);
                  break;
               case 5:
                  phoneOptions.setVoiceMailNumber(ConverterUtilities.readString(buffer), false, lineId);
                  break;
               case 6:
                  int var25 = ConverterUtilities.readInt(buffer);
                  if (var25 >= 0) {
                     var25 |= 4126;
                     if (PhoneUtilities.isQwertyReducedKeyboard()) {
                        var25 |= 16384;
                     }

                     phoneOptions.setBooleanOptions(var25);
                     boolean enabled = (var25 & 128) == 0;
                     smartDialingOptions.setAutoAppendNDDForDialing(enabled);
                     enabled = (var25 & 64) == 0;
                     smartDialingOptions.enableSmartDialing(enabled);
                  }
                  break;
               case 7:
                  phoneOptions.setForwardingNumber(0, ConverterUtilities.readString(buffer));
                  break;
               case 8:
                  phoneOptions.setForwardingNumber(1, ConverterUtilities.readString(buffer));
                  break;
               case 9:
                  phoneOptions.setForwardingNumber(2, ConverterUtilities.readString(buffer));
                  break;
               case 10:
                  phoneOptions.setForwardingNumber(3, ConverterUtilities.readString(buffer));
                  break;
               case 11:
                  smartNationalPhoneNumberLength = ConverterUtilities.readInt(buffer);
                  break;
               case 13:
                  phoneOptions.setVoiceMailAdditionalTones(ConverterUtilities.readString(buffer), lineId);
                  break;
               case 14:
                  smartDialingOptions.setAdditionalTonesForCorporateExtensions(networkType, ConverterUtilities.readString(buffer));
                  break;
               case 15:
                  int intDatax = ConverterUtilities.readInt(buffer);
                  if (intDatax >= 0) {
                     smartDialingOptions.setCorporateExtensionLength(intDatax);
                  }
                  break;
               case 16:
                  CallTimers.getCallTimers().setTimers(ConverterUtilities.readIntArray(buffer));
                  break;
               case 17:
                  int var23 = ConverterUtilities.readInt(buffer);
                  if (TTYOption.hasDisplayableFields()) {
                     TTY.requestModeChange(var23);
                  }
                  break;
               case 18:
                  int intData = ConverterUtilities.readInt(buffer);
                  if (intData >= 0) {
                     phoneOptions.setPhoneListViewType(intData);
                  }
                  break;
               case 19:
                  directConnectOptions.setAllOptions(ConverterUtilities.readIntArray(buffer));
                  break;
               case 20:
                  smartDialingOptions.setCorporateExtensionLengthExclusions(ConverterUtilities.readIntArray(buffer));
                  break;
               case 21:
                  smartDialingOptions.setAdditionalTonesForExtensions(networkType, ConverterUtilities.readString(buffer));
                  break;
               case 22:
                  int gpsMode = ConverterUtilities.readInt(buffer);
                  if (GPS.isSupported()) {
                     GPS.requestModeChange(gpsMode);
                  }
                  break;
               case 23:
                  AudioInternal.requestAVCModeChange(ConverterUtilities.readInt(buffer));
                  break;
               case 24:
                  ConverterUtilities.readInt(buffer);
                  break;
               case 25:
                  int defaultCallVolume = ConverterUtilities.readInt(buffer);
                  phoneOptions.setDefaultCallVolume(defaultCallVolume);
                  break;
               case 26:
                  String fwdingNumber = ConverterUtilities.readString(buffer);
                  boolean lastFwdingNumber = !ConverterUtilities.isType(buffer, 26);
                  phoneOptions.addSavedForwardingNumber(fwdingNumber, true, lastFwdingNumber);
                  break;
               case 27:
                  int showCallLogsOpt = ConverterUtilities.readInt(buffer);
                  phoneOptions.setShowCallLogsOption(showCallLogsOpt);
                  break;
               case 28:
                  int hac_tag = ConverterUtilities.readInt(buffer);
                  if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
                     AudioInternal.requestHACModeChange(hac_tag == 1);
                     HACRibbonIndicator.getInstance().updateIndicator();
                  }
                  break;
               case 29:
                  networkType = ConverterUtilities.readInt(buffer);
                  break;
               case 30:
                  phoneOptions.setRingtoneLightStyle(ConverterUtilities.readInt(buffer));
                  break;
               case 31:
               case 34:
                  lineId = ConverterUtilities.readInt(buffer);
                  break;
               case 32:
                  int defaultECA = ConverterUtilities.readInt(buffer);
                  phoneOptions.setDefaultEnhanceCallAudio(defaultECA);
                  break;
               case 33:
                  int prevECA = ConverterUtilities.readInt(buffer);
                  phoneOptions.setPreviousEnhanceCallAudio(prevECA);
                  break;
               case 35:
                  phoneOptions.setLineDescription(lineId, ConverterUtilities.readString(buffer));
                  lineId = 1;
            }
         }

         if (smartNationalPhoneNumberLength < 0 && smartLocalPhoneNumberLength > 0 && smartAreaCode != null && smartAreaCode.length() > 0) {
            smartNationalPhoneNumberLength = smartLocalPhoneNumberLength + smartAreaCode.length();
         }

         if (smartNationalPhoneNumberLength >= 0) {
            smartDialingOptions.setNationalPhoneNumberLength(smartNationalPhoneNumberLength);
         }
      } finally {
         break label147;
      }

      smartDialingOptions.commit();
      directConnectOptions.commit();
      phoneOptions.commit();
      RIMGlobalMessagePoster.postGlobalEvent(-2282475915901395486L);
      return true;
   }

   @Override
   public final boolean getSyncData(DataBuffer buffer, int version) {
      PhoneOptions phoneOptions = PhoneOptions.getOptions();
      DirectConnectOptions directConnectOptions = DirectConnectOptions.getOptions();
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      this.writeString(buffer, 1, smartDialingOptions.getCountryCodeString());
      this.writeString(buffer, 2, smartDialingOptions.getAreaCode());
      this.writeString(buffer, 3, smartDialingOptions.getCorporatePhoneNumber());
      ConverterUtilities.convertInt(buffer, 15, smartDialingOptions.getCorporateExtensionLength(), 4);
      ConverterUtilities.convertInt(buffer, 11, smartDialingOptions.getNationalPhoneNumberLength(), 4);
      int[] networks = smartDialingOptions.getAdditionalTonesNetworks();

      for (int idx = networks.length - 1; idx >= 0; idx--) {
         int network = networks[idx];
         ConverterUtilities.convertInt(buffer, 29, network, 4);
         this.writeString(buffer, 14, smartDialingOptions.getAdditionalTonesForCorporateExtensions(network));
         this.writeString(buffer, 21, smartDialingOptions.getAdditionalTonesForExtensions(network));
      }

      int[] extLengthExclusions = smartDialingOptions.getCorporateExtensionLengthExclusions();
      if (extLengthExclusions != null && extLengthExclusions.length > 0) {
         ConverterUtilities.writeIntArray(buffer, 20, extLengthExclusions);
      }

      int[] allDirectConnectOptions = directConnectOptions.getAllOptions();
      if (allDirectConnectOptions != null && allDirectConnectOptions.length > 0) {
         ConverterUtilities.writeIntArray(buffer, 19, allDirectConnectOptions);
      }

      int booleanOptions = phoneOptions.getBooleanOptions();
      if (smartDialingOptions.autoAppendNDDForDialing()) {
         booleanOptions &= -129;
      } else {
         booleanOptions |= 128;
      }

      if (smartDialingOptions.isSmartDialingEnabled()) {
         booleanOptions &= -65;
      } else {
         booleanOptions |= 64;
      }

      ConverterUtilities.convertInt(buffer, 6, booleanOptions, 4);
      ConverterUtilities.convertInt(buffer, 18, phoneOptions.getPhoneListViewType(), 4);
      IntHashtable voiceMailNumbers = phoneOptions.getVoiceMailNumbers();
      IntEnumeration keys = voiceMailNumbers.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         ConverterUtilities.writeInt(buffer, 31, key);
         this.writeString(buffer, 5, PersistentContent.decodeString(voiceMailNumbers.get(key)));
      }

      IntHashtable voiceMailTones = phoneOptions.getVoiceMailAllAdditionalTones();
      keys = voiceMailTones.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         ConverterUtilities.writeInt(buffer, 31, key);
         this.writeString(buffer, 13, PersistentContent.decodeString(voiceMailTones.get(key)));
      }

      this.writeString(buffer, 7, phoneOptions.getForwardingNumber(0));
      this.writeString(buffer, 8, phoneOptions.getForwardingNumber(1));
      this.writeString(buffer, 9, phoneOptions.getForwardingNumber(2));
      this.writeString(buffer, 10, phoneOptions.getForwardingNumber(3));
      int[] callTimers = CallTimers.getCallTimers().getTimers();
      if (callTimers != null && callTimers.length > 0) {
         ConverterUtilities.writeIntArray(buffer, 16, callTimers);
      }

      ConverterUtilities.convertInt(buffer, 17, TTY.getMode(), 4);
      if ((Phone.getInstance().getNetworkFeatures() & 524288) != 0) {
         ConverterUtilities.convertInt(buffer, 28, AudioInternal.getHACMode() ? 1 : 0, 4);
      }

      if (GPS.isSupported()) {
         ConverterUtilities.convertInt(buffer, 22, GPS.getMode(), 4);
      }

      int avcMode = AudioInternal.getAVCMode();
      if (avcMode != 0) {
         ConverterUtilities.convertInt(buffer, 23, avcMode, 4);
      }

      ConverterUtilities.convertInt(buffer, 25, phoneOptions.getDefaultCallVolume(), 4);
      ConverterUtilities.convertInt(buffer, 32, phoneOptions.getDefaultEnhanceCallAudio(), 4);
      ConverterUtilities.convertInt(buffer, 33, phoneOptions.getPreviousEnhanceCallAudio(), 4);
      IntHashtable lineDescriptions = phoneOptions.getLineDescriptions();
      keys = lineDescriptions.keys();

      while (keys.hasMoreElements()) {
         int key = keys.nextElement();
         ConverterUtilities.writeInt(buffer, 34, key);
         this.writeString(buffer, 35, (String)lineDescriptions.get(key));
      }

      String[] savedForwardingNumbers = phoneOptions.getSavedForwardingNumbers();

      for (int i = 0; i < savedForwardingNumbers.length; i++) {
         this.writeString(buffer, 26, savedForwardingNumbers[i]);
      }

      ConverterUtilities.writeInt(buffer, 27, phoneOptions.getShowCallLogsOption());
      ConverterUtilities.writeInt(buffer, 30, phoneOptions.getRingtoneLightStyle());
      return true;
   }

   private final void writeString(DataBuffer buffer, int tag, String string) {
      if (string != null && string.length() > 0) {
         ConverterUtilities.writeStringSmart(buffer, tag, string);
      }
   }

   private final String readDigits(DataBuffer buffer, StringBuffer sb) {
      String string;
      try {
         string = ConverterUtilities.readString(buffer);
      } finally {
         ;
      }

      int len = string.length();
      sb.setLength(0);

      for (int i = 0; i < len; i++) {
         char c = string.charAt(i);
         if (Character.isDigit(c)) {
            sb.append(c);
         }
      }

      return sb.toString();
   }

   @Override
   public final void responseTTYModeChange(boolean success, int mode) {
      this.fireSyncItemUpdated();
   }

   @Override
   public final void responseHACModeChange(boolean success, boolean on) {
   }

   @Override
   public final void ttyStatusUpdate(boolean used) {
   }

   @Override
   public final void ttyDataAvailable() {
   }

   @Override
   public final void ttyDataReady() {
   }

   @Override
   public final void ttyReadBufferFull() {
   }
}
