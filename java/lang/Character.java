package java.lang;

import net.rim.device.api.util.CharacterUtilities;

public final class Character {
   private char value;
   public static final int MIN_RADIX = 2;
   public static final int MAX_RADIX = 36;
   public static final char MIN_VALUE = '\u0000';
   public static final char MAX_VALUE = '\uffff';

   public Character(char value) {
      this.value = value;
   }

   public final char charValue() {
      return this.value;
   }

   @Override
   public final int hashCode() {
      return this.value;
   }

   @Override
   public final boolean equals(Object obj) {
      return !(obj instanceof Character) ? false : ((Character)obj).value == this.value;
   }

   @Override
   public final String toString() {
      char[] buf = new char[]{this.value};
      return String.valueOf(buf);
   }

   public static final boolean isLowerCase(char ch) {
      return CharacterUtilities.isLowerCase(ch);
   }

   public static final boolean isUpperCase(char ch) {
      return CharacterUtilities.isUpperCase(ch);
   }

   public static final boolean isDigit(char ch) {
      return CharacterUtilities.isDigit(ch);
   }

   public static final char toLowerCase(char ch) {
      return CharacterUtilities.isUpperCase(ch) ? CharacterUtilities.toLowerCase(ch) : ch;
   }

   public static final char toUpperCase(char ch) {
      return CharacterUtilities.isLowerCase(ch) ? CharacterUtilities.toUpperCase(ch) : ch;
   }

   public static final int digit(char ch, int radix) {
      int value = -1;
      if (radix >= 2 && radix <= 36) {
         if (isDigit(ch)) {
            value = ch - '0';
         } else if (isUpperCase(ch) || isLowerCase(ch)) {
            value = (ch & 31) + 9;
         }
      }

      return value < radix ? value : -1;
   }
}
