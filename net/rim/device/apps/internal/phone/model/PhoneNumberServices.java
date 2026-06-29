package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.device.apps.api.addressbook.AddressBookServices;
import net.rim.vm.Array;
import net.rim.vm.WeakReference;

public class PhoneNumberServices {
   private static WeakReference _workBufferWR = new WeakReference(null);
   private static WeakReference _byteBufferWR = new WeakReference(null);

   public static char getMnemonicKeyMapping(char key) {
      char lowerKey = CharacterUtilities.toLowerCase(key, 1701707776);
      switch (lowerKey) {
         case 'a':
         case 'b':
         case 'c':
         case 'α':
         case 'β':
         case 'γ':
            return '2';
         case 'd':
         case 'e':
         case 'f':
         case 'δ':
         case 'ε':
         case 'ζ':
            return '3';
         case 'g':
         case 'h':
         case 'i':
         case 'η':
         case 'θ':
         case 'ι':
            return '4';
         case 'j':
         case 'k':
         case 'l':
         case 'κ':
         case 'λ':
         case 'μ':
            return '5';
         case 'm':
         case 'n':
         case 'o':
         case 'ν':
         case 'ξ':
         case 'ο':
            return '6';
         case 'p':
         case 'q':
         case 'r':
         case 's':
         case 'π':
         case 'ρ':
         case 'ς':
         case 'σ':
            return '7';
         case 't':
         case 'u':
         case 'v':
         case 'τ':
         case 'υ':
         case 'φ':
            return '8';
         case 'w':
         case 'x':
         case 'y':
         case 'z':
         case 'χ':
         case 'ψ':
         case 'ω':
            return '9';
         default:
            return 'ÿ';
      }
   }

   public static boolean isDTMFKey(char key) {
      return Character.isDigit(key) || key == '#' || key == '*';
   }

   public static int getReverseLookupCode(byte[] bytes, int start, int length) {
      byte[] _byteBuffer = WeakReferenceUtilities.getByteArray(_byteBufferWR, 32);
      synchronized (_byteBuffer) {
         if (length > _byteBuffer.length) {
            Array.resize(_byteBuffer, length);
         }

         int count = 0;
         int end = start + length;

         for (int idx = start; idx < end; idx++) {
            char ch = (char)bytes[idx];
            if (isDTMFKey(ch) || ch == ',' || ch == '!') {
               _byteBuffer[count++] = (byte)ch;
            } else if (ch >= 'A' && ch <= 'Z') {
               _byteBuffer[count++] = (byte)getMnemonicKeyMapping(ch);
            }
         }

         return AddressBookServices.getReverseLookupCode(_byteBuffer, 0, count);
      }
   }

   static String convertInternalToDisplay(String text) {
      if (text != null && text.length() != 0) {
         boolean convertedChar = false;
         StringBuffer _workBuffer = WeakReferenceUtilities.getStringBuffer(_workBufferWR);
         synchronized (_workBuffer) {
            _workBuffer.setLength(0);
            int length = text.length();

            for (int i = 0; i < length; i++) {
               char internalChar = text.charAt(i);
               char displayChar = convertCharToDisplay(internalChar);
               _workBuffer.append(displayChar);
               if (displayChar != internalChar) {
                  convertedChar = true;
               }
            }
         }

         return convertedChar ? _workBuffer.toString() : text;
      } else {
         return text;
      }
   }

   public static String convertDisplayToInternal(String text) {
      if (text != null && text.length() != 0) {
         boolean convertedChar = false;
         StringBuffer _workBuffer = WeakReferenceUtilities.getStringBuffer(_workBufferWR);
         synchronized (_workBuffer) {
            _workBuffer.setLength(0);
            int length = text.length();

            for (int i = 0; i < length; i++) {
               char displayChar = text.charAt(i);
               char internalChar = convertCharToInternal(displayChar);
               _workBuffer.append(internalChar);
               if (internalChar != displayChar) {
                  convertedChar = true;
               }
            }
         }

         return convertedChar ? _workBuffer.toString() : text;
      } else {
         return text;
      }
   }

   public static String convertForDisplayWithExtension(String text, boolean trimPausesAndWaits) {
      String displayText = convertInternalToDisplay(text);
      if (trimPausesAndWaits && displayText != null) {
         int idxPause = displayText.indexOf(62462, 1);
         int idxWait = displayText.indexOf(62466, 1);
         if (idxPause >= 0 && (idxWait < 0 || idxWait > idxPause)) {
            return displayText.substring(0, idxPause);
         }

         if (idxWait >= 0) {
            return displayText.substring(0, idxWait);
         }
      }

      return displayText;
   }

   public static char convertCharToInternal(char displayChar) {
      char internalChar = displayChar;
      switch (displayChar) {
         case '\uf3fe':
            return ',';
         case '\uf402':
            internalChar = '!';
         default:
            return internalChar;
      }
   }

   public static char convertCharToDisplay(char internalChar) {
      char displayChar = internalChar;
      switch (internalChar) {
         case '!':
            displayChar = '\uf402';
         default:
            return displayChar;
         case ',':
            return '\uf3fe';
      }
   }

   public static String stripPlusSign(String number) {
      return number != null && number.length() > 0 && number.charAt(0) == '+' ? number.substring(1, number.length()) : number;
   }
}
