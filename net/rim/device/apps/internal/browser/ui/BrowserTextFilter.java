package net.rim.device.apps.internal.browser.ui;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.vm.Array;

final class BrowserTextFilter extends TextFilter {
   private int _optionalInputLength;
   private char _optionalInputFormat = 'M';
   private char[] _format;
   private boolean[] _literal;
   private int _literalPrefixEnd = -1;
   private BasicEditField _edit;

   BrowserTextFilter(BasicEditField edit, String format) {
      this.buildFormatArrays(format);
      this._edit = edit;
   }

   final char[] getFormat() {
      return this._format;
   }

   final int getLiteralPrefixEnd() {
      return this._literalPrefixEnd;
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final void buildFormatArrays(String format) {
      if (format != null && format.length() != 0) {
         int formatSize = format.length();
         this._format = new char[formatSize];
         this._literal = new boolean[formatSize];
         boolean literalPrefix = true;
         int j = 0;

         for (int i = 0; i < formatSize; i++) {
            char c = format.charAt(i);
            switch (c) {
               case '*':
                  if (i + 1 == formatSize && j - 1 >= 0 && !this._literal[j - 1]) {
                     char defaultFormat = this._format[j - 1];
                     switch (defaultFormat) {
                        case 'A':
                        case 'M':
                        case 'N':
                        case 'X':
                        case 'a':
                        case 'm':
                        case 'n':
                        case 'x':
                           this._optionalInputFormat = defaultFormat;
                           this._optionalInputLength = -1;
                           continue;
                        default:
                           this.setDefaultFormat();
                           return;
                     }
                  } else {
                     if (i != formatSize - 2) {
                        this.setDefaultFormat();
                        return;
                     }

                     this._optionalInputFormat = format.charAt(++i);
                     this._optionalInputLength = -1;
                     switch (this._optionalInputFormat) {
                        case 'A':
                        case 'M':
                        case 'N':
                        case 'X':
                        case 'a':
                        case 'm':
                        case 'n':
                        case 'x':
                           continue;
                        default:
                           this.setDefaultFormat();
                           return;
                     }
                  }
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
                  boolean var9 = false /* VF: Semaphore variable */;

                  try {
                     var9 = true;
                     this._optionalInputLength = Integer.parseInt(format.substring(i, formatSize - 1));
                     i = formatSize - 1;
                     var9 = false;
                  } finally {
                     if (var9) {
                        this.setDefaultFormat();
                        return;
                     }
                  }

                  this._optionalInputFormat = format.charAt(i);
                  switch (this._optionalInputFormat) {
                     case 'A':
                     case 'M':
                     case 'N':
                     case 'X':
                     case 'a':
                     case 'm':
                     case 'n':
                     case 'x':
                        continue;
                     default:
                        this.setDefaultFormat();
                        return;
                  }
               case 'A':
               case 'M':
               case 'N':
               case 'X':
               case 'a':
               case 'm':
               case 'n':
               case 'x':
                  this._format[j] = c;
                  this._literal[j] = false;
                  j++;
                  if (literalPrefix) {
                     literalPrefix = false;
                  }
                  break;
               case '\\':
                  if (i == formatSize - 1) {
                     this.setDefaultFormat();
                     return;
                  }

                  this._format[j] = format.charAt(++i);
                  this._literal[j] = true;
                  j++;
                  if (literalPrefix) {
                     this._literalPrefixEnd++;
                  }
                  break;
               default:
                  this.setDefaultFormat();
                  return;
            }
         }

         if (j == 0) {
            this._format = null;
            this._literal = null;
         } else {
            if (j < this._format.length) {
               Array.resize(this._format, j);
               Array.resize(this._literal, j);
            }
         }
      } else {
         this.setDefaultFormat();
      }
   }

   final boolean isLiteralAtIndex(int index) {
      if (index < 0) {
         return false;
      }

      int requiredFormatLength = 0;
      if (this._literal != null) {
         requiredFormatLength = this._literal.length;
      }

      return index < requiredFormatLength ? this._literal[index] : false;
   }

   final char getFormatAtIndex(int index) {
      if (index < 0) {
         return ' ';
      }

      int requiredFormatLength = 0;
      if (this._format != null) {
         requiredFormatLength = this._format.length;
      }

      if (index < requiredFormatLength) {
         return this._format[index];
      } else {
         return this._optionalInputLength != -1 && (this._optionalInputLength <= 0 || index >= this._optionalInputLength + requiredFormatLength)
            ? ' '
            : this._optionalInputFormat;
      }
   }

   private final boolean validateChar(char c, char format) {
      switch (format) {
         case 'A':
            if (!CharacterUtilities.isUpperCase(c) && !CharacterUtilities.isSymbol(c) && !CharacterUtilities.isPunctuation(c) && c != ' ') {
               return false;
            }

            return true;
         case 'M':
         case 'm':
            if (!CharacterUtilities.isLetter(c)
               && !Character.isDigit(c)
               && !CharacterUtilities.isSymbol(c)
               && !CharacterUtilities.isPunctuation(c)
               && c != ' '
               && c != '\n') {
               return false;
            }

            return true;
         case 'N':
            return Character.isDigit(c);
         case 'X':
            if (!CharacterUtilities.isUpperCase(c)
               && !Character.isDigit(c)
               && !CharacterUtilities.isSymbol(c)
               && !CharacterUtilities.isPunctuation(c)
               && c != ' ') {
               return false;
            }

            return true;
         case 'a':
            if (!CharacterUtilities.isLowerCase(c) && !CharacterUtilities.isSymbol(c) && !CharacterUtilities.isPunctuation(c) && c != ' ') {
               return false;
            }

            return true;
         case 'n':
            if (!Character.isDigit(c) && !CharacterUtilities.isSymbol(c) && !CharacterUtilities.isPunctuation(c) && c != ' ') {
               return false;
            }

            return true;
         case 'x':
            if (!CharacterUtilities.isLowerCase(c)
               && !Character.isDigit(c)
               && !CharacterUtilities.isSymbol(c)
               && !CharacterUtilities.isPunctuation(c)
               && c != ' ') {
               return false;
            }

            return true;
         default:
            return false;
      }
   }

   private final char autoCase(char key, char format) {
      int caseDifference = 32;
      if ((format == 'A' || format == 'X') && key >= 'a' && key <= 'z') {
         return (char)(key - caseDifference);
      } else if ((format == 'a' || format == 'x') && key >= 'A' && key <= 'Z') {
         return (char)(key + caseDifference);
      } else {
         return (format != 'N' && format != 'n' || (key < 'a' || key > 'z') && (key < 'A' || key > 'Z') && key != 137)
               && (format != 'N' || key != ' ' || !InternalServices.isReducedFormFactor())
            ? key
            : Keypad.getAltedChar(key);
      }
   }

   private final boolean validateChar(char character, int index) {
      return this.isLiteralAtIndex(index) ? character == this.getFormatAtIndex(index) : this.validateChar(character, this.getFormatAtIndex(index));
   }

   final boolean validate(String value, int fromIndex, int toIndex, int formatStart) {
      int length = value.length();
      if (fromIndex == toIndex) {
         return true;
      }

      if (fromIndex <= toIndex && fromIndex >= 0 && toIndex <= length) {
         for (int i = fromIndex; i < toIndex; i++) {
            if (!this.validateChar(value.charAt(i), formatStart++)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   final boolean validateString(String value, int fromIndex, boolean treatAsPrefix) {
      if (fromIndex < 0 || fromIndex > value.length()) {
         fromIndex = 0;
      }

      int requiredInputLength = this._format != null ? this._format.length : 0;
      int valueLength = value != null ? value.length() : 0;
      if (!treatAsPrefix && requiredInputLength > valueLength) {
         return false;
      }

      if (!treatAsPrefix && this._optionalInputLength == 0 && requiredInputLength != valueLength) {
         return false;
      }

      if (treatAsPrefix && this._optionalInputLength == 0 && requiredInputLength < valueLength) {
         return false;
      }

      if (this._optionalInputLength > 0 && requiredInputLength + this._optionalInputLength < valueLength) {
         return false;
      }

      for (int i = fromIndex; i < valueLength; i++) {
         char currChar = value.charAt(i);
         if (i < requiredInputLength) {
            if (this._literal[i]) {
               if (currChar != this._format[i]) {
                  return false;
               }
            } else if (!this.validateChar(currChar, this._format[i])) {
               return false;
            }
         } else if (!this.validateChar(currChar, this._optionalInputFormat)) {
            return false;
         }
      }

      return true;
   }

   private final void setDefaultFormat() {
      this._format = null;
      this._literal = null;
      this._literalPrefixEnd = -1;
      this._optionalInputFormat = 'M';
      this._optionalInputLength = -1;
   }

   final boolean validate(char character, int index, boolean deleting) {
      boolean charValid = this.validateChar(character, index);
      if (index != this._edit.getTextLength() && charValid) {
         String text = this._edit.getText();
         return deleting ? this.validate(text, index + 1, text.length(), index + 1) : this.validate(text, index, text.length(), index + 1);
      } else {
         return charValid;
      }
   }

   @Override
   public final char convert(char character, AbstractString text, int position, int status) {
      return this.isLiteralAtIndex(position) ? this.getFormatAtIndex(position) : this.autoCase(character, this.getFormatAtIndex(position));
   }

   @Override
   public final boolean validate(char character, AbstractString text, int position) {
      return this.validate(character, position, false);
   }

   @Override
   public final char convert(char character, int status) {
      int position = this._edit.getCursorPosition();
      return this.isLiteralAtIndex(position) ? this.getFormatAtIndex(position) : this.autoCase(character, this.getFormatAtIndex(position));
   }

   @Override
   public final boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int lv = 0; lv < textLength; lv++) {
            if (!this.validateChar(text.charAt(lv), lv)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public final boolean validate(char character) {
      int position = this._edit.getCursorPosition();
      return this.validate(character, position, false);
   }

   @Override
   public final long getPreferredInputStyle() {
      return 1073741824;
   }

   @Override
   public final int getMaxLength() {
      int requiredInputLength = this._format != null ? this._format.length : 0;
      return this._optionalInputLength >= 0 ? requiredInputLength + this._optionalInputLength : super.getMaxLength();
   }
}
