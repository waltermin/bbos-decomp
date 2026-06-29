package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.phone.pattern.WorldPhoneInfo;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class PhoneNumberConverter {
   private static String EXTENSION_KEY = "ext";
   private static int _cc;
   private static String _ccString;
   private static WeakReference _strBufferWR1 = (WeakReference)(new Object(null));
   private static WeakReference _strBufferWR2 = (WeakReference)(new Object(null));

   public static String format(String number, String pattern) {
      if (pattern != null && pattern.length() != 0) {
         StringBuffer buffer = (StringBuffer)(new Object());
         int patternIndex = pattern.length();

         for (int idx = number.length() - 1; idx >= 0; idx--) {
            while (patternIndex > 0) {
               char ch = pattern.charAt(--patternIndex);
               if (ch == '#') {
                  break;
               }

               buffer.insert(0, ch);
            }

            buffer.insert(0, number.charAt(idx));
         }

         return buffer.toString();
      } else {
         return number;
      }
   }

   private static boolean startsWithIllegalSmartDialingCharacter(char[] num) {
      if (num != null && num.length > 0) {
         char ch = num[0];
         return ch == '*' || ch == '#';
      } else {
         return false;
      }
   }

   public static void convertForTransmission(StringBuffer output, char[] input, Object context) {
      boolean isSMS = ContextObject.getFlag(context, 55) || ContextObject.getFlag(context, 74);
      boolean smartDialing = ContextObject.getFlag(context, 117);
      boolean wildcardDigits = ContextObject.getPrivateFlag(context, 4936088360624690805L, 67);
      boolean simDigits = ContextObject.getPrivateFlag(context, 4936088360624690805L, 68);
      boolean canPrompt = isEventThread();
      if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 91)) {
         canPrompt = false;
      }

      StringBuffer dtmf = (StringBuffer)(new Object());
      convertForTransmission(output, dtmf, input, smartDialing, wildcardDigits, simDigits, isSMS, canPrompt);
      if (context instanceof Object) {
         ContextObject contextObject = (ContextObject)context;
         if (dtmf.length() > 0) {
            contextObject.put(7528018505720453076L, dtmf.toString());
            return;
         }

         contextObject.remove(7528018505720453076L);
      }
   }

   public static void convertForTransmission(StringBuffer output, StringBuffer dtmf, char[] input, boolean smartDialing) {
      convertForTransmission(output, dtmf, input, smartDialing, false, false, false, false);
   }

   public static void convertForTransmission(
      StringBuffer output, StringBuffer dtmf, char[] input, boolean smartDialing, boolean wildcardDigits, boolean simDigits, boolean isSMS, boolean canPrompt
   ) {
      boolean canDirectDialExtensions = Phone.getInstance().supportsCorporateExtensions(0);
      convertForTransmission(output, dtmf, input, smartDialing, wildcardDigits, simDigits, isSMS, canPrompt, canDirectDialExtensions);
   }

   public static void convertForTransmission(
      StringBuffer output,
      StringBuffer dtmf,
      char[] input,
      boolean smartDialing,
      boolean wildcardDigits,
      boolean simDigits,
      boolean isSMS,
      boolean canPrompt,
      boolean canDirectDialExtensions
   ) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      boolean prependNDD = isSMS ? smartDialingOptions.autoAppendNDDForSMS() : smartDialingOptions.autoAppendNDDForDialing();
      convertForTransmission(output, dtmf, input, smartDialing, wildcardDigits, simDigits, isSMS, prependNDD, canPrompt, canDirectDialExtensions);
   }

   public static void convertForTransmission(
      StringBuffer output,
      StringBuffer dtmf,
      char[] input,
      boolean smartDialing,
      boolean wildcardDigits,
      boolean simDigits,
      boolean isSMS,
      boolean prependNDD,
      boolean canPrompt,
      boolean canDirectDialExtensions
   ) {
      for (int i = input.length - 1; i >= 0; i--) {
         input[i] = CharacterUtilities.foldFullWidth(input[i]);
      }

      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      if (smartDialingOptions.getCountryCode() <= 0) {
         smartDialing = false;
      }

      if (startsWithIllegalSmartDialingCharacter(input)) {
         smartDialing = false;
      }

      if (smartDialing && !canDialPlus()) {
         int mobilityCC = 302;
         int roamingCC = smartDialingOptions.getCountryCode();
         char[] roamingNDD = WorldPhoneInfo.getNationalDialingDigits(roamingCC);
         char[] roamingIDD = WorldPhoneInfo.getInternationalDialingDigits(roamingCC, mobilityCC);
         if (roamingNDD == null || roamingIDD == null) {
            smartDialing = false;
         }
      }

      int dtmfIndex = findExtensionOffset(input);
      copyTransmittableDigits(output, input, dtmfIndex, wildcardDigits, simDigits);
      dtmf.setLength(0);
      if (dtmfIndex < input.length) {
         boolean isCorporateExtension = output.length() == 0;
         StringUtilities.append(dtmf, (String)(new Object(input)), dtmfIndex, input.length - dtmfIndex);
         convertAfterDialTones(dtmf, isCorporateExtension);
      }

      int network = RadioInfo.getNetworkType();
      String additionalTones = smartDialingOptions.getAdditionalTonesForCorporateExtensions(network);
      if (output.length() == 0 && dtmf.length() > 0) {
         boolean startsWithX = startsWithX(input, dtmfIndex);
         if (canDirectDialExtensions && startsWithX) {
            smartDialing = false;
            copyTransmittableDigits(output, dtmf, -1, false, false);
            dtmf.setLength(0);
         } else if (additionalTones.length() == 0 && startsWithX) {
            smartDialing = smartDialingOptions.getCountryCode() > 0;
            dtmf.insert(0, getCorporatePhoneNumber(canPrompt));
            String corporateNumber = dtmf.toString();
            dtmf.setLength(0);
            copyTransmittableDigits(output, corporateNumber, -1, false, false);
         } else {
            smartDialing = smartDialingOptions.getCountryCode() > 0;
            copyTransmittableDigits(output, getCorporatePhoneNumber(canPrompt), -1, false, false);
         }
      } else if (smartDialing && !isSMS && dtmf.length() == 0 && isValidExtensionLength(output.length(), canPrompt) && isAllDigits(output)) {
         if (canDirectDialExtensions) {
            smartDialing = false;
         } else if (additionalTones.length() == 0) {
            String corporateNumber = ((StringBuffer)(new Object())).append(getCorporatePhoneNumber(canPrompt)).append(output.toString()).toString();
            copyTransmittableDigits(output, corporateNumber, -1, false, false);
         } else {
            dtmf.append(additionalTones);
            StringUtilities.append(dtmf, output);
            convertAfterDialTones(dtmf, true);
            copyTransmittableDigits(output, getCorporatePhoneNumber(canPrompt), -1, false, false);
         }
      }

      if (smartDialing) {
         applySmartDialing(output, canPrompt);
      }

      if (!canDialPlus() && output.length() > 0 && output.charAt(0) == '+') {
         output.deleteCharAt(0);
         int mobilityCC = 302;
         int roamingCC = smartDialingOptions.getCountryCode();
         if (roamingCC > 0) {
            int targetCC = WorldPhoneInfo.parseCountryCode(output);
            if (targetCC != roamingCC) {
               char[] roamingIDD = WorldPhoneInfo.getInternationalDialingDigits(roamingCC, mobilityCC);
               if (roamingIDD != null) {
                  output.insert(0, roamingIDD);
               }
            } else {
               int deleteCount = WorldPhoneInfo.getCountryCodeLength(targetCC);
               output.delete(0, deleteCount);
               if (prependNDD) {
                  char[] roamingNDD = WorldPhoneInfo.getNationalDialingDigits(roamingCC);
                  if (roamingNDD != null) {
                     output.insert(0, roamingNDD);
                  }
               }
            }
         }
      }

      if (simDigits) {
         stripNonSIMDigits(output, wildcardDigits);
         stripNonSIMDigits(dtmf, wildcardDigits);
      }
   }

   public static void convertAfterDialTones(StringBuffer toneBuffer, boolean isCorporateExtension) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      int network = RadioInfo.getNetworkType();
      String extTones = isCorporateExtension
         ? smartDialingOptions.getAdditionalTonesForCorporateExtensions(network)
         : smartDialingOptions.getAdditionalTonesForExtensions(network);
      String extensionKey = EXTENSION_KEY;
      int index = indexOf(toneBuffer, extensionKey);
      if (index != -1) {
         toneBuffer.delete(0, index + extensionKey.length());
         toneBuffer.insert(0, extTones);
      }

      index = 0;

      while (index < toneBuffer.length()) {
         char c = CharacterUtilities.foldFullWidth(toneBuffer.charAt(index));
         switch (c) {
            case '!':
            case '#':
            case '*':
            case ',':
               index++;
               break;
            case 'X':
            case 'x':
               if (index == 0) {
                  toneBuffer.deleteCharAt(index);
                  toneBuffer.insert(index, extTones);
                  index += extTones.length();
               } else {
                  if (index == 1 && toneBuffer.charAt(0) == ',') {
                     toneBuffer.delete(0, 2);
                     toneBuffer.insert(0, extTones);
                     index = extTones.length();
                     continue;
                  }

                  toneBuffer.setCharAt(index, PhoneNumberServices.getMnemonicKeyMapping(c));
                  index++;
               }
               break;
            case '\uf3fe':
               toneBuffer.setCharAt(index, ',');
               index++;
               break;
            case '\uf402':
               toneBuffer.setCharAt(index, '!');
               index++;
               break;
            default:
               if (Character.isDigit(c)) {
                  index++;
               } else {
                  char mnemonicKey = PhoneNumberServices.getMnemonicKeyMapping(c);
                  if (Character.isDigit(mnemonicKey)) {
                     toneBuffer.setCharAt(index, mnemonicKey);
                     index++;
                  } else {
                     toneBuffer.deleteCharAt(index);
                  }
               }
         }
      }
   }

   private static int indexOf(StringBuffer buffer, String searchString) {
      char firstChar = searchString.charAt(0);
      int count = buffer.length() - searchString.length();

      label27:
      for (int idx = 0; idx < count; idx++) {
         char ch = Character.toLowerCase(buffer.charAt(idx));
         if (ch == firstChar) {
            for (int i = 1; i < searchString.length(); i++) {
               ch = Character.toLowerCase(buffer.charAt(idx + i));
               if (ch != searchString.charAt(i)) {
                  continue label27;
               }
            }

            return idx;
         }
      }

      return -1;
   }

   private static boolean startsWithX(char[] buffer, int index) {
      while (index < buffer.length) {
         char ch = buffer[index];
         if (ch == 'x' || ch == 'X') {
            return true;
         }

         if (!isPunctuation(ch)) {
            return false;
         }

         index++;
      }

      return false;
   }

   private static void copyTransmittableDigits(StringBuffer output, Object input, int count, boolean wildcardDigitSupport, boolean simDigitSupport) {
      output.setLength(0);
      if (!(input instanceof byte[])) {
         if (!(input instanceof char[])) {
            String currentIndex = input.toString();
            if (count < 0) {
               count = currentIndex.length();
            }

            output.append(currentIndex);
         } else {
            char[] chars = (char[])input;
            if (count < 0) {
               count = chars.length;
            }

            output.append(chars, 0, count);
         }
      } else {
         byte[] inputBytes = (byte[])input;
         if (count < 0) {
            count = inputBytes.length;
         }

         StringUtilities.append(output, inputBytes, 0, count);
      }

      for (int currentIndex = count - 1; currentIndex >= 0; currentIndex--) {
         char ch = output.charAt(currentIndex);
         ch = CharacterUtilities.foldFullWidth(ch);
         if ((!simDigitSupport || ch != ',') && (!wildcardDigitSupport || ch != '?') && (ch < '0' || ch > '9') && ch != '*' && ch != '#' && ch != '+') {
            char mnemonicKey = PhoneNumberServices.getMnemonicKeyMapping(ch);
            if (mnemonicKey != 255) {
               output.setCharAt(currentIndex, mnemonicKey);
            } else {
               output.deleteCharAt(currentIndex);
            }
         }
      }
   }

   private static void applySmartDialing(StringBuffer number, boolean canPrompt) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      int countryCode = smartDialingOptions.getCountryCode();
      String acString = smartDialingOptions.getAreaCode();
      int nationalLength = smartDialingOptions.getNationalPhoneNumberLength();
      char[] idd = WorldPhoneInfo.getInternationalDialingDigits(countryCode, countryCode);
      char[] ndd = WorldPhoneInfo.getNationalDialingDigits(countryCode);
      int[] nnl = WorldPhoneInfo.getNationalPhoneNumberLengthRange(countryCode);
      int iddLength = idd != null ? idd.length : 0;
      int nddLength = ndd != null ? ndd.length : 0;
      int acLength = acString != null ? acString.length() : 0;
      if (canPrompt && countryCode == 1 && acLength == 0 && smartDialingOptions.getAllowPromptForAreaCode()) {
         String tmpAC = "";

         try {
            String phoneNum = Phone.getInstance().getNumber(0);
            if (phoneNum != null) {
               int len = phoneNum.length();
               if (len >= 10) {
                  tmpAC = phoneNum.substring(len - 10, len - 7);
               }
            }
         } finally {
            ;
         }

         PhoneNumberConverter$GetAreaCodeDialog gacd = new PhoneNumberConverter$GetAreaCodeDialog(tmpAC);
         tmpAC = gacd.getAreaCode();
         if (tmpAC != null && tmpAC.length() == 3) {
            acString = tmpAC;
            acLength = acString.length();
            smartDialingOptions.setAreaCode(acString);
         }

         smartDialingOptions.setAllowPromptForAreaCode(false);
         smartDialingOptions.commit();
      }

      if (countryCode > 0 && nationalLength > 0 && (number.length() <= 0 || number.charAt(0) != '+')) {
         int lnLength = nationalLength - acLength;
         boolean addPlus = false;
         boolean addCC = false;
         boolean addAC = false;
         int numberLength = number.length();
         boolean startsWithIDD = iddLength > 0 && matchDigits(idd, number, 0);
         boolean startsWithNDD = !startsWithIDD && nddLength > 0 && matchDigits(ndd, number, 0);
         if (startsWithNDD && WorldPhoneInfo.canNationalNumbersStartWithNDD(countryCode) && nnl != null && numberLength >= nnl[0] && numberLength <= nnl[1]) {
            startsWithNDD = false;
         }

         if (WorldPhoneInfo.isSpecialNationalNumber(number, countryCode)) {
            addPlus = false;
            addCC = false;
            addAC = false;
         } else if (acLength > 0 && numberLength == lnLength) {
            addPlus = true;
            addCC = true;
            addAC = true;
         } else if (numberLength == lnLength + acLength && !startsWithNDD) {
            addPlus = true;
            addCC = true;
         } else if (numberLength == lnLength + acLength + nddLength && startsWithNDD) {
            number.delete(0, nddLength);
            addPlus = true;
            addCC = true;
         } else {
            if (nnl == null || !startsWithNDD || numberLength < nnl[0] + nddLength || numberLength > nnl[1] + nddLength) {
               if (nnl != null && numberLength >= nnl[0] && numberLength <= nnl[1]) {
                  return;
               }

               convertToInternationalPhoneNumber(number);
               return;
            }

            number.delete(0, nddLength);
            addPlus = true;
            addCC = true;
         }

         if (addAC) {
            number.insert(0, acString);
         }

         if (addCC) {
            if (_cc != countryCode) {
               _cc = countryCode;
               _ccString = Integer.toString(countryCode);
            }

            number.insert(0, _ccString);
         }

         if (addPlus) {
            number.insert(0, '+');
         }
      } else {
         convertToInternationalPhoneNumber(number);
      }
   }

   private static void convertToInternationalPhoneNumber(StringBuffer number) {
      if (number.length() > 4 && number.charAt(0) == '0') {
         int cc = SmartDialingOptions.getOptions().getCountryCode();
         char[] idd = WorldPhoneInfo.getInternationalDialingDigits(cc, cc);
         if (idd != null) {
            int iddLen = idd.length;
            if (matchDigits(idd, number, 0)) {
               StringBuffer tmpNumber = (StringBuffer)(new Object());
               StringUtilities.append(tmpNumber, number, iddLen, number.length() - iddLen);
               promoteToInternationalForm(tmpNumber);
               if (tmpNumber.charAt(0) == '+') {
                  number.setLength(0);
                  StringUtilities.append(number, tmpNumber);
                  return;
               }
            }
         }
      }

      promoteToInternationalForm(number);
   }

   private static void promoteToInternationalForm(StringBuffer number) {
      int countryCode = WorldPhoneInfo.parseCountryCode(number);
      int[] range = WorldPhoneInfo.getNationalPhoneNumberLengthRange(countryCode);
      if (range != null) {
         char[] ndd = WorldPhoneInfo.getNationalDialingDigits(countryCode);
         int nddLength = ndd == null ? 0 : ndd.length;
         boolean hasPlusSign = number.charAt(0) == '+';
         int numberLength = number.length();
         if (hasPlusSign) {
            numberLength--;
         }

         int ccLength;
         if (countryCode < 10) {
            ccLength = 1;
         } else if (countryCode < 100) {
            ccLength = 2;
         } else {
            ccLength = 3;
         }

         if (!hasPlusSign && numberLength - ccLength >= range[0] && numberLength - ccLength <= range[1]) {
            if (ndd != null && matchDigits(ndd, number, ccLength) && !WorldPhoneInfo.canNationalNumbersStartWithNDD(countryCode)) {
               number.delete(ccLength, ccLength + nddLength);
            }

            number.insert(0, '+');
            return;
         }

         if (ndd != null && numberLength - ccLength - nddLength >= range[0] && numberLength - ccLength - nddLength <= range[1]) {
            if (hasPlusSign) {
               if (matchDigits(ndd, number, 1 + ccLength)) {
                  number.delete(1 + ccLength, 1 + ccLength + nddLength);
                  return;
               }
            } else if (matchDigits(ndd, number, ccLength)) {
               number.delete(ccLength, ccLength + nddLength);
               number.insert(0, '+');
               return;
            }
         } else if (hasPlusSign
            && ndd != null
            && matchDigits(ndd, number, 1 + ccLength)
            && (!WorldPhoneInfo.canNationalNumbersStartWithNDD(countryCode) || numberLength - ccLength < range[0] || numberLength - ccLength > range[1])) {
            number.delete(1 + ccLength, 1 + ccLength + nddLength);
         }
      }
   }

   private static String getCorporatePhoneNumber(boolean canPrompt) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      String number = smartDialingOptions.getCorporatePhoneNumber();
      if (number.length() == 0 && canPrompt) {
         Runnable runnable = (Runnable)ApplicationRegistry.getApplicationRegistry().get(-4905871728754809133L);
         if (runnable != null) {
            runnable.run();
            number = smartDialingOptions.getCorporatePhoneNumber();
         }
      }

      return number;
   }

   static int findExtensionOffset(char[] chars) {
      if (chars != null && chars.length != 0) {
         int index = indexOfExtension(chars);
         int idx = 0;

         while (idx < index) {
            switch (chars[idx]) {
               case '!':
               case ',':
               case 'x':
               case '\uf3fe':
               case '\uf402':
                  return idx;
               case 'X':
                  if (isValidExtensionIndex(chars, idx)) {
                     return idx;
                  }
               default:
                  idx++;
            }
         }

         if (index < chars.length) {
            return index;
         }

         index = -1;
         idx = 0;

         for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
               if (ch != 'X' || index != -1) {
                  index = -1;
                  break;
               }

               index = i;
            } else if (index != -1 && isDigit(ch)) {
               idx++;
            }
         }

         return index != -1 && idx > 0 ? index : chars.length;
      } else {
         return 0;
      }
   }

   private static int indexOfExtension(char[] chars) {
      int length = chars.length;
      int charsOfExtFound = 0;

      for (int idx = 0; idx < length; idx++) {
         char ch = chars[idx];
         if (ch == EXTENSION_KEY.charAt(charsOfExtFound)) {
            if (++charsOfExtFound == EXTENSION_KEY.length()) {
               return idx - charsOfExtFound + 1;
            }
         } else if (Character.toLowerCase(ch) == EXTENSION_KEY.charAt(0)) {
            charsOfExtFound = 1;
         } else {
            charsOfExtFound = 0;
         }
      }

      return length;
   }

   private static boolean isWhitespace(char ch) {
      switch (ch) {
         case '\t':
         case '\n':
         case '\r':
         case ' ':
            return true;
         default:
            return false;
      }
   }

   private static boolean isDigit(char ch) {
      return ch >= '0' && ch <= '9';
   }

   private static boolean isPunctuation(char ch) {
      switch (ch) {
         case '!':
         case '"':
         case '\'':
         case '(':
         case ')':
         case ',':
         case '.':
         case ':':
         case ';':
         case '<':
         case '>':
         case '?':
         case '[':
         case ']':
         case '{':
         case '|':
         case '}':
            return true;
         default:
            return false;
      }
   }

   private static boolean isValidExtensionIndex(char[] chars, int index) {
      if (index > 0) {
         char ch = chars[index - 1];
         if (!isWhitespace(ch) && !isPunctuation(ch)) {
            return false;
         }
      }

      int length = chars.length;
      boolean foundDigit = false;

      for (int idx = index + 1; idx < length; idx++) {
         char ch = chars[idx];
         if (isDigit(ch)) {
            foundDigit = true;
         } else if (!isWhitespace(ch) && !isPunctuation(ch)) {
            return false;
         }
      }

      return foundDigit;
   }

   public static boolean isSupplementaryServiceCode(char[] number) {
      return number != null && number.length > 0 && (number[0] == '*' || number[0] == '#') && number[number.length - 1] == '#';
   }

   public static boolean isSupplementaryServiceCode(byte[] number) {
      return number != null && number.length > 0 && (number[0] == 42 || number[0] == 35) && number[number.length - 1] == 35;
   }

   public static String extractNumberFromSSC(String number) {
      char[] num = number.toCharArray();
      if (isSupplementaryServiceCode(num)) {
         boolean foundSSC = false;
         boolean foundPreTags = false;
         boolean foundMidTags = false;
         int length = num.length;

         for (int i = 0; i < length; i++) {
            char c = num[i];
            if (c != '*' && c != '#') {
               if (isDigit(c)) {
                  if (foundPreTags && !foundMidTags) {
                     foundSSC = true;
                  } else if (foundMidTags && i < length - 1) {
                     return number.substring(i, length - 2);
                  }
               }
            } else if (foundSSC) {
               foundMidTags = true;
            } else {
               foundPreTags = true;
            }
         }
      }

      return null;
   }

   private static boolean isValidExtensionLength(int digitCount, boolean canPrompt) {
      SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
      int length = smartDialingOptions.getCorporateExtensionLength();
      if (length != 0) {
         return digitCount == length;
      }

      int[] exclusions = smartDialingOptions.getCorporateExtensionLengthExclusions();

      for (int idx = 0; idx < exclusions.length; idx++) {
         if (digitCount == exclusions[idx]) {
            return false;
         }
      }

      if (digitCount < 2 || digitCount > 5) {
         return false;
      }

      if (canPrompt && ApplicationRegistry.getApplicationRegistry().get(-4905871728754809133L) != null) {
         String fmtString = PhoneResources.getString(6051);
         Object[] fmtParams = new Object[]{new Object(digitCount)};
         String prompt = MessageFormat.format(fmtString, fmtParams);
         Dialog dialog = (Dialog)(new Object(3, prompt, 0, Bitmap.getPredefinedBitmap(1), 0));
         dialog.setEscapeEnabled(false);
         int result = dialog.doModal();
         if (result == 4) {
            if (getCorporatePhoneNumber(canPrompt).length() > 0) {
               smartDialingOptions.setCorporateExtensionLength(digitCount);
               smartDialingOptions.commit();
            }

            return true;
         } else {
            int size = exclusions.length;
            Array.resize(exclusions, size + 1);
            exclusions[size] = digitCount;
            smartDialingOptions.setCorporateExtensionLengthExclusions(exclusions);
            smartDialingOptions.commit();
            return false;
         }
      } else {
         return false;
      }
   }

   private static boolean isAllDigits(StringBuffer buffer) {
      int len = buffer.length();

      for (int idx = 0; idx < len; idx++) {
         char ch = buffer.charAt(idx);
         if (!Character.isDigit(ch)) {
            return false;
         }
      }

      return true;
   }

   private static boolean isEventThread() {
      try {
         Application app = Application.getApplication();
         if (app != null) {
            return app.isEventThread();
         }
      } finally {
         return false;
      }

      return false;
   }

   private static boolean canDialPlus() {
      return RadioInfo.getActiveWAFs() != 2;
   }

   private static boolean matchDigits(char[] digits, StringBuffer number, int offset) {
      if (digits != null && digits.length != 0) {
         int digitsLen = digits.length;
         if (number.length() - offset < digitsLen) {
            return false;
         }

         for (int idx = 0; idx < digitsLen; idx++) {
            if (number.charAt(idx + offset) != digits[idx]) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private static void stripNonSIMDigits(StringBuffer buffer, boolean allowWildcards) {
      int index = 0;

      while (index < buffer.length()) {
         char c = buffer.charAt(index);
         if (c >= '0' && c <= '9') {
            index++;
         } else {
            switch (c) {
               case '#':
               case '*':
               case '+':
               case ',':
                  index++;
                  break;
               case '?':
                  if (allowWildcards) {
                     index++;
                     break;
                  }
               default:
                  buffer.deleteCharAt(index);
            }
         }
      }
   }

   public static synchronized String convertForBluetooth(PhoneNumberModel model) {
      StringBuffer number = WeakReferenceUtilities.getStringBuffer(_strBufferWR1);
      StringBuffer dtmf = WeakReferenceUtilities.getStringBuffer(_strBufferWR2);
      number.setLength(0);
      dtmf.setLength(0);
      convertForTransmission(number, dtmf, model.getValue().toCharArray(), false, false, true, false, false);
      number.append(dtmf);

      for (int j = number.length() - 1; j >= 0; j--) {
         if (number.charAt(j) == ',') {
            number.setCharAt(j, 'p');
         }
      }

      return number.toString();
   }
}
