package org.apache.oro.text.regexp;

public class Perl5Substitution extends StringSubstitution {
   int _numInterpolations;
   int[] _subOpcodes;
   int _subOpcodesCount;
   char[] _substitutionChars;
   String _lastInterpolation;
   public static final int INTERPOLATE_ALL;
   public static final int INTERPOLATE_NONE;
   private static final int __OPCODE_STORAGE_SIZE;
   private static final int __MAX_GROUPS;
   static final int _OPCODE_COPY;
   static final int _OPCODE_LOWERCASE_CHAR;
   static final int _OPCODE_UPPERCASE_CHAR;
   static final int _OPCODE_LOWERCASE_MODE;
   static final int _OPCODE_UPPERCASE_MODE;
   static final int _OPCODE_ENDCASE_MODE;

   private static final boolean __isInterpolationCharacter(char ch) {
      return Character.isDigit(ch) || ch == '&';
   }

   private void __addElement(int value) {
      int len = this._subOpcodes.length;
      if (this._subOpcodesCount == len) {
         int[] newarray = new int[len + 32];
         System.arraycopy(this._subOpcodes, 0, newarray, 0, len);
         this._subOpcodes = newarray;
      }

      this._subOpcodes[this._subOpcodesCount++] = value;
   }

   private void __parseSubs(String sub) {
      char[] subChars = this._substitutionChars = sub.toCharArray();
      int subLength = subChars.length;
      this._subOpcodes = new int[32];
      this._subOpcodesCount = 0;
      int posParam = 0;
      int offset = -1;
      boolean saveDigits = false;
      boolean escapeMode = false;
      boolean caseMode = false;

      for (int current = 0; current < subLength; current++) {
         char c = subChars[current];
         int next = current + 1;
         if (saveDigits) {
            int digit = Character.digit(c, 10);
            if (digit > -1) {
               if (posParam <= 65535) {
                  posParam *= 10;
                  posParam += digit;
               }

               if (next == subLength) {
                  this.__addElement(posParam);
               }
               continue;
            }

            if (c == '&' && subChars[current - 1] == '$') {
               this.__addElement(0);
               posParam = 0;
               saveDigits = false;
               continue;
            }

            this.__addElement(posParam);
            posParam = 0;
            saveDigits = false;
         }

         if ((c == '$' || c == '\\') && !escapeMode) {
            if (offset >= 0) {
               this.__addElement(current - offset);
               offset = -1;
            }

            if (next != subLength) {
               char nextc = subChars[next];
               if (c == '$') {
                  saveDigits = __isInterpolationCharacter(nextc);
               } else if (c == '\\') {
                  if (nextc == 'l') {
                     if (!caseMode) {
                        this.__addElement(-2);
                        current++;
                     }
                  } else if (nextc == 'u') {
                     if (!caseMode) {
                        this.__addElement(-3);
                        current++;
                     }
                  } else if (nextc == 'L') {
                     this.__addElement(-4);
                     current++;
                     caseMode = true;
                  } else if (nextc == 'U') {
                     this.__addElement(-5);
                     current++;
                     caseMode = true;
                  } else if (nextc == 'E') {
                     this.__addElement(-6);
                     current++;
                     caseMode = false;
                  } else {
                     escapeMode = true;
                  }
               }
            }
         } else {
            escapeMode = false;
            if (offset < 0) {
               offset = current;
               this.__addElement(-1);
               this.__addElement(offset);
            }

            if (next == subLength) {
               this.__addElement(next - offset);
            }
         }
      }
   }

   String _finalInterpolatedSub(MatchResult result) {
      StringBuffer buffer = (StringBuffer)(new Object(10));
      this._calcSub(buffer, result);
      return buffer.toString();
   }

   void _calcSub(StringBuffer buffer, MatchResult result) {
      int[] subOpcodes = this._subOpcodes;
      int caseMode = 0;
      char[] str = this._substitutionChars;
      char[] match = result.group(0).toCharArray();
      int size = this._subOpcodesCount;

      for (int element = 0; element < size; element++) {
         int value = subOpcodes[element];
         int offset;
         int count;
         char[] sub;
         if (value >= 0 && value < result.groups()) {
            offset = result.begin(value);
            if (offset < 0) {
               continue;
            }

            int end = result.end(value);
            if (end < 0) {
               continue;
            }

            int len = result.length();
            if (offset >= len || end > len || offset >= end) {
               continue;
            }

            count = end - offset;
            sub = match;
         } else {
            if (value != -1) {
               if (value != -2 && value != -3) {
                  if (value != -4 && value != -5) {
                     if (value == -6) {
                        caseMode = 0;
                     }
                     continue;
                  }

                  caseMode = value;
                  continue;
               }

               if (caseMode != -4 && caseMode != -5) {
                  caseMode = value;
               }
               continue;
            }

            if (++element >= size) {
               continue;
            }

            offset = subOpcodes[element];
            if (++element >= size) {
               continue;
            }

            count = subOpcodes[element];
            sub = str;
         }

         if (caseMode == -2) {
            buffer.append(Character.toLowerCase(sub[offset++]));
            buffer.append(sub, offset, --count);
            caseMode = 0;
         } else if (caseMode == -3) {
            buffer.append(Character.toUpperCase(sub[offset++]));
            buffer.append(sub, offset, --count);
            caseMode = 0;
         } else if (caseMode == -4) {
            int end = offset + count;

            while (offset < end) {
               buffer.append(Character.toLowerCase(sub[offset++]));
            }
         } else if (caseMode == -5) {
            int end = offset + count;

            while (offset < end) {
               buffer.append(Character.toUpperCase(sub[offset++]));
            }
         } else {
            buffer.append(sub, offset, count);
         }
      }
   }

   public Perl5Substitution() {
      this("", 0);
   }

   public Perl5Substitution(String substitution) {
      this(substitution, 0);
   }

   public Perl5Substitution(String substitution, int numInterpolations) {
      this.setSubstitution(substitution, numInterpolations);
   }

   @Override
   public void setSubstitution(String substitution) {
      this.setSubstitution(substitution, 0);
   }

   public void setSubstitution(String substitution, int numInterpolations) {
      super.setSubstitution(substitution);
      this._numInterpolations = numInterpolations;
      if (numInterpolations == -1 || substitution.indexOf(36) == -1 && substitution.indexOf(92) == -1) {
         this._subOpcodes = null;
      } else {
         this.__parseSubs(substitution);
      }

      this._lastInterpolation = null;
   }

   @Override
   public void appendSubstitution(
      StringBuffer appendBuffer, MatchResult match, int substitutionCount, PatternMatcherInput originalInput, PatternMatcher matcher, Pattern pattern
   ) {
      if (this._subOpcodes == null) {
         super.appendSubstitution(appendBuffer, match, substitutionCount, originalInput, matcher, pattern);
      } else if (this._numInterpolations >= 1 && substitutionCount >= this._numInterpolations) {
         if (substitutionCount == this._numInterpolations) {
            this._lastInterpolation = this._finalInterpolatedSub(match);
         }

         appendBuffer.append(this._lastInterpolation);
      } else {
         this._calcSub(appendBuffer, match);
      }
   }
}
