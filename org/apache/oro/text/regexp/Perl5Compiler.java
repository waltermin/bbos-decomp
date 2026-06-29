package org.apache.oro.text.regexp;

public final class Perl5Compiler implements PatternCompiler {
   private CharStringPointer __input;
   private boolean __sawBackreference;
   private char[] __modifierFlags = new char[]{'\u0000', '\u0000'};
   private int __numParentheses;
   private int __programSize;
   private int __cost;
   private char[] __program;
   private static final int __WORSTCASE = 0;
   private static final int __NONNULL = 1;
   private static final int __SIMPLE = 2;
   private static final int __SPSTART = 4;
   private static final int __TRYAGAIN = 8;
   private static final char __CASE_INSENSITIVE = '\u0001';
   private static final char __GLOBAL = '\u0002';
   private static final char __KEEP = '\u0004';
   private static final char __MULTILINE = '\b';
   private static final char __SINGLELINE = '\u0010';
   private static final char __EXTENDED = ' ';
   private static final char __READ_ONLY = '耀';
   private static final String __HEX_DIGIT = "0123456789abcdef0123456789ABCDEFx";
   public static final int DEFAULT_MASK = 0;
   public static final int CASE_INSENSITIVE_MASK = 1;
   public static final int MULTILINE_MASK = 8;
   public static final int SINGLELINE_MASK = 16;
   public static final int EXTENDED_MASK = 32;
   public static final int READ_ONLY_MASK = 32768;

   public static final String quotemeta(char[] expression) {
      StringBuffer buffer = (StringBuffer)(new Object(2 * expression.length));

      for (int ch = 0; ch < expression.length; ch++) {
         if (!OpCode._isWordCharacter(expression[ch])) {
            buffer.append('\\');
         }

         buffer.append(expression[ch]);
      }

      return buffer.toString();
   }

   public static final String quotemeta(String expression) {
      return quotemeta(expression.toCharArray());
   }

   private static final boolean __isSimpleRepetitionOp(char ch) {
      return ch == '*' || ch == '+' || ch == '?';
   }

   private static final boolean __isComplexRepetitionOp(char[] ch, int offset) {
      return offset < ch.length && offset >= 0
         ? ch[offset] == '*' || ch[offset] == '+' || ch[offset] == '?' || ch[offset] == '{' && __parseRepetition(ch, offset)
         : false;
   }

   private static final boolean __parseRepetition(char[] str, int offset) {
      if (str[offset] != '{') {
         return false;
      }

      offset++;
      if (offset < str.length && Character.isDigit(str[offset])) {
         while (offset < str.length && Character.isDigit(str[offset])) {
            offset++;
         }

         if (offset < str.length && str[offset] == ',') {
            offset++;
         }

         while (offset < str.length && Character.isDigit(str[offset])) {
            offset++;
         }

         return offset < str.length && str[offset] == '}';
      } else {
         return false;
      }
   }

   private static final int __parseHex(char[] str, int offset, int maxLength, int[] scanned) {
      int val = 0;

      int index;
      for (scanned[0] = 0; offset < str.length && maxLength-- > 0 && (index = "0123456789abcdef0123456789ABCDEFx".indexOf(str[offset])) != -1; scanned[0]++) {
         val <<= 4;
         val |= index & 15;
         offset++;
      }

      return val;
   }

   private static final int __parseOctal(char[] str, int offset, int maxLength, int[] scanned) {
      int val = 0;

      for (scanned[0] = 0; offset < str.length && maxLength > 0 && str[offset] >= '0' && str[offset] <= '7'; scanned[0]++) {
         val <<= 3;
         val |= str[offset] - '0';
         maxLength--;
         offset++;
      }

      return val;
   }

   private static final void __setModifierFlag(char[] flags, char ch) {
      switch (ch) {
         case 'g':
            flags[0] = (char)(flags[0] | 2);
            return;
         case 'i':
            flags[0] = (char)(flags[0] | 1);
            return;
         case 'm':
            flags[0] = (char)(flags[0] | 8);
            return;
         case 'o':
            flags[0] = (char)(flags[0] | 4);
            return;
         case 's':
            flags[0] = (char)(flags[0] | 16);
            return;
         case 'x':
            flags[0] = (char)(flags[0] | 32);
            return;
      }
   }

   private final void __emitCode(char code) {
      if (this.__program != null) {
         this.__program[this.__programSize] = code;
      }

      this.__programSize++;
   }

   private final int __emitNode(char operator) {
      int offset = this.__programSize;
      if (this.__program == null) {
         this.__programSize += 2;
         return offset;
      } else {
         this.__program[this.__programSize++] = operator;
         this.__program[this.__programSize++] = 0;
         return offset;
      }
   }

   private final int __emitArgNode(char operator, char arg) {
      int offset = this.__programSize;
      if (this.__program == null) {
         this.__programSize += 3;
         return offset;
      } else {
         this.__program[this.__programSize++] = operator;
         this.__program[this.__programSize++] = 0;
         this.__program[this.__programSize++] = arg;
         return offset;
      }
   }

