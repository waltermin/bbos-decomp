package net.rim.device.apps.internal.bis.utils;

import net.rim.device.api.ui.text.TextFilter;

public final class PasswordTextFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return !this.validate(character) ? '\u0000' : character;
   }

   @Override
   public final boolean validate(char character) {
      return character >= '!' && character <= '~';
   }
}
