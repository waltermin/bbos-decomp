package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class Tokenizer implements TokenConstants {
   private int _peekedToken;
   private int _ssPeekedToken;
   private boolean _hadLineTerminator;
   private boolean _ssHadLineTerminator;
   private int _lineNumber = 1;
   private int _ssLineNumber;
   private int _lastTokenLineNumber = 0;
   private int _ssLastTokenLineNumber;
   private int _lineStart;
   private int _ssLineStart;
   private int _tokenBuffIndex = 0;
   private int _ssTokenBuffIndex;
   private int _inputStreamIndex;
   private int _ssInputStreamIndex;
   private int _tokenBuffLength = 0;
   private char[] _tokenBuff = new char[0];
   private byte[] _tokenBuffBytes = new byte[0];
   private boolean _tokenIsByteString = false;
   private String _inputStream;
   private int _tokenValue;
   private int _ssTokenValue;
   private double _tokenDouble;
   private double _ssTokenDouble;
   private int _tokenIndex;
   private int _ssTokenIndex;
   private int _flagsIndex;
   private int _ssFlagsIndex;
   private byte[] _tokenStreamBuff = new byte[0];
   private int _tokenStreamIndex;
   private int _ssTokenStreamIndex;
   private int _lastGetTokenStreamIndex;
   private int _ssLastGetTokenStreamIndex;
   private int _lastPeekTokenStreamIndex;
   private int _ssLastPeekTokenStreamIndex;
   private int _tokenStreamLength = 0;
   private boolean _onlyDecimalNumbers = false;
   private Compiler _compiler;
   static final int EOF;
   private static final String[] _keywordTable = new String[]{
      "break",
      "case",
      "catch",
      "continue",
      "default",
      "delete",
      "do",
      "else",
      "finally",
      "for",
      "function",
      "if",
      "in",
      "instanceof",
      "new",
      "return",
      "switch",
      "this",
      "throw",
      "try",
      "typeof",
      "var",
      "void",
      "while",
      "with",
      "null",
      "true",
      "false",
      "const",
      "abstract",
      "boolean",
      "byte",
      "char",
      "class",
      "debugger",
      "double",
      "enum",
      "export",
      "extends",
      "final",
      "float",
      "goto",
      "implements",
      "import",
      "int",
      "interface",
      "long",
      "native",
      "package",
      "private",
      "protected",
      "public",
      "short",
      "static",
      "super",
      "synchronized",
      "throws",
      "transient",
      "volatile"
   };
   private static final String[] _punctToStringTable = new String[]{
      "-",
      "--",
      "-=",
      "!",
      "!=",
      "!==",
      "%",
      "%=",
      "&",
      "&&",
      "&=",
      "(",
      ")",
      "*",
      "*=",
      ",",
      ".",
      "/",
      "/=",
      ":",
      ";",
      "?",
      "[",
      "]",
      "^",
      "^=",
      "{",
      "|",
      "||",
      "|=",
      "}",
      "~",
      "+",
      "++",
      "+=",
      "<",
      "<<",
      "<<=",
      "<=",
      "=",
      "==",
      "===",
      ">",
      ">=",
      ">>",
      ">>=",
      ">>>",
      ">>>=",
      "..",
      "::",
      "@"
   };
   public static final int ACCURATE_LINES;
   public static final int COMPRESSED_LINES;
   public static final int ONE_LINE;
   public static final int PRETTY_PRINT;

   void saveState() {
      this._ssPeekedToken = this._peekedToken;
      this._ssTokenValue = this._tokenValue;
      this._ssTokenDouble = this._tokenDouble;
      this._ssTokenIndex = this._tokenIndex;
      this._ssFlagsIndex = this._flagsIndex;
      this._ssHadLineTerminator = this._hadLineTerminator;
      this._ssLineNumber = this._lineNumber;
      this._ssLastTokenLineNumber = this._lastTokenLineNumber;
      this._ssLineStart = this._lineStart;
      this._ssTokenBuffIndex = this._tokenBuffIndex;
      this._ssTokenStreamIndex = this._tokenStreamIndex;
      this._ssLastGetTokenStreamIndex = this._lastGetTokenStreamIndex;
      this._ssLastPeekTokenStreamIndex = this._lastPeekTokenStreamIndex;
      this._ssInputStreamIndex = this._inputStreamIndex;
   }

   void restoreState() {
      this._peekedToken = this._ssPeekedToken;
      this._tokenValue = this._ssTokenValue;
      this._tokenDouble = this._ssTokenDouble;
      this._tokenIndex = this._ssTokenIndex;
      this._flagsIndex = this._ssFlagsIndex;
      this._hadLineTerminator = this._ssHadLineTerminator;
      this._lineNumber = this._ssLineNumber;
      this._lastTokenLineNumber = this._ssLastTokenLineNumber;
      this._lineStart = this._ssLineStart;
      this._tokenBuffIndex = this._ssTokenBuffIndex;
      this._tokenStreamIndex = this._ssTokenStreamIndex;
      this._lastGetTokenStreamIndex = this._ssLastGetTokenStreamIndex;
      this._lastPeekTokenStreamIndex = this._ssLastPeekTokenStreamIndex;
      this._inputStreamIndex = this._ssInputStreamIndex;
   }

   public int tokenValue() {
      return this._tokenValue;
   }

   public double tokenDouble() {
      return this._tokenDouble;
   }

   private int addDouble() {
      return this.addDouble((Double)(new Object(this._tokenDouble)));
   }

   private int addDouble(Double d) {
      if (this._compiler != null) {
         this._tokenIndex = this._compiler.addDouble(d);
      }

      return 201;
   }

   private int convertDouble() {
      try {
         Double d = Double.valueOf(this.tokenString());
         this._tokenDouble = d;
         return this.addDouble(d);
      } finally {
         throw new CompileError(Resources.getString(10));
      }
   }

   String tokenIdentifier() {
      return this._compiler == null ? "" : this._compiler.getId(this._tokenIndex);
   }

   String tokenStringLiteral() {
      return this._compiler == null ? "" : this._compiler.getString(this._tokenIndex);
   }

   String flagsStringLiteral() {
      return this._compiler == null ? "" : this._compiler.getString(this._flagsIndex);
   }

   String tokenRawString() {
      return this.tokenString();
   }

   public String tokenString() {
      return (String)(this._tokenIsByteString
         ? new Object(this._tokenBuffBytes, 0, this._tokenBuffIndex)
         : new Object(this._tokenBuff, 0, this._tokenBuffIndex));
   }

   String tokenToString(int token) {
      return this.tokenToString(token, this._tokenIndex);
   }

   public static char hexDigit(int ch) {
      return ch <= 9 ? (char)(ch + 48) : (char)(ch - 10 + 65);
   }

   private static String escape(String str, char quoteChar) {
      int length = str.length();
      StringBuffer b = (StringBuffer)(new Object(length));

      for (int i = 0; i < length; i++) {
         char ch = str.charAt(i);
         if (ch == '\\') {
            b.append('\\');
            b.append('\\');
         } else if (ch == quoteChar) {
            b.append('\\');
            b.append(ch);
         } else {
            switch (ch) {
               case '\u001f':
               case '\\':
                  b.append('\\');
                  b.append('u');
                  b.append(hexDigit(ch >> '\f' & 15));
                  b.append(hexDigit(ch >> '\b' & 15));
                  b.append(hexDigit(ch >> 4 & 15));
                  b.append(hexDigit(ch >> 0 & 15));
                  break;
               case ' ':
               case '!':
               case '"':
               case '#':
               case '$':
               case '%':
               case '&':
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
               case '[':
               case ']':
               case '^':
               case '_':
               case '`':
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
               case '{':
               case '|':
               case '}':
               case '~':
               default:
                  b.append(ch);
            }
         }
      }

      return b.toString();
   }

   private String tokenToString(int token, int index) {
      if (token >= 1 && token <= 59) {
         return _keywordTable[token - 1];
      }

      if (token >= 100 && token <= 150) {
         return _punctToStringTable[token - 100];
      }

      switch (token) {
         case 199:
            return "";
         case 200:
            return ((StringBuffer)(new Object("\""))).append(escape(this._compiler.getString(index), '"')).append("\"").toString();
         case 201:
            return this._compiler.getDouble(index).toString();
         case 202:
            return Integer.toString(index);
         case 203:
            return this._compiler.getId(index);
         case 204:
         default:
            return ((StringBuffer)(new Object("/"))).append(this._compiler.getString(index)).append("/").toString();
      }
   }

   String tokenToAnnotatedString(int token) {
      switch (token) {
         case 199:
            if (token >= 1 && token <= 29) {
               return ((StringBuffer)(new Object("KEYWORD \""))).append(_keywordTable[token - 1]).append("\"").toString();
            } else if (token >= 30 && token <= 59) {
               return ((StringBuffer)(new Object("RESERVD \""))).append(_keywordTable[token - 1]).append("\"").toString();
            } else {
               if (token >= 100 && token <= 150) {
                  return ((StringBuffer)(new Object("PUNCT   \""))).append(_punctToStringTable[token - 100]).append("\"").toString();
               }

               return null;
            }
         case 200:
            return ((StringBuffer)(new Object("STRING   \""))).append(this.tokenString()).append("\"").toString();
         case 201:
            return ((StringBuffer)(new Object("DOUBLE  \""))).append(this.tokenString()).append("\"").toString();
         case 202:
         default:
            return ((StringBuffer)(new Object("INTEGER  \""))).append(this.tokenString()).append("\"").toString();
         case 203:
            return ((StringBuffer)(new Object("ID      \""))).append(this.tokenString()).append("\"").toString();
         case 204:
            return ((StringBuffer)(new Object("REGEXP   /"))).append(this.tokenString()).append("/").toString();
      }
   }

   String tokenToExpectingString(int token) {
      switch (token) {
         case 199:
            if (token >= 1 && token <= 29) {
               return _keywordTable[token - 1];
            } else {
               if (token >= 100 && token <= 150) {
                  return _punctToStringTable[token - 100];
               }

               return null;
            }
         case 200:
            return Resources.getString(23);
         case 201:
         case 202:
         default:
            return Resources.getString(21);
         case 203:
            return Resources.getString(20);
         case 204:
            return Resources.getString(22);
      }
   }

   private void clearToken() {
      this._tokenBuffIndex = 0;
      this._tokenIsByteString = true;
   }

   private void addToken(int ch) {
      if (ch > 127) {
         this._tokenIsByteString = false;
      }

      if (this._tokenBuffIndex >= this._tokenBuffLength) {
         this._tokenBuffLength += 100;
         this._tokenBuff = Misc.charArrayResize(this._tokenBuff, this._tokenBuffLength);
         this._tokenBuffBytes = Misc.byteArrayResize(this._tokenBuffBytes, this._tokenBuffLength);
      }

      this._tokenBuffBytes[this._tokenBuffIndex] = (byte)ch;
      this._tokenBuff[this._tokenBuffIndex++] = (char)ch;
   }

   public static boolean isWhiteSpace(int ch) {
      switch (ch) {
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 32:
         case 160:
         case 5760:
         case 8192:
         case 8193:
         case 8194:
         case 8195:
         case 8196:
         case 8197:
         case 8198:
         case 8199:
         case 8200:
         case 8201:
         case 8202:
         case 8203:
         case 8232:
         case 8233:
         case 8239:
         case 12288:
            return true;
         default:
            return false;
      }
   }

   public static int octalValue(int ch) {
      switch (ch) {
         case 47:
            return -1;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55:
         default:
            return ch - 48;
      }
   }

   public static int hexValue(int ch) {
      switch (ch) {
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
            return ch - 48;
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
            return ch - 65 + 10;
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
            return ch - 97 + 10;
         default:
            return -1;
      }
   }

   private boolean isLineSeparator(int ch) {
      switch (ch) {
         case 13:
            while (this.nextCharIs(13)) {
            }

            this.nextCharIs(10);
         case 10:
         case 8232:
         case 8233:
            return true;
         default:
            return false;
      }
   }

   public int peekToken() {
      if (this._peekedToken == 0) {
         int lastGetTokenStreamIndex = this._lastGetTokenStreamIndex;
         this._peekedToken = this.getToken();
         this._lastPeekTokenStreamIndex = this._lastGetTokenStreamIndex;
         this._lastGetTokenStreamIndex = lastGetTokenStreamIndex;
      }

      return this._peekedToken;
   }

   private boolean nextCharIs(int ch) {
      try {
         int peek = this._inputStream.charAt(this._inputStreamIndex);
         if (peek != ch) {
            return false;
         }

         this._inputStreamIndex++;
         return true;
      } finally {
         ;
      }
   }

   private int makeTokenString() {
      if (this._compiler != null) {
         this._tokenIndex = this._compiler.addString(this.tokenString());
      }

      return 200;
   }

   private int makeTokenRegExp(String flags) {
      if (this._compiler != null) {
         this._tokenIndex = this._compiler.addString(this.tokenString());
         this._flagsIndex = this._compiler.addString(flags);
      }

      return 204;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int getString(int closeQuote) {
      while (true) {
         boolean var33 = false /* VF: Semaphore variable */;

         int ch;
         try {
            var33 = true;
            ch = this._inputStream.charAt(this._inputStreamIndex);
            this._inputStreamIndex++;
            var33 = false;
         } finally {
            if (var33) {
               throw new CompileError(Resources.getString(28));
            }
         }

         if (ch == closeQuote) {
            return this.makeTokenString();
         }

         if (ch == 92) {
            boolean var27 = false /* VF: Semaphore variable */;

            try {
               var27 = true;
               ch = this._inputStream.charAt(this._inputStreamIndex);
               this._inputStreamIndex++;
               var27 = false;
            } finally {
               if (var27) {
                  throw new CompileError(Resources.getString(28));
               }
            }

            switch (ch) {
               case 48:
               case 49:
               case 50:
               case 51:
               case 52:
               case 53:
               case 54:
               case 55:
                  ch = octalValue(ch);

                  try {
                     int dig1 = octalValue(this._inputStream.charAt(this._inputStreamIndex));
                     if (dig1 != -1) {
                        this._inputStreamIndex++;
                        ch = ch * 8 + dig1;
                        int dig2 = octalValue(this._inputStream.charAt(this._inputStreamIndex));
                        if (dig2 != -1) {
                           int all3 = ch * 8 + dig2;
                           if (all3 <= 255) {
                              this._inputStreamIndex++;
                              ch = all3;
                           }
                        }
                     }
                     break;
                  } finally {
                     break;
                  }
               case 98:
                  ch = 8;
                  break;
               case 102:
                  ch = 12;
                  break;
               case 110:
                  ch = 10;
                  break;
               case 114:
                  ch = 13;
                  break;
               case 116:
                  ch = 9;
                  break;
               case 117:
                  try {
                     int dig1 = hexValue(this._inputStream.charAt(this._inputStreamIndex));
                     if (dig1 != -1) {
                        int dig2 = hexValue(this._inputStream.charAt(this._inputStreamIndex + 1));
                        if (dig2 != -1) {
                           int dig3 = hexValue(this._inputStream.charAt(this._inputStreamIndex + 2));
                           if (dig3 != -1) {
                              int dig4 = hexValue(this._inputStream.charAt(this._inputStreamIndex + 3));
                              if (dig4 != -1) {
                                 this._inputStreamIndex += 4;
                                 ch = (char)dig1;
                                 ch <<= 4;
                                 ch += dig2;
                                 ch <<= 4;
                                 ch += dig3;
                                 ch <<= 4;
                                 ch += dig4;
                              }
                           }
                        }
                     }
                     break;
                  } finally {
                     break;
                  }
               case 118:
                  ch = 11;
                  break;
               case 120:
                  label240:
                  try {
                     int dig1 = hexValue(this._inputStream.charAt(this._inputStreamIndex));
                     if (dig1 != -1) {
                        int dig2 = hexValue(this._inputStream.charAt(this._inputStreamIndex + 1));
                        if (dig2 != -1) {
                           this._inputStreamIndex += 2;
                           ch = (char)dig1;
                           ch <<= 4;
                           ch += dig2;
                        }
                     }
                  } finally {
                     break label240;
                  }
            }
         }

         this.addToken(ch);
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int getDecimalDigits(int ch) {
      boolean overflow = false;
      int maxIntSum = 214748363;
      this._tokenValue = ch - 48;

      while (true) {
         boolean var6 = false /* VF: Semaphore variable */;

         try {
            var6 = true;
            ch = this._inputStream.charAt(this._inputStreamIndex);
            var6 = false;
         } finally {
            if (var6) {
               if (overflow) {
                  return 201;
               }

               return 202;
            }
         }

         switch (ch) {
            case 47:
               if (overflow) {
                  return 201;
               }

               return 202;
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
         }

         this.addToken(ch);
         this._inputStreamIndex++;
         ch -= 48;
         if (overflow) {
            this._tokenDouble = this._tokenDouble * 4621819117588971520L + ch;
         } else if (this._tokenValue < maxIntSum) {
            this._tokenValue = this._tokenValue * 10 + ch;
         } else {
            overflow = true;
            this._tokenDouble = this._tokenValue * 4621819117588971520L + ch;
         }
      }
   }

   public static double convertBinary(String str, int start, int end, int radix) {
      Tokenizer$StringToBinary s2b = new Tokenizer$StringToBinary(str, start, end, radix);

      int bit;
      do {
         bit = s2b.getBit();
      } while (bit == 0);

      if (bit == -1) {
         return (double)0L;
      }

      long number = 1;

      for (int i = 0; i < 52; i++) {
         bit = s2b.getBit();
         if (bit < 0) {
            return number;
         }

         number = 2 * number + bit;
      }

      int bit53 = bit;
      int bit54 = s2b.getBit();
      if (bit54 < 0) {
         return number;
      }

      int exp = 54;
      int trailingBits = 0;

      while (true) {
         bit = s2b.getBit();
         if (bit < 0) {
            if (bit54 != 0 && (bit53 != 0 || trailingBits != 0)) {
               number += 1;
               if (number > 9007199254740991L) {
                  number = 0;
                  exp++;
               }
            }

            exp += 1022;
            if (exp >= 2047) {
               number = 9218868437227405312L;
            } else {
               number &= 4503599627370495L;
               number |= (long)exp << 52;
            }

            return Double.longBitsToDouble(number);
         }

         trailingBits |= bit;
         exp++;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int getHexDigits() {
      boolean overflow = false;
      int maxIntSum = 134217726;
      int start = this._inputStreamIndex;
      this._tokenValue = 0;

      while (true) {
         boolean var8 = false /* VF: Semaphore variable */;

         char ch;
         try {
            var8 = true;
            ch = this._inputStream.charAt(this._inputStreamIndex);
            var8 = false;
         } finally {
            if (var8) {
               if (overflow) {
                  this._tokenDouble = convertBinary(this._inputStream, start, this._inputStreamIndex, 16);
                  return 201;
               }

               return 202;
            }
         }

         int chValue;
         switch (ch) {
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
               chValue = ch - '0';
               break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
               chValue = ch - 'A' + 10;
               break;
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
               chValue = ch - 'a' + 10;
               break;
            default:
               if (overflow) {
                  this._tokenDouble = convertBinary(this._inputStream, start, this._inputStreamIndex, 16);
                  return 201;
               }

               return 202;
         }

         if (overflow) {
            this._tokenDouble = this._tokenDouble * 4625196817309499392L + chValue;
         } else if (this._tokenValue < maxIntSum) {
            this._tokenValue = this._tokenValue * 16 + chValue;
         } else {
            overflow = true;
            this._tokenDouble = this._tokenValue * 4625196817309499392L + chValue;
         }

         this.addToken(ch);
         this._inputStreamIndex++;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int getOctalDigits() {
      boolean overflow = false;
      int maxIntSum = 268435454;
      this._tokenValue = 0;
      this.saveState();

      while (true) {
         boolean var6 = false /* VF: Semaphore variable */;

         char ch;
         try {
            var6 = true;
            ch = this._inputStream.charAt(this._inputStreamIndex);
            var6 = false;
         } finally {
            if (var6) {
               if (overflow) {
                  return 201;
               }

               return 202;
            }
         }

         switch (ch) {
            case 47:
               if (overflow) {
                  return 201;
               }

               return 202;
            case 48:
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
               this.addToken(ch);
               this._inputStreamIndex++;
               ch -= 48;
               if (overflow) {
                  this._tokenDouble = this._tokenDouble * 4620693217682128896L + ch;
               } else if (this._tokenValue < maxIntSum) {
                  this._tokenValue = this._tokenValue * 8 + ch;
               } else {
                  overflow = true;
                  this._tokenDouble = this._tokenValue * 4620693217682128896L + ch;
               }
               break;
            case 56:
            case 57:
            default:
               this.restoreState();
               return this.getDecimalDigits(48);
         }
      }
   }

   private boolean optionalExponent() {
      int ch;
      try {
         ch = this._inputStream.charAt(this._inputStreamIndex);
      } finally {
         ;
      }

      if (ch != 101 && ch != 69) {
         return false;
      }

      this.addToken(ch);
      this._inputStreamIndex++;

      try {
         var15 = this._inputStream.charAt(this._inputStreamIndex);
      } finally {
         ;
      }

      if (var15 == '+' || var15 == '-') {
         this.addToken(var15);
         this._inputStreamIndex++;

         try {
            var15 = this._inputStream.charAt(this._inputStreamIndex);
         } finally {
            ;
         }
      }

      switch (var15) {
         case '/':
            throw new CompileError(Resources.getString(7));
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
         default:
            this.addToken(var15);
            this._inputStreamIndex++;
            this.getDecimalDigits(var15);
            return true;
      }
   }

   private int getNumber(int ch) {
      switch (ch) {
         case 46:
            this.addToken(46);
            this.getDecimalDigits(48);
            this.optionalExponent();
            return this.convertDouble();
         case 48:
            if (!this._onlyDecimalNumbers) {
               this._tokenValue = 0;
               this.addToken(48);

               try {
                  var11 = this._inputStream.charAt(this._inputStreamIndex);
               } finally {
                  ;
               }

               switch (var11) {
                  case '.':
                     this.addToken(46);
                     this._inputStreamIndex++;
                     this.getDecimalDigits(48);
                     this.optionalExponent();
                     return this.convertDouble();
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
                     int var13 = this.getOctalDigits();
                     if (var13 == 201) {
                        return this.addDouble();
                     }

                     return 202;
                  case 'X':
                  case 'x':
                     this.addToken(var11);
                     this._inputStreamIndex++;
                     int rc = this.getHexDigits();
                     if (rc == 201) {
                        return this.addDouble();
                     }

                     return 202;
                  default:
                     if (this.optionalExponent()) {
                        return this.convertDouble();
                     }

                     return 202;
               }
            }
         default:
            this.addToken(ch);
            int rc = this.getDecimalDigits(ch);

            try {
               var10 = this._inputStream.charAt(this._inputStreamIndex);
            } finally {
               ;
            }

            if (var10 == '.') {
               this.addToken(var10);
               this._inputStreamIndex++;
               this.getDecimalDigits(48);
               rc = 201;
            }

            if (this.optionalExponent()) {
               rc = 201;
            }

            return rc == 201 ? this.convertDouble() : rc;
      }
   }

   void createTokenIdentifier(String token) {
      if (this._compiler != null) {
         this._tokenIndex = this._compiler.addId(this.tokenString());
      }
   }

   private int makeTokenIdentifier() {
      this.createTokenIdentifier(this.tokenString());
      return 203;
   }

   private int getId() {
      int id;
      id = 203;
      label128:
      switch (this._tokenBuffIndex) {
         case 1:
         case 11:
            break;
         case 2:
         default:
            switch (this._tokenBuff[1]) {
               case 'f':
                  id = 12;
                  break label128;
               case 'n':
                  id = 13;
                  break label128;
               case 'o':
                  id = 7;
               default:
                  break label128;
            }
         case 3:
            switch (this._tokenBuff[0]) {
               case 'f':
                  id = 10;
                  break label128;
               case 'i':
                  id = 45;
                  break label128;
               case 'n':
                  id = 15;
                  break label128;
               case 't':
                  id = 20;
                  break label128;
               case 'v':
                  id = 22;
               default:
                  break label128;
            }
         case 4:
            switch (this._tokenBuff[0]) {
               case 'b':
                  id = 32;
                  break label128;
               case 'c':
                  switch (this._tokenBuff[1]) {
                     case 'a':
                        id = 2;
                        break label128;
                     case 'h':
                        id = 33;
                     default:
                        break label128;
                  }
               case 'e':
                  switch (this._tokenBuff[1]) {
                     case 'l':
                        id = 8;
                        break label128;
                     case 'n':
                        id = 37;
                     default:
                        break label128;
                  }
               case 'g':
                  id = 42;
                  break label128;
               case 'l':
                  id = 47;
                  break label128;
               case 'n':
                  id = 26;
                  break label128;
               case 't':
                  switch (this._tokenBuff[1]) {
                     case 'h':
                        id = 18;
                        break label128;
                     case 'r':
                        id = 27;
                     default:
                        break label128;
                  }
               case 'v':
                  id = 23;
                  break label128;
               case 'w':
                  id = 25;
               default:
                  break label128;
            }
         case 5:
            switch (this._tokenBuff[2]) {
               case 'a':
                  id = 34;
                  break label128;
               case 'e':
                  id = 1;
                  break label128;
               case 'i':
                  id = 24;
                  break label128;
               case 'l':
                  id = 28;
                  break label128;
               case 'n':
                  switch (this._tokenBuff[0]) {
                     case 'c':
                        id = 29;
                        break label128;
                     case 'f':
                        id = 40;
                     default:
                        break label128;
                  }
               case 'o':
                  switch (this._tokenBuff[0]) {
                     case 'f':
                        id = 41;
                        break label128;
                     case 's':
                        id = 53;
                     default:
                        break label128;
                  }
               case 'p':
                  id = 55;
                  break label128;
               case 'r':
                  id = 19;
                  break label128;
               case 't':
                  id = 3;
               default:
                  break label128;
            }
         case 6:
            switch (this._tokenBuff[1]) {
               case 'a':
                  id = 48;
                  break label128;
               case 'e':
                  switch (this._tokenBuff[0]) {
                     case 'd':
                        id = 6;
                        break label128;
                     case 'r':
                        id = 16;
                     default:
                        break label128;
                  }
               case 'h':
                  id = 57;
                  break label128;
               case 'm':
                  id = 44;
                  break label128;
               case 'o':
                  id = 36;
                  break label128;
               case 't':
                  id = 54;
                  break label128;
               case 'u':
                  id = 52;
                  break label128;
               case 'w':
                  id = 17;
                  break label128;
               case 'x':
                  id = 38;
                  break label128;
               case 'y':
                  id = 21;
               default:
                  break label128;
            }
         case 7:
            switch (this._tokenBuff[1]) {
               case 'a':
                  id = 49;
                  break label128;
               case 'e':
                  id = 5;
                  break label128;
               case 'i':
                  id = 9;
                  break label128;
               case 'o':
                  id = 31;
                  break label128;
               case 'r':
                  id = 50;
                  break label128;
               case 'x':
                  id = 39;
               default:
                  break label128;
            }
         case 8:
            switch (this._tokenBuff[0]) {
               case 'a':
                  id = 30;
                  break label128;
               case 'c':
                  id = 4;
                  break label128;
               case 'd':
                  id = 35;
                  break label128;
               case 'f':
                  id = 11;
                  break label128;
               case 'v':
                  id = 59;
               default:
                  break label128;
            }
         case 9:
            switch (this._tokenBuff[0]) {
               case 'i':
                  id = 46;
                  break label128;
               case 'p':
                  id = 51;
                  break label128;
               case 't':
                  id = 58;
               default:
                  break label128;
            }
         case 10:
            switch (this._tokenBuff[1]) {
               case 'l':
                  break label128;
               case 'm':
               default:
                  id = 43;
                  break label128;
               case 'n':
                  id = 14;
                  break label128;
            }
         case 12:
            switch (this._tokenBuff[0]) {
               case 's':
                  id = 56;
            }
      }

      if (id == 203) {
         return this.makeTokenIdentifier();
      }

      String toCompare = _keywordTable[id - 1];

      for (int i = 0; i < this._tokenBuffIndex; i++) {
         if (this._tokenBuff[i] != toCompare.charAt(i)) {
            return this.makeTokenIdentifier();
         }
      }

      return id;
   }

   boolean lineTerminatorPreceedsToken() {
      return this._hadLineTerminator;
   }

   int getLineNumber() {
      return this._lineNumber;
   }

   int getColumn() {
      return this._inputStreamIndex - this._lineStart;
   }

   String getLine() {
      this.saveState();

      label50:
      try {
         label49:
         while (true) {
            switch (this._inputStream.charAt(this._inputStreamIndex++)) {
               case '\n':
               case '\r':
               case '\u2028':
               case '\u2029':
                  break label49;
            }
         }
      } finally {
         break label50;
      }

      String line;
      try {
         line = this._inputStream.substring(this._lineStart, this._inputStreamIndex - 1);
      } finally {
         ;
      }

      this.restoreState();
      return line;
   }

   private void advanceLine() {
      this._lineNumber++;
      this._hadLineTerminator = true;
      this._lineStart = this._inputStreamIndex;
   }

   int getRegExp(boolean haveEquals) {
      this._peekedToken = 0;
      int rc = this.getRegExpHelper(haveEquals);
      this._tokenStreamIndex--;
      this.addTokenToStream(rc);
      return rc;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private int getRegExpHelper(boolean haveEquals) {
      if (haveEquals) {
         this.addToken(61);
      }

      while (true) {
         boolean var16 = false /* VF: Semaphore variable */;

         char ch;
         try {
            var16 = true;
            ch = this._inputStream.charAt(this._inputStreamIndex);
            this._inputStreamIndex++;
            var16 = false;
         } finally {
            if (var16) {
               throw new CompileError(Resources.getString(27));
            }
         }

         if (ch == '/') {
            StringBuffer flags = (StringBuffer)(new Object());

            while (true) {
               boolean var12 = false /* VF: Semaphore variable */;

               try {
                  var12 = true;
                  ch = this._inputStream.charAt(this._inputStreamIndex);
                  var12 = false;
               } finally {
                  if (var12) {
                     return this.makeTokenRegExp(flags.toString());
                  }
               }

               switch (ch) {
                  case 'g':
                  case 'i':
                  case 'm':
                     this._inputStreamIndex++;
                     flags.append((char)ch);
                     break;
                  default:
                     return this.makeTokenRegExp(flags.toString());
               }
            }
         }

         if (ch == '\n') {
            throw new CompileError(Resources.getString(27));
         }

         this.addToken(ch);
         if (ch == '\\') {
            boolean var8 = false /* VF: Semaphore variable */;

            try {
               var8 = true;
               ch = this._inputStream.charAt(this._inputStreamIndex);
               this._inputStreamIndex++;
               var8 = false;
            } finally {
               if (var8) {
                  throw new CompileError(Resources.getString(27));
               }
            }

            this.addToken(ch);
         }
      }
   }

   private int eatSingleLineComment() {
      this._inputStreamIndex++;

      int ch;
      do {
         try {
            ch = this._inputStream.charAt(this._inputStreamIndex);
            this._inputStreamIndex++;
         } finally {
            ;
         }
      } while (ch != -1 && !this.isLineSeparator(ch));

      this.advanceLine();
      return this.getTokenHelper();
   }

   private int eatMultiLineComment() {
      this._inputStreamIndex++;

      while (true) {
         int ch;
         try {
            ch = this._inputStream.charAt(this._inputStreamIndex);
            this._inputStreamIndex++;
         } finally {
            ;
         }

         if (ch == -1) {
            throw new CompileError(Resources.getString(24));
         }

         if (this.isLineSeparator(ch)) {
            this.advanceLine();
         } else if (ch == 42 && this.nextCharIs(47)) {
            return this.getTokenHelper();
         }
      }
   }

   private int eatWhiteSpace() {
      while (this._inputStreamIndex < this._inputStream.length()) {
         int ch = this._inputStream.charAt(this._inputStreamIndex);
         this._inputStreamIndex++;
         switch (ch) {
            case 9:
            case 11:
            case 12:
            case 32:
            case 160:
            case 5760:
            case 8192:
            case 8193:
            case 8194:
            case 8195:
            case 8196:
            case 8197:
            case 8198:
            case 8199:
            case 8200:
            case 8201:
            case 8202:
            case 8203:
            case 8239:
            case 12288:
               break;
            case 13:
               while (this.nextCharIs(13)) {
               }

               this.nextCharIs(10);
            case 10:
            case 8232:
            case 8233:
               this.advanceLine();
               break;
            default:
               return ch;
         }
      }

      return -1;
   }

   public int getToken() {
      if (this._peekedToken != 0) {
         int tok = this._peekedToken;
         this._peekedToken = 0;
         this._lastGetTokenStreamIndex = this._lastPeekTokenStreamIndex;
         return tok;
      } else {
         this._hadLineTerminator = false;
         int rc = this.getTokenHelper();
         this.addTokenToStream(rc);
         return rc;
      }
   }

   private int getTokenHelper() {
      boolean haveEscape;
      boolean haveAnyEscape;
      int ch;
      haveEscape = false;
      haveAnyEscape = false;
      ch = this.eatWhiteSpace();
      label1868:
      switch (ch) {
         case -1:
            return 0;
         case 45:
            try {
               if (this._inputStream.charAt(this._inputStreamIndex) == '-' && this._inputStream.charAt(this._inputStreamIndex + 1) == '>') {
                  return this.eatSingleLineComment();
               }
               break;
            } finally {
               break;
            }
         case 47:
            try {
               switch (this._inputStream.charAt(this._inputStreamIndex)) {
                  case '*':
                     return this.eatMultiLineComment();
                  case '/':
                     return this.eatSingleLineComment();
                  default:
                     break label1868;
               }
            } finally {
               break;
            }
         case 60:
            label1865:
            try {
               if (this._inputStream.charAt(this._inputStreamIndex) == '!'
                  && this._inputStream.charAt(this._inputStreamIndex + 1) == '-'
                  && this._inputStream.charAt(this._inputStreamIndex + 2) == '-') {
                  return this.eatSingleLineComment();
               }
            } finally {
               break label1865;
            }
      }

      this.clearToken();
      switch (ch) {
         case 33:
            if (this.nextCharIs(61)) {
               if (this.nextCharIs(61)) {
                  return 105;
               }

               return 104;
            }

            return 103;
         case 34:
         case 39:
            return this.getString(ch);
         case 36:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 95:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
            break;
         case 37:
            if (this.nextCharIs(61)) {
               return 107;
            }

            return 106;
         case 38:
            if (this.nextCharIs(38)) {
               return 109;
            }

            if (this.nextCharIs(61)) {
               return 110;
            }

            return 108;
         case 40:
            return 111;
         case 41:
            return 112;
         case 42:
            if (this.nextCharIs(61)) {
               return 114;
            }

            return 113;
         case 43:
            if (this.nextCharIs(43)) {
               return 133;
            }

            if (this.nextCharIs(61)) {
               return 134;
            }

            return 132;
         case 44:
            return 115;
         case 45:
            if (this.nextCharIs(45)) {
               return 101;
            }

            if (this.nextCharIs(61)) {
               return 102;
            }

            return 100;
         case 46:
            int peek;
            try {
               peek = this._inputStream.charAt(this._inputStreamIndex);
            } finally {
               ;
            }

            switch (peek) {
               case 45:
               case 47:
                  return 116;
               case 46:
               default:
                  this._inputStreamIndex++;
                  return 148;
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
                  return this.getNumber(ch);
            }
         case 47:
            if (this.nextCharIs(61)) {
               return 118;
            }

            return 117;
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
         default:
            return this.getNumber(ch);
         case 58:
            if (this.nextCharIs(58)) {
               return 149;
            }

            return 119;
         case 59:
            return 120;
         case 60:
            if (this.nextCharIs(60)) {
               if (this.nextCharIs(61)) {
                  return 137;
               }

               return 136;
            }

            if (this.nextCharIs(61)) {
               return 138;
            }

            return 135;
         case 61:
            if (this.nextCharIs(61)) {
               if (this.nextCharIs(61)) {
                  return 141;
               }

               return 140;
            }

            return 139;
         case 62:
            if (this.nextCharIs(61)) {
               return 143;
            }

            if (this.nextCharIs(62)) {
               if (this.nextCharIs(62)) {
                  if (this.nextCharIs(61)) {
                     return 147;
                  }

                  return 146;
               }

               if (this.nextCharIs(61)) {
                  return 145;
               }

               return 144;
            }

            return 142;
         case 63:
            return 121;
         case 64:
            return 150;
         case 91:
            return 122;
         case 92:
            try {
               int i = this._inputStreamIndex;
               if (this._inputStream.charAt(i) != 'u') {
                  return 0;
               }

               ch = 0;

               for (int j = 0; j < 4; j++) {
                  int hexVal = hexValue(this._inputStream.charAt(++i));
                  if (hexVal == -1) {
                     return 0;
                  }

                  ch = (ch << 4) + hexVal;
               }

               this._inputStreamIndex += 5;
               haveEscape = true;
               haveAnyEscape = true;
            } finally {
               ;
            }
         case 32:
         case 35:
         case 96:
            if (!haveEscape) {
               return 0;
            }
            break;
         case 93:
            return 123;
         case 94:
            if (this.nextCharIs(61)) {
               return 125;
            }

            return 124;
         case 123:
            return 126;
         case 124:
            if (this.nextCharIs(124)) {
               return 128;
            }

            if (this.nextCharIs(61)) {
               return 129;
            }

            return 127;
         case 125:
            return 130;
         case 126:
            return 131;
      }

      this.addToken(ch);

      while (true) {
         haveEscape = false;

         try {
            ch = this._inputStream.charAt(this._inputStreamIndex);
         } finally {
            ;
         }

         switch (ch) {
            case 36:
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
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 95:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
               break;
            case 92:
            default:
               try {
                  int i = this._inputStreamIndex;
                  if (this._inputStream.charAt(++i) != 'u') {
                     return haveAnyEscape ? this.makeTokenIdentifier() : this.getId();
                  }

                  ch = 0;

                  for (int j = 0; j < 4; j++) {
                     int hexVal = hexValue(this._inputStream.charAt(++i));
                     if (hexVal == -1) {
                        return haveAnyEscape ? this.makeTokenIdentifier() : this.getId();
                     }

                     ch = (ch << 4) + hexVal;
                  }

                  this._inputStreamIndex += 5;
                  haveEscape = true;
                  haveAnyEscape = true;
               } finally {
                  return haveAnyEscape ? this.makeTokenIdentifier() : this.getId();
               }
            case 35:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 91:
            case 93:
            case 94:
            case 96:
               if (!haveEscape) {
                  return haveAnyEscape ? this.makeTokenIdentifier() : this.getId();
               }
         }

         this.addToken(ch);
         this._inputStreamIndex++;
      }
   }

   public Tokenizer(String str) {
      this(null, str);
      this._onlyDecimalNumbers = true;
   }

   Tokenizer(Compiler c, String str) {
      this._compiler = c;
      this._inputStream = str;
      this._inputStreamIndex = 0;
      this._peekedToken = 0;
   }

   private void addByteToTokenStream(int value) {
      if (this._tokenStreamIndex >= this._tokenStreamLength) {
         this._tokenStreamLength += 1000;
         this._tokenStreamBuff = Misc.byteArrayResize(this._tokenStreamBuff, this._tokenStreamLength);
      }

      this._tokenStreamBuff[this._tokenStreamIndex] = (byte)value;
      this._tokenStreamIndex++;
   }

   private void addIntToTokenStream(int value) {
      if (value <= 127 && value >= 0) {
         this.addByteToTokenStream(value);
      } else if (value <= 16383 && value >= 0) {
         this.addByteToTokenStream(value >> 8 & 63 | 128);
         this.addByteToTokenStream(value);
      } else {
         this.addByteToTokenStream(255);
         this.addByteToTokenStream(value >> 24);
         this.addByteToTokenStream(value >> 16);
         this.addByteToTokenStream(value >> 8);
         this.addByteToTokenStream(value >> 0);
      }
   }

   private void addTokenToStream(int token) {
      if (this._lineNumber != this._lastTokenLineNumber) {
         this.addByteToTokenStream(205);
         this.addIntToTokenStream(this._lineNumber);
         this._lastTokenLineNumber = this._lineNumber;
      }

      this._lastGetTokenStreamIndex = this._tokenStreamIndex;
      this.addByteToTokenStream(token);
      switch (token) {
         case 200:
         case 201:
         case 203:
            this.addIntToTokenStream(this._tokenIndex);
            return;
         case 202:
            this.addIntToTokenStream(this._tokenValue);
         case 199:
            return;
         case 204:
         default:
            this.addIntToTokenStream(this._tokenIndex);
            this.addIntToTokenStream(this._flagsIndex);
      }
   }

   private static int getTokenStreamInt(CompiledScript code, int i, int[] bump) {
      int s = code.getToken(++i);
      if (s <= 127) {
         bump[0] = 1;
         return s;
      } else if (s == 255) {
         bump[0] = 5;
         s = code.getToken(++i);
         s <<= 8;
         s += code.getToken(++i);
         s <<= 8;
         s += code.getToken(++i);
         s <<= 8;
         return s + code.getToken(++i);
      } else {
         bump[0] = 2;
         s &= 63;
         s <<= 8;
         return s + code.getToken(++i);
      }
   }

   int getLastGetTokenStreamIndex() {
      return this._lastGetTokenStreamIndex;
   }

   int getLastPeekTokenStreamIndex() {
      return this._lastPeekTokenStreamIndex;
   }

   byte[] getTokenStream() {
      this._tokenStreamLength = this._tokenStreamIndex;
      this._tokenStreamBuff = Misc.byteArrayResize(this._tokenStreamBuff, this._tokenStreamLength);
      return this._tokenStreamBuff;
   }

   void dump(CompiledScript code, int lineType) {
      if (this._compiler != null) {
         this._compiler.print(getSource(code, 1, 0, this._tokenStreamIndex, lineType));
      }
   }

   public static String getSource(CompiledScript code) {
      return getSource(code, 0, code.getTokenStart(), code.getTokenEnd(), 3);
   }

   public static String getSource(CompiledScript code, int offset) {
      return getSource(code, offset, true);
   }

   public static String getSource(CompiledScript code, int offset, boolean includeSubScripts) {
      if (!code.hasTokens()) {
         return Resources.getString(19);
      }

      for (int first = 0; first < code.getNumLines(); first++) {
         if (code.getLineOffset(first) == offset) {
            while (true) {
               try {
                  if (code.getLineOffset(first + 1) != offset) {
                     break;
                  }
               } finally {
                  break;
               }

               first++;
            }

            int firstOffset = code.getLineTokenOffset(first);
            int lastOffset = code.getNextLineTokenOffset(first, includeSubScripts);
            return getSource(code, -1, firstOffset, lastOffset, 2);
         }
      }

      return "";
   }

   static void nl(StringBuffer b) {
      int last = b.length() - 1;

      while (last >= 0 && b.charAt(last) == ' ') {
         last--;
      }

      b.setLength(last + 1);
      b.append("\n");
   }

   static String getSource(CompiledScript code, int lastLine, int start, int end, int lineType) {
      if (!code.hasTokens()) {
         return Resources.getString(19);
      }

      int indent = 0;
      int pendingPrintln = 0;
      StringBuffer b = (StringBuffer)(new Object());
      if (lineType == 3) {
         nl(b);
      }

      int lastTok = 0;
      int[] bump = new int[1];
      boolean isVersion12 = code.getVersion() == 120;

      for (int i = start; i < end; i++) {
         int tok = code.getToken(i);
         if (tok == 205) {
            int line = getTokenStreamInt(code, i, bump);
            i += bump[0];
            if (lastLine != -1 && lastLine < line) {
               pendingPrintln += line - lastLine;
               if (lineType == 3) {
                  pendingPrintln = 1;
               }
            }

            lastLine = line;
         } else {
            String toPrint = null;
            if (tok >= 1 && tok <= 59) {
               toPrint = _keywordTable[tok - 1];
            } else if (tok >= 100 && tok <= 150) {
               if (isVersion12) {
                  switch (tok) {
                     case 105:
                        tok = 104;
                        break;
                     case 141:
                        tok = 140;
                  }
               }

               toPrint = _punctToStringTable[tok - 100];
            } else {
               switch (tok) {
                  case 199:
                     continue;
                  case 200:
                     toPrint = ((StringBuffer)(new Object("\""))).append(escape(code.getString(getTokenStreamInt(code, i, bump)), '"')).append("\"").toString();
                     i += bump[0];
                     break;
                  case 201:
                     toPrint = Double.toString(code.getDouble(getTokenStreamInt(code, i, bump)));
                     i += bump[0];
                     break;
                  case 202:
                     toPrint = Integer.toString(getTokenStreamInt(code, i, bump));
                     i += bump[0];
                     break;
                  case 203:
                     toPrint = code.getId(getTokenStreamInt(code, i, bump));
                     i += bump[0];
                     break;
                  case 204:
                  default:
                     toPrint = ((StringBuffer)(new Object("/"))).append(code.getString(getTokenStreamInt(code, i, bump))).append("/").toString();
                     i += bump[0];
                     toPrint = ((StringBuffer)(new Object())).append(toPrint).append(code.getString(getTokenStreamInt(code, i, bump))).toString();
                     i += bump[0];
               }
            }

            if (tok == 130) {
               indent -= 4;
               if (lineType == 3) {
                  pendingPrintln = 1;
               }
            }

            if (lineType == 0) {
               while (pendingPrintln > 1) {
                  nl(b);
                  pendingPrintln--;
               }
            }

            boolean nl = false;
            if (lineType != 2 && pendingPrintln > 0) {
               nl = true;
               nl(b);
               if (lineType != 1) {
                  for (int j = 0; j < indent; j++) {
                     b.append(" ");
                  }
               }

               pendingPrintln = 0;
            }

            if (tok == 126) {
               indent += 4;
               if (lineType == 3) {
                  pendingPrintln = 1;
               }
            }

            boolean spaceBefore = false;
            boolean spaceAfter = false;
            if (lineType == 3) {
               switch (tok) {
                  case 2:
                  case 3:
                  case 10:
                  case 11:
                  case 12:
                  case 17:
                  case 24:
                  case 25:
                     spaceAfter = true;
                     break;
                  case 13:
                  case 14:
                  case 100:
                  case 102:
                  case 104:
                  case 105:
                  case 106:
                  case 107:
                  case 108:
                  case 109:
                  case 110:
                  case 113:
                  case 114:
                  case 117:
                  case 118:
                  case 124:
                  case 125:
                  case 127:
                  case 128:
                  case 129:
                  case 132:
                  case 134:
                  case 135:
                  case 136:
                  case 137:
                  case 138:
                  case 139:
                  case 140:
                  case 141:
                  case 142:
                  case 143:
                  case 144:
                  case 145:
                  case 146:
                  case 147:
                     spaceAfter = true;
                     spaceBefore = true;
                     break;
                  case 115:
                  case 120:
                     spaceAfter = true;
                     break;
                  case 126:
                     spaceBefore = true;
                     break;
                  case 130:
                     spaceAfter = true;
               }
            }

            if (lastTok != 0 && (tok < 100 || tok > 150) && (lastTok < 100 || lastTok > 150)) {
               spaceBefore = true;
            }

            if (tok == lastTok) {
               spaceBefore = true;
            }

            if (nl) {
               spaceBefore = false;
            }

            if (spaceBefore) {
               b.append(" ");
            }

            b.append(toPrint);
            if (spaceAfter) {
               lastTok = 116;
               b.append(" ");
            } else {
               lastTok = tok;
            }
         }
      }

      if (lineType == 3) {
         nl(b);
      }

      return b.toString();
   }
}
