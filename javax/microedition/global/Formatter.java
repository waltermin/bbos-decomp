package javax.microedition.global;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.util.Arrays;

public final class Formatter {
   private String _locale;
   public static final int DATE_LONG;
   public static final int DATE_SHORT;
   public static final int DATETIME_LONG;
   public static final int DATETIME_SHORT;
   public static final int TIME_LONG;
   public static final int TIME_SHORT;
   private static String EMPTY = "";
   private static String EN_LOCALE = "en";

   public Formatter() {
      this(System.getProperty("microedition.locale"));
   }

   public Formatter(String locale) {
      if (locale != null && !locale.equals(EMPTY)) {
         this._locale = GlobalUtilities.convertUnderscoreToHyphens(locale);
         if (!GlobalUtilities.isValidLocale(this._locale)) {
            throw new IllegalArgumentException();
         }

         if (!this.isSupportedLocale(this._locale)) {
            throw new UnsupportedLocaleException();
         }
      } else {
         this._locale = null;
      }
   }

   public final String formatCurrency(double number) {
      return this._locale == null ? Double.toString(number) : "$" + this.correctDecimals(Double.toString(number), 2);
   }

   public final String formatCurrency(double number, String currencyCode) {
      if (currencyCode == null) {
         throw new NullPointerException();
      } else if (currencyCode.length() != 3
         || !Character.isUpperCase(currencyCode.charAt(0))
         || !Character.isUpperCase(currencyCode.charAt(1))
         || !Character.isUpperCase(currencyCode.charAt(2))) {
         throw new IllegalArgumentException();
      } else if (Double.isNaN(number) || Double.isInfinite(number)) {
         return Double.toString(number);
      } else {
         return this._locale == null ? Double.toString(number) + " " + currencyCode : this.correctDecimals(Double.toString(number), 2) + " " + currencyCode;
      }
   }

