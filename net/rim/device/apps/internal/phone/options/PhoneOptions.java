package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.synchronization.SyncItem;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.system.PersistentContentListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.apps.api.options.OptionsBase;
import net.rim.device.apps.api.phone.SIMPhoneNumberWriter;
import net.rim.device.apps.api.ribbon.RibbonLauncher;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.system.SIMCardEfHandler;
import net.rim.device.internal.system.Security;
import net.rim.vm.Array;

public final class PhoneOptions extends OptionsBase implements PersistentContentListener {
   private PhoneOptions$PersistedPhoneOptions _persistedPhoneOptions;
   private PersistentObject _errorLogPersistentObject;
   private String _errorLog;
   private static final long PERSISTED_PHONE_OPTIONS;
   private static final long PERSISTED_PHONE_ERROR_LOG;
   public static final long NEW_OPTIONS_SYNCED;
   public static final long OPTIONS_RESET;
   public static final long SHOW_CALL_LOGS_OPTION_CHANGED;
   public static final long PHONE_LIST_VIEW_TYPE_CHANGED;
   private static final int MAX_PHONE_LOG_LENGTH;
   public static final long VOICEMAIL_NUMBER_UPDATED;
   public static final int SHOW_CALL_LOGS_IN_MESSAGE_LIST;
   public static final int LOG_ATTEMPTED_CALLS;
   public static final int LOG_MISSED_CALLS;
   public static final int LOG_INCOMING_CALLS;
   public static final int LOG_OUTGOING_CALLS;
   public static final int BLOCK_MY_IDENTITY;
   public static final int DISABLE_SMART_DIALING;
   public static final int DO_NOT_AUTO_APPEND_NDD;
   public static final int AUTO_CALL_ANSWER;
   public static final int AUTO_CALL_HANGUP;
   public static final int NOTIFY_ON_PRIVACY_STATE_CHANGE;
   public static final int NOTIFY_ON_ROAMING_STATE_CHANGE;
   public static final int LOG_DIRECT_CONNECT_CALLS;
   public static final int DONT_CONFIRM_DELETE;
   public static final int DIAL_FROM_RIBBON;
   public static final int ENABLE_SHORT_DIALING;
   public static final int HIDE_MY_NUMBER;
   public static final int UNBLOCK_MY_IDENTITY;
   public static final int SHOW_MISSED_CALL_LOGS;
   public static final int SHOW_ALL_CALL_LOGS;
   public static final int SHOW_NO_CALL_LOGS;
   public static final int SHOW_ALL_CALL_LOGS_NO_DIRECT_CONNECT;
   public static final int HOTLIST_VIEW_SORT_TIME;
   public static final int HOTLIST_VIEW_SORT_FREQUENCY;
   public static final int HOTLIST_VIEW_SORT_NAME;
   public static final int CALL_LOG_VIEW;
   public static final int RINGTONE_LIGHT_OFF;
   public static final int RINGTONE_LIGHT_TRACKBALL;
   public static final int RINGTONE_LIGHT_STATUS;
   public static final int RINGTONE_LIGHT_BOTH;
   private static PhoneOptions _options;
   private static final String[] EMPTY_FORWARDING_NUMBERS_STRING = new Object[0];

   private PhoneOptions() {
      PersistentContent.addListener(this);
      this._errorLogPersistentObject = RIMPersistentStore.getPersistentObject(1955576900267989249L);
      synchronized (this._errorLogPersistentObject) {
         this._errorLog = (String)this._errorLogPersistentObject.getContents();
         if (this._errorLog == null) {
            this._errorLog = "";
            this._errorLogPersistentObject.setContents(this._errorLog, 51);
            this._errorLogPersistentObject.commit();
         }
      }
   }

   public static final PhoneOptions getOptions() {
      if (_options == null) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         _options = (PhoneOptions)ar.getOrWaitFor(1483928926021854353L);
         if (_options == null) {
            _options = new PhoneOptions();
            ar.put(1483928926021854353L, _options);
         }
      }

