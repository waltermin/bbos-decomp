package net.rim.device.apps.internal.browser.css;

import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;

public final class CSSTokenizer implements CSSTokens {
   private String _source;
   private int _index;
   private int _length;
   private int _token;
   private int _startIndex;
   private int _endIndex;
   private static final String IMPORT_RULE;
   private static final String PAGE_RULE;
   private static final String MEDIA_RULE;
   private static final String CHARSET_RULE;
   private static final String IMPORTANT_PRIORITY;

   public CSSTokenizer(String source) {
      this._source = source;
      this._length = this._source.length();
   }

   public final String getSource() {
      return this._source;
   }

   public final int getStartIndex() {
      return this._startIndex;
   }

   public final int getEndIndex() {
      return this._endIndex;
   }

   public final String getStringValue() {
      return this._endIndex >= this._startIndex ? this._source.substring(this._startIndex, this._endIndex) : null;
   }

   public final int getDecimalIndex() {
      return StringUtilities.indexOf(this._source, (char)46, this._startIndex, this._endIndex);
   }

   public final int getIntegerValue(int decimalIndex, boolean scale) {
      int max = this._endIndex;
      if (decimalIndex >= this._startIndex && decimalIndex < this._endIndex) {
         max = decimalIndex;
      }

      int value = 0;

      label86:
      try {
         value = NumberUtilities.parseInt(this._source, this._startIndex, max, 10);
      } finally {
         break label86;
      }

      if (scale) {
         value *= 1000;
         if (decimalIndex >= this._startIndex && decimalIndex < this._endIndex) {
            max = Math.min(this._endIndex, decimalIndex + 4);
            int decimal = 0;

            label78:
            try {
               decimal = NumberUtilities.parseInt(this._source, decimalIndex + 1, max, 10);
            } finally {
               break label78;
            }

            int digits = max - (decimalIndex + 1);
            if (digits == 1) {
               decimal *= 100;
            } else if (digits == 2) {
               decimal *= 10;
            }

            value += decimal;
         }
      }

      return value;
   }

   public final int getToken() {
      return this._token;
   }

