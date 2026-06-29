package net.rim.device.apps.internal.browser.markup;

public final class CharacterEntitiesParser {
   private final CharArrayHelper _mainBuffer = new CharArrayHelper();
   private int _mainIndex;
   private final CharArrayHelper _auxiliaryBuffer = new CharArrayHelper();

   public CharacterEntitiesParser() {
      this._mainBuffer.setDisallowObjectReplaceChar(true);
      this._auxiliaryBuffer.setDisallowObjectReplaceChar(true);
   }

   public final void parse(char[] text, int offset, int length) {
      this._mainBuffer.reset();
      this._auxiliaryBuffer.reset();
      if (text != null && length > 0) {
         this.readStart(text, offset, length);
      }
   }

   public final void readStart(char[] text, int offset, int length) {
      boolean readCR = false;

      while (offset < length) {
         char item = text[offset++];
         if (readCR) {
            if (item != '\n') {
               this._mainBuffer.append('\n');
            }

            readCR = false;
         }

         switch (item) {
            case '\r':
               readCR = true;
               break;
            case '&':
               this.actionAppendAndMark(item);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            default:
               this._mainBuffer.append(item);
         }
      }
   }

   public final void readNumericOrSymbolic(char[] text, int offset, int length) {
      if (offset < length) {
         char item = text[offset++];
         switch (item) {
            case '"':
            case '$':
            case '%':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
               this.readStart(text, offset - 1, length);
               return;
            case '#':
            default:
               this._mainBuffer.append(item);
               this.readHexOrDecimal(text, offset, length);
               return;
            case '&':
               this.actionAppendAndMark(item);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
               this._auxiliaryBuffer.append(item);
               this.readSymbolic(text, offset, length);
         }
      }
   }

   public final void readHexOrDecimal(char[] text, int offset, int length) {
      if (offset < length) {
         char item = text[offset++];
         switch (item) {
            case '&':
               this.actionReplaceNumeric(item, 10);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               this._auxiliaryBuffer.append(item);
               this.readDecimal(text, offset, length);
               return;
            case 'X':
            case 'x':
               this._mainBuffer.append(item);
               this.readHex(text, offset, length);
               return;
            default:
               this.actionAppendAndRemoveIndex(item);
               this.readStart(text, offset, length);
         }
      }
   }

   public final void readSymbolic(char[] text, int offset, int length) {
      while (offset < length) {
         char item = text[offset++];
         switch (item) {
            case '%':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
               this.resolveFromSymbol();
               this.actionAppendAndRemoveIndex(item);
               this.readStart(text, offset, length);
               return;
            case '&':
               this.actionReplaceSymbolic(item);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
               this._auxiliaryBuffer.append(item);
               break;
            case ';':
            default:
               this.actionReplaceSymbolic(item);
               this.readStart(text, offset, length);
               return;
         }
      }

      this.resolveFromSymbol();
   }

   public final void readDecimal(char[] text, int offset, int length) {
      int startOffset = offset;

      while (offset < length) {
         char item = text[offset++];
         switch (item) {
            case '%':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case ':':
               if (offset != startOffset + 1) {
                  this.actionReplaceNumeric(item, 10);
               } else {
                  this.actionAppendAndRemoveIndex(item);
               }

               this.readStart(text, offset, length);
               return;
            case '&':
               this.actionReplaceNumeric(item, 10);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
               this._auxiliaryBuffer.append(item);
               break;
            case ';':
            default:
               this.actionReplaceNumeric(item, 10);
               this.readStart(text, offset, length);
               return;
         }
      }

      this.resolveFromNumeric(10);
   }

   public final void readHex(char[] text, int offset, int length) {
      int startOffset = offset;

      while (offset < length) {
         char item = text[offset++];
         switch (item) {
            case '&':
               this.actionReplaceNumeric(item, 16);
               this.readNumericOrSymbolic(text, offset, length);
               return;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
               this._auxiliaryBuffer.append(item);
               break;
            case ';':
               this.actionReplaceNumeric(item, 16);
               this.readStart(text, offset, length);
               return;
            default:
               if (offset != startOffset + 1) {
                  this.actionReplaceNumeric(item, 16);
               } else {
                  this.actionAppendAndRemoveIndex(item);
               }

               this.readStart(text, offset, length);
               return;
         }
      }

      this.resolveFromNumeric(16);
   }

   public final CharArrayHelper getRawResult() {
      return this._mainBuffer;
   }

   public final String getResult() {
      return this._mainBuffer.toString();
   }

   public final boolean isResultMultibyte() {
      return this._mainBuffer.isMultibyte();
   }

