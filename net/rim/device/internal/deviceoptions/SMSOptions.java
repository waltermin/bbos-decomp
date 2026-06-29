package net.rim.device.internal.deviceoptions;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.RIMGlobalMessagePoster;
import net.rim.device.api.system.RIMPersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.internal.system.RadioInternal;

public final class SMSOptions {
   public static final long GUID_SMS_OPTIONS_CHANGED = 6063360555319689575L;
   public static final long GUID_SMS_UI_CHANGED = 7884295420352689779L;
   private static final long SMS_OPTIONS_DATA_KEY = -4677898393278760324L;
   private static PersistentObject _persistentObject = RIMPersistentStore.getPersistentObject(-4677898393278760324L);
   private static SMSOptions$SMSOptionsData _smsOptionsData;
   public static final int NUMBER_PREVIOUS_ITEMS_MAX = 50;
   public static final int NUMBER_PREVIOUS_ITEMS_DEFAULT = 7;
   public static final int NUMBER_PREVIOUS_ITEMS_DEFAULT_IDEN = 0;

   private SMSOptions() {
   }

   static final void init() {
      if (RadioInfo.areWAFsSupported(1)) {
         RadioInternal.smsSetRoute(_smsOptionsData._smsRoute);
         RadioInternal.smsStoreOnSIM(_smsOptionsData._storeOnSIM);
      }
   }

   private static final void commit(boolean notifyOfChanges) {
      _persistentObject.commit();
      if (notifyOfChanges) {
         RIMGlobalMessagePoster.postGlobalEvent(6063360555319689575L);
      }
   }

   public static final byte[] getVoicemailIndicators(int imsiCRC) {
      return imsiCRC == _smsOptionsData._imsiCRC ? _smsOptionsData._voicemailIndicators : new byte[4];
   }

   static final byte[] getVoicemailIndicators() {
      return _smsOptionsData._voicemailIndicators;
   }

   public static final void setVoicemailIndicators(byte[] voicemailIndicators, int imsiCRC) {
      _smsOptionsData._voicemailIndicators = voicemailIndicators;
      _smsOptionsData._imsiCRC = imsiCRC;
      commit(false);
   }

   public static final int getRoute() {
      return _smsOptionsData._smsRoute;
   }

   public static final void setRoute(int route) {
      _smsOptionsData._smsRoute = route;
      if (RadioInfo.areWAFsSupported(1)) {
         RadioInternal.smsSetRoute(route);
      }

      commit(true);
   }

   public static final boolean getDeliveryReports() {
      return _smsOptionsData._deliveryReports;
   }

   public static final void setDeliveryReports(boolean deliveryReports) {
      _smsOptionsData._deliveryReports = deliveryReports;
      commit(true);
   }

   public static final boolean getMultipleRecipients() {
      return _smsOptionsData._multipleRecipients;
   }

   public static final void setMultipleRecipients(boolean multipleRecipients) {
      _smsOptionsData._multipleRecipients = multipleRecipients;
      commit(true);
   }

   public static final boolean getStoreOnSIM() {
      return SIMCard.isSupported() ? _smsOptionsData._storeOnSIM : false;
   }

   public static final void setStoreOnSIM(boolean storeOnSIM) {
      _smsOptionsData._storeOnSIM = storeOnSIM;
      if (RadioInfo.areWAFsSupported(1)) {
         RadioInternal.smsStoreOnSIM(storeOnSIM);
      }

      commit(true);
   }

   public static final boolean getDisableAutoText() {
      return _smsOptionsData._disableAutoText;
   }

   public static final boolean getEnableCellBroadcast() {
      return _smsOptionsData._enableCellBroadcast;
   }

   public static final void setEnableCellBroadcast(boolean enableCellBroadcast) {
      _smsOptionsData._enableCellBroadcast = enableCellBroadcast;
      commit(true);
   }

   public static final void setDisableAutoText(boolean disableAutoText) {
      _smsOptionsData._disableAutoText = disableAutoText;
      commit(true);
   }

   public static final int getNumPreviousItems() {
      return _smsOptionsData._numPreviousItems;
   }

   public static final void setNumPreviousItems(int numPreviousItems) {
      _smsOptionsData._numPreviousItems = numPreviousItems;
      commit(true);
   }

   public static final int getPresetUiId() {
      return _smsOptionsData._presetUiId;
   }

   public static final void setUiId(int uiId, boolean forceSetting) {
      if (!_smsOptionsData._smsUiPreset || forceSetting) {
         _smsOptionsData._smsUiPreset = true;
         boolean postEvent = _smsOptionsData._presetUiId != uiId;
         _smsOptionsData._presetUiId = uiId;
         commit(false);
         if (postEvent) {
            RIMGlobalMessagePoster.postGlobalEvent(7884295420352689779L);
         }
      }
   }

   public static final int getMessageListUiId() {
      return _smsOptionsData._messageListUiId;
   }

   public static final void setMessageListUiId(int messageListUiId, boolean forceSetting) {
      if (!_smsOptionsData._messageListUiPreset || forceSetting) {
         _smsOptionsData._messageListUiPreset = true;
         _smsOptionsData._messageListUiId = messageListUiId;
         commit(false);
      }
   }

   public static final int getFallbackCoding() {
      return _smsOptionsData._fallbackMessageCoding;
   }

   public static final void setFallbackCoding(int coding) {
      _smsOptionsData._fallbackMessageCoding = coding;
      commit(true);
   }

   static {
      synchronized (_persistentObject) {
         _smsOptionsData = (SMSOptions$SMSOptionsData)_persistentObject.getContents();
         if (_smsOptionsData == null) {
            _smsOptionsData = new SMSOptions$SMSOptionsData();
            _persistentObject.setContents(_smsOptionsData, 51, false);
            commit(false);
         }
      }
   }
}
