package net.rim.device.internal.deviceoptions;

import net.rim.device.api.system.Branding;
import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.vm.Persistable;

final class SMSOptions$SMSOptionsData implements Persistable {
   public int _smsRoute;
   public boolean _deliveryReports;
   public boolean _storeOnSIM;
   public int _numPreviousItems;
   public int _presetUiId;
   public int _messageListUiId;
   public boolean _smsUiPreset;
   public boolean _messageListUiPreset;
   public boolean _multipleRecipients;
   public boolean _disableAutoText;
   public boolean _enableCellBroadcast;
   public byte[] _voicemailIndicators;
   public int _imsiCRC;
   public int _fallbackMessageCoding;

   public SMSOptions$SMSOptionsData() {
      int networkType = RadioInfo.getNetworkType();
      this._fallbackMessageCoding = -1;
      this._smsRoute = 1;
      this._multipleRecipients = true;
      if (networkType == 4) {
         this._deliveryReports = CDMAInfo.getSMSStatusReportRequest();
      } else {
         this._deliveryReports = false;
      }

      this._imsiCRC = 0;
      this._voicemailIndicators = new byte[4];
      this._storeOnSIM = false;
      this._presetUiId = 0;
      this._messageListUiId = 0;
      this._smsUiPreset = false;
      this._messageListUiPreset = false;
      this._disableAutoText = false;
      byte[] data = Branding.getData(21248);
      if (data != null && data.length > 0) {
         this._enableCellBroadcast = data[0] != 0;
      } else {
         this._enableCellBroadcast = false;
      }

      if (networkType == 5) {
         this._numPreviousItems = 0;
      } else {
         this._numPreviousItems = 7;
      }
   }
}
