package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.internal.ui.component.PhoneNumberEditField;

public final class SmartPhoneNumberEditField extends PhoneNumberEditField {
   private static final int MAX_CHARS;

   public SmartPhoneNumberEditField(String label, String initialValue, Object context) {
      super(label, getTrimmedInitialValue(initialValue, 80), 80, getPreferredFilterType(context));
   }

   private static final String getTrimmedInitialValue(String initialValue, int maxLength) {
      String value = PhoneNumberServices.convertInternalToDisplay(initialValue);
      if (value != null && value.length() > maxLength) {
         value = value.substring(0, maxLength);
      }

      return value;
   }

   private static final int getPreferredFilterType(Object context) {
      int textFilterType = 6;
      if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 68)) {
         return ContextObject.getPrivateFlag(context, 4936088360624690805L, 67) ? 11 : 10;
      }

      if (ContextObject.getPrivateFlag(context, 4936088360624690805L, 80)) {
         return 13;
      }

      if (ContextObject.getFlag(context, 55) || ContextObject.getFlag(context, 74)) {
         textFilterType = 12;
      }

      return textFilterType;
   }

   @Override
   public final void setFilter(TextFilter filter) {
      String preservedText = this.getText(false);
      super.setFilter(filter);
      this.setText(preservedText);
   }

   @Override
   public final int insert(String text) {
      String convertedString = PhoneNumberServices.convertInternalToDisplay(text);
      return super.insert(convertedString);
   }

   @Override
   protected final boolean insert(char key, int status) {
      char displayChar = key;
      if (status != 1) {
         displayChar = PhoneNumberServices.convertCharToDisplay(key);
      }

      return super.insert(displayChar, status);
   }

   @Override
   public final String getText() {
      return this.getText(true);
   }

   public final String getText(boolean convertToInternal) {
      String text = super.getText();
      return convertToInternal ? PhoneNumberServices.convertDisplayToInternal(text) : text;
   }
}
