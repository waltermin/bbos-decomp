package net.rim.tid.im.conv.europe.repository;

import java.util.Calendar;
import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.DirectConnect;
import net.rim.device.api.system.Phone;
import net.rim.device.api.ui.autotext.AutoText;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.deviceoptions.Owner;
import net.rim.tid.util.Utils;

public class AutotextReader {
   protected static AutoText _autoText = AutoText.getAutoText();

   public static String getAutotextReplacement(String aReplaceString) {
      Object autoTextEntry = _autoText.checkWord(aReplaceString);
      return getAutotextReplacementForEntry(autoTextEntry);
   }

   public static String getAutotextReplacementForEntry(Object aEntry) {
      if (aEntry == null) {
         return null;
      }

      String replacementString = replaceMacros(_autoText.getReplacementStringPattern(aEntry));
      return replacementString.length() == 0 ? null : adjustCase(replacementString, _autoText.getReplacedString(aEntry), _autoText.getReplacementCase(aEntry));
   }

   private static String replaceMacros(String text) {
      int macroIndex = text.indexOf(37);
      if (macroIndex == -1) {
         return text;
      }

      StringBuffer newText = new StringBuffer(text.substring(0, macroIndex));
      int textLength = text.length();

      for (int i = macroIndex; i < textLength; i++) {
         char c = text.charAt(i);
         if (c == '%' && i < textLength - 1) {
            switch (text.charAt(i + 1)) {
               case '%':
                  newText.append('%');
                  i++;
                  break;
               case 'B':
                  i++;
                  break;
               case 'D':
                  DateFormat.getInstance(40).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'O':
                  newText.append(Owner.getOwnerInfo());
                  i++;
                  break;
               case 'P':
                  newText.append(Integer.toHexString(DeviceInfo.getDeviceId()));
                  i++;
                  break;
               case 'T':
                  DateFormat.getInstance(5).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'U':
                  if (DirectConnect.isSupported()) {
                     newText.append(DirectConnect.getUFMI());
                     i++;
                  } else {
                     newText.append(c);
                  }
                  break;
               case 'V':
                  String insertion = DeviceInfo.getDeviceName() + '/' + ApplicationDescriptor.currentApplicationDescriptor().getVersion();
                  newText.append(insertion);
                  i++;
                  break;
               case 'b':
                  int newTextLength = newText.length();
                  if (newTextLength > 0) {
                     newText.deleteCharAt(newTextLength - 1);
                  }

                  i++;
                  break;
               case 'd':
                  DateFormat.getInstance(56).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               case 'o':
                  newText.append(Owner.getOwnerName());
                  i++;
                  break;
               case 'p':
                  label74:
                  try {
                     String phoneNumber = Phone.getInstance().getNumber(0);
                     if (phoneNumber != null) {
                        newText.append(phoneNumber);
                     }
                  } finally {
                     break label74;
                  }

                  i++;
                  break;
               case 't':
                  DateFormat.getInstance(7).format(Calendar.getInstance(), newText, null);
                  i++;
                  break;
               default:
                  newText.append(c);
            }
         } else {
            newText.append(c);
         }
      }

      return newText.toString();
   }

   private static String adjustCase(String replacement, String original, int caseType) {
      if (replacement.length() == 0) {
         return "";
      }

      String formattedReplacement = replacement;
      switch (caseType) {
         default:
            if (isAllUpperCase(original)) {
               return Utils.toUpperCase(replacement);
            } else if (CharacterUtilities.isUpperCase(original.charAt(0))) {
               StringBuffer formatBuffer = new StringBuffer(replacement);
               formatBuffer.setCharAt(0, Utils.toUpperCase(replacement.charAt(0)));
               formattedReplacement = formatBuffer.toString();
            }
         case 1:
            return formattedReplacement;
      }
   }

   private static boolean isAllUpperCase(String s) {
      int len = s.length();
      if (len == 0 || CharacterUtilities.isLowerCase(s.charAt(0))) {
         return false;
      } else {
         return CharacterUtilities.isLowerCase(s.charAt(len - 1)) ? false : s.equals(Utils.toUpperCase(s));
      }
   }
}