   public final String formatDateTime(Calendar dateTime, int style) {
      if (dateTime == null) {
         throw new NullPointerException();
      }

      if (this._locale == null) {
         switch (style) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            case 1:
            default:
               return this.createISO8601Date(dateTime);
            case 2:
            case 3:
               return this.createISO8601Time(dateTime);
            case 4:
            case 5:
               return this.createISO8601Date(dateTime) + 'T' + this.createISO8601Time(dateTime);
         }
      } else if (this._locale.equals(EN_LOCALE)) {
         int internalStyle;
         switch (style) {
            case -1:
               throw new IllegalArgumentException();
            case 0:
            default:
               internalStyle = 56;
               break;
            case 1:
               internalStyle = 40;
               break;
            case 2:
               internalStyle = 7;
               break;
            case 3:
               internalStyle = 5;
               break;
            case 4:
               internalStyle = 54;
               break;
            case 5:
               internalStyle = 54;
         }

         return DateFormat.getInstance(internalStyle).format(dateTime);
      } else {
         return EMPTY;
      }
   }

   private final String createISO8601Date(Calendar dateTime) {
      return Integer.toString(dateTime.get(1)) + '-' + this.forceTwoDigits(dateTime.get(2) + 1) + '-' + dateTime.get(5);
   }

   private final String createISO8601Time(Calendar dateTime) {
      return this.forceTwoDigits(dateTime.get(11))
         + ':'
         + this.forceTwoDigits(dateTime.get(12))
         + ':'
         + this.forceTwoDigits(dateTime.get(13))
         + '.'
         + dateTime.get(14)
         + 'Z';
   }

   private final String forceTwoDigits(int value) {
      return value >= 10 ? Integer.toString(value) : "0" + Integer.toString(value);
   }

   public static final String formatMessage(String template, String[] params) {
      if (template != null && params != null) {
         String result = EMPTY;
         int nextPlaceholder = 0;
         int pos = 0;

         while (true) {
            nextPlaceholder = template.indexOf(123, pos);
            if (nextPlaceholder < 0) {
               return result + template.substring(pos);
            }

            if (template.charAt(nextPlaceholder + 1) == '{') {
               result = result + template.substring(pos, nextPlaceholder + 1);
               pos = nextPlaceholder + 2;
            } else {
               if (!Character.isDigit(template.charAt(nextPlaceholder + 1))) {
                  throw new IllegalArgumentException();
               }

               boolean twoDigits = false;
               int index;
               if (template.charAt(nextPlaceholder + 2) == '}') {
                  index = Character.digit(template.charAt(nextPlaceholder + 1), 10);
               } else {
                  if (template.charAt(nextPlaceholder + 3) != '}') {
                     throw new IllegalArgumentException();
                  }

                  index = Character.digit(template.charAt(nextPlaceholder + 1), 10) * 10 + Character.digit(template.charAt(nextPlaceholder + 2), 10);
                  twoDigits = true;
               }

               if (index > params.length - 1) {
                  throw new IllegalArgumentException();
               }

               result = result + template.substring(pos, nextPlaceholder) + params[index];
               pos = nextPlaceholder + (twoDigits ? 4 : 3);
            }
         }
      } else {
         throw new NullPointerException();
      }
   }

   public final String formatNumber(double number) {
      return this._locale == null ? Double.toString(number) : Double.toString(number);
   }

   public final String formatNumber(double number, int decimals) {
      if (decimals < 1 || decimals > 15) {
         throw new IllegalArgumentException();
      } else {
         return this._locale == null
            ? this.correctDecimals(this.standardNotation(Double.toString(number)), decimals)
            : this.correctDecimals(this.standardNotation(Double.toString(number)), decimals);
      }
   }

   public final String formatNumber(long number) {
      return this._locale == null ? Long.toString(number) : Long.toString(number);
   }

   public final String formatPercentage(float value, int decimals) {
      if (decimals < 1 || decimals > 15) {
         throw new IllegalArgumentException();
      } else {
         return this._locale == null
            ? this.correctDecimals(this.standardNotation(Float.toString(value * 1120403456)), decimals) + '%'
            : this.correctDecimals(this.standardNotation(Float.toString(value * 1120403456)), decimals) + '%';
      }
   }

   private final String addZeroes(String originalValue, int num) {
      String ret = originalValue;

      for (int i = 0; i < num; i++) {
         ret = ret + '0';
      }

      return ret;
   }

   private final String standardNotation(String originalValue) {
      int decimalPos = originalValue.indexOf(46);
      int ePos = originalValue.indexOf(69);
      if (ePos == -1) {
         return originalValue;
      } else {
         int exponent = Integer.parseInt(originalValue.substring(ePos + 1));
         String temp = originalValue.substring(0, decimalPos) + originalValue.substring(decimalPos + 1, ePos);
         if (exponent > 0) {
            decimalPos += exponent;
            return temp.length() < decimalPos
               ? this.addZeroes(temp, decimalPos - temp.length()) + ".0"
               : temp.substring(0, decimalPos) + '.' + temp.substring(decimalPos);
         } else {
            return originalValue.charAt(0) == '-' ? this.addZeroes("-0.", -exponent - 1) + temp.substring(1) : this.addZeroes("0.", -exponent - 1) + temp;
         }
      }
   }

   private final String correctDecimals(String originalValue, int decimals) {
      int decimalPos = originalValue.indexOf(46);
      int numDecimals = originalValue.length() - decimalPos - 1;
      if (numDecimals > decimals) {
         return originalValue.substring(0, decimalPos + decimals + 1);
      } else {
         return decimals > numDecimals ? this.addZeroes(originalValue, decimals - numDecimals) : originalValue;
      }
   }

   public final String formatPercentage(long value) {
      return this._locale == null ? Long.toString(value) + "%" : Long.toString(value) + "%";
   }

   public final String getLocale() {
      return this._locale;
   }

   public static final String[] getSupportedLocales() {
      return new String[]{EN_LOCALE};
   }

   private final boolean isSupportedLocale(String locale) {
      return Arrays.contains(getSupportedLocales(), locale);
   }
}
