package net.rim.device.internal.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.component.RichTextField;

public final class RichTextFieldUtilities {
   private static final char MODIFICATION_FLAG;
   private static final byte FONT_REGULAR;
   private static final byte FONT_MODIFIED;

   public static final RichTextField getBoldFormattedRichTextField(String text) {
      return getBoldFormattedRichTextField(text, 0);
   }

   public static final RichTextField getBoldFormattedRichTextField(String text, long style) {
      Font defaultFont = Font.getDefault();
      Font[] fontFormats = new Font[]{null, defaultFont.derive(defaultFont.getStyle() | 1)};
      return getRichTextField(text, style, fontFormats);
   }

   private static final RichTextField getRichTextField(String text, long style, Font[] fontFormats) {
      int textLength = text.length();
      char[] chars = text.toCharArray();
      int sections = 0;
      int sectionLength = 0;

      for (int i = 0; i < textLength; i++) {
         if (chars[i] == '*') {
            if (sectionLength > 0) {
               sections++;
            }

            sectionLength = 0;
         } else {
            sectionLength++;
         }
      }

      if (sectionLength > 0) {
         sections++;
      }

      int[] formatOffsets = new int[sections + 1];
      byte[] formatIndices = new byte[sections];
      int readOffset = 0;
      int writeOffset = 0;
      int formatSection = 0;
      boolean isModified = false;
      formatOffsets[0] = 0;
      sectionLength = 0;

      while (readOffset < textLength) {
         char data = chars[readOffset++];
         if (data == '*') {
            if (sectionLength > 0) {
               formatIndices[formatSection] = (byte)(isModified ? 1 : 0);
               formatOffsets[++formatSection] = writeOffset;
            }

            sectionLength = 0;
            isModified = !isModified;
         } else {
            chars[writeOffset++] = data;
            sectionLength++;
         }
      }

      if (sectionLength > 0) {
         formatIndices[formatSection] = (byte)(isModified ? 1 : 0);
         formatOffsets[formatSection + 1] = writeOffset;
      }

      return new RichTextField(new String(chars, 0, writeOffset), formatOffsets, formatIndices, fontFormats, style);
   }
}
