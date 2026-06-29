package net.rim.device.apps.internal.sms.ui;

import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.apps.api.addressbook.FriendlyNameAddressModel;
import net.rim.device.apps.api.addressbook.GroupAddressCardModel;
import net.rim.device.apps.internal.sms.SMSModel;
import net.rim.device.apps.internal.sms.SMSService;

public class SMSFilter extends TextFilter {
   private boolean _allowEnter;
   private int _encoding;

   public SMSFilter(boolean allowEnter, int encoding) {
      this._allowEnter = allowEnter;
      this._encoding = encoding;
   }

   @Override
   public char convert(char character, int status) {
      if (character == 137) {
         return '\u0000';
      } else if (!this.validate(character) && !CharacterUtilities.isISOControl(character)) {
         char original = CharacterUtilities.getOriginal(character);
         return this.validate(original) ? original : '?';
      } else {
         return character;
      }
   }

   public int getEncodedLength(AbstractString string) {
      if (this._encoding != 0) {
         return string.length();
      }

      int length = 0;

      for (int i = string.length() - 1; i >= 0; i--) {
         length += this.getDefaultEncodingLength(string.charAt(i));
      }

      return length;
   }

   public int getEncodedLength(String string) {
      if (this._encoding != 0) {
         return string.length();
      }

      int length = 0;

      for (int i = string.length() - 1; i >= 0; i--) {
         length += this.getDefaultEncodingLength(string.charAt(i));
      }

      return length;
   }

   public int getEncodedLength(char c) {
      return this._encoding == 0 ? this.getDefaultEncodingLength(c) : 1;
   }

   private int getDefaultEncodingLength(char c) {
      switch (c) {
         case '\f':
         case '[':
         case '\\':
         case ']':
         case '^':
         case '{':
         case '|':
         case '}':
         case '~':
         case '€':
            return 2;
         default:
            return 1;
      }
   }

   @Override
   public boolean validate(char c) {
      if (c == '\n' && !this._allowEnter) {
         return false;
      } else {
         return c == '￼' ? true : SMSPacketHeader.validateForMessageCoding(c, this._encoding);
      }
   }

   public int getNumberOfCharactersToReserveForAddresses(SMSModel model) {
      if (model != null && SMSService.isEmailAddressAsSMSAddressSupported()) {
         Object[] addresses = model._payload.getAddresses();
         if (addresses == null) {
            return 0;
         }

         int numCharsToReserve = 0;

         for (int i = addresses.length - 1; i >= 0; i--) {
            numCharsToReserve = Math.max(numCharsToReserve, this.getNumberOfCharactersRequiredForAddress(addresses[i]));
         }

         return numCharsToReserve;
      } else {
         return 0;
      }
   }

   public int getNumberOfCharactersRequiredForAddress(Object address) {
      int requiredSpace = 0;
      if (address instanceof Object) {
         return this.getEncodedLength(((FriendlyNameAddressModel)address).getAddress()) + 1;
      }

      if (address instanceof Object) {
         GroupAddressCardModel gacm = (GroupAddressCardModel)address;

         for (int i = 0; i < gacm.size(); i++) {
            if (gacm.getAddressModelTypeAt(i) == 0) {
               requiredSpace = Math.max(requiredSpace, this.getEncodedLength(((FriendlyNameAddressModel)gacm.getAddressModelAt(i)).getAddress()) + 1);
            }
         }
      }

      return requiredSpace;
   }
}