   public final int nextToken() {
      int c;
      c = this.getChar();
      label173:
      switch (c) {
         case -1:
            return this._token = 0;
         case 9:
         case 10:
         case 12:
         case 13:
         case 32:
            while (CSSUtilities.isWhitespaceCharacter((char)this.nextChar())) {
            }

            switch (this.getChar()) {
               case 43:
                  this.advance();
                  return this._token = 49;
               case 44:
                  this.advance();
                  return this._token = 46;
               case 62:
                  this.advance();
                  return this._token = 50;
               case 123:
                  this.advance();
                  return this._token = 40;
               default:
                  return this._token = 1;
            }
         case 33:
            while (CSSUtilities.isWhitespaceCharacter((char)this.nextChar())) {
            }

            if (StringUtilities.regionMatches(this._source, true, this._index, "important", 0, 9, 1701707776)) {
               this._index += 9;
               return this._token = 16;
            }

            throw new CSSParseException("important-symbol");
         case 34:
            this.advance();
            return this._token = this.readString1();
         case 35:
            c = this.nextChar();
            if (c == -1) {
               throw new CSSParseException("eof");
            }

            if (!CSSUtilities.isNameCharacter((char)c) && c != 92) {
               throw new CSSParseException("hash");
            }

            this._startIndex = this._index;

            while (true) {
               switch (c) {
                  case 92:
                     this.advance();
                     this.readEscape();
                     c = this.getChar();
                     continue;
               }

               if (c == -1 || !CSSUtilities.isNameCharacter((char)c)) {
                  this._endIndex = this._index;
                  return this._token = 9;
               }

               c = this.nextChar();
            }
         case 39:
            this.advance();
            return this._token = this.readString2();
         case 40:
            this.advance();
            return this._token = 42;
         case 41:
            this.advance();
            return this._token = 43;
         case 42:
            this.advance();
            return this._token = 53;
         case 43:
            this.advance();
            return this._token = 49;
         case 44:
            this.advance();
            return this._token = 46;
         case 45:
            this._startIndex = this._index;
            c = this.nextChar();
            if (c == 45) {
               if (this.nextChar() == 62) {
                  this.advance();
                  return this._token = 4;
               }

               throw new CSSParseException("cdc");
            }

            if (c == -1 || !CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               return this._token = 51;
            }
            break;
         case 46:
            this._startIndex = this._index;
            if (CSSUtilities.isNumberCharacter((char)this.nextChar())) {
               return this._token = this.readDecimal();
            }

            return this._token = 52;
         case 47:
            if (this.nextChar() != 42) {
               return this._token = 48;
            }

            this.advance();
            this._startIndex = this._index;
            int i = this._source.indexOf("*/", this._index);
            if (i != -1) {
               this._endIndex = i;
               this._index = i + 2;
               return this._token = 2;
            }

            this._index = this._length;
            throw new CSSParseException("eof");
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         case 56:
         case 57:
            return this._token = this.readNumber();
         case 58:
            this.advance();
            return this._token = 47;
         case 59:
            this.advance();
            return this._token = 39;
         case 60:
            if (this.nextChar() == 33 && this.nextChar() == 45 && this.nextChar() == 45) {
               this.advance();
               return this._token = 3;
            }

            throw new CSSParseException("cdo");
         case 61:
            this.advance();
            return this._token = 54;
         case 62:
            this.advance();
            return this._token = 50;
         case 64:
            this.advance();
            return this._token = this.readAtRule();
         case 85:
         case 117:
            this._startIndex = this._index;
            c = this.nextChar();
            switch (c) {
               case 82:
               case 114:
                  c = this.nextChar();
                  switch (c) {
                     case 76:
                     case 108:
                        c = this.nextChar();
                        switch (c) {
                           case 40:
                              this.advance();
                              return this._token = this.readURI();
                        }
                  }
               default:
                  break label173;
            }
         case 91:
            this.advance();
            return this._token = 44;
         case 93:
            this.advance();
            return this._token = 45;
         case 123:
            this.advance();
            return this._token = 40;
         case 124:
            if (this.nextChar() == 61) {
               this.advance();
               return this._token = 6;
            }

            throw new CSSParseException("dashmatch");
         case 125:
            this.advance();
            return this._token = 41;
         case 126:
            if (this.nextChar() == 61) {
               this.advance();
               return this._token = 5;
            }

            throw new CSSParseException("includes");
         default:
            if (!CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               this.advance();
               throw new CSSParseException("identifier");
            }

            this._startIndex = this._index;
      }

      while (true) {
         switch (c) {
            case 40:
               this._endIndex = this._index;
               this.advance();
               return this._token = 37;
            case 92:
               this.advance();
               this.readEscape();
               c = this.getChar();
               break;
            default:
               if (c == -1 || !CSSUtilities.isNameCharacter((char)c)) {
                  this._endIndex = this._index;
                  return this._token = 8;
               }

               c = this.nextChar();
         }
      }
   }

   private final int getChar() {
      return this._index >= this._length ? -1 : this._source.charAt(this._index);
   }

   private final void advance() {
      this._index++;
   }

   private final int nextChar() {
      this._index++;
      return this.getChar();
   }

   private final void readEscape() {
      int c = this.getChar();
      if (CSSUtilities.isHexadecimalCharacter((char)c)) {
         c = this.nextChar();
         if (CSSUtilities.isHexadecimalCharacter((char)c)) {
            c = this.nextChar();
            if (CSSUtilities.isHexadecimalCharacter((char)c)) {
               c = this.nextChar();
               if (CSSUtilities.isHexadecimalCharacter((char)c)) {
                  c = this.nextChar();
                  if (CSSUtilities.isHexadecimalCharacter((char)c)) {
                     c = this.nextChar();
                     if (CSSUtilities.isHexadecimalCharacter((char)c)) {
                        c = this.nextChar();
                     }
                  }
               }
            }
         }

         if (CSSUtilities.isWhitespaceCharacter((char)c)) {
            boolean cr = c == 13;
            c = this.nextChar();
            if (cr && c == 10) {
               this.advance();
            }
         }
      } else {
         if (c == -1) {
            throw new CSSParseException("eof");
         }

         if (!CSSUtilities.isNonAsciiCharacter((char)c) && (c < 32 || c > 126)) {
            throw new CSSParseException("escape");
         }

         this.advance();
      }
   }

