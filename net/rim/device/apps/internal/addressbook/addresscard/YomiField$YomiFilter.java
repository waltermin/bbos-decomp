package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.text.TextFilter;

final class YomiField$YomiFilter extends TextFilter {
   @Override
   public final char convert(char character, int status) {
      return character;
   }

   @Override
   public final boolean validate(char character) {
      return true;
   }

   @Override
   public final long getPreferredInputStyle() {
      return super.getPreferredInputStyle() | 301989888;
   }
}
