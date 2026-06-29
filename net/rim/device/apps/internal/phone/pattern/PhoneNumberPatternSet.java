package net.rim.device.apps.internal.phone.pattern;

import net.rim.device.api.util.AbstractString;

final class PhoneNumberPatternSet {
   public static final int validatePhoneNumber(int hashVal, int smartDialingCountryCode, AbstractString str, int beginIndex, int digitCount) {
      int countryCode = WorldPhoneInfo.parseCountryCode(str, beginIndex);
      if (CannedHashPatterns.isInternational(hashVal, countryCode) && testInternationalPhoneNumber(countryCode, str, beginIndex, digitCount)) {
         return countryCode;
      } else {
         return CannedHashPatterns.isLocal(hashVal, smartDialingCountryCode) && testLocalPhoneNumber(smartDialingCountryCode, str, beginIndex, digitCount)
            ? smartDialingCountryCode
            : -1;
      }
   }

   static final boolean testLocalPhoneNumber(int countryCode, AbstractString str, int beginIndex, int digitCount) {
      switch (countryCode) {
         case 1:
            if (digitCount != 7 && digitCount != 10) {
               break;
            }

            char firstDigit = parseDialingDigit(str, beginIndex, 0);
            if (firstDigit == '0' || firstDigit == '1') {
               return false;
            }
            break;
         case 33:
            if (digitCount == 10) {
               char firstDigit = parseDialingDigit(str, beginIndex, 0);
               if (firstDigit != '0') {
                  return false;
               }
            }
            break;
         case 44:
            if (digitCount == 11) {
               char firstDigit = parseDialingDigit(str, beginIndex, 0);
               if (firstDigit != '0') {
                  return false;
               }
            }
      }

      return true;
   }

   private static final boolean testInternationalPhoneNumber(int countryCode, AbstractString str, int beginIndex, int digitCount) {
      int[] range = WorldPhoneInfo.getNationalPhoneNumberLengthRange(countryCode);
      if (range == null) {
         return startsWithPlusSign(str, beginIndex);
      }

      int ccLen = WorldPhoneInfo.getCountryCodeLength(countryCode);
      if (digitCount >= ccLen + range[0] && digitCount <= ccLen + range[1]) {
         return true;
      }

      char[] ndd = WorldPhoneInfo.getNationalDialingDigits(countryCode);
      if (ndd == null) {
         return false;
      }

      int nddLen = ndd.length;
      if (digitCount - nddLen >= ccLen + range[0] && digitCount - nddLen <= ccLen + range[1]) {
         for (int idx = 0; idx < ndd.length; idx++) {
            if (parseDialingDigit(str, beginIndex, ccLen + idx) != ndd[idx]) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static final char parseDialingDigit(AbstractString str, int charIndex, int digitPosition) {
      while (true) {
         char c = str.charAt(charIndex);
         if (isDTMFKey(c)) {
            if (digitPosition == 0) {
               return c;
            }

            digitPosition--;
         }

         charIndex++;
      }
   }

   private static final boolean startsWithPlusSign(AbstractString str, int charIndex) {
      int len = str.length();

      for (int idx = charIndex; idx < len; idx++) {
         char ch = str.charAt(charIndex);
         if (isDTMFKey(ch)) {
            return false;
         }

         if (ch == '+') {
            return true;
         }
      }

      return false;
   }

   private static final boolean isDTMFKey(char key) {
      return Character.isDigit(key) || key == '#' || key == '*';
   }
}
