package net.rim.blackberry.api.phone.phonelogs;

import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.apps.internal.phone.data.CallerIDInfo;
import net.rim.device.apps.internal.phone.model.AbstractPhoneNumberModel;

public final class PhoneCallLogID {
   private CallerIDInfo _info;
   private String _rawNumber;

   public PhoneCallLogID(String number) {
      this._info = PhoneUtilities.createCallerIDInfo(number);
      this._rawNumber = number;
   }

   PhoneCallLogID(CallerIDInfo info) {
      this._info = info;
      AbstractPhoneNumberModel number = (AbstractPhoneNumberModel)this._info.getNumber();
      if (number != null) {
         this._rawNumber = number.toString();
      } else {
         this._rawNumber = "";
      }
   }

   public final String getName() {
      return this._info != null ? this._info.getFriendlyName() : null;
   }

   public final String getNumber() {
      return this._rawNumber;
   }

   public final String getAddressBookFormattedNumber() {
      if (this._info != null) {
         AbstractPhoneNumberModel number = (AbstractPhoneNumberModel)this._info.getNumber();
         if (number != null) {
            return number.toString();
         }
      }

      return "";
   }

   public final String getType() {
      if (this._info != null) {
         AbstractPhoneNumberModel number = (AbstractPhoneNumberModel)this._info.getNumber();
         if (number != null) {
            return number.getTypeString();
         }
      }

      return "";
   }

   public final void setName(String name) {
      if (this._info != null) {
         this._info.setFriendlyName(name);
      }
   }
}