   private final void __programInsertOperator(char operator, int operand) {
      int offset = OpCode._opType[operator] == '\n' ? 2 : 0;
      if (this.__program == null) {
         this.__programSize += 2 + offset;
      } else {
         int src = this.__programSize;
         this.__programSize += 2 + offset;

         for (int dest = this.__programSize; src > operand; this.__program[--dest] = this.__program[src]) {
            src--;
         }

         this.__program[operand++] = operator;
         this.__program[operand++] = 0;

         while (offset-- > 0) {
            this.__program[operand++] = 0;
         }
      }
   }

   private final void __programAddTail(int current, int value) {
      if (this.__program != null && current != -1) {
         int scan = current;

         while (true) {
            int temp = OpCode._getNext(this.__program, scan);
            if (temp == -1) {
               int offset;
               if (this.__program[scan] == '\r') {
                  offset = scan - value;
               } else {
                  offset = value - scan;
               }

               this.__program[scan + 1] = (char)offset;
               return;
            }

            scan = temp;
         }
      }
   }

   private final void __programAddOperatorTail(int current, int value) {
      if (this.__program != null && current != -1 && OpCode._opType[this.__program[current]] == '\f') {
         this.__programAddTail(OpCode._getNextOperator(current), value);
      }
   }

   private final char __getNextChar() {
      char ret = this.__input._postIncrement();

      while (true) {
         char value = this.__input._getValue();
         if (value != '(' || this.__input._getValueRelative(1) != '?' || this.__input._getValueRelative(2) != '#') {
            if ((this.__modifierFlags[0] & ' ') == 0) {
               break;
            }

            if (Util.isWhitespace(value)) {
               this.__input._increment();
            } else {
               if (value != '#') {
                  break;
               }

               while (value != '\uffff' && value != '\n') {
                  value = this.__input._increment();
               }

               this.__input._increment();
            }
         } else {
            while (value != '\uffff' && value != ')') {
               value = this.__input._increment();
            }

            this.__input._increment();
         }
      }

      return ret;
   }

