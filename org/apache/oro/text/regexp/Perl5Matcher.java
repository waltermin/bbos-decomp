package org.apache.oro.text.regexp;

import java.util.Stack;

public final class Perl5Matcher implements PatternMatcher {
   private boolean __multiline = false;
   private boolean __lastSuccess = false;
   private boolean __caseInsensitive = false;
   private char __previousChar;
   private char[] __input;
   private char[] __originalInput;
   private Perl5Repetition __currentRep;
   private int __numParentheses;
   private int __bol;
   private int __eol;
   private int __currentOffset;
   private int __endOffset;
   private char[] __program;
   private int __expSize;
   private int __inputOffset;
   private int __lastParen;
   private int[] __beginMatchOffsets;
   private int[] __endMatchOffsets;
   private Stack __stack = new Stack();
   private Perl5MatchResult __lastMatchResult = null;
   private int __lastMatchInputEndOffset = -100;
   private static final char __EOS = '\uffff';
   private static final int __INITIAL_NUM_OFFSETS = 20;
   private static final int __DEFAULT_LAST_MATCH_END_OFFSET = -100;

   private static final boolean __compare(char[] s1, int s1Offs, char[] s2, int s2Offs, int n) {
      int cnt = 0;

      while (cnt < n) {
         if (s1Offs >= s1.length) {
            return false;
         }

         if (s2Offs >= s2.length) {
            return false;
         }

         if (s1[s1Offs] != s2[s2Offs]) {
            return false;
         }

         cnt++;
         s1Offs++;
         s2Offs++;
      }

      return true;
   }

   private static final int __findFirst(char[] input, int current, int endOffset, char[] mustString) {
      if (input.length == 0) {
         return endOffset;
      }

      char ch = mustString[0];

      while (current < endOffset) {
         if (ch == input[current]) {
            int saveCurrent = current;

            int count;
            for (count = 0; current < endOffset && count < mustString.length && mustString[count] == input[current]; current++) {
               count++;
            }

            current = saveCurrent;
            if (count >= mustString.length) {
               return current;
            }
         }

         current++;
      }

      return current;
   }

   private final void __pushState(int parenFloor) {
      int stateEntries = 3 * (this.__expSize - parenFloor);
      int[] state;
      if (stateEntries <= 0) {
         state = new int[3];
      } else {
         state = new int[stateEntries + 3];
      }

      state[0] = this.__expSize;
      state[1] = this.__lastParen;
      state[2] = this.__inputOffset;

      for (int paren = this.__expSize; paren > parenFloor; stateEntries -= 3) {
         state[stateEntries] = this.__endMatchOffsets[paren];
         state[stateEntries + 1] = this.__beginMatchOffsets[paren];
         state[stateEntries + 2] = paren--;
      }

      this.__stack.push(state);
   }

   private final void __popState() {
      int[] state = (int[])this.__stack.pop();
      this.__expSize = state[0];
      this.__lastParen = state[1];
      this.__inputOffset = state[2];

      for (int entry = 3; entry < state.length; entry += 3) {
         int paren = state[entry + 2];
         this.__beginMatchOffsets[paren] = state[entry + 1];
         if (paren <= this.__lastParen) {
            this.__endMatchOffsets[paren] = state[entry];
         }
      }

      for (int paren = this.__lastParen + 1; paren <= this.__numParentheses; paren++) {
         if (paren > this.__expSize) {
            this.__beginMatchOffsets[paren] = -1;
         }

         this.__endMatchOffsets[paren] = -1;
      }
   }

   private final boolean isEOLChar(char ch) {
      switch (ch) {
         case '\n':
         case '\r':
         case '\u2028':
         case '\u2029':
            return true;
         default:
            return false;
      }
   }

   private final void __initInterpreterGlobals(Perl5Pattern expression, char[] input, int beginOffset, int endOffset, int currentOffset) {
      this.__caseInsensitive = expression._isCaseInsensitive;
      this.__input = input;
      this.__endOffset = endOffset;
      this.__currentRep = new Perl5Repetition();
      this.__currentRep._numInstances = 0;
      this.__currentRep._lastRepetition = null;
      this.__program = expression._program;
      this.__stack.setSize(0);
      if (currentOffset != beginOffset && currentOffset > 0) {
         this.__previousChar = input[currentOffset - 1];
         if (!this.__multiline && this.isEOLChar(this.__previousChar)) {
            this.__previousChar = 0;
         }
      } else {
         this.__previousChar = '\n';
      }

      this.__numParentheses = expression._numParentheses;
      this.__currentOffset = currentOffset;
      this.__bol = beginOffset;
      this.__eol = endOffset;
      endOffset = this.__numParentheses + 1;
      if (this.__beginMatchOffsets == null || endOffset > this.__beginMatchOffsets.length) {
         if (endOffset < 20) {
            endOffset = 20;
         }

         this.__beginMatchOffsets = new int[endOffset];
         this.__endMatchOffsets = new int[endOffset];
      }
   }