   private final int readString1() {
      this._startIndex = this._index;
      int c = this.getChar();

      while (true) {
         switch (c) {
            case -1:
               throw new CSSParseException("eof");
            case 34:
               this._endIndex = this._index;
               this.advance();
               return 7;
            case 39:
               c = this.nextChar();
               break;
            case 92:
               c = this.nextChar();
               if (CSSUtilities.isNewLineCharacter((char)c)) {
                  boolean cr = c == 13;
                  c = this.nextChar();
                  if (cr && c == 10) {
                     c = this.nextChar();
                  }
               } else {
                  this.readEscape();
                  c = this.getChar();
               }
               break;
            default:
               if (!CSSUtilities.isStringCharacter((char)c)) {
                  throw new CSSParseException("string-1");
               }

               c = this.nextChar();
         }
      }
   }

   private final int readString2() {
      this._startIndex = this._index;
      int c = this.getChar();

      while (true) {
         switch (c) {
            case -1:
               throw new CSSParseException("eof");
            case 34:
               c = this.nextChar();
               break;
            case 39:
               this._endIndex = this._index;
               this.advance();
               return 7;
            case 92:
               c = this.nextChar();
               if (CSSUtilities.isNewLineCharacter((char)c)) {
                  boolean cr = c == 13;
                  c = this.nextChar();
                  if (cr && c == 10) {
                     c = this.nextChar();
                  }
               } else {
                  this.readEscape();
                  c = this.getChar();
               }
               break;
            default:
               if (!CSSUtilities.isStringCharacter((char)c)) {
                  throw new CSSParseException("string-2");
               }

               c = this.nextChar();
         }
      }
   }

   private final int readAtRule() {
      this._startIndex = this._index;
      int c = this.getChar();
      switch (c) {
         case -1:
            throw new CSSParseException("eof");
         case 45:
            c = this.nextChar();
            if (c == -1) {
               throw new CSSParseException("eof");
            }

            if (!CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               throw new CSSParseException("at-rule");
            }
            break;
         case 67:
         case 99:
            if (StringUtilities.regionMatches(this._source, true, this._index, "charset", 0, 7, 1701707776)) {
               this._index += 7;
               return 14;
            }
            break;
         case 73:
         case 105:
            if (StringUtilities.regionMatches(this._source, true, this._index, "import", 0, 6, 1701707776)) {
               this._index += 6;
               return 10;
            }
            break;
         case 77:
         case 109:
            if (StringUtilities.regionMatches(this._source, true, this._index, "media", 0, 5, 1701707776)) {
               this._index += 5;
               return 12;
            }
            break;
         case 80:
         case 112:
            if (StringUtilities.regionMatches(this._source, true, this._index, "page", 0, 4, 1701707776)) {
               this._index += 4;
               return 11;
            }
            break;
         default:
            if (!CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               throw new CSSParseException("at-rule");
            }
      }

      while (true) {
         switch (c) {
            case 92:
               this.advance();
               this.readEscape();
               c = this.getChar();
               continue;
         }

         if (c == -1 || !CSSUtilities.isNameCharacter((char)c)) {
            this._endIndex = this._index;
            return 15;
         }

         c = this.nextChar();
      }
   }

   private final int readNumber() {
      this._startIndex = this._index;

      for (int c = this.getChar(); c != 46; c = this.nextChar()) {
         if (!CSSUtilities.isNumberCharacter((char)c)) {
            return this.readUnits(true);
         }
      }

      if (CSSUtilities.isNumberCharacter((char)this.nextChar())) {
         return this.readDecimal();
      } else {
         throw new CSSParseException("number");
      }
   }

   private final int readDecimal() {
      int c = this.getChar();

      while (CSSUtilities.isNumberCharacter((char)c)) {
         c = this.nextChar();
      }

      return this.readUnits(false);
   }

