package net.rim.device.internal.ui.autotext;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;
import net.rim.device.api.system.DirectConnect;
import net.rim.vm.Array;

final class AutoTextMacro {
   private static final char MACRO_SYM = '%';
   private static final char SHORT_DATE_CHAR = 'd';
   private static final char LONG_DATE_CHAR = 'D';
   private static final char SHORT_TIME_CHAR = 't';
   private static final char LONG_TIME_CHAR = 'T';
   private static final char OWNER_NAME_CHAR = 'o';
   private static final char OWNER_INFO_CHAR = 'O';
   private static final char BACKSPACE_CHAR = 'b';
   private static final char DELETE_CHAR = 'B';
   private static final char PHONE_NUMBER_CHAR = 'p';
   private static final char DEVICE_UFMI_CHAR = 'U';
   private static final char DEVICE_PIN_CHAR = 'P';
   private static final char PERCENT_CHAR = '%';
   private static final char VERSION_CHAR = 'V';
   private static final char[] MACRO_CHARS = new char[]{
      'd', 'D', 't', 'T', 'o', 'O', 'p', (char)(DirectConnect.isSupported() ? 'U' : '\u0000'), 'P', 'b', 'B', '%', 'V'
   };

   private AutoTextMacro() {
   }

   static final String getMacroRep(int type) {
      int numTypes = getMacroChoices().length;
      if (type >= 0 && type < numTypes) {
         int index = 0;

         while (type > 0) {
            index++;
            type--;

            while (MACRO_CHARS[index] == 0) {
               index++;
            }
         }

         char[] a = new char[]{'%', MACRO_CHARS[index]};
         return new String(a);
      } else {
         throw new ArrayIndexOutOfBoundsException("There are " + numTypes + " macro indices. Please enter a number in the interval [0, " + (numTypes - 1) + "]");
      }
   }

   static final String[] getMacroChoices() {
      ResourceBundleFamily family = ResourceBundle.getBundle(8562590855522002223L, "net.rim.device.internal.resource.Input");
      String[] entries = (String[])family.getObject(16, true);
      String[] choices = new String[entries.length];
      int count = 0;

      for (int i = 0; i < choices.length; i++) {
         if (MACRO_CHARS[i] != 0) {
            byte[] a = new byte[]{32, 40, 37, (byte)MACRO_CHARS[i], 41};
            choices[count++] = entries[i] + new String(a);
         }
      }

      if (count < choices.length) {
         Array.resize(choices, count);
      }

      return choices;
   }
}
