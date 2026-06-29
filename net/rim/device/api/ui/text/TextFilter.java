package net.rim.device.api.ui.text;

import net.rim.device.api.util.AbstractString;

public class TextFilter {
   private int _maxLength = Integer.MAX_VALUE;
   public static final int DEFAULT;
   public static final int NUMERIC;
   public static final int UPPERCASE;
   public static final int LOWERCASE;
   public static final int HEXADECIMAL;
   public static final int INTEGER;
   public static final int PHONE;
   public static final int URL;
   public static final int EMAIL;
   public static final int PIN_ADDRESS;
   public static final int ADN_SIM_PHONE;
   public static final int SIM_PHONE;
   public static final int FDN_SIM_PHONE;
   public static final int SMS_PHONE;
   public static final int DEFAULT_SMART_PHONE;
   public static final int VOICEMAIL_PHONE;
   public static final int REAL_NUMERIC;
   public static final int FILENAME;
   public static final int IP;
   public static final int JAPANESE_YOMI;
   public static final byte STYLE_SHIFT;
   private static TextFilter _numeric;
   private static TextFilter _realNumeric;
   private static TextFilter _uppercase;
   private static TextFilter _lowercase;
   private static TextFilter _hexadecimal;
   private static TextFilter _integer;
   private static TextFilter _phone;
   private static TextFilter _FDNphone;
   private static TextFilter _ADNphone;
   private static TextFilter _SMSphone;
   private static TextFilter _defaultSmartPhone;
   private static TextFilter _voicePhone;
   private static TextFilter _url;
   private static TextFilter _email;
   private static TextFilter _pin;
   private static TextFilter _filename;
   private static TextFilter _ip;

   public static boolean isNumericFilter(int aStyle) {
      boolean ret = false;
      switch (aStyle) {
         case 1:
         case 4:
         case 5:
         case 6:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         case 17:
         default:
            ret = true;
         case 0:
         case 2:
         case 3:
         case 7:
         case 8:
         case 16:
            return ret;
      }
   }

   protected TextFilter() {
   }

   public char convert(char _1, int _2) {
      throw null;
   }

   public char convert(char character, AbstractString text, int position, int status) {
      return this.convert(character, status);
   }

   public static final TextFilter get(int type) {
      switch (type) {
         case -1:
            return null;
         case 0:
         default:
            return null;
         case 1:
            if (_numeric == null) {
               _numeric = new NumericTextFilter();
            }

            return _numeric;
         case 2:
            if (_uppercase == null) {
               _uppercase = new UppercaseTextFilter();
            }

            return _uppercase;
         case 3:
            if (_lowercase == null) {
               _lowercase = new LowercaseTextFilter();
            }

            return _lowercase;
         case 4:
            if (_hexadecimal == null) {
               _hexadecimal = new HexadecimalTextFilter();
            }

            return _hexadecimal;
         case 5:
            if (_integer == null) {
               _integer = new NumericTextFilter(1);
            }

            return _integer;
         case 6:
            if (_phone == null) {
               _phone = new PhoneTextFilter(247);
            }

            return _phone;
         case 7:
            if (_url == null) {
               _url = new URLTextFilter();
            }

            return _url;
         case 8:
            if (_email == null) {
               _email = new EmailAddressTextFilter();
            }

            return _email;
         case 9:
            if (_pin == null) {
               _pin = new PINAddressTextFilter();
            }

            return _pin;
         case 10:
            if (_ADNphone == null) {
               _ADNphone = new PhoneTextFilter(133);
            }

            return _ADNphone;
         case 11:
            if (_FDNphone == null) {
               _FDNphone = new PhoneTextFilter(141);
            }

            return _FDNphone;
         case 12:
            if (_SMSphone == null) {
               _SMSphone = new PhoneTextFilter(404);
            }

            return _SMSphone;
         case 13:
            if (_defaultSmartPhone == null) {
               _defaultSmartPhone = new PhoneTextFilter(180);
            }

            return _defaultSmartPhone;
         case 14:
            if (_voicePhone == null) {
               _voicePhone = new PhoneTextFilter(388);
            }

            return _voicePhone;
         case 15:
            if (_realNumeric == null) {
               _realNumeric = new NumericTextFilter(3);
            }

            return _realNumeric;
         case 16:
            if (_filename == null) {
               _filename = new FilenameTextFilter();
            }

            return _filename;
         case 17:
            if (_ip == null) {
               _ip = new IPTextFilter();
            }

            return _ip;
      }
   }

   public int getMaxLength() {
      return this._maxLength;
   }

   protected void setMaxLength(int maxLength) {
      this._maxLength = maxLength;
   }

   public boolean validate(char _1) {
      throw null;
   }

   public boolean validate(char character, AbstractString text, int position) {
      return this.validate(character);
   }

   public boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int lv = 0; lv < textLength; lv++) {
            if (!this.validate(text.charAt(lv))) {
               return false;
            }
         }
      }

      return true;
   }

   public long getPreferredInputStyle() {
      return 0;
   }
}
