package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.util.StringUtilities;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public class PhoneNumberUtilities {
   private static WeakReference _workBufferWR = new WeakReference(null);

   public static String cleanPhoneNumber(String original) {
      if (original == null) {
         return null;
      }

      StringBuffer _workBuffer = WeakReferenceUtilities.getStringBuffer(_workBufferWR);
      synchronized (_workBuffer) {
         if (cleanPhoneNumber(original, _workBuffer)) {
            original = _workBuffer.toString();
         }

         return original;
      }
   }

   private static boolean validCharacter(char c, int index) {
      return Character.isDigit(c) ? true : c == '#' || c == '*' || c == '+';
   }

   private static boolean cleanPhoneNumber(String phoneNumber, StringBuffer stringBuffer) {
      boolean invalidCharFound = false;
      if (phoneNumber.length() == 0) {
         return false;
      }

      synchronized (stringBuffer) {
         int count = phoneNumber.length();
         stringBuffer.setLength(0);
         int i = 0;
         int numLeadingWhitespaceChars = 0;

         while (phoneNumber.charAt(i) == ' ') {
            i++;
         }

         numLeadingWhitespaceChars = i;

         while (i < count) {
            char c = phoneNumber.charAt(i);
            if (validCharacter(c, i - numLeadingWhitespaceChars)) {
               stringBuffer.append(c);
            } else {
               invalidCharFound = true;
            }

            i++;
         }

         return invalidCharFound;
      }
   }

   public static int computeReverseLookupHashCode(String phoneNumber) {
      StringBuffer _workBuffer = WeakReferenceUtilities.getStringBuffer(_workBufferWR);
      synchronized (_workBuffer) {
         cleanPhoneNumber(phoneNumber, _workBuffer);
         return StringUtilities.computeHashCode(_workBuffer);
      }
   }
}
