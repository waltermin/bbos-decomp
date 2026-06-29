package net.rim.device.apps.internal.options.items;

import net.rim.device.api.ui.text.UppercaseTextFilter;

class SoftTokensOptionsItem$Base32TextFilter extends UppercaseTextFilter {
   @Override
   public boolean validate(char character) {
      return '0' <= character && character <= '9' || Character.isUpperCase(character);
   }
}
