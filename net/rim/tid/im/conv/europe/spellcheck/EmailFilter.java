package net.rim.tid.im.conv.europe.spellcheck;

import net.rim.device.internal.ui.StringBufferGap;

public class EmailFilter implements ITextFilter {
   private int _checkedPosition = -1;

   @Override
   public int filter(StringBufferGap aText, int aStart, int aEnd, boolean aBackwards) {
      int len = aText.length();
      if (aStart == len) {
         if (!aBackwards) {
            return 0;
         }

         aStart--;
      }

      int index = aStart;
      boolean has_column = false;

      while (index > 0 && index > this._checkedPosition && aText.charAt(index - 1) != '\n') {
         has_column = has_column || aText.charAt(index) == ':';
         index--;
      }

      if (index <= this._checkedPosition) {
         this._checkedPosition = aEnd;
         return 0;
      }

      this._checkedPosition = aEnd;
      String original = "Original Message---";
      boolean skip_line = false;
      boolean skip_header = false;
      int orig_len = 0;
      if (aText.charAt(index) == '>') {
         skip_line = true;
      } else {
         while (index > 0 && has_column) {
            has_column = false;
            index--;

            while (index > 0 && aText.charAt(index - 1) != '\n') {
               has_column = has_column || aText.charAt(index) == ':';
               index--;
            }
         }

         while (index < len && aText.charAt(index) == '-') {
            index++;
            orig_len++;
         }

         if (orig_len >= 3) {
            skip_header = true;

            for (int i = 0; i < original.length(); i++) {
               if (index + i >= len || original.charAt(i) != aText.charAt(index + i)) {
                  skip_header = false;
                  break;
               }
            }

            if (skip_header) {
               while (index < len && aText.charAt(index) == '-') {
                  index++;
                  orig_len++;
               }
            }
         }
      }

      if (!skip_line && !skip_header) {
         return 0;
      }

      if (!aBackwards) {
         if (skip_line) {
            index = aStart;

            while (index < len && aText.charAt(index) != '\n') {
               index++;
            }
         } else {
            index += original.length() + orig_len;
            has_column = true;

            while (index < len && has_column) {
               has_column = false;
               int store_index = ++index;

               while (index < len && aText.charAt(index) != '\n') {
                  has_column = has_column || aText.charAt(index) == ':';
                  index++;
               }

               if (!has_column) {
                  index = store_index;
               }
            }
         }
      }

      this._checkedPosition = index;
      return index - aStart;
   }

   @Override
   public void reset() {
      this._checkedPosition = -1;
   }
}