      return _options;
   }

   final void resetOptions() {
      this._persistedPhoneOptions.reset();
      this.commit();
      Phone.getInstance().setCLIR(this.getCLIR());
      this._errorLog = "";
      this._errorLogPersistentObject.setContents(this._errorLog, 51);
      this._errorLogPersistentObject.commit();
   }

   @Override
   public final void enableSynchronization() {
      try {
         super.enableSynchronization();
      } finally {
         return;
      }
   }

   @Override
   protected final PersistentObject getPersistentObject() {
      PersistentObject persistentObject = RIMPersistentStore.getPersistentObject(1483928926021854353L);
      synchronized (persistentObject) {
         this._persistedPhoneOptions = (PhoneOptions$PersistedPhoneOptions)persistentObject.getContents();
         if (this._persistedPhoneOptions == null) {
            this._persistedPhoneOptions = new PhoneOptions$PersistedPhoneOptions();
            persistentObject.setContents(this._persistedPhoneOptions, 51, false);
            persistentObject.commit();
         }

         return persistentObject;
      }
   }

   @Override
   protected final SyncItem getSyncItem() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      synchronized (ar) {
         SyncItem syncItem = (SyncItem)ar.get(9065083732853317491L);
         if (syncItem == null) {
            syncItem = new PhoneOptions$PhoneOptionsSyncItem();
            ar.put(9065083732853317491L, syncItem);
         }

         return syncItem;
      }
   }

   public final void setShowCallLogsOption(int newOption) {
      this.setShowCallLogsOption(newOption, true);
   }

   public final void setShowCallLogsOption(int newOption, boolean postOptionChangedGlobalEvent) {
      this._persistedPhoneOptions.setShowCallLogsOption(newOption, postOptionChangedGlobalEvent);
      this.commit();
   }

   public final int getShowCallLogsOption() {
      return this._persistedPhoneOptions._showCallLogsOption;
   }

   public final int getBooleanOptions() {
      return this._persistedPhoneOptions._booleanOptions;
   }

   public final void setBooleanOptions(int booleanOptions) {
      int currentBooleanOptions = this.getBooleanOptions();
      this._persistedPhoneOptions._booleanOptions = booleanOptions;
      if ((currentBooleanOptions & 16384) != (booleanOptions & 16384)) {
         RibbonLauncher.getInstance().disableHotKeys(this.getBooleanOption(16384));
      }

      Phone.getInstance().setCLIR(this.getCLIR());
   }

   public final boolean getBooleanOption(int optionId) {
      return (this.getBooleanOptions() & optionId) != 0;
   }

   public final void setBooleanOption(int optionId, boolean value) {
      int booleanOptions = this.getBooleanOptions();
      boolean changeMade = false;
      if (value) {
         if ((booleanOptions & optionId) == 0) {
            booleanOptions |= optionId;
            changeMade = true;
         }
      } else if ((booleanOptions & optionId) != 0) {
         booleanOptions &= ~optionId;
         changeMade = true;
      }

      if (changeMade) {
         this.setBooleanOptions(booleanOptions);
         Phone.getInstance().setCLIR(this.getCLIR());
      }
   }

   public final int getCLIR() {
      if (this.getBooleanOption(32)) {
         return 1;
      } else {
         return this.getBooleanOption(131072) ? 2 : 0;
      }
   }

   public final String getVoiceMailNumber(int lineId) {
      if (this.hasSIMVoiceMailNumberChanged(lineId)) {
         return "";
      }

      Object obj = this._persistedPhoneOptions._voiceMailNumberEncoding.get(lineId);
      return PersistentContent.decodeString(obj);
   }

   final IntHashtable getVoiceMailNumbers() {
      return this._persistedPhoneOptions._voiceMailNumberEncoding;
   }

   final IntHashtable getVoiceMailAllAdditionalTones() {
      return this._persistedPhoneOptions._voiceMailTonesEncoding;
   }

   private final boolean hasSIMVoiceMailNumberChanged(int lineId) {
      String cachedNumber = this._persistedPhoneOptions._cachedSIMVoiceMailNumber;
      int cachedLength = cachedNumber == null ? 0 : cachedNumber.length();
      String currentNumber = PhoneUtilities.getSIMVoiceMailNumber(lineId);
      int currentLength = currentNumber == null ? 0 : currentNumber.length();
      if (currentLength == 0 && cachedLength == 0) {
         return false;
      } else {
         return currentLength != cachedLength ? true : !currentNumber.equals(cachedNumber);
      }
   }

   private static final boolean writeToSIMIsAllowed(String voiceMailNumber, int lineId) {
      if (voiceMailNumber.length() == 0) {
         return false;
      }

      switch (Branding.getVendorId()) {
         case 100:
         case 114:
         case 123:
         case 134:
         case 169:
         case 170:
         case 171:
         case 201:
         case 202:
         case 250:
            return false;
         default:
            if (lineId > 2) {
               return false;
            } else {
               try {
                  return RadioInfo.getNetworkType() == 3 && SIMCard.isReady();
               } finally {
                  ;
               }
            }
      }
   }

   public final void setVoiceMailNumber(String voiceMailNumber, boolean writeToSIM) {
      this.setVoiceMailNumber(voiceMailNumber, writeToSIM, PhoneUtilities.getCurrentLineId());
   }

   final void setVoiceMailNumber(String voiceMailNumber, boolean writeToSIM, int lineId) {
      if (voiceMailNumber != null) {
         boolean encrypt = !Security.getInstance().getAllowOutgoingCallWhileLocked();
         if (writeToSIM && writeToSIMIsAllowed(voiceMailNumber, lineId)) {
            if (!this.writeTo3GSIM(voiceMailNumber, lineId)) {
               this.writeToSIM(voiceMailNumber, lineId);
            }

            if (voiceMailNumber.equals(PhoneUtilities.getSIMVoiceMailNumber(lineId))) {
               voiceMailNumber = "";
            }
         }

         if (voiceMailNumber.length() > 0) {
            this._persistedPhoneOptions._voiceMailNumberEncoding.put(lineId, PersistentContent.encode(voiceMailNumber, true, encrypt));
         } else {
            this._persistedPhoneOptions._voiceMailNumberEncoding.remove(lineId);
         }

         this._persistedPhoneOptions._cachedSIMVoiceMailNumber = PhoneUtilities.getSIMVoiceMailNumber(lineId);
         this.commit();
         RIMGlobalMessagePoster.postGlobalEvent(6314221642431705640L);
      }
   }

   private final boolean writeTo3GSIM(String voiceMailNumber, int lineId) {
      PhoneOptions$SIMRead3G r3g = new PhoneOptions$SIMRead3G(lineId);
      ((SIMCardEfHandler)(new Object())).startTask(r3g, true);
      byte[] buffer = r3g.getBuffer();
      if (buffer != null && buffer.length > 0) {
         int record = buffer[0];
         if (record > 0) {
            SIMPhoneNumberWriter.write(voiceMailNumber, 1, 79, record, false);
            return true;
         }
      }

      return false;
   }

   private final void writeToSIM(String voiceMailNumber, int lineId) {
      SIMPhoneNumberWriter.write(voiceMailNumber, 1, 54, lineId, false);
   }

   public final String getVoiceMailAdditionalTones() {
      return this.getVoiceMailAdditionalTones(PhoneUtilities.getCurrentLineId());
   }

   public final String getVoiceMailAdditionalTones(int lineId) {
      Object obj = this._persistedPhoneOptions._voiceMailTonesEncoding.get(lineId);
      return PersistentContent.decodeString(obj);
   }

   public final void setVoiceMailAdditionalTones(String voiceMailTones) {
      this.setVoiceMailAdditionalTones(voiceMailTones, PhoneUtilities.getCurrentLineId());
   }

   final void setVoiceMailAdditionalTones(String voiceMailTones, int lineId) {
      if (voiceMailTones != null) {
         boolean encrypt = !Security.getInstance().getAllowOutgoingCallWhileLocked();
         this._persistedPhoneOptions._voiceMailTonesEncoding.put(lineId, PersistentContent.encode(voiceMailTones, true, encrypt));
         this.commit();
      }
   }

   final IntHashtable getLineDescriptions() {
      return this._persistedPhoneOptions._lineDescriptions;
   }

   public final String getLineDescription(int lineId) {
      Object obj = this._persistedPhoneOptions._lineDescriptions.get(lineId);
      return obj != null ? obj.toString() : null;
   }

   public final void setLineDescription(int lineId, String description) {
      if (description != null && description.length() != 0) {
         this._persistedPhoneOptions._lineDescriptions.put(lineId, description);
      } else {
         this._persistedPhoneOptions._lineDescriptions.remove(lineId);
      }

      this.commit();
   }

   public final String[] getSavedForwardingNumbers() {
      return this.getSavedForwardingNumbers(null);
   }

   public final String[] getSavedForwardingNumbers(String[] activeNumbers) {
      if (this._persistedPhoneOptions._savedFwdingNumbers == null) {
         return EMPTY_FORWARDING_NUMBERS_STRING;
      }

      String[] savedFwdNumbers = this.getSavedFwdingNumbers();
      if (activeNumbers != null) {
         for (int i = savedFwdNumbers.length - 1; i >= 0; i--) {
            int arrayIndex = PhoneUtilities.getArrayIndex(savedFwdNumbers[i], activeNumbers);
            if (arrayIndex != -1) {
               this._persistedPhoneOptions._savedFwdingNumbers[i] = PersistentContent.encode(activeNumbers[arrayIndex]);
               this.commit();
            }
         }
      }

      return savedFwdNumbers;
   }

   public final void clearSavedForwardingNumbers() {
      if (this._persistedPhoneOptions._savedFwdingNumbers != null) {
         this._persistedPhoneOptions._savedFwdingNumbers = null;
         this.commit();
      }
   }

   final void deleteSavedForwardingNumber(int index) {
      int origCount = this._persistedPhoneOptions._savedFwdingNumbers.length;
      if (index >= 0 && index < origCount) {
         for (int i = index; i < origCount - 1; i++) {
            this._persistedPhoneOptions._savedFwdingNumbers[i] = this._persistedPhoneOptions._savedFwdingNumbers[i + 1];
         }

         Array.resize(this._persistedPhoneOptions._savedFwdingNumbers, origCount - 1);
         this.commit();
      }
   }

   final int addSavedForwardingNumber(String number) {
      return this.addSavedForwardingNumber(number, false, true);
   }

   private final int addSavedForwardingNumber(String number, boolean syncOperation, boolean commit) {
      if (PhoneUtilities.isEmptyString(number)) {
         return -1;
      }

      int indexOfExistingItem = -1;
      if (this._persistedPhoneOptions._savedFwdingNumbers == null) {
         this._persistedPhoneOptions._savedFwdingNumbers = new Object[0];
      } else {
         indexOfExistingItem = PhoneUtilities.getArrayIndex(number, this.getSavedFwdingNumbers());
      }

      if (indexOfExistingItem < 0) {
         int len = this._persistedPhoneOptions._savedFwdingNumbers.length;
         Array.resize(this._persistedPhoneOptions._savedFwdingNumbers, len + 1);
         this._persistedPhoneOptions._savedFwdingNumbers[len] = PersistentContent.encode(number);
         if (commit) {
            this.commit();
         }

         return len;
      } else {
         return indexOfExistingItem;
      }
   }

   private final String[] getSavedFwdingNumbers() {
      String[] decodedNumbers = new Object[this._persistedPhoneOptions._savedFwdingNumbers.length];

      for (int i = 0; i < decodedNumbers.length; i++) {
         decodedNumbers[i] = PersistentContent.decodeString(this._persistedPhoneOptions._savedFwdingNumbers[i]);
      }

      return decodedNumbers;
   }

   public final String getForwardingNumber(int type) {
      return PersistentContent.decodeString(this._persistedPhoneOptions._forwardingNumbersEncoding[type]);
   }

   public final void setForwardingNumber(int type, String forwardingNumber) {
      if (forwardingNumber != null) {
         this._persistedPhoneOptions._forwardingNumbersEncoding[type] = PersistentContent.encode(forwardingNumber);
      }
   }

   public final int getPhoneListViewType() {
      return this._persistedPhoneOptions._phonelistViewType;
   }

   public final void setPhoneListViewType(int viewType) {
      this._persistedPhoneOptions._phonelistViewType = viewType;
   }

   public final int getDefaultCallVolume() {
      return this._persistedPhoneOptions._defaultCallVolume;
   }

   public final void setDefaultCallVolume(int volume) {
      this._persistedPhoneOptions._defaultCallVolume = volume;
   }

   public final int getDefaultEnhanceCallAudio() {
      return this._persistedPhoneOptions._defaultEnhanceCallAudio;
   }

   public final void setDefaultEnhanceCallAudio(int eca) {
      this._persistedPhoneOptions._defaultEnhanceCallAudio = eca;
   }

   public final int getPreviousEnhanceCallAudio() {
      return this._persistedPhoneOptions._previousEnhanceCallAudio;
   }

   public final void setPreviousEnhanceCallAudio(int eca) {
      this._persistedPhoneOptions._previousEnhanceCallAudio = eca;
   }

   public final int getRingtoneLightStyle() {
      return this._persistedPhoneOptions._ringtoneLight;
   }

   public final void setRingtoneLightStyle(int style) {
      this._persistedPhoneOptions._ringtoneLight = style;
   }

   public final void logPhoneError(String errType, int error, String message) {
      message = ((StringBuffer)(new Object()))
         .append(errType)
         .append('-')
         .append(error & 65535)
         .append('-')
         .append(error >>> 16)
         .append(' ')
         .append(message)
         .toString();
      StringBuffer datetime = (StringBuffer)(new Object());
      DateFormat df = DateFormat.getInstance(63);
      df.formatLocal(datetime, System.currentTimeMillis());
      String errorLog = ((StringBuffer)(new Object()))
         .append(datetime.toString())
         .append(' ')
         .append(message)
         .append('\n')
         .append(this.getPhoneErrorLog())
         .toString();
      if (errorLog.length() > 256) {
         int len = errorLog.indexOf(10);

         for (int pos = errorLog.indexOf(10); pos != -1; pos = errorLog.indexOf(10, pos + 1)) {
            if (pos < 256) {
               len = pos;
            }
         }

         errorLog = errorLog.substring(0, len);
      }

      this._errorLog = errorLog;
      this._errorLogPersistentObject.setContents(this._errorLog, 51);
      this._errorLogPersistentObject.commit();
   }

   public final String getPhoneErrorLog() {
      return this._errorLog;
   }

   public final int getHACMode() {
      return this._persistedPhoneOptions._HACMode;
   }

   public final void setHACMode(int mode) {
      if (this._persistedPhoneOptions._HACMode != mode) {
         this._persistedPhoneOptions._HACMode = mode;
         this.commit();
      }
   }

   public final int getTTYMode() {
      return this._persistedPhoneOptions._TTYMode;
   }

   public final void setTTYMode(int ttyMode) {
      if (this._persistedPhoneOptions._TTYMode != ttyMode) {
         this._persistedPhoneOptions._TTYMode = ttyMode;
         this.commit();
      }
   }

   public static final void editOptions(Object context) {
      UiApplication.getUiApplication().pushScreen(new PhoneOptionsScreen(context));
   }

   @Override
   public final void persistentContentStateChanged(int state) {
   }

   @Override
   public final void persistentContentModeChanged(int generation) {
      this._persistedPhoneOptions.reCrypt();
      this.commit();
   }
}