   private final void __setLastMatchResult() {
      int maxEndOffs = 0;
      this.__lastMatchResult = new Perl5MatchResult(this.__numParentheses + 1);
      if (this.__endMatchOffsets[0] > this.__originalInput.length) {
         throw new ArrayIndexOutOfBoundsException();
      }

      for (this.__lastMatchResult._matchBeginOffset = this.__beginMatchOffsets[0]; this.__numParentheses >= 0; this.__numParentheses--) {
         int offs = this.__beginMatchOffsets[this.__numParentheses];
         if (offs >= 0) {
            this.__lastMatchResult._beginGroupOffset[this.__numParentheses] = offs - this.__lastMatchResult._matchBeginOffset;
         } else {
            this.__lastMatchResult._beginGroupOffset[this.__numParentheses] = -1;
         }

         offs = this.__endMatchOffsets[this.__numParentheses];
         if (offs >= 0) {
            this.__lastMatchResult._endGroupOffset[this.__numParentheses] = offs - this.__lastMatchResult._matchBeginOffset;
            if (offs > maxEndOffs && offs <= this.__originalInput.length) {
               maxEndOffs = offs;
            }
         } else {
            this.__lastMatchResult._endGroupOffset[this.__numParentheses] = -1;
         }
      }

      this.__lastMatchResult._match = new String(this.__originalInput, this.__beginMatchOffsets[0], maxEndOffs - this.__beginMatchOffsets[0]);
      this.__originalInput = null;
   }

