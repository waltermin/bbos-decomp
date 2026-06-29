package net.rim.device.apps.internal.qm.peer.common;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.TextMetrics;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.system.InternalServices;

public final class QmUtil {
   private static TextMetrics _metrics = (TextMetrics)(new Object());
   public static final int HW_LAYOUT_REDUCED;
   public static final int HW_LAYOUT_REDUCED_24;

   public static final boolean isReducedKeyboard() {
      return InternalServices.isReducedFormFactor();
   }

   public static final boolean isASCII(String text) {
      int len = text.length();

      for (int i = len - 1; i >= 0; i--) {
         char c = text.charAt(i);
         if (c > 127) {
            return false;
         }
      }

      return true;
   }

   public static final boolean isEmpty(String text) {
      if (text != null && text.length() != 0) {
         int len = text.length();

         for (int i = len - 1; i >= 0; i--) {
            if (!CharacterUtilities.isSpaceChar(text.charAt(i))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   public static final int calculateTrueFontHeight(String text) {
      if (text != null && !isASCII(text)) {
         Font font = Font.getDefault();
         int baseline = font.getBaseline();
         synchronized (_metrics) {
            String name = text;
            font.measureText(name, 0, name.length(), null, _metrics);
            int above = Math.max(-_metrics.iBoundsTlY, baseline);
            int below = Math.max(_metrics.iBoundsBrY, font.getDescent());
            return Math.max(font.getHeight(), above + below);
         }
      } else {
         return Font.getDefault().getHeight();
      }
   }

   public static final int calculateCharacterDecoratorVerticalOffset(String text) {
      if (text != null && !isASCII(text)) {
         Font font = Font.getDefault();
         int baseline = font.getBaseline();
         TextMetrics metrics = (TextMetrics)(new Object());
         String name = text;
         font.measureText(name, 0, name.length(), null, metrics);
         int above = Math.max(-metrics.iBoundsTlY, baseline);
         return above - baseline;
      } else {
         return 0;
      }
   }

   public static final Object encodeString(String s) {
      return PersistentContent.encode(s, true, true);
   }

   public static final String decodeString(Object o) {
      return decodeString(o, true);
   }

   public static final String decodeString(Object o, boolean defaultString) {
      try {
         return PersistentContent.decodeString(o);
      } finally {
         return defaultString ? "<Content Protection is enabled>" : null;
      }
   }
}
