package net.rim.device.api.i18n;

import java.util.Calendar;

public class SimpleDateFormat extends DateFormat {
   private char[] _compiledPattern;
   private int _compiledLength;
   private int _numFields;
   private String _pattern;
   private String _patternResult;
   private DateFormatSymbols _symbols;
   private int _localeCode;
   private boolean _localeFixed;
   private int _style;
   private static final int TEXT_FIELD = 65535;
   private static final int CHAR_FIELD = 65534;
   private static final int NI_FIELD = 65533;

   private SimpleDateFormat(Locale locale, boolean localeFixed) {
      if (locale == null) {
         throw new IllegalArgumentException();
      }

      this._localeFixed = localeFixed;
      this._localeCode = locale.getCode();
      if (this._localeFixed) {
         this._symbols = DateFormatSymbols.getInstance(locale);
      } else {
         this._symbols = DateFormatSymbols.getInstance();
      }
   }

   public SimpleDateFormat(int style) {
      this(Locale.getDefault(), false);
      this.applyPattern(style);
   }

   public SimpleDateFormat(String pattern) {
      this(Locale.getDefault(), false);
      this.applyPattern(pattern);
   }

   public SimpleDateFormat(String pattern, Locale locale) {
      this(locale, true);
      this.applyPattern(pattern);
   }

   public String getPattern() {
      return this._patternResult;
   }

   public void applyPattern(int style) {
      this._pattern = null;
      this._style = style;
      this._patternResult = this._symbols.getPattern(style);
      this.compilePattern(this._patternResult);
   }

   public void applyPattern(String pattern) {
      if (pattern == null) {
         throw new NullPointerException();
      }

      this._patternResult = this._pattern = pattern;
      this.compilePattern(pattern);
   }

   private void checkLocale() {
      if (!this._localeFixed) {
         Locale locale = Locale.getDefault();
         if (locale.getCode() != this._localeCode) {
            DateFormatSymbols.settingChanged();
            this._patternResult = this._pattern;
            if (this._patternResult == null) {
               this._patternResult = this._symbols.getPattern(this._style);
            }

            this.compilePattern(this._patternResult);
         }
      }
   }

   private void compilePattern(String pattern) {
      int patternLen = pattern.length();
      char[] compiledPattern = new char[patternLen * 2];
      int compiledLength = 0;
      int numFields = 0;
      int index = 0;

      while (index < patternLen) {
         int length;
         label59: {
            char ch = pattern.charAt(index);
            int type;
            switch (ch) {
               case '\'':
                  type = 65535;
                  break;
               case 'D':
                  type = 65533;
                  break;
               case 'E':
                  type = 7;
                  break;
               case 'F':
                  type = 65533;
                  break;
               case 'G':
                  type = 65533;
                  break;
               case 'H':
                  type = 11;
                  break;
               case 'M':
                  type = 2;
                  break;
               case 'S':
                  type = 14;
                  break;
               case 'W':
                  type = 65533;
                  break;
               case 'a':
                  type = 9;
                  break;
               case 'd':
                  type = 5;
                  break;
               case 'h':
                  type = 10;
                  break;
               case 'm':
                  type = 12;
                  break;
               case 's':
                  type = 13;
                  break;
               case 'w':
                  type = 65533;
                  break;
               case 'y':
                  type = 1;
                  break;
               case 'z':
                  type = 90;
                  break;
               default:
                  type = 65534;
            }

            compiledPattern[compiledLength++] = (char)type;
            length = 1;
            int saveCompileIndex;
            int actualLength;
            int i;
            switch (type) {
               case 65532:
                  length = findLength(pattern, index, ch);
                  compiledPattern[compiledLength++] = (char)length;
                  numFields++;
                  break label59;
               case 65533:
                  length = findLength(pattern, index, ch);
                  numFields++;
                  break label59;
               case 65534:
                  compiledPattern[compiledLength++] = ch;
                  break label59;
               case 65535:
               default:
                  length = findLength(pattern, index, ch);
                  saveCompileIndex = compiledLength++;
                  actualLength = length >> 1;
                  i = length;
            }

            while (i >= 2) {
               compiledPattern[compiledLength++] = '\'';
               i -= 2;
            }

            if (i == 1) {
               int quoted = pattern.indexOf(39, index + length);
               if (quoted == -1) {
                  quoted = patternLen;
               }

               pattern.getChars(index + length, quoted, compiledPattern, compiledLength);
               quoted -= index + length;
               compiledLength += quoted;
               actualLength += quoted;
               length += quoted + 1;
            }

            compiledPattern[saveCompileIndex] = (char)actualLength;
         }

         index += length;
      }

      this._compiledPattern = compiledPattern;
      this._compiledLength = compiledLength;
      this._numFields = numFields;
   }

   private static int findLength(String s, int offset, char ch) {
      int stringLength = s.length();
      int length = 1;
      offset++;

      while (offset < stringLength && ch == s.charAt(offset)) {
         length++;
         offset++;
      }

      return length;
   }

