package net.rim.device.apps.games.brickbreaker;

import net.rim.device.api.ui.text.TextFilter;

public final class HighScoreTextFilter extends TextFilter {
   private static char[] specialChars = new char[]{
      '+',
      ' ',
      '!',
      '|',
      '$',
      '%',
      '&',
      '*',
      ',',
      '-',
      '.',
      '/',
      ':',
      '<',
      '>',
      '@',
      '[',
      '\\',
      ']',
      '^',
      '_',
      ';',
      '=',
      '{',
      '}',
      '~',
      '¡',
      '£',
      '¤',
      '¥',
      '§',
      '©',
      '«',
      '®',
      '±',
      '¶',
      '»',
      '\u0000',
      '\u0014',
      '퀄',
      '朮',
      '晩',
      '⸊',
      '牣',
      '\u0a62',
      '戮',
      '湩',
      '⸊',
      '湰',
      '੧',
      '\u0001',
      '퀊',
      '3',
      '\u0000',
      '\u0088',
      '퀄',
      '䥇',
      '㡆',
      '愹',
      '\u001c',
      '\u001c',
      '\u0091',
      '\uff00',
      '\uffff',
      '\u0000',
      '\uff00',
      '\uffff',
      '\u0000',
      '℀',
      'ӹ',
      '\u0001',
      'Ȁ',
      'Ⰰ',
      '\u0000'
   };
   private static char[] specialRange1 = new char[]{'¿', 'ÿ', '\u0004', '퀈'};
   static final HighScoreTextFilter filter = new HighScoreTextFilter();

   @Override
   public final char convert(char character, int status) {
      return this.validate(character) ? character : '\u0000';
   }

   @Override
   public final boolean validate(char character) {
      if (!Character.isDigit(character) && !Character.isLowerCase(character) && !Character.isUpperCase(character)) {
         if (character >= specialRange1[0] && character <= specialRange1[1]) {
            return true;
         }

         for (int i = 0; i < specialChars.length; i++) {
            if (specialChars[i] == character) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public final long getPreferredInputStyle() {
      return 3221258240L;
   }

   static final boolean validate(String text) {
      int length = text.length();

      for (int i = 0; i < length; i++) {
         if (!filter.validate(text.charAt(i))) {
            return false;
         }
      }

      return true;
   }
}
