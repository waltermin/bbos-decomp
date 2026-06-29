package net.rim.device.api.ui.text;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.CharacterUtilities;
import net.rim.device.internal.system.InternalServices;
import net.rim.tid.im.layout.SLKeyLayout;

public class PhoneTextFilter extends TextFilter {
   private boolean _isReducedKeyboard = InternalServices.isReducedFormFactor();
   private final int _style;
   public static final int ACCEPT_PAUSE;
   public static final int ACCEPT_WAIT;
   public static final int ACCEPT_CONTROL;
   public static final int ACCEPT_WILD_CARD;
   public static final int ACCEPT_ALPHA;
   public static final int ACCEPT_FORMATTING;
   public static final int ACCEPT_EXTENSION;
   public static final int ACCEPT_INTERNATIONAL;
   public static final int INTERNATIONAL_MUST_BE_FIRST;
   public static final int ACCEPT_EVERYTHING_EXCEPT_WILD_CARD;

   public PhoneTextFilter() {
      this(247);
   }

   public PhoneTextFilter(int style) {
      this._style = style;
   }

   @Override
   public char convert(char character, int status) {
      if (CharacterUtilities.isUpperCase(character) && this.validate(character)) {
         return character;
      }

      if (!this._isReducedKeyboard || character != ' ' && character != '0') {
         if ((status & 1) == 0) {
            char altCharacter = Keypad.getAltedChar(character);
            if (status == 0 && this.validate(altCharacter)) {
               return altCharacter;
            }

            if (!this.validate(character) && (status & 32768) == 0) {
               if (altCharacter != 0) {
                  character = altCharacter;
               }

               if (character == '!') {
                  character = '\uf402';
               }

               if (character == ',') {
                  character = '\uf3fe';
               }
            }

            return character;
         } else {
            if (character == ' ') {
               return character;
            }

            SLKeyLayout layout = Keypad.getLayout();
            int code = layout.getOriginalKeyCode(character, SLKeyLayout.convertStatusToModifiers(status));
            return Keypad.map(code, 2);
         }
      } else if ((status & 32768) != 0) {
         return character;
      } else {
         return (char)((status & 1) != 0 ? ' ' : '0');
      }
   }

   private static char getNormalChar(char character) {
      return (char)(character == Keypad.getAltedChar('x') ? 'x' : '\u0000');
   }

   @Override
   public boolean validate(char character) {
      character = CharacterUtilities.foldFullWidth(character);
      switch (character) {
         case ' ':
         case '(':
         case ')':
         case '-':
         case '.':
            return this.flagSet(32);
         case '#':
         case '*':
            return this.flagSet(4);
         case '+':
            return this.flagSet(128);
         case '?':
            return this.flagSet(8);
         case 'e':
         case 't':
         case 'x':
            return this.flagSet(64);
         case '\uf3fe':
            return this.flagSet(1);
         case '\uf402':
            return this.flagSet(2);
         default:
            if ('0' <= character && character <= '9') {
               return true;
            } else if ('A' <= character && character <= 'Z') {
               return this.flagSet(16);
            } else {
               return 913 <= character && character <= 937 ? this.flagSet(16) : false;
            }
      }
   }

   private boolean flagSet(int flag) {
      return (this._style & flag) != 0;
   }

   @Override
   public boolean validate(AbstractString text) {
      if (text != null) {
         int textLength = text.length();

         for (int lv = 0; lv < textLength; lv++) {
            char character = text.charAt(lv);
            if (character == '+' && lv != 0 && this.flagSet(256)) {
               return false;
            }

            if (!this.validate(character)) {
               return false;
            }
         }
      }

      return true;
   }

   @Override
   public long getPreferredInputStyle() {
      return 1174405120;
   }
}