   private final int __parseAlternation(int[] retFlags) {
      int flags = 0;
      retFlags[0] = 0;
      int offset = this.__emitNode('\f');
      int chain = -1;
      if (this.__input._getOffset() == 0) {
         this.__input._setOffset(-1);
         this.__getNextChar();
      } else {
         this.__input._decrement();
         this.__getNextChar();
      }

      char value = this.__input._getValue();

      while (value != '\uffff' && value != '|' && value != ')') {
         flags &= -9;
         int latest = this.__parseBranch(retFlags);
         if (latest == -1) {
            if ((flags & 8) == 0) {
               return -1;
            }

            value = this.__input._getValue();
         } else {
            retFlags[0] |= flags & 1;
            if (chain == -1) {
               retFlags[0] |= flags & 4;
            } else {
               this.__cost++;
               this.__programAddTail(chain, latest);
            }

            chain = latest;
            value = this.__input._getValue();
         }
      }

      if (chain == -1) {
         this.__emitNode('\u000f');
      }

      return offset;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int __parseAtom(int[] retFlags) {
      int[] flags = new int[]{0, -804650956, 0, 0};
      retFlags[0] = 0;
      boolean doDefault = false;
      int offset = -1;

      label554:
      while (true) {
         char value = this.__input._getValue();
         switch (value) {
            case '#':
               if ((this.__modifierFlags[0] & ' ') != 0) {
                  while (!this.__input._isAtEnd() && this.__input._getValue() != '\n') {
                     this.__input._increment();
                  }

                  if (!this.__input._isAtEnd()) {
                     break;
                  }
               }
            default:
               this.__input._increment();
               doDefault = true;
               break label554;
            case '$':
               this.__getNextChar();
               if ((this.__modifierFlags[0] & '\b') != 0) {
                  offset = this.__emitNode('\u0005');
               } else if ((this.__modifierFlags[0] & 16) != 0) {
                  offset = this.__emitNode('\u0006');
               } else {
                  offset = this.__emitNode('\u0004');
               }
               break label554;
            case '(':
               this.__getNextChar();
               offset = this.__parseExpression(true, flags);
               if (offset == -1) {
                  if ((flags[0] & 8) == 0) {
                     return -1;
                  }
                  break;
               } else {
                  retFlags[0] |= flags[0] & 5;
                  break label554;
               }
            case ')':
            case '|':
               if ((flags[0] & 8) != 0) {
                  retFlags[0] |= 8;
                  return -1;
               }

               throw new MalformedPatternException(
                  ((StringBuffer)(new Object("Error in expression at "))).append(this.__input._toString(this.__input._getOffset())).toString()
               );
            case '*':
            case '+':
            case '?':
               throw new MalformedPatternException("?+* follows nothing in expression");
            case '.':
               this.__getNextChar();
               if ((this.__modifierFlags[0] & 16) != 0) {
                  offset = this.__emitNode('\b');
               } else {
                  offset = this.__emitNode('\u0007');
               }

               this.__cost++;
               retFlags[0] |= 3;
               break label554;
            case '[':
               this.__input._increment();
               offset = this.__parseUnicodeClass();
               retFlags[0] |= 3;
               break label554;
            case '\\':
               value = this.__input._increment();
               switch (value) {
                  case '\u0000':
                  case '\uffff':
                     if (this.__input._isAtEnd()) {
                        throw new MalformedPatternException("Trailing \\ in expression.");
                     }
                  default:
                     doDefault = true;
                     break label554;
                  case '0':
                  case 'a':
                  case 'c':
                  case 'e':
                  case 'f':
                  case 'n':
                  case 'r':
                  case 't':
                  case 'x':
                     doDefault = true;
                     break label554;
                  case '1':
                  case '2':
                  case '3':
                  case '4':
                  case '5':
                  case '6':
                  case '7':
                  case '8':
                  case '9':
                     StringBuffer buffer = (StringBuffer)(new Object(10));
                     int num = 0;

                     for (char var23 = this.__input._getValueRelative(num); Character.isDigit(var23); var23 = this.__input._getValueRelative(++num)) {
                        buffer.append(var23);
                     }

                     try {
                        num = Integer.parseInt(buffer.toString());
                     } catch (Throwable var21) {
                        throw new MalformedPatternException(
                           ((StringBuffer)(new Object("Unexpected number format exception.  Please report this bug.NumberFormatException message: ")))
                              .append(e.getMessage())
                              .toString()
                        );
                     }

                     if (num > 9 && num >= this.__numParentheses) {
                        doDefault = true;
                     } else {
                        if (num >= this.__numParentheses) {
                           throw new MalformedPatternException(((StringBuffer)(new Object("Invalid backreference: \\"))).append(num).toString());
                        }

                        this.__sawBackreference = true;
                        offset = this.__emitArgNode('\u001a', (char)num);
                        retFlags[0] |= 1;
                        value = this.__input._getValue();

                        while (Character.isDigit(value)) {
                           value = this.__input._increment();
                        }

                        this.__input._decrement();
                        this.__getNextChar();
                     }
                     break label554;
                  case 'A':
                     offset = this.__emitNode('\u0003');
                     retFlags[0] |= 2;
                     this.__getNextChar();
                     break label554;
                  case 'B':
                     offset = this.__emitNode('\u0015');
                     retFlags[0] |= 2;
                     this.__getNextChar();
                     break label554;
                  case 'D':
                     offset = this.__emitNode('\u0019');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
                  case 'G':
                     offset = this.__emitNode('\u001e');
                     retFlags[0] |= 2;
                     this.__getNextChar();
                     break label554;
                  case 'S':
                     offset = this.__emitNode('\u0017');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
                  case 'W':
                     offset = this.__emitNode('\u0013');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
                  case 'Z':
                     offset = this.__emitNode('\u0006');
                     retFlags[0] |= 2;
                     this.__getNextChar();
                     break label554;
                  case 'b':
                     offset = this.__emitNode('\u0014');
                     retFlags[0] |= 2;
                     this.__getNextChar();
                     break label554;
                  case 'd':
                     offset = this.__emitNode('\u0018');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
                  case 's':
                     offset = this.__emitNode('\u0016');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
                  case 'w':
                     offset = this.__emitNode('\u0012');
                     retFlags[0] |= 3;
                     this.__getNextChar();
                     break label554;
               }
            case '^':
               this.__getNextChar();
               if ((this.__modifierFlags[0] & '\b') != 0) {
                  offset = this.__emitNode('\u0002');
               } else if ((this.__modifierFlags[0] & 16) != 0) {
                  offset = this.__emitNode('\u0003');
               } else {
                  offset = this.__emitNode('\u0001');
               }
               break label554;
         }
      }

      if (doDefault) {
         offset = this.__emitNode('\u000e');
         this.__emitCode('\uffff');
         int length = 0;
         int pOffset = this.__input._getOffset() - 1;

         label510:
         for (int maxOffset = this.__input._getLength(); length < 127 && pOffset < maxOffset; length++) {
            int lastOffset;
            char ender;
            label582: {
               lastOffset = pOffset;
               char var25 = this.__input._getValue(pOffset);
               switch (var25) {
                  case '#':
                     if ((this.__modifierFlags[0] & ' ') != 0) {
                        while (pOffset < maxOffset && this.__input._getValue(pOffset) != '\n') {
                           pOffset++;
                        }
                     }
                  case '\t':
                  case '\n':
                  case '\u000b':
                  case '\f':
                  case '\r':
                  case ' ':
                     if ((this.__modifierFlags[0] & ' ') != 0) {
                        pOffset++;
                        length--;
                        continue;
                     }
                     break;
                  case '$':
                  case '(':
                  case ')':
                  case '.':
                  case '[':
                  case '^':
                  case '|':
                     break label510;
                  case '\\':
                     var25 = this.__input._getValue(++pOffset);
                     switch (var25) {
                        case '\u0000':
                        case '\uffff':
                           if (pOffset >= maxOffset) {
                              throw new MalformedPatternException("Trailing \\ in expression.");
                           }
                        default:
                           ender = this.__input._getValue(pOffset++);
                           break label582;
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
                           boolean doOctal = false;
                           var25 = this.__input._getValue(pOffset);
                           if (var25 == '0') {
                              doOctal = true;
                           }

                           var25 = this.__input._getValue(pOffset + 1);
                           if (Character.isDigit(var25)) {
                              StringBuffer buffer = (StringBuffer)(new Object(10));
                              int num = pOffset;

                              for (char var29 = this.__input._getValue(num); Character.isDigit(var29); var29 = this.__input._getValue(++num)) {
                                 buffer.append(var29);
                              }

                              try {
                                 num = Integer.parseInt(buffer.toString());
                              } catch (Throwable var20) {
                                 throw new MalformedPatternException(
                                    ((StringBuffer)(new Object("Unexpected number format exception.  Please report this bug.NumberFormatException message: ")))
                                       .append(e.getMessage())
                                       .toString()
                                 );
                              }

                              if (!doOctal) {
                                 doOctal = num >= this.__numParentheses;
                              }
                           }

                           if (!doOctal) {
                              pOffset--;
                              break label510;
                           }

                           int[] var38 = new int[1];
                           ender = (char)__parseOctal(this.__input._array, pOffset, 3, var38);
                           pOffset += var38[0];
                           break label582;
                        case 'A':
                        case 'B':
                        case 'D':
                        case 'G':
                        case 'S':
                        case 'W':
                        case 'Z':
                        case 'b':
                        case 'd':
                        case 's':
                        case 'w':
                           pOffset--;
                           break label510;
                        case 'a':
                           ender = '\u0007';
                           pOffset++;
                           break label582;
                        case 'c':
                           int var10001 = ++pOffset;
                           pOffset++;
                           ender = this.__input._getValue(var10001);
                           if (Character.isLowerCase(ender)) {
                              ender = Character.toUpperCase(ender);
                           }

                           ender = (char)(ender ^ 64);
                           break label582;
                        case 'e':
                           ender = '\u001b';
                           pOffset++;
                           break label582;
                        case 'f':
                           ender = '\f';
                           pOffset++;
                           break label582;
                        case 'n':
                           ender = '\n';
                           pOffset++;
                           break label582;
                        case 'r':
                           ender = '\r';
                           pOffset++;
                           break label582;
                        case 't':
                           ender = '\t';
                           pOffset++;
                           break label582;
                        case 'u':
                           int[] var37 = new int[1];
                           ender = (char)__parseHex(this.__input._array, ++pOffset, 4, var37);
                           if (var37[0] == 4) {
                              pOffset += 4;
                           } else {
                              ender = 'u';
                           }
                           break label582;
                        case 'v':
                           ender = '\u000b';
                           pOffset++;
                           break label582;
                        case 'x':
                           int[] numLength = new int[1];
                           ender = (char)__parseHex(this.__input._array, ++pOffset, 2, numLength);
                           pOffset += numLength[0];
                           break label582;
                     }
               }

               ender = this.__input._getValue(pOffset++);
            }

            if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(ender)) {
               ender = Character.toLowerCase(ender);
            }

            if (pOffset < maxOffset && __isComplexRepetitionOp(this.__input._array, pOffset)) {
               if (length > 0) {
                  pOffset = lastOffset;
               } else {
                  length++;
                  this.__emitCode(ender);
               }
               break;
            }

            this.__emitCode(ender);
         }

         this.__input._setOffset(pOffset - 1);
         this.__getNextChar();
         if (length < 0) {
            throw new MalformedPatternException("Unexpected compilation failure.  Please report this bug!");
         }

         if (length > 0) {
            retFlags[0] |= 1;
         }

         if (length == 1) {
            retFlags[0] |= 2;
         }

         if (this.__program != null) {
            this.__program[OpCode._getOperand(offset)] = (char)length;
         }

         this.__emitCode('\uffff');
      }

      return offset;
   }

