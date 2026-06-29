package net.rim.device.apps.internal.phone.options;

import net.rim.device.internal.ui.component.PhoneNumberEditField;

final class SmartDialingOption$RestrictedPhoneNumberEditField extends PhoneNumberEditField {
   public SmartDialingOption$RestrictedPhoneNumberEditField(String label, String initialValue) {
      super(label, initialValue, 40, 1);
   }

   @Override
   public final boolean validate(char character) {
      return Character.isDigit(character);
   }
}
