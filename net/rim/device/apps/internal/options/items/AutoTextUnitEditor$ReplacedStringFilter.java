package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.text.LowercaseTextFilter;

final class AutoTextUnitEditor$ReplacedStringFilter extends LowercaseTextFilter {
   @Override
   public final boolean validate(char character) {
      return AutoTextUnitEditor._autoTextEngine.isClauseSeparator(character) || AutoTextUnitEditor._autoTextEngine.isSentenceTerminator(character)
         ? false
         : !Character.isUpperCase(character);
   }
}
