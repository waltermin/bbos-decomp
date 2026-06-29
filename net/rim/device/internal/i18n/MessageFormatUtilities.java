package net.rim.device.internal.i18n;

import net.rim.device.api.i18n.MessageFormat;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.vm.Array;

public final class MessageFormatUtilities {
   public static final RichTextField getBoldArgumentField(String pattern, String[] arguments) {
      return getBoldArgumentField(pattern, arguments, 45035996273704960L);
   }

   public static final RichTextField getBoldArgumentField(String pattern, String[] arguments, long richTextStyle) {
      int[] offsets = new int[0];
      byte[] attributes = new byte[0];
      Font[] fonts = new Font[0];
      String message = getParametersForBoldArgumentField(pattern, arguments, offsets, attributes, fonts);
      return new RichTextField(message, offsets, attributes, fonts, richTextStyle);
   }

   public static final String getParametersForBoldArgumentField(String pattern, String[] arguments, int[] offsets, byte[] attributes, Font[] fonts) {
      if (fonts == null) {
         throw new IllegalArgumentException();
      }

      Array.resize(fonts, 2);
      fonts[0] = Font.getDefault();
      fonts[1] = Font.getDefault().derive(1);
      return getParametersForField(pattern, arguments, offsets, attributes);
   }

   private static final String getParametersForField(String pattern, String[] arguments, int[] offsets, byte[] attributes) {
      if (pattern != null && arguments != null && offsets != null && attributes != null) {
         MessageFormat messageFormat = new MessageFormat(pattern);
         String appliedPattern = messageFormat.format(null, new StringBuffer(), null).toString();
         int numArguments = arguments.length;
         int numOffsets = numArguments * 2 + 2;
         Array.resize(offsets, numOffsets);
         offsets[0] = 0;
         Array.resize(attributes, numOffsets - 1);
         attributes[0] = 0;
         int currentOffset = 1;
         int currentTotalArgumentLengthDifference = 0;

         for (int i = 0; i < numArguments; i++) {
            String argument = arguments[i];
            String argumentPlaceholder = "{" + i + '}';
            int argumentPlaceholderOffset = appliedPattern.indexOf(argumentPlaceholder);
            if (argumentPlaceholderOffset == -1) {
               throw new IllegalArgumentException();
            }

            int argumentLength = argument.length();
            int startOffset = argumentPlaceholderOffset + currentTotalArgumentLengthDifference;
            int endOffset = startOffset + argumentLength;
            currentTotalArgumentLengthDifference += argumentLength - argumentPlaceholder.length();
            if (startOffset == 0) {
               Array.resize(offsets, offsets.length - 1);
               Array.resize(attributes, attributes.length - 1);
               currentOffset = 0;
            }

            offsets[currentOffset] = startOffset;
            attributes[currentOffset++] = 1;
            offsets[currentOffset] = endOffset;
            attributes[currentOffset++] = 0;
         }

         offsets[currentOffset] = appliedPattern.length() + currentTotalArgumentLengthDifference;
         return messageFormat.format(arguments);
      } else {
         throw new IllegalArgumentException();
      }
   }
}