   @Override
   public StringBuffer format(Calendar calendar, StringBuffer toAppendTo_o, FieldPosition pos_io) {
      this.checkLocale();
      int desiredField = -2;
      if (pos_io != null) {
         desiredField = pos_io.getField();
      }

      char[] compiledPattern = this._compiledPattern;
      int compiledLength = this._compiledLength;
      toAppendTo_o.ensureCapacity(compiledLength);
      if (calendar == null) {
         int index = 0;

         while (index < compiledLength) {
            int field = compiledPattern[index++];
            int startindex = toAppendTo_o.length();
            int length = compiledPattern[index++];
            char undefined = this._symbols.getUndefinedSymbol();
            switch (field) {
               case 1:
               case 2:
               case 5:
               case 7:
               case 9:
               case 10:
               case 11:
               case 12:
               case 13:
               case 14:
               case 90:
                  while (--length >= 0) {
                     toAppendTo_o.append(undefined);
                  }
                  break;
               case 65533:
                  toAppendTo_o.append("NI");
                  break;
               case 65534:
                  toAppendTo_o.append(compiledPattern[index - 1]);
                  break;
               case 65535:
                  toAppendTo_o.append(compiledPattern, index, length);
                  index += length;
            }

            if (field == desiredField && pos_io != null) {
               pos_io.setBeginIndex(startindex);
               pos_io.setEndIndex(toAppendTo_o.length());
               desiredField = -2;
            }
         }
      } else {
         int index = 0;

         while (index < compiledLength) {
            int field = compiledPattern[index++];
            int startindex = toAppendTo_o.length();
            int length = compiledPattern[index++];
            switch (field) {
               case 1:
                  if (length > 3) {
                     toAppendTo_o.append(calendar.get(1));
                  } else if (length == 2) {
                     int shortYear = calendar.get(1) % 100;
                     if (shortYear < 10) {
                        toAppendTo_o.append('0');
                     }

                     toAppendTo_o.append(shortYear);
                  } else {
                     toAppendTo_o.append("NI");
                  }
                  break;
               case 2:
                  int value = calendar.get(2);
                  if (length > 3) {
                     toAppendTo_o.append(this._symbols.getMonths()[value]);
                  } else if (length == 3) {
                     toAppendTo_o.append(this._symbols.getShortMonths()[value]);
                  } else {
                     if (length == 2 && ++value < 10) {
                        toAppendTo_o.append('0');
                     }

                     toAppendTo_o.append(value);
                  }
                  break;
               case 5:
                  int var26 = calendar.get(5);
                  if (length >= 2 && var26 < 10) {
                     toAppendTo_o.append('0');
                  }

                  toAppendTo_o.append(var26);
                  break;
               case 7:
                  int var25 = calendar.get(7);
                  if (length > 3) {
                     toAppendTo_o.append(this._symbols.getWeekdays()[var25 - 1]);
                  } else {
                     toAppendTo_o.append(this._symbols.getShortWeekdays()[var25 - 1]);
                  }
                  break;
               case 9:
                  int var24 = calendar.get(11) < 12 ? 0 : 1;
                  if (length > 1) {
                     toAppendTo_o.append(this._symbols.getAmPmStrings()[var24]);
                  } else {
                     toAppendTo_o.append(this._symbols.getShortAmPmStrings()[var24]);
                  }
                  break;
               case 10:
                  field = 10;
                  int var23 = calendar.get(10);
                  if (var23 == 0) {
                     var23 = 12;
                  }

                  toAppendTo_o.append(var23);
                  break;
               case 11:
                  int var22 = calendar.get(11);
                  if (length >= 2 && var22 < 10) {
                     toAppendTo_o.append('0');
                  }

                  toAppendTo_o.append(var22);
                  break;
               case 12:
                  int var21 = calendar.get(12);
                  if (length >= 2 && var21 < 10) {
                     toAppendTo_o.append('0');
                  }

                  toAppendTo_o.append(var21);
                  break;
               case 13:
                  int var20 = calendar.get(13);
                  if (length >= 2 && var20 < 10) {
                     toAppendTo_o.append('0');
                  }

                  toAppendTo_o.append(var20);
                  break;
               case 14:
                  int var19 = calendar.get(14);
                  if (length >= 3 && var19 < 100) {
                     toAppendTo_o.append('0');
                  }

                  if (length >= 2 && var19 < 10) {
                     toAppendTo_o.append('0');
                  }

                  toAppendTo_o.append(var19);
                  break;
               case 90:
                  toAppendTo_o.append(calendar.getTimeZone().getID());
                  break;
               case 65533:
                  toAppendTo_o.append("NI");
                  break;
               case 65534:
                  toAppendTo_o.append(compiledPattern[index - 1]);
                  break;
               case 65535:
                  toAppendTo_o.append(compiledPattern, index, length);
                  index += length;
            }

            if (field == desiredField && pos_io != null) {
               pos_io.setBeginIndex(startindex);
               pos_io.setEndIndex(toAppendTo_o.length());
               desiredField = -2;
            }
         }
      }

      if (desiredField != -2) {
         pos_io.setBeginIndex(0);
         pos_io.setEndIndex(0);
      }

      return toAppendTo_o;
   }

   @Override
   public int[] getFields() {
      this.checkLocale();
      int[] fields = new int[this._numFields];
      char[] compiledPattern = this._compiledPattern;
      int compiledLength = this._compiledLength;
      int patternIndex = 0;
      int fieldIndex = 0;
      patternIndex = 0;

      while (patternIndex < compiledLength) {
         int type = compiledPattern[patternIndex++];
         switch (type) {
            case 65532:
               fields[fieldIndex++] = type;
               patternIndex++;
               break;
            case 65533:
               fields[fieldIndex++] = -1;
               break;
            case 65534:
               patternIndex++;
               break;
            case 65535:
            default:
               int len = compiledPattern[patternIndex++];
               patternIndex += len;
         }
      }

      return fields;
   }

   public String toPattern() {
      return this._patternResult;
   }
}
