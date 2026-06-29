package net.rim.device.apps.internal.browser.markup;

public final class CharArrayHelper {
   private int _length;
   private char[] _contents;
   private boolean _isMultibyte;
   private boolean _appendNewLineOnNextCall;
   private boolean _coalesceAcrossAppend = true;
   private boolean _disallowObjectReplaceChar;

   public CharArrayHelper() {
      this._contents = new char[50];
   }

   public final void setDisallowObjectReplaceChar(boolean value) {
      this._disallowObjectReplaceChar = value;
   }

   public final int getLength() {
      return this._length;
   }

   public final void setLength(int value) {
      this._length = value;
   }

   public final void reset() {
      this._length = 0;
      this._isMultibyte = false;
      this._appendNewLineOnNextCall = false;
   }

   public final void setCoalescingAcrossAppendingMode(boolean appendingMode) {
      this._coalesceAcrossAppend = appendingMode;
   }

   public final char[] getContents() {
      return this._contents;
   }

   public final void append(char[] chars, boolean multibyte) {
      this.append(chars, chars.length, multibyte);
   }

   public final void append(CharArrayHelper other) {
      this.append(other._contents, other._length, other._isMultibyte);
   }

   public final void appendWhitespace(char whitespace, boolean coalesce) {
      if (this._coalesceAcrossAppend && this._appendNewLineOnNextCall) {
         this._appendNewLineOnNextCall = false;
         this.append('\n');
      }

      boolean newline = false;
      switch (whitespace) {
         case '\t':
         case ' ':
         case ' ':
            newline = false;
            break;
         case '\n':
         case '\f':
         case '\r':
            newline = true;
            break;
         default:
            this.append(whitespace);
            return;
      }

      if (this._length != 0) {
         if (!coalesce) {
            if (!newline) {
               this.append(' ');
            } else {
               int last = this._length - 1;
               switch (this._contents[last]) {
                  case '\t':
                  case ' ':
                  case ' ':
                     this._contents[last] = '\n';
                     return;
                  default:
                     if (this._coalesceAcrossAppend) {
                        this._appendNewLineOnNextCall = true;
                     } else {
                        this.append('\n');
                     }
               }
            }
         } else {
            int start = this._length;
            int last = -1;

            for (int index = this._length - 1; index >= 0; index--) {
               char value = this._contents[index];
               if (value != '\n' && value != '\r' && value != '\f') {
                  if (value != ' ' && value != '\t' && value != 160) {
                     break;
                  }

                  start--;
               } else {
                  last = index;
                  start--;
               }
            }

            if (start == this._length) {
               if (newline) {
                  if (this._coalesceAcrossAppend) {
                     this._appendNewLineOnNextCall = true;
                  } else {
                     this.append('\n');
                  }
               } else {
                  this.append(' ');
               }
            } else if (newline) {
               this._contents[start] = '\n';
               this._length = start + 1;
            } else {
               this._length = last != -1 ? last + 1 : start + 1;
            }
         }
      }
   }

   private final void ensureCapacity(int length) {
      int remaining = this._contents.length - this._length;
      if (length > remaining) {
         char[] newContents = new char[this._contents.length + length + 100];
         System.arraycopy(this._contents, 0, newContents, 0, this._length);
         this._contents = newContents;
      }
   }

   private final void append(char[] other, int length, boolean isMultibyte) {
      if (isMultibyte) {
         this._isMultibyte = true;
      }

      this.ensureCapacity(length + 1);
      if (this._coalesceAcrossAppend && this._appendNewLineOnNextCall) {
         this._contents[this._length++] = '\n';
         this._appendNewLineOnNextCall = false;
      }

      System.arraycopy(other, 0, this._contents, this._length, length);
      this._length += length;
   }

   public final void append(char value) {
      if (value > 255) {
         this._isMultibyte = true;
      }

      if (this._disallowObjectReplaceChar && value == '￼') {
         value = 8203;
      }

      this.ensureCapacity(10);
      if (this._coalesceAcrossAppend && this._appendNewLineOnNextCall) {
         this._contents[this._length++] = '\n';
         this._appendNewLineOnNextCall = false;
      }

      this._contents[this._length++] = value;
   }

   public final char charAt(int index) {
      return this._contents[index];
   }

   public final void setCharAt(int index, char c) {
      this._contents[index] = c;
   }

   @Override
   public final String toString() {
      return new String(this._contents, 0, this._length);
   }

   public final void trim() {
      char[] trimmedContents = new char[this._length];
      System.arraycopy(this._contents, 0, trimmedContents, 0, this._length);
      this._contents = trimmedContents;
   }

   public final void trimAndRemoveWhitespaceFromHead() {
      int startPos = 0;

      for (int i = 0; i < this._length; i++) {
         switch (this._contents[i]) {
            case '\t':
            case '\n':
            case '\f':
            case '\r':
            case ' ':
               startPos++;
               break;
            default:
               i = this._length;
         }
      }

      char[] trimmedContents = new char[this._length - startPos];
      System.arraycopy(this._contents, startPos, trimmedContents, 0, trimmedContents.length);
      this._contents = trimmedContents;
      this._length = trimmedContents.length;
   }

   public final boolean isMultibyte() {
      return this._isMultibyte;
   }
}
