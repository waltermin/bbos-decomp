package net.rim.device.apps.internal.phone.data;

import net.rim.device.api.collection.ReadableList;
import net.rim.device.apps.api.addressbook.AddressCardModel;
import net.rim.device.apps.api.addressbook.CompanyInfoModel;
import net.rim.device.apps.api.addressbook.PersonNameModel;
import net.rim.device.apps.api.quickcontact.QuickContactList;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;
import net.rim.device.apps.internal.phone.model.PhoneNumberModel;
import net.rim.vm.Array;

final class CallSummaryInfo {
   private CallerIDInfo _cidi;
   private PersonNameModel _personNameInfo;
   private CompanyInfoModel _companyInfo;
   private boolean _haveAddress;
   private boolean _displayCompanyName;
   private String _primaryCallerIDString;
   private int[] _numberTypes;
   private String[] _numberStrings;
   private String[] _numberTypeStrings;
   private char[] _speedDialKeys;
   private int _phoneNumberCount;

   CallSummaryInfo(CallLogItem cli) {
      PhoneCallModelImpl callLog = cli.getCallLog();
      this._cidi = (CallerIDInfo)callLog.getCallerIDInfo(true);
      Object address = this._cidi.getAddress();
      Object number = this._cidi.getNumber();
      if (address instanceof AddressCardModel) {
         this._personNameInfo = ((AddressCardModel)address).getName();
      }

      this._primaryCallerIDString = this._cidi.getDisplayString(address);
      this._haveAddress = address != null;
      this._displayCompanyName = this._cidi.displayCompanyInfo();
      if (this._haveAddress) {
         this.getPhoneNumbers(address);
      } else if (number instanceof PhoneNumberModel) {
         this.addNumber((PhoneNumberModel)number);
      }

      if (address instanceof AddressCardModel) {
         this._companyInfo = ((AddressCardModel)address).getCompanyInfo();
      }
   }

   final String getCallerIDString() {
      return this._primaryCallerIDString;
   }

   final String getCompanyName() {
      return this._companyInfo != null ? this._companyInfo.toString() : null;
   }

   final String getNumberString(int type) {
      for (int i = 0; i < this._numberTypes.length; i++) {
         if (this._numberTypes[i] == type) {
            return this._numberStrings[i];
         }
      }

      return null;
   }

   final boolean haveAddress() {
      return this._haveAddress;
   }

   final boolean displayCompanyName() {
      return this._displayCompanyName;
   }

   final PersonNameModel getPersonName() {
      return this._personNameInfo;
   }

   final char getSpeedDialKey() {
      return this.getSpeedDialKey(0);
   }

   final char getSpeedDialKey(int numberType) {
      if (this._haveAddress) {
         for (int i = 0; i < this._numberTypes.length; i++) {
            if (this._numberTypes[i] == numberType) {
               return this._speedDialKeys[i];
            }
         }
      } else if (this._speedDialKeys != null && this._speedDialKeys.length > 0) {
         return this._speedDialKeys[0];
      }

      return '\u0000';
   }

   final int getPhoneNumberCount() {
      return this._phoneNumberCount;
   }

   private final void getPhoneNumbers(Object addressCard) {
      if (addressCard instanceof ReadableList) {
         ReadableList list = (ReadableList)addressCard;

         for (int i = list.size() - 1; i >= 0; i--) {
            Object o = list.getAt(i);
            if (o instanceof PhoneNumberModel) {
               PhoneNumberModel pnm = (PhoneNumberModel)o;
               this.addNumber(pnm);
            }
         }
      }
   }

   private final void addNumber(PhoneNumberModel number) {
      if (this._numberStrings == null) {
         this._numberTypes = new int[0];
         this._numberStrings = new String[0];
         this._numberTypeStrings = new String[0];
         this._speedDialKeys = new char[0];
      }

      Array.resize(this._numberTypes, this._numberTypes.length + 1);
      Array.resize(this._numberStrings, this._numberStrings.length + 1);
      Array.resize(this._numberTypeStrings, this._numberTypeStrings.length + 1);
      Array.resize(this._speedDialKeys, this._speedDialKeys.length + 1);
      char speedDialKey = QuickContactList.getInstance().getQuickContactKey(number);
      if (speedDialKey != 0) {
         this._speedDialKeys[this._speedDialKeys.length - 1] = speedDialKey;
      }

      int type = number.getType();
      this._numberTypes[this._numberTypes.length - 1] = type;
      String typeString = type != 0 ? number.toString() + " " + number.getTypeString() : number.toString();
      this._numberStrings[this._numberStrings.length - 1] = typeString;
      if (type != 0) {
         int flags = 6;
         this._numberTypeStrings[this._numberTypeStrings.length - 1] = AbstractPhoneNumberModel.getTypeString(type, flags);
      }

      this._phoneNumberCount++;
   }
}