   private final boolean __interpret(Perl5Pattern expression, char[] input, int beginOffset, int endOffset, int currentOffset) {
      boolean success;
      label381: {
         int minLength = 0;
         int dontTry = 0;
         this.__initInterpreterGlobals(expression, input, beginOffset, endOffset, currentOffset);
         success = false;
         char[] mustString = expression._mustString;
         if (mustString != null && ((expression._anchor & 3) == 0 || (this.__multiline || (expression._anchor & 2) != 0) && expression._back >= 0)) {
            this.__currentOffset = __findFirst(this.__input, this.__currentOffset, endOffset, mustString);
            if (this.__currentOffset >= endOffset) {
               if ((expression._options & 32768) == 0) {
                  expression._mustUtility++;
               }

               success = false;
               break label381;
            }

            if (expression._back >= 0) {
               this.__currentOffset = this.__currentOffset - expression._back;
               if (this.__currentOffset < currentOffset) {
                  this.__currentOffset = currentOffset;
               }

               minLength = expression._back + mustString.length;
            } else if (!expression._isExpensive && (expression._options & 32768) == 0 && --expression._mustUtility < 0) {
               mustString = expression._mustString = null;
               this.__currentOffset = currentOffset;
            } else {
               this.__currentOffset = currentOffset;
               minLength = mustString.length;
            }
         }

         if ((expression._anchor & 3) != 0) {
            if (this.__currentOffset == beginOffset && this.__tryExpression(beginOffset)) {
               success = true;
            } else if (this.__multiline || (expression._anchor & 2) != 0 || (expression._anchor & 8) != 0) {
               if (minLength > 0) {
                  dontTry = minLength - 1;
               }

               endOffset -= dontTry;
               if (this.__currentOffset > currentOffset) {
                  this.__currentOffset--;
               }

               while (this.__currentOffset < endOffset) {
                  if (this.isEOLChar(this.__input[this.__currentOffset++]) && this.__currentOffset < endOffset && this.__tryExpression(this.__currentOffset)) {
                     success = true;
                     break;
                  }
               }
            }
         } else if (expression._startString == null) {
            int offset = expression._startClassOffset;
            if (expression._startClassOffset == -1) {
               if (minLength > 0) {
                  dontTry = minLength - 1;
               }

               endOffset -= dontTry;

               do {
                  if (this.__tryExpression(this.__currentOffset)) {
                     success = true;
                     break;
                  }
               } while (this.__currentOffset++ < endOffset);
            } else {
               boolean doEvery = (expression._anchor & 4) == 0;
               if (minLength > 0) {
                  dontTry = minLength - 1;
               }

               endOffset -= dontTry;
               boolean tmp = true;
               char op;
               label316:
               switch (op = this.__program[offset]) {
                  case '\t':
                     offset = OpCode._getOperand(offset);

                     while (this.__currentOffset < endOffset) {
                        char ch = this.__input[this.__currentOffset];
                        if (ch < 256 && (this.__program[offset + (ch >> 4)] & 1 << (ch & 15)) == 0) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break label316;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0012':
                     while (this.__currentOffset < endOffset) {
                        char ch = this.__input[this.__currentOffset];
                        if (OpCode._isWordCharacter(ch)) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0013':
                     while (this.__currentOffset < endOffset) {
                        char var26 = this.__input[this.__currentOffset];
                        if (!OpCode._isWordCharacter(var26)) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0014':
                     if (minLength > 0) {
                        dontTry++;
                        endOffset--;
                     }

                     if (this.__currentOffset != beginOffset) {
                        char var24 = this.__input[this.__currentOffset - 1];
                        tmp = OpCode._isWordCharacter(var24);
                     } else {
                        tmp = OpCode._isWordCharacter(this.__previousChar);
                     }

                     for (; this.__currentOffset < endOffset; this.__currentOffset++) {
                        char ch = this.__input[this.__currentOffset];
                        if (tmp != OpCode._isWordCharacter(ch)) {
                           tmp = !tmp;
                           if (this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break label316;
                           }
                        }
                     }

                     if ((minLength > 0 || tmp) && this.__tryExpression(this.__currentOffset)) {
                        success = true;
                     }
                     break;
                  case '\u0015':
                     if (minLength > 0) {
                        dontTry++;
                        endOffset--;
                     }

                     if (this.__currentOffset != beginOffset) {
                        char ch = this.__input[this.__currentOffset - 1];
                        tmp = OpCode._isWordCharacter(ch);
                     } else {
                        tmp = OpCode._isWordCharacter(this.__previousChar);
                     }

                     for (; this.__currentOffset < endOffset; this.__currentOffset++) {
                        char ch = this.__input[this.__currentOffset];
                        if (tmp != OpCode._isWordCharacter(ch)) {
                           tmp = !tmp;
                        } else if (this.__tryExpression(this.__currentOffset)) {
                           success = true;
                           break label316;
                        }
                     }

                     if ((minLength > 0 || !tmp) && this.__tryExpression(this.__currentOffset)) {
                        success = true;
                     }
                     break;
                  case '\u0016':
                     while (this.__currentOffset < endOffset) {
                        if (Util.isWhitespace(this.__input[this.__currentOffset])) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0017':
                     while (this.__currentOffset < endOffset) {
                        if (!Util.isWhitespace(this.__input[this.__currentOffset])) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0018':
                     while (this.__currentOffset < endOffset) {
                        if (Character.isDigit(this.__input[this.__currentOffset])) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '\u0019':
                     while (this.__currentOffset < endOffset) {
                        if (!Character.isDigit(this.__input[this.__currentOffset])) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
                     break;
                  case '#':
                  case '$':
                     offset = OpCode._getOperand(offset);

                     while (this.__currentOffset < endOffset) {
                        char ch = this.__input[this.__currentOffset];
                        if (this.__matchUnicodeClass(ch, this.__program, offset, op)) {
                           if (tmp && this.__tryExpression(this.__currentOffset)) {
                              success = true;
                              break;
                           }

                           tmp = doEvery;
                        } else {
                           tmp = true;
                        }

                        this.__currentOffset++;
                     }
               }
            }
         } else {
            mustString = expression._startString;
            if ((expression._anchor & 4) != 0) {
               char ch = mustString[0];

               while (this.__currentOffset < endOffset) {
                  if (ch == this.__input[this.__currentOffset]) {
                     if (this.__tryExpression(this.__currentOffset)) {
                        success = true;
                        break;
                     }

                     this.__currentOffset++;

                     while (this.__currentOffset < endOffset && this.__input[this.__currentOffset] == ch) {
                        this.__currentOffset++;
                     }
                  }

                  this.__currentOffset++;
               }
            } else {
               while ((this.__currentOffset = __findFirst(this.__input, this.__currentOffset, endOffset, mustString)) < endOffset) {
                  if (this.__tryExpression(this.__currentOffset)) {
                     success = true;
                     break;
                  }

                  this.__currentOffset++;
               }
            }
         }
      }

      this.__lastSuccess = success;
      this.__lastMatchResult = null;
      return success;
   }

   private final boolean __matchUnicodeClass(char code, char[] __program, int offset, char opcode) {
      boolean isANYOF = opcode == '#';

      while (__program[offset] != 0) {
         if (__program[offset] == '%') {
            offset++;
            if (code >= __program[offset] && code <= __program[offset + 1]) {
               return isANYOF;
            }

            offset += 2;
         } else if (__program[offset] != '1') {
            isANYOF = __program[offset] == '/' ? isANYOF : !isANYOF;
            int var9 = ++offset;
            offset++;
            switch (__program[var9]) {
               case '\u0012':
                  if (OpCode._isWordCharacter(code)) {
                     return isANYOF;
                  }
                  break;
               case '\u0013':
                  if (!OpCode._isWordCharacter(code)) {
                     return isANYOF;
                  }
                  break;
               case '\u0016':
                  if (Util.isWhitespace(code)) {
                     return isANYOF;
                  }
                  break;
               case '\u0017':
                  if (!Util.isWhitespace(code)) {
                     return isANYOF;
                  }
                  break;
               case '\u0018':
                  if (Character.isDigit(code)) {
                     return isANYOF;
                  }
                  break;
               case '\u0019':
                  if (!Character.isDigit(code)) {
                     return isANYOF;
                  }
                  break;
               case '&':
                  if (Util.isLetter(code)) {
                     return isANYOF;
                  }
                  break;
               case '\'':
                  if (Util.isSpaceChar(code)) {
                     return isANYOF;
                  }
                  break;
               case '(':
                  if (Util.isISOControl(code)) {
                     return isANYOF;
                  }
                  break;
               case '*':
                  if (Character.isLowerCase(code)) {
                     return isANYOF;
                  }

                  if (this.__caseInsensitive && Character.isUpperCase(code)) {
                     return isANYOF;
                  }
                  break;
               case '+':
                  if (Util.isSpaceChar(code)) {
                     return isANYOF;
                  }
               case ')':
                  if (Util.isLetterOrDigit(code)) {
                     return isANYOF;
                  }
                  break;
               case '-':
                  if (Character.isUpperCase(code)) {
                     return isANYOF;
                  }

                  if (this.__caseInsensitive && Character.isLowerCase(code)) {
                     return isANYOF;
                  }
                  break;
               case '.':
                  if (code >= '0' && code <= '9' || code >= 'a' && code <= 'f' || code >= 'A' && code <= 'F') {
                     return isANYOF;
                  }
                  break;
               case '2':
                  if (Util.isLetterOrDigit(code)) {
                     return isANYOF;
                  }
                  break;
               case '3':
                  if (code < 128) {
                     return isANYOF;
                  }
            }
         } else {
            int var10001 = ++offset;
            offset++;
            if (__program[var10001] == code) {
               return isANYOF;
            }
         }
      }

      return !isANYOF;
   }

   private final boolean __tryExpression(int offset) {
      this.__inputOffset = offset;
      this.__lastParen = 0;
      this.__expSize = 0;
      if (this.__numParentheses > 0) {
         for (int count = 0; count <= this.__numParentheses; count++) {
            this.__beginMatchOffsets[count] = -1;
            this.__endMatchOffsets[count] = -1;
         }
      }

      if (this.__match(1)) {
         this.__beginMatchOffsets[0] = offset;
         this.__endMatchOffsets[0] = this.__inputOffset;
         return true;
      } else {
         return false;
      }
   }

   private final int __repeat(int offset, int max) {
      int scan = this.__inputOffset;
      int eol = this.__eol;
      if (max != 65535 && max < eol - scan) {
         eol = scan + max;
      }

      int operand = OpCode._getOperand(offset);
      char op;
      switch (op = this.__program[offset]) {
         case '\u0007':
            while (scan < eol && !this.isEOLChar(this.__input[scan])) {
               scan++;
            }
            break;
         case '\b':
            scan = eol;
            break;
         case '\t':
            char ch;
            if (scan < eol && (ch = this.__input[scan]) < 256) {
               while (ch < 256 && (this.__program[operand + (ch >> 4)] & 1 << (ch & 15)) == 0) {
                  if (++scan >= eol) {
                     break;
                  }

                  ch = this.__input[scan];
               }
            }
            break;
         case '\u000e':
            operand++;

            while (scan < eol && this.__program[operand] == this.__input[scan]) {
               scan++;
            }
            break;
         case '\u0012':
            while (scan < eol && OpCode._isWordCharacter(this.__input[scan])) {
               scan++;
            }
            break;
         case '\u0013':
            while (scan < eol && !OpCode._isWordCharacter(this.__input[scan])) {
               scan++;
            }
            break;
         case '\u0016':
            while (scan < eol && Util.isWhitespace(this.__input[scan])) {
               scan++;
            }
            break;
         case '\u0017':
            while (scan < eol && !Util.isWhitespace(this.__input[scan])) {
               scan++;
            }
            break;
         case '\u0018':
            while (scan < eol && Character.isDigit(this.__input[scan])) {
               scan++;
            }
            break;
         case '\u0019':
            while (scan < eol && !Character.isDigit(this.__input[scan])) {
               scan++;
            }
            break;
         case '#':
         case '$':
            if (scan < eol) {
               for (char ch = this.__input[scan]; this.__matchUnicodeClass(ch, this.__program, operand, op); ch = this.__input[scan]) {
                  if (++scan >= eol) {
                     break;
                  }
               }
            }
      }

      int ret = scan - this.__inputOffset;
      this.__inputOffset = scan;
      return ret;
   }

   private final boolean __match(int offset) {
      boolean inputRemains = true;
      boolean minMod = false;
      int input = this.__inputOffset;
      inputRemains = input < this.__endOffset;
      char nextChar = inputRemains ? this.__input[input] : '\uffff';
      int scan = offset;
      int maxScan = this.__program.length;

      while (scan < maxScan) {
         int next = OpCode._getNext(this.__program, scan);
         char op;
         switch (op = this.__program[scan]) {
            case '\uffff':
            case '\r':
            case '\u000f':
               break;
            case '\u0000':
            case '!':
               this.__inputOffset = input;
               if (this.__inputOffset == this.__lastMatchInputEndOffset) {
                  return false;
               }

               return true;
            case '\u0001':
            default:
               if (input == this.__bol
                  ? !this.isEOLChar(this.__previousChar)
                  : !this.__multiline || !inputRemains && input >= this.__eol || !this.isEOLChar(this.__input[input - 1])) {
                  return false;
               }
               break;
            case '\u0002':
               if (input == this.__bol
                  ? !this.isEOLChar(this.__previousChar)
                  : !inputRemains && input >= this.__eol || !this.isEOLChar(this.__input[input - 1])) {
                  return false;
               }
               break;
            case '\u0003':
               if (input != this.__bol || !this.isEOLChar(this.__previousChar)) {
                  return false;
               }
               break;
            case '\u0004':
               if ((inputRemains || input < this.__eol) && !this.isEOLChar(nextChar)) {
                  return false;
               }

               if (!this.__multiline && this.__eol - input > 1) {
                  return false;
               }
               break;
            case '\u0005':
               if ((inputRemains || input < this.__eol) && !this.isEOLChar(nextChar)) {
                  return false;
               }
               break;
            case '\u0006':
               if ((inputRemains || input < this.__eol) && !this.isEOLChar(nextChar)) {
                  return false;
               }

               if (this.__eol - input > 1) {
                  return false;
               }
               break;
            case '\u0007':
               if (!inputRemains && input >= this.__eol || this.isEOLChar(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\b':
               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\t':
               int var25 = OpCode._getOperand(scan);
               if (nextChar == '\uffff' && inputRemains) {
                  nextChar = this.__input[input];
               }

               if (nextChar >= 256 || (this.__program[var25 + (nextChar >> 4)] & 1 << (nextChar & 15)) != 0) {
                  return false;
               }

               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\n':
            case '\u0010':
            case '\u0011':
               int line;
               int arg;
               if (op == '\n') {
                  line = OpCode._getArg1(this.__program, scan);
                  arg = OpCode._getArg2(this.__program, scan);
                  scan = OpCode._getNextOperator(scan) + 2;
               } else if (op == 16) {
                  line = 0;
                  arg = 65535;
                  scan = OpCode._getNextOperator(scan);
               } else {
                  line = 1;
                  arg = 65535;
                  scan = OpCode._getNextOperator(scan);
               }

               int current;
               if (this.__program[next] == 14) {
                  nextChar = this.__program[OpCode._getOperand(next) + 1];
                  current = 0;
               } else {
                  nextChar = '\uffff';
                  current = -1000;
               }

               this.__inputOffset = input;
               if (minMod) {
                  minMod = false;
                  if (line > 0 && this.__repeat(scan, line) < line) {
                     return false;
                  }

                  while (arg >= line || arg == 65535 && line > 0) {
                     if ((current == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == nextChar) && this.__match(next)) {
                        return true;
                     }

                     this.__inputOffset = input + line;
                     if (this.__repeat(scan, 1) == 0) {
                        return false;
                     }

                     this.__inputOffset = input + ++line;
                  }
               } else {
                  arg = this.__repeat(scan, arg);
                  if (line < arg && OpCode._opType[this.__program[next]] == 4 && (!this.__multiline && this.__program[next] != 5 || this.__program[next] == 6)) {
                     line = arg;
                  }

                  while (arg >= line) {
                     if ((current == -1000 || this.__inputOffset >= this.__endOffset || this.__input[this.__inputOffset] == nextChar) && this.__match(next)) {
                        return true;
                     }

                     this.__inputOffset = input + --arg;
                  }
               }

               return false;
            case '\u000b': {
               Perl5Repetition rep = new Perl5Repetition();
               rep._lastRepetition = this.__currentRep;
               this.__currentRep = rep;
               rep._parenFloor = this.__lastParen;
               rep._numInstances = -1;
               rep._min = OpCode._getArg1(this.__program, scan);
               rep._max = OpCode._getArg2(this.__program, scan);
               rep._scan = OpCode._getNextOperator(scan) + 2;
               rep._next = next;
               rep._minMod = minMod;
               rep._lastLocation = -1;
               this.__inputOffset = input;
               minMod = this.__match(OpCode._getPrevOperator(next));
               this.__currentRep = rep._lastRepetition;
               return minMod;
            }
            case '\f':
               if (this.__program[next] == '\f') {
                  int lastParen = this.__lastParen;

                  do {
                     this.__inputOffset = input;
                     if (this.__match(OpCode._getNextOperator(scan))) {
                        return true;
                     }

                     int var34;
                     for (var34 = this.__lastParen; var34 > lastParen; var34--) {
                        this.__endMatchOffsets[var34] = -1;
                     }

                     this.__lastParen = var34;
                     scan = OpCode._getNext(this.__program, scan);
                  } while (scan != -1 && this.__program[scan] == '\f');

                  return false;
               }

               next = OpCode._getNextOperator(scan);
               break;
            case '\u000e':
               int current = OpCode._getOperand(scan);
               int line = this.__program[current++];
               if (this.__program[current] != nextChar) {
                  return false;
               }

               if (this.__eol - input < line) {
                  return false;
               }

               if (line > 1 && !__compare(this.__program, current, this.__input, input, line)) {
                  return false;
               }

               input += line;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0012':
               if (!inputRemains) {
                  return false;
               }

               if (!OpCode._isWordCharacter(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0013':
               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               if (OpCode._isWordCharacter(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0014':
            case '\u0015':
               boolean a;
               if (input == this.__bol) {
                  a = OpCode._isWordCharacter(this.__previousChar);
               } else {
                  a = OpCode._isWordCharacter(this.__input[input - 1]);
               }

               boolean b = OpCode._isWordCharacter(nextChar);
               if (a == b == (this.__program[scan] == 20)) {
                  return false;
               }
               break;
            case '\u0016':
               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               if (!Util.isWhitespace(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0017':
               if (!inputRemains) {
                  return false;
               }

               if (Util.isWhitespace(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0018':
               if (!Character.isDigit(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u0019':
               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               if (Character.isDigit(nextChar)) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
               break;
            case '\u001a':
               int arg = OpCode._getArg1(this.__program, scan);
               int var21 = this.__beginMatchOffsets[arg];
               if (var21 == -1) {
                  return false;
               }

               if (this.__endMatchOffsets[arg] == -1) {
                  return false;
               }

               if (var21 != this.__endMatchOffsets[arg]) {
                  if (this.__input[var21] != nextChar) {
                     return false;
                  }

                  int var28 = this.__endMatchOffsets[arg] - var21;
                  if (input + var28 > this.__eol) {
                     return false;
                  }

                  if (var28 > 1 && !__compare(this.__input, var21, this.__input, input, var28)) {
                     return false;
                  }

                  input += var28;
                  inputRemains = input < this.__endOffset;
                  nextChar = inputRemains ? this.__input[input] : '\uffff';
               }
               break;
            case '\u001b':
               int var32 = OpCode._getArg1(this.__program, scan);
               this.__beginMatchOffsets[var32] = input;
               if (var32 > this.__expSize) {
                  this.__expSize = var32;
               }
               break;
            case '\u001c':
               int var31 = OpCode._getArg1(this.__program, scan);
               this.__endMatchOffsets[var31] = input;
               if (var31 > this.__lastParen) {
                  this.__lastParen = var31;
               }
               break;
            case '\u001d':
               minMod = true;
               break;
            case '\u001e':
               if (input != this.__bol) {
                  return true;
               }
               break;
            case '\u001f':
               this.__inputOffset = input;
               scan = OpCode._getNextOperator(scan);
               if (!this.__match(scan)) {
                  return false;
               }
               break;
            case ' ':
               this.__inputOffset = input;
               scan = OpCode._getNextOperator(scan);
               if (this.__match(scan)) {
                  return false;
               }
               break;
            case '"': {
               Perl5Repetition rep = this.__currentRep;
               int arg = rep._numInstances + 1;
               this.__inputOffset = input;
               if (input == rep._lastLocation) {
                  this.__currentRep = rep._lastRepetition;
                  int var27 = this.__currentRep._numInstances;
                  if (this.__match(rep._next)) {
                     return true;
                  }

                  this.__currentRep._numInstances = var27;
                  this.__currentRep = rep;
                  return false;
               }

               if (arg < rep._min) {
                  rep._numInstances = arg;
                  rep._lastLocation = input;
                  if (this.__match(rep._scan)) {
                     return true;
                  }

                  rep._numInstances = arg - 1;
                  return false;
               }

               if (rep._minMod) {
                  this.__currentRep = rep._lastRepetition;
                  int line = this.__currentRep._numInstances;
                  if (this.__match(rep._next)) {
                     return true;
                  }

                  this.__currentRep._numInstances = line;
                  this.__currentRep = rep;
                  if (arg >= rep._max) {
                     return false;
                  }

                  this.__inputOffset = input;
                  rep._numInstances = arg;
                  rep._lastLocation = input;
                  if (this.__match(rep._scan)) {
                     return true;
                  }

                  rep._numInstances = arg - 1;
                  return false;
               }

               if (arg < rep._max) {
                  this.__pushState(rep._parenFloor);
                  rep._numInstances = arg;
                  rep._lastLocation = input;
                  if (this.__match(rep._scan)) {
                     return true;
                  }

                  this.__popState();
                  this.__inputOffset = input;
               }

               this.__currentRep = rep._lastRepetition;
               int line = this.__currentRep._numInstances;
               if (this.__match(rep._next)) {
                  return true;
               }

               rep._numInstances = line;
               this.__currentRep = rep;
               rep._numInstances = arg - 1;
               return false;
            }
            case '#':
            case '$':
               int current = OpCode._getOperand(scan);
               if (nextChar == '\uffff' && inputRemains) {
                  nextChar = this.__input[input];
               }

               if (!this.__matchUnicodeClass(nextChar, this.__program, current, op)) {
                  return false;
               }

               if (!inputRemains && input >= this.__eol) {
                  return false;
               }

               input++;
               inputRemains = input < this.__endOffset;
               nextChar = inputRemains ? this.__input[input] : '\uffff';
         }

         scan = next;
      }

      return false;
   }

   public final void setMultiline(boolean multiline) {
      this.__multiline = multiline;
   }

   public final boolean isMultiline() {
      return this.__multiline;
   }

   final char[] _toLower(char[] input) {
      char[] inp = new char[input.length];
      System.arraycopy(input, 0, inp, 0, input.length);
      input = inp;

      for (int current = 0; current < input.length; current++) {
         if (Character.isUpperCase(input[current])) {
            input[current] = Character.toLowerCase(input[current]);
         }
      }

      return input;
   }

   @Override
   public final boolean matchesPrefix(char[] input, Pattern pattern, int offset) {
      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input;
      if (expression._isCaseInsensitive) {
         input = this._toLower(input);
      }

      this.__initInterpreterGlobals(expression, input, 0, input.length, offset);
      this.__lastSuccess = this.__tryExpression(offset);
      this.__lastMatchResult = null;
      return this.__lastSuccess;
   }

   @Override
   public final boolean matchesPrefix(char[] input, Pattern pattern) {
      return this.matchesPrefix(input, pattern, 0);
   }

   @Override
   public final boolean matchesPrefix(String input, Pattern pattern) {
      return this.matchesPrefix(input.toCharArray(), pattern, 0);
   }

   @Override
   public final boolean matchesPrefix(PatternMatcherInput input, Pattern pattern) {
      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input._originalBuffer;
      char[] inp;
      if (expression._isCaseInsensitive) {
         if (input._toLowerBuffer == null) {
            input._toLowerBuffer = this._toLower(this.__originalInput);
         }

         inp = input._toLowerBuffer;
      } else {
         inp = this.__originalInput;
      }

      this.__initInterpreterGlobals(expression, inp, input._beginOffset, input._endOffset, input._currentOffset);
      this.__lastSuccess = this.__tryExpression(input._currentOffset);
      this.__lastMatchResult = null;
      return this.__lastSuccess;
   }

   @Override
   public final boolean matches(char[] input, Pattern pattern) {
      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input;
      if (expression._isCaseInsensitive) {
         input = this._toLower(input);
      }

      this.__initInterpreterGlobals(expression, input, 0, input.length, 0);
      this.__lastSuccess = this.__tryExpression(0) && this.__endMatchOffsets[0] == input.length;
      this.__lastMatchResult = null;
      return this.__lastSuccess;
   }

   @Override
   public final boolean matches(String input, Pattern pattern) {
      return this.matches(input.toCharArray(), pattern);
   }

   @Override
   public final boolean matches(PatternMatcherInput input, Pattern pattern) {
      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input._originalBuffer;
      char[] inp;
      if (expression._isCaseInsensitive) {
         if (input._toLowerBuffer == null) {
            input._toLowerBuffer = this._toLower(this.__originalInput);
         }

         inp = input._toLowerBuffer;
      } else {
         inp = this.__originalInput;
      }

      this.__initInterpreterGlobals(expression, inp, input._beginOffset, input._endOffset, input._beginOffset);
      this.__lastMatchResult = null;
      if (!this.__tryExpression(input._beginOffset)
         || this.__endMatchOffsets[0] != input._endOffset && input.length() != 0 && input._beginOffset != input._endOffset) {
         this.__lastSuccess = false;
         return false;
      } else {
         this.__lastSuccess = true;
         return true;
      }
   }

   @Override
   public final boolean contains(String input, Pattern pattern) {
      return this.contains(input.toCharArray(), pattern);
   }

   @Override
   public final boolean contains(char[] input, Pattern pattern) {
      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input;
      if (expression._isCaseInsensitive) {
         input = this._toLower(input);
      }

      return this.__interpret(expression, input, 0, input.length, 0);
   }

   @Override
   public final boolean contains(PatternMatcherInput input, Pattern pattern) {
      if (input._currentOffset > input._endOffset) {
         return false;
      }

      Perl5Pattern expression = (Perl5Pattern)pattern;
      this.__originalInput = input._originalBuffer;
      this.__originalInput = input._originalBuffer;
      char[] inp;
      if (expression._isCaseInsensitive) {
         if (input._toLowerBuffer == null) {
            input._toLowerBuffer = this._toLower(this.__originalInput);
         }

         inp = input._toLowerBuffer;
      } else {
         inp = this.__originalInput;
      }

      this.__lastMatchInputEndOffset = input.getMatchEndOffset();
      boolean matchFound = this.__interpret(expression, inp, input._beginOffset, input._endOffset, input._currentOffset);
      if (matchFound) {
         input.setCurrentOffset(this.__endMatchOffsets[0]);
         input.setMatchOffsets(this.__beginMatchOffsets[0], this.__endMatchOffsets[0]);
      } else {
         input.setCurrentOffset(input._endOffset + 1);
      }

      this.__lastMatchInputEndOffset = -100;
      return matchFound;
   }

   @Override
   public final MatchResult getMatch() {
      if (!this.__lastSuccess) {
         return null;
      }

      if (this.__lastMatchResult == null) {
         this.__setLastMatchResult();
      }

      return this.__lastMatchResult;
   }
}