   private final void actionAppendAndMark(char character) {
      this._mainIndex = this._mainBuffer.getLength();
      this._mainBuffer.append(character);
   }

   private final void actionAppendAndRemoveIndex(char character) {
      this._mainIndex = -1;
      this._mainBuffer.append(this._auxiliaryBuffer);
      this._mainBuffer.append(character);
      this._auxiliaryBuffer.reset();
   }

   private final void actionReplaceSymbolic(char character) {
      boolean result = this.resolveFromSymbol();
      if (character == '&') {
         this.actionAppendAndMark(character);
      } else {
         if (!result) {
            this._mainBuffer.append(character);
         }
      }
   }

   private final void actionReplaceNumeric(char character, int base) {
      boolean result = this.resolveFromNumeric(base);
      if (character == '&') {
         this.actionAppendAndMark(character);
      } else {
         if (!result) {
            this._mainBuffer.append(character);
         }
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final boolean resolveFromNumeric(int base) {
      boolean result = false;
      if (this._auxiliaryBuffer.getLength() > 0 && this._mainIndex != -1) {
         String symbol = this._auxiliaryBuffer.toString();
         boolean var6 = false /* VF: Semaphore variable */;

         label59:
         try {
            var6 = true;
            char aNumberFormatException = (char)Integer.parseInt(symbol, base);
            switch (aNumberFormatException) {
               case '\u007f':
               case '\u0081':
               case '\u008d':
               case '\u008f':
               case '\u0090':
               case '\u009d':
                  break;
               case '\u0080':
               default:
                  aNumberFormatException = 8364;
                  break;
               case '\u0082':
                  aNumberFormatException = 8218;
                  break;
               case '\u0083':
                  aNumberFormatException = 402;
                  break;
               case '\u0084':
                  aNumberFormatException = 8222;
                  break;
               case '\u0085':
                  aNumberFormatException = 8230;
                  break;
               case '\u0086':
                  aNumberFormatException = 8224;
                  break;
               case '\u0087':
                  aNumberFormatException = 8225;
                  break;
               case '\u0088':
                  aNumberFormatException = 710;
                  break;
               case '\u0089':
                  aNumberFormatException = 8240;
                  break;
               case '\u008a':
                  aNumberFormatException = 352;
                  break;
               case '\u008b':
                  aNumberFormatException = 8249;
                  break;
               case '\u008c':
                  aNumberFormatException = 338;
                  break;
               case '\u008e':
                  aNumberFormatException = 381;
                  break;
               case '\u0091':
                  aNumberFormatException = 8216;
                  break;
               case '\u0092':
                  aNumberFormatException = 8217;
                  break;
               case '\u0093':
                  aNumberFormatException = 8220;
                  break;
               case '\u0094':
                  aNumberFormatException = 8221;
                  break;
               case '\u0095':
                  aNumberFormatException = 8226;
                  break;
               case '\u0096':
                  aNumberFormatException = 8211;
                  break;
               case '\u0097':
                  aNumberFormatException = 8212;
                  break;
               case '\u0098':
                  aNumberFormatException = 732;
                  break;
               case '\u0099':
                  aNumberFormatException = 8482;
                  break;
               case '\u009a':
                  aNumberFormatException = 353;
                  break;
               case '\u009b':
                  aNumberFormatException = 8250;
                  break;
               case '\u009c':
                  aNumberFormatException = 339;
                  break;
               case '\u009e':
                  aNumberFormatException = 382;
                  break;
               case '\u009f':
                  aNumberFormatException = 376;
            }

            this._mainBuffer.setLength(this._mainIndex);
            this._mainBuffer.append(aNumberFormatException);
            result = true;
            var6 = false;
         } finally {
            if (var6) {
               this._mainBuffer.append(this._auxiliaryBuffer);
               break label59;
            }
         }
      }

      this._auxiliaryBuffer.reset();
      this._mainIndex = -1;
      return result;
   }

   private final boolean resolveFromSymbol() {
      boolean result = false;
      if (this._auxiliaryBuffer.getLength() > 0 && this._mainIndex != -1) {
         String symbol = this._auxiliaryBuffer.toString();
         char resolved = HTMLUtilities.resolveEntity(symbol);
         if (resolved != '\uffff') {
            this._mainBuffer.setLength(this._mainIndex);
            this._mainBuffer.append(resolved);
            result = true;
         } else {
            this._mainBuffer.append(this._auxiliaryBuffer);
         }
      }

      this._auxiliaryBuffer.reset();
      this._mainIndex = -1;
      return result;
   }
}