   private final int __parseUnicodeClass() {
      boolean range = false;
      char lastclss = '\uffff';
      int[] numLength = new int[]{0, -804650956, 0, 0};
      boolean[] negFlag = new boolean[]{false};
      int offset;
      if (this.__input._getValue() == '^') {
         offset = this.__emitNode('$');
         this.__input._increment();
      } else {
         offset = this.__emitNode('#');
      }

      char clss = this.__input._getValue();
      boolean skipTest;
      if (clss != ']' && clss != '-') {
         skipTest = false;
      } else {
         skipTest = true;
      }

      while (!this.__input._isAtEnd() && (clss = this.__input._getValue()) != ']' || skipTest) {
         skipTest = false;
         boolean opcodeFlag = false;
         this.__input._increment();
         if (clss == '\\' || clss == '[') {
            if (clss == '\\') {
               clss = this.__input._postIncrement();
            } else {
               char posixOpCode = this.__parsePOSIX(negFlag);
               if (posixOpCode != 0) {
                  opcodeFlag = true;
                  clss = posixOpCode;
               }
            }

            if (!opcodeFlag) {
               switch (clss) {
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
                     clss = (char)__parseOctal(this.__input._array, this.__input._getOffset() - 1, 3, numLength);
                     this.__input._increment(numLength[0] - 1);
                     break;
                  case 'D':
                     opcodeFlag = true;
                     clss = '\u0019';
                     lastclss = '\uffff';
                     break;
                  case 'S':
                     opcodeFlag = true;
                     clss = '\u0017';
                     lastclss = '\uffff';
                     break;
                  case 'W':
                     opcodeFlag = true;
                     clss = '\u0013';
                     lastclss = '\uffff';
                     break;
                  case 'a':
                     clss = '\u0007';
                     break;
                  case 'b':
                     clss = '\b';
                     break;
                  case 'c':
                     clss = this.__input._postIncrement();
                     if (Character.isLowerCase(clss)) {
                        clss = Character.toUpperCase(clss);
                     }

                     clss = (char)(clss ^ 64);
                     break;
                  case 'd':
                     opcodeFlag = true;
                     clss = '\u0018';
                     lastclss = '\uffff';
                     break;
                  case 'e':
                     clss = '\u001b';
                     break;
                  case 'f':
                     clss = '\f';
                     break;
                  case 'n':
                     clss = '\n';
                     break;
                  case 'r':
                     clss = '\r';
                     break;
                  case 's':
                     opcodeFlag = true;
                     clss = '\u0016';
                     lastclss = '\uffff';
                     break;
                  case 't':
                     clss = '\t';
                     break;
                  case 'u':
                     clss = (char)__parseHex(this.__input._array, this.__input._getOffset(), 4, numLength);
                     if (numLength[0] == 4) {
                        this.__input._increment(numLength[0]);
                     } else {
                        clss = 'u';
                     }
                     break;
                  case 'v':
                     clss = '\u000b';
                     break;
                  case 'w':
                     opcodeFlag = true;
                     clss = '\u0012';
                     lastclss = '\uffff';
                     break;
                  case 'x':
                     clss = (char)__parseHex(this.__input._array, this.__input._getOffset(), 2, numLength);
                     this.__input._increment(numLength[0]);
               }
            }
         }

         if (range) {
            if (lastclss > clss) {
               throw new MalformedPatternException("Invalid [] range in expression.");
            }

            range = false;
         } else {
            lastclss = clss;
            if (!opcodeFlag
               && this.__input._getValue() == '-'
               && this.__input._getOffset() + 1 < this.__input._getLength()
               && this.__input._getValueRelative(1) != ']') {
               this.__input._increment();
               range = true;
               continue;
            }
         }

         if (lastclss == clss) {
            if (opcodeFlag) {
               if (!negFlag[0]) {
                  this.__emitCode('/');
               } else {
                  this.__emitCode('0');
               }
            } else {
               this.__emitCode('1');
            }

            this.__emitCode(clss);
            if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(clss) && Character.isUpperCase(lastclss)) {
               this.__programSize--;
               this.__emitCode(Character.toLowerCase(clss));
            }
         }

         if (lastclss < clss) {
            this.__emitCode('%');
            this.__emitCode(lastclss);
            this.__emitCode(clss);
            if ((this.__modifierFlags[0] & 1) != 0 && Character.isUpperCase(clss) && Character.isUpperCase(lastclss)) {
               this.__programSize -= 2;
               this.__emitCode(Character.toLowerCase(lastclss));
               this.__emitCode(Character.toLowerCase(clss));
            }

            lastclss = '\uffff';
            range = false;
         }

         lastclss = clss;
      }