   private final int readUnits(boolean integer) {
      int c;
      this._endIndex = this._index;
      c = this.getChar();
      label255:
      switch (c) {
         case 37:
            this.advance();
            return 33;
         case 45:
            c = this.nextChar();
            if (c == -1) {
               throw new CSSParseException("eof");
            }

            if (!CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               throw new CSSParseException("units");
            }
            break;
         case 67:
         case 99:
            switch (c = this.nextChar()) {
               case 77:
               case 109:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 20;
                  }
               default:
                  break label255;
            }
         case 68:
         case 100:
            switch (c = this.nextChar()) {
               case 69:
               case 101:
                  switch (c = this.nextChar()) {
                     case 71:
                     case 103:
                        c = this.nextChar();
                        if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                           return 25;
                        }
                  }
               default:
                  break label255;
            }
         case 69:
         case 101:
            switch (c = this.nextChar()) {
               case 77:
               case 109:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 17;
                  }
                  break label255;
               case 88:
               case 120:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 18;
                  }
               default:
                  break label255;
            }
         case 71:
         case 103:
            switch (c = this.nextChar()) {
               case 82:
               case 114:
                  switch (c = this.nextChar()) {
                     case 65:
                     case 97:
                        switch (c = this.nextChar()) {
                           case 68:
                           case 100:
                              c = this.nextChar();
                              if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                                 return 27;
                              }
                        }
                  }
               default:
                  break label255;
            }
         case 72:
         case 104:
            switch (c = this.nextChar()) {
               case 90:
               case 122:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 30;
                  }
               default:
                  break label255;
            }
         case 73:
         case 105:
            switch (c = this.nextChar()) {
               case 78:
               case 110:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 22;
                  }
               default:
                  break label255;
            }
         case 75:
         case 107:
            switch (c = this.nextChar()) {
               case 72:
               case 104:
                  switch (c = this.nextChar()) {
                     case 90:
                     case 122:
                        c = this.nextChar();
                        if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                           return 31;
                        }
                  }
               default:
                  break label255;
            }
         case 77:
         case 109:
            switch (c = this.nextChar()) {
               case 77:
               case 109:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 21;
                  }
                  break label255;
               case 83:
               case 115:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 28;
                  }
               default:
                  break label255;
            }
         case 80:
         case 112:
            switch (c = this.nextChar()) {
               case 67:
               case 99:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 24;
                  }
                  break label255;
               case 84:
               case 116:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 23;
                  }
                  break label255;
               case 88:
               case 120:
                  c = this.nextChar();
                  if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                     return 19;
                  }
               default:
                  break label255;
            }
         case 82:
         case 114:
            switch (c = this.nextChar()) {
               case 65:
               case 97:
                  switch (c = this.nextChar()) {
                     case 68:
                     case 100:
                        c = this.nextChar();
                        if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
                           return 26;
                        }
                  }
               default:
                  break label255;
            }
         case 83:
         case 115:
            c = this.nextChar();
            if (c == -1 || !CSSUtilities.isNameCharacter((char)c) && c != 92) {
               return 29;
            }
            break;
         default:
            if (c == -1 || !CSSUtilities.isNameStartCharacter((char)c) && c != 92) {
               if (integer) {
                  return 34;
               }

               return 35;
            }
      }

      while (true) {
         switch (c) {
            case 92:
               this.advance();
               this.readEscape();
               c = this.getChar();
               continue;
         }

         if (c == -1 || !CSSUtilities.isNameCharacter((char)c)) {
            return 32;
         }

         c = this.nextChar();
      }
   }

   private final int readURI() {
      int c = this.getChar();

      while (CSSUtilities.isWhitespaceCharacter((char)c)) {
         c = this.nextChar();
      }

      if (c == 34) {
         this.advance();
         this.readString1();
         c = this.getChar();

         while (CSSUtilities.isWhitespaceCharacter((char)c)) {
            c = this.nextChar();
         }

         if (c == 41) {
            this.advance();
            return 36;
         } else {
            throw new CSSParseException("uri");
         }
      } else if (c == 39) {
         this.advance();
         this.readString2();
         c = this.getChar();

         while (CSSUtilities.isWhitespaceCharacter((char)c)) {
            c = this.nextChar();
         }

         if (c == 41) {
            this.advance();
            return 36;
         } else {
            throw new CSSParseException("uri");
         }
      } else {
         if (c == 41) {
            throw new CSSParseException("uri");
         }

         this._startIndex = this._index;

         while (true) {
            switch (c) {
               case -1:
                  throw new CSSParseException("eof");
               case 41:
                  this._endIndex = this._index;
                  this.advance();
                  return 36;
               case 92:
                  this.advance();
                  this.readEscape();
                  c = this.getChar();
                  break;
               default:
                  if (!CSSUtilities.isURICharacter((char)c)) {
                     if (CSSUtilities.isWhitespaceCharacter((char)c)) {
                        c = this.nextChar();

                        while (CSSUtilities.isWhitespaceCharacter((char)c)) {
                           c = this.nextChar();
                        }

                        if (c == 41) {
                           this._endIndex = this._index;
                           this.advance();
                           return 36;
                        }
                     }

                     throw new CSSParseException("uri");
                  }

                  c = this.nextChar();
            }
         }
      }
   }
}
