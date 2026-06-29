package net.rim.device.apps.internal.browser.css;

import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;

public final class CSSString {
   private String _source;
   private int _startIndex;
   private int _endIndex;
   private int _length;

   public CSSString(String source, int startIndex, int endIndex) {
      if (StringUtilities.indexOf(source, '\\', startIndex, endIndex) == -1) {
         this._source = source;
         this._startIndex = startIndex;
         this._endIndex = endIndex;
         this._length = endIndex - startIndex;
      } else {
         this._source = this.decode(source, startIndex, endIndex);
         this._startIndex = 0;
         this._endIndex = this._source.length();
         this._length = this._endIndex;
      }
   }

   private final String decode(String source, int startIndex, int endIndex) {
      StringBuffer buffer = new StringBuffer();
      int i = startIndex;

      while (i < endIndex) {
         char c = source.charAt(i++);
         if (c != '\\') {
            buffer.append(c);
         } else if (i < endIndex) {
            c = source.charAt(i++);
            if (CSSUtilities.isNewLineCharacter(c)) {
               buffer.append('\n');
               if (c == '\r' && i < endIndex && source.charAt(i) == '\n') {
                  i++;
               }
            } else if (!CSSUtilities.isHexadecimalCharacter(c)) {
               if (CSSUtilities.isNonAsciiCharacter(c) || c >= ' ' && c <= '~') {
                  buffer.append(c);
               }
            } else {
               int hexStart = i - 1;

               while (i < endIndex && i < hexStart + 6 && CSSUtilities.isHexadecimalCharacter(source.charAt(i))) {
                  i++;
               }

               buffer.append((char)NumberUtilities.parseInt(source, hexStart, i, 16));
               if (i < endIndex) {
                  c = source.charAt(i);
                  if (CSSUtilities.isWhitespaceCharacter(c)) {
                     i++;
                     if (c == '\r' && i < endIndex && source.charAt(i) == '\n') {
                        i++;
                     }
                  }
               }
            }
         }
      }

      return buffer.toString();
   }

   public final int getLength() {
      return this._length;
   }

   public final String getString() {
      if (this._startIndex == 0 && this._endIndex == this._source.length()) {
         return this._source;
      }

      this._source = this._source.substring(this._startIndex, this._endIndex);
      this._startIndex = 0;
      this._endIndex = this._length;
      return this._source;
   }

   @Override
   public final String toString() {
      return this.getString();
   }

   @Override
   public final boolean equals(Object object) {
      return this.equals(object, false);
   }

   public final boolean equalsIgnoreCase(Object object) {
      return this.equals(object, true);
   }

   private final boolean equals(Object object, boolean ignoreCase) {
      if (object == this) {
         return true;
      }

      String source;
      int startIndex;
      int length;
      if (!(object instanceof CSSString)) {
         if (!(object instanceof String)) {
            return false;
         }

         String string = (String)object;
         source = string;
         startIndex = 0;
         length = string.length();
      } else {
         CSSString cssString = (CSSString)object;
         source = cssString._source;
         startIndex = cssString._startIndex;
         length = cssString._length;
         if (source == this._source && startIndex == this._startIndex && length == this._length) {
            return true;
         }
      }

      return this._length == length && StringUtilities.regionMatches(this._source, ignoreCase, this._startIndex, source, startIndex, this._length, 1701707776);
   }

   public final boolean startsWith(String value, boolean ignoreCase) {
      return StringUtilities.regionMatches(this._source, ignoreCase, this._startIndex, value, 0, Math.min(value.length(), this._length), 1701707776);
   }

   public final char charAt(int index) {
      return this._source.charAt(this._startIndex + index);
   }

   @Override
   public final int hashCode() {
      return StringUtilities.hashCode(this._source, this._startIndex, this._endIndex, false);
   }
}
