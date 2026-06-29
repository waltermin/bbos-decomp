package net.rim.device.apps.internal.implus;

import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.text.HexadecimalTextFilter;

final class OneWayPagerTextFilter extends HexadecimalTextFilter {
   private EditField _oneWayPagerField;
   private static final EmailWordTextFilter _emailWordTextFilter = new EmailWordTextFilter();

   public OneWayPagerTextFilter(EditField oneWayPagerField) {
      this._oneWayPagerField = oneWayPagerField;
   }

   @Override
   public final char convert(char character, int status) {
      if (character != ' ' && character != '.') {
         String fieldText = this._oneWayPagerField.getText();
         if (fieldText != null) {
            for (int i = fieldText.length() - 1; i >= 0; i--) {
               if (fieldText.charAt(i) == '.' && this._oneWayPagerField.getComposedTextStart() > i) {
                  return _emailWordTextFilter.convert(character, status);
               }
            }
         }

         return super.convert(character, status);
      } else {
         return '.';
      }
   }

   @Override
   public final boolean validate(char character) {
      String fieldText = this._oneWayPagerField.getText();
      boolean foundPeriod = false;
      if (fieldText != null) {
         for (int i = fieldText.length() - 1; i >= 0; i--) {
            char fieldChar = fieldText.charAt(i);
            if (fieldChar == '.') {
               foundPeriod = true;
               if (this._oneWayPagerField.getComposedTextStart() > i) {
                  return _emailWordTextFilter.validate(character);
               }
            }
         }
      }

      return character == '.' ? !foundPeriod : super.validate(character);
   }
}
