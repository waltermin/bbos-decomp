package net.rim.wica.runtime.util;

import java.util.Vector;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.Memory;

public class Util {
   public static final SimpleDateFormat DEFAULT_DATE_FORMATTER = (SimpleDateFormat)(new Object(54));
   private static Util$ProvisioningLowMemoryListener _lmmListener;
   private static final String URL_PREFIX = "http://";

   public static String printBytes(byte[] bytes) {
      StringBuffer output = (StringBuffer)(new Object());
      int numBytes = bytes.length;

      for (int i = 0; i < numBytes; i++) {
         if ((bytes[i] & 255) < 16) {
            output.append('0');
         }

         output.append(Integer.toString(bytes[i] & 255, 16));
      }

      return output.toString();
   }

   public static int arrayFind(Object[] objArray, Object obj) {
      int index = -1;
      if (objArray != null) {
         for (int i = objArray.length - 1; i >= 0 && index == -1; i--) {
            if (objArray[i].equals(obj)) {
               index = i;
            }
         }
      }

      return index;
   }

   public static boolean arrayContains(Object[] objArray, Object obj) {
      return arrayFind(objArray, obj) != -1;
   }

   public static String[] split(String value, String delimiter) {
      int lastIndex = 0;
      Vector strings = null;
      int currentIndex = 0;

      while ((currentIndex = value.indexOf(delimiter, lastIndex)) != -1) {
         if (strings == null) {
            strings = (Vector)(new Object());
         }

         strings.addElement(value.substring(lastIndex, currentIndex));
         lastIndex = currentIndex + delimiter.length();
      }

      if (strings == null) {
         return new Object[]{value};
      }

      strings.addElement(value.substring(lastIndex));
      String[] stringArray = new Object[strings.size()];
      strings.copyInto(stringArray);
      return stringArray;
   }

   public static String[] split(String value, char delimiter) {
      char[] valueChars = value.toCharArray();
      int lastIndex = 0;
      Vector strings = null;

      for (int i = 0; i < valueChars.length; i++) {
         char c = valueChars[i];
         if (c == delimiter) {
            if (strings == null) {
               strings = (Vector)(new Object());
            }

            strings.addElement(new Object(valueChars, lastIndex, i - lastIndex));
            lastIndex = i + 1;
         }
      }

      if (strings == null) {
         return new Object[]{value};
      }

      strings.addElement(new Object(valueChars, lastIndex, valueChars.length - lastIndex));
      String[] stringArray = new Object[strings.size()];
      strings.copyInto(stringArray);
      return stringArray;
   }

   public static String[] splitAndTrim(String value, char delimiter) {
      String[] result = split(value, delimiter);

      for (int i = 0; i < result.length; i++) {
         result[i] = result[i].trim();
      }

      return result;
   }

   public static String[] splitAndTrim(String value, String delimiter) {
      String[] result = split(value, delimiter);

      for (int i = 0; i < result.length; i++) {
         result[i] = result[i].trim();
      }

      return result;
   }

   public static boolean isNonEmptyString(String s) {
      return s != null && s.length() > 0;
   }

   public static boolean isValidURL(String url) {
      if (isNonEmptyString(url)) {
         try {
            new Object(url);
            return true;
         } finally {
            ;
         }
      } else {
         return false;
      }
   }

   public static String filterURL(String url) {
      return isValidURL(url) ? url : ((StringBuffer)(new Object("http://"))).append(url).toString();
   }

   private static synchronized boolean attemptFlashRecovery(int size) {
      boolean retval = true;
      if (size > Memory.getFlashFree()) {
         if (_lmmListener == null) {
            _lmmListener = new Util$ProvisioningLowMemoryListener(null);
         }

         LowMemoryManager.addLowMemoryFailedListener(_lmmListener);

         do {
            net.rim.vm.Memory.recoverFlash(size);
            LowMemoryManager.poll();
         } while (!_lmmListener._lmm_failed && size > Memory.getFlashFree());

         LowMemoryManager.removeLowMemoryFailedListener(_lmmListener);
         retval = !_lmmListener._lmm_failed;
      }

      return retval;
   }

   public static synchronized boolean ensureAvailableFlash(int size) {
      boolean flashRecovered = attemptFlashRecovery(size);
      if (!flashRecovered) {
         net.rim.vm.Memory.emergencyGC();
         flashRecovered = attemptFlashRecovery(size);
      }

      return flashRecovered;
   }

   public static String filterNumber(String text) {
      int textLength = text.length();
      char[] buffer = new char[textLength];
      int newLength = 0;

      for (int lv = 0; lv < textLength; lv++) {
         char character = text.charAt(lv);
         if (character >= '0' && character <= '9' || character == '-' || character == '.') {
            buffer[newLength] = character;
            newLength++;
         }
      }

      return (String)(new Object(buffer, 0, newLength));
   }

   public static boolean isValidNumber(String value) {
      try {
         Double.parseDouble(value);
         return true;
      } finally {
         ;
      }
   }

   public static boolean isValidLong(String value) {
      try {
         Long.parseLong(value);
         return true;
      } finally {
         ;
      }
   }

   public static long convertStringToLong(String value) {
      long longValue = 0;
      if (!value.trim().equals("")) {
         try {
            return Long.parseLong(value, 10);
         } finally {
            longValue = (long)Double.parseDouble(value);
            return longValue;
         }
      } else {
         return longValue;
      }
   }

   public static int convertStringToInt(String value) {
      int intValue = 0;
      if (!value.trim().equals("")) {
         try {
            return Integer.parseInt(value, 10);
         } finally {
            intValue = (int)Double.parseDouble(value);
            return intValue;
         }
      } else {
         return intValue;
      }
   }
}