      if (this.__input._getValue() != ']') {
         throw new MalformedPatternException("Unmatched [] in expression.");
      }

      this.__getNextChar();
      this.__emitCode('\u0000');
      return offset;
   }

   private final char __parsePOSIX(boolean[] negFlag) {
      int offset = this.__input._getOffset();
      int len = this.__input._getLength();
      int pos = offset;
      char value = this.__input._getValue(pos++);
      if (value != ':') {
         return '\u0000';
      }

      if (this.__input._getValue(pos) == '^') {
         negFlag[0] = true;
         pos++;
      } else {
         negFlag[0] = false;
      }

      StringBuffer buf = (StringBuffer)(new Object());

      try {
         while ((value = this.__input._getValue(pos++)) != ':' && pos < len) {
            buf.append(value);
         }
      } finally {
         ;
      }

      return (char)(this.__input._getValue(pos++) != ']' ? '\u0000' : '\u0000');
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int __parseBranch(int[] retFlags) {
      boolean nestCheck = false;
      boolean handleRepetition = false;
      int[] flags = new int[]{0, -804650956, 0, 0};
      int min = 0;
      int max = 65535;
      int offset = this.__parseAtom(flags);
      if (offset == -1) {
         if ((flags[0] & 8) != 0) {
            retFlags[0] |= 8;
         }

         return -1;
      } else {
         char operator = this.__input._getValue();
         if (operator == '(' && this.__input._getValueRelative(1) == '?' && this.__input._getValueRelative(2) == '#') {
            while (operator != '\uffff' && operator != ')') {
               operator = this.__input._increment();
            }

            if (operator != '\uffff') {
               this.__getNextChar();
               operator = this.__input._getValue();
            }
         }

         if (operator == '{' && __parseRepetition(this.__input._array, this.__input._getOffset())) {
            int next = this.__input._getOffset() + 1;
            int maxOffset;
            int pos = maxOffset = this.__input._getLength();

            char value;
            for (value = this.__input._getValue(next); Character.isDigit(value) || value == ','; value = this.__input._getValue(++next)) {
               if (value == ',') {
                  if (pos != maxOffset) {
                     break;
                  }

                  pos = next;
               }
            }

            if (value == '}') {
               StringBuffer buffer = (StringBuffer)(new Object(10));
               if (pos == maxOffset) {
                  pos = next;
               }

               this.__input._increment();
               int num = this.__input._getOffset();

               for (char var22 = this.__input._getValue(num); Character.isDigit(var22); var22 = this.__input._getValue(++num)) {
                  buffer.append(var22);
               }

               try {
                  min = Integer.parseInt(buffer.toString());
               } catch (Throwable var20) {
                  throw new MalformedPatternException(
                     ((StringBuffer)(new Object("Unexpected number format exception.  Please report this bug.NumberFormatException message: ")))
                        .append(e.getMessage())
                        .toString()
                  );
               }

               value = this.__input._getValue(pos);
               if (value == ',') {
                  pos++;
               } else {
                  pos = this.__input._getOffset();
               }

               num = pos;
               buffer = (StringBuffer)(new Object(10));

               for (char var24 = this.__input._getValue(num); Character.isDigit(var24); var24 = this.__input._getValue(++num)) {
                  buffer.append(var24);
               }

               try {
                  if (num != pos) {
                     max = Integer.parseInt(buffer.toString());
                  }
               } catch (Throwable var21) {
                  throw new MalformedPatternException(
                     ((StringBuffer)(new Object("Unexpected number format exception.  Please report this bug.NumberFormatException message: ")))
                        .append(e.getMessage())
                        .toString()
                  );
               }

               if (max == 0 && this.__input._getValue(pos) != '0') {
                  max = 65535;
               }

               this.__input._setOffset(next);
               this.__getNextChar();
               nestCheck = true;
               handleRepetition = true;
            }
         }

         if (!nestCheck) {
            handleRepetition = false;
            if (!__isSimpleRepetitionOp(operator)) {
               retFlags[0] = flags[0];
               return offset;
            }

            this.__getNextChar();
            retFlags[0] = operator != '+' ? 4 : 1;
            if (operator == '*' && (flags[0] & 2) != 0) {
               this.__programInsertOperator('\u0010', offset);
               this.__cost += 4;
            } else if (operator == '*') {
               min = 0;
               handleRepetition = true;
            } else if (operator == '+' && (flags[0] & 2) != 0) {
               this.__programInsertOperator('\u0011', offset);
               this.__cost += 3;
            } else if (operator == '+') {
               min = 1;
               handleRepetition = true;
            } else if (operator == '?') {
               min = 0;
               max = 1;
               handleRepetition = true;
            }
         }

         if (handleRepetition) {
            if ((flags[0] & 2) != 0) {
               this.__cost = this.__cost + (2 + this.__cost) / 2;
               this.__programInsertOperator('\n', offset);
            } else {
               this.__cost = this.__cost + 4 + this.__cost;
               this.__programAddTail(offset, this.__emitNode('"'));
               this.__programInsertOperator('\u000b', offset);
               this.__programAddTail(offset, this.__emitNode('\u000f'));
            }

            if (min > 0) {
               retFlags[0] = 1;
            }

            if (max != 0 && max < min) {
               throw new MalformedPatternException(
                  ((StringBuffer)(new Object("Invalid interval {"))).append(min).append(",").append(max).append("}").toString()
               );
            }

            if (this.__program != null) {
               this.__program[offset + 2] = (char)min;
               this.__program[offset + 3] = (char)max;
            }
         }

         if (this.__input._getValue() == '?') {
            this.__getNextChar();
            this.__programInsertOperator('\u001d', offset);
            this.__programAddTail(offset, offset + 2);
         }

         if (__isComplexRepetitionOp(this.__input._array, this.__input._getOffset())) {
            throw new MalformedPatternException("Nested repetitions *?+ in expression");
         } else {
            return offset;
         }
      }
   }

   private final int __parseExpression(boolean isParenthesized, int[] hintFlags) {
      char[] posFlags = new char[]{'\u0000', '\u0000'};
      char[] negFlags = new char[]{'\u0000', '\u0000'};
      int nodeOffset = -1;
      int parenthesisNum = 0;
      int[] flags = new int[]{0, -804650956, 0, 0};
      String modifiers = "iogmsx-";
      char[] modifierFlags = posFlags;
      hintFlags[0] = 1;
      char paren;
      if (!isParenthesized) {
         paren = 0;
      } else {
         paren = 1;
         if (this.__input._getValue() != '?') {
            parenthesisNum = this.__numParentheses++;
            nodeOffset = this.__emitArgNode('\u001b', (char)parenthesisNum);
         } else {
            this.__input._increment();
            char value;
            paren = value = this.__input._postIncrement();
            switch (value) {
               case '!':
               case ':':
               case '=':
                  break;
               case '#':
                  value = this.__input._getValue();

                  while (value != '\uffff' && value != ')') {
                     value = this.__input._increment();
                  }

                  if (value != ')') {
                     throw new MalformedPatternException("Sequence (?#... not terminated");
                  }

                  this.__getNextChar();
                  hintFlags[0] = 8;
                  return -1;
               default:
                  this.__input._decrement();

                  for (value = this.__input._getValue(); value != '\uffff' && modifiers.indexOf(value) != -1; value = this.__input._increment()) {
                     if (value == '-') {
                        modifierFlags = negFlags;
                     } else {
                        __setModifierFlag(modifierFlags, value);
                     }
                  }

                  this.__modifierFlags[0] = (char)(this.__modifierFlags[0] | posFlags[0]);
                  this.__modifierFlags[0] = (char)(this.__modifierFlags[0] & ~negFlags[0]);
                  if (value != ')') {
                     throw new MalformedPatternException(((StringBuffer)(new Object("Sequence (?"))).append(value).append("...) not recognized").toString());
                  }

                  this.__getNextChar();
                  hintFlags[0] = 8;
                  return -1;
            }
         }
      }

      int br = this.__parseAlternation(flags);
      if (br == -1) {
         return -1;
      }

      if (nodeOffset != -1) {
         this.__programAddTail(nodeOffset, br);
      } else {
         nodeOffset = br;
      }

      if ((flags[0] & 1) == 0) {
         hintFlags[0] &= -2;
      }

      for (hintFlags[0] |= flags[0] & 4; this.__input._getValue() == '|'; hintFlags[0] |= flags[0] & 4) {
         this.__getNextChar();
         br = this.__parseAlternation(flags);
         if (br == -1) {
            return -1;
         }

         this.__programAddTail(nodeOffset, br);
         if ((flags[0] & 1) == 0) {
            hintFlags[0] &= -2;
         }
      }

      int ender;
      switch (paren) {
         case '\u0001':
            ender = this.__emitArgNode('\u001c', (char)parenthesisNum);
            break;
         case '!':
         case '=':
            ender = this.__emitNode('!');
            hintFlags[0] &= -2;
            break;
         case ':':
            ender = this.__emitNode('\u000f');
            break;
         default:
            ender = this.__emitNode('\u0000');
      }

      this.__programAddTail(nodeOffset, ender);

      for (int var17 = nodeOffset; var17 != -1; var17 = OpCode._getNext(this.__program, var17)) {
         this.__programAddOperatorTail(var17, ender);
      }

      if (paren == '=') {
         this.__programInsertOperator('\u001f', nodeOffset);
         this.__programAddTail(nodeOffset, this.__emitNode('\u000f'));
      } else if (paren == '!') {
         this.__programInsertOperator(' ', nodeOffset);
         this.__programAddTail(nodeOffset, this.__emitNode('\u000f'));
      }

      if (paren == 0 || !this.__input._isAtEnd() && this.__getNextChar() == ')') {
         if (paren != 0 || this.__input._isAtEnd()) {
            return nodeOffset;
         } else if (this.__input._getValue() == ')') {
            throw new MalformedPatternException("Unmatched parentheses.");
         } else {
            throw new MalformedPatternException("Unreached characters at end of expression.  Please report this bug!");
         }
      } else {
         throw new MalformedPatternException("Unmatched parentheses.");
      }
   }

   @Override
   public final Pattern compile(char[] pattern, int options) {
      int[] flags = new int[]{0, -804650956, 0, 0};
      boolean sawOpen = false;
      boolean sawPlus = false;
      int minLength = 0;
      this.__input = new CharStringPointer(pattern);
      int caseInsensitive = options & 1;
      this.__modifierFlags[0] = (char)options;
      this.__sawBackreference = false;
      this.__numParentheses = 1;
      this.__programSize = 0;
      this.__cost = 0;
      this.__program = null;
      this.__emitCode('\u0000');
      if (this.__parseExpression(false, flags) == -1) {
         throw new MalformedPatternException("Unknown compilation error.");
      }

      if (this.__programSize >= 65534) {
         throw new MalformedPatternException("Expression is too large.");
      }

      this.__program = new char[this.__programSize];
      Perl5Pattern regexp = new Perl5Pattern();
      regexp._program = this.__program;
      regexp._expression = (String)(new Object(pattern));
      this.__input._setOffset(0);
      this.__numParentheses = 1;
      this.__programSize = 0;
      this.__cost = 0;
      this.__emitCode('\u0000');
      if (this.__parseExpression(false, flags) == -1) {
         throw new MalformedPatternException("Unknown compilation error.");
      }

      caseInsensitive = this.__modifierFlags[0] & 1;
      regexp._isExpensive = this.__cost >= 10;
      regexp._startClassOffset = -1;
      regexp._anchor = 0;
      regexp._back = -1;
      regexp._options = options;
      regexp._startString = null;
      regexp._mustString = null;
      String mustString = null;
      String startString = null;
      int scan = 1;
      if (this.__program[OpCode._getNext(this.__program, scan)] == 0) {
         int var23;
         int first = var23 = OpCode._getNextOperator(scan);
         char op = this.__program[first];

         while (true) {
            label239: {
               if (op == 27) {
                  sawOpen = true;
                  if (true) {
                     break label239;
                  }
               }

               if ((op != '\f' || this.__program[OpCode._getNext(this.__program, first)] == '\f')
                  && op != 17
                  && op != 29
                  && (OpCode._opType[op] != '\n' || OpCode._getArg1(this.__program, first) <= 0)) {
                  boolean doItAgain = true;

                  while (doItAgain) {
                     doItAgain = false;
                     op = this.__program[first];
                     if (op == 14) {
                        startString = (String)(new Object(this.__program, OpCode._getOperand(first + 1), this.__program[OpCode._getOperand(first)]));
                     } else if (OpCode._isInArray(op, OpCode._opLengthOne, 2)) {
                        regexp._startClassOffset = first;
                     } else if (op != 20 && op != 21) {
                        if (OpCode._opType[op] == 1) {
                           if (op == 1) {
                              regexp._anchor = 1;
                           } else if (op == 2) {
                              regexp._anchor = 2;
                           } else {
                              regexp._anchor = 3;
                           }

                           first = OpCode._getNextOperator(first);
                           doItAgain = true;
                        } else if (op == 16 && OpCode._opType[this.__program[OpCode._getNextOperator(first)]] == 7 && (regexp._anchor & 3) != 0) {
                           regexp._anchor = 11;
                           first = OpCode._getNextOperator(first);
                           doItAgain = true;
                        }
                     } else {
                        regexp._startClassOffset = first;
                     }
                  }

                  if (sawPlus && (!sawOpen || !this.__sawBackreference)) {
                     regexp._anchor |= 4;
                  }

                  StringBuffer lastLongest = (StringBuffer)(new Object());
                  StringBuffer longest = (StringBuffer)(new Object());
                  int length = 0;
                  minLength = 0;
                  int curBack = 0;
                  int back = 0;
                  int backmost = 0;

                  while (var23 > 0 && (op = this.__program[var23]) != 0) {
                     if (op == '\f') {
                        if (this.__program[OpCode._getNext(this.__program, var23)] == '\f') {
                           curBack = -30000;

                           while (this.__program[var23] == '\f') {
                              var23 = OpCode._getNext(this.__program, var23);
                           }
                        } else {
                           var23 = OpCode._getNextOperator(var23);
                        }
                     } else if (op == ' ') {
                        curBack = -30000;
                        var23 = OpCode._getNext(this.__program, var23);
                     } else {
                        if (op != 14) {
                           if (OpCode._isInArray(op, OpCode._opLengthVaries, 0)) {
                              curBack = -30000;
                              length = 0;
                              if (lastLongest.length() > longest.length()) {
                                 longest = lastLongest;
                                 backmost = back;
                              }

                              lastLongest = (StringBuffer)(new Object());
                              if (op == 17 && OpCode._isInArray(this.__program[OpCode._getNextOperator(var23)], OpCode._opLengthOne, 0)) {
                                 minLength++;
                              } else if (OpCode._opType[op] == '\n'
                                 && OpCode._isInArray(this.__program[OpCode._getNextOperator(var23) + 2], OpCode._opLengthOne, 0)) {
                                 minLength += OpCode._getArg1(this.__program, var23);
                              }
                           } else if (OpCode._isInArray(op, OpCode._opLengthOne, 0)) {
                              curBack++;
                              minLength++;
                              length = 0;
                              if (lastLongest.length() > longest.length()) {
                                 longest = lastLongest;
                                 backmost = back;
                              }

                              lastLongest = (StringBuffer)(new Object());
                           }
                        } else {
                           first = var23;

                           int temp;
                           while (this.__program[temp = OpCode._getNext(this.__program, var23)] == 28) {
                              var23 = temp;
                           }

                           minLength += this.__program[OpCode._getOperand(first)];
                           int var27 = this.__program[OpCode._getOperand(first)];
                           if (curBack - back == length) {
                              lastLongest.append((String)(new Object(this.__program, OpCode._getOperand(first) + 1, var27)));
                              length += var27;
                              curBack += var27;
                              first = OpCode._getNext(this.__program, var23);
                           } else if (var27 >= length + (curBack >= 0 ? 1 : 0)) {
                              length = var27;
                              lastLongest = (StringBuffer)(new Object((String)(new Object(this.__program, OpCode._getOperand(first) + 1, var27))));
                              back = curBack;
                              curBack += length;
                              first = OpCode._getNext(this.__program, var23);
                           } else {
                              curBack += var27;
                           }
                        }

                        var23 = OpCode._getNext(this.__program, var23);
                     }
                  }

                  if (lastLongest.length() + (OpCode._opType[this.__program[first]] == 4 ? 1 : 0) > longest.length()) {
                     longest = lastLongest;
                     backmost = back;
                  } else {
                     new Object();
                  }

                  if (longest.length() > 0 && startString == null) {
                     mustString = longest.toString();
                     if (backmost < 0) {
                        backmost = -1;
                     }

                     regexp._back = backmost;
                  } else {
                     longest = null;
                  }
                  break;
               }
            }

            if (op == 17) {
               sawPlus = true;
            } else {
               first += OpCode._operandLength[op];
            }

            first = OpCode._getNextOperator(first);
            op = this.__program[first];
         }
      }

      regexp._isCaseInsensitive = (caseInsensitive & 1) != 0;
      regexp._numParentheses = this.__numParentheses - 1;
      regexp._minLength = minLength;
      if (mustString != null) {
         regexp._mustString = mustString.toCharArray();
         regexp._mustUtility = 100;
      }

      if (startString != null) {
         regexp._startString = startString.toCharArray();
      }

      return regexp;
   }

   @Override
   public final Pattern compile(char[] pattern) {
      return this.compile(pattern, 0);
   }

   @Override
   public final Pattern compile(String pattern) {
      return this.compile(pattern.toCharArray(), 0);
   }

   @Override
   public final Pattern compile(String pattern, int options) {
      return this.compile(pattern.toCharArray(), options);
   }
}
