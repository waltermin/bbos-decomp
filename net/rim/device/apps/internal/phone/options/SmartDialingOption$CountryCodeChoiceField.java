package net.rim.device.apps.internal.phone.options;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.internal.phone.pattern.WorldPhoneInfo;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

class SmartDialingOption$CountryCodeChoiceField extends ObjectChoiceField {
   private int _prevIndex = this.getSelectedIndex();
   private int _typedCC;
   private static final short[] COUNTRY_CODES = WorldPhoneInfo.getCountryCodes();

   public SmartDialingOption$CountryCodeChoiceField(String label, int initialValue) {
      super(label, getChoices(), indexOf(initialValue));
   }

   public int getCountryCode() {
      int index = this.getSelectedIndex();
      return index > 0 ? COUNTRY_CODES[index - 1] : 0;
   }

   @Override
   protected boolean keyControl(char character, int status, int time) {
      char alted = Keypad.getAltedChar(character);
      return Character.isDigit(alted) ? this.keyChar(alted, status, time) : super.keyControl(character, status, time);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      char altedKey = Keypad.getAltedChar(key);
      if (Character.isDigit(altedKey)) {
         this.changeCountryCode(altedKey - '0');
         return true;
      }

      if (Character.isDigit(key)) {
         this.changeCountryCode(key - '0');
         return true;
      }

      if (key == '\n') {
         this._typedCC = 0;
      } else if (key == '\b') {
         this._typedCC /= 10;
      }

      return super.keyChar(key, status, time);
   }

   public void reset(int newCC) {
      int newIndex = indexOf(newCC);
      if (newIndex > 0) {
         this._typedCC = 0;
         this.setSelectedIndex(newIndex);
      }
   }

   private void changeCountryCode(int digit) {
      int newCC;
      if (WorldPhoneInfo.isValidCountryCode(this._typedCC)) {
         newCC = digit;
      } else {
         newCC = this._typedCC * 10 + digit;
      }

      int curCC = this.getCountryCode();
      if (newCC != curCC && newCC != curCC / 10 && newCC != curCC / 100) {
         int newIndex = indexOf(newCC);
         if (newIndex == 0) {
            newIndex = indexOf(newCC * 10);
            if (newIndex == 0) {
               newIndex = indexOf(newCC * 100);
            }
         }

         if (newIndex > 0) {
            this._typedCC = newCC;
            this.setSelectedIndex(newIndex);
         }
      } else {
         this._typedCC = newCC;
      }
   }

   @Override
   protected int moveFocus(int amount, int status, int time) {
      this._typedCC = 0;
      return super.moveFocus(amount, status, time);
   }

   @Override
   protected void onUnfocus() {
      this._typedCC = 0;
      super.onUnfocus();
   }

   @Override
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if (this.getManager() != null) {
         int index = this.getSelectedIndex();
         if (index != this._prevIndex) {
            int cc = index > 0 ? COUNTRY_CODES[index - 1] : 0;
            this.notifyCountryCodeUpdated(cc);
            this._prevIndex = index;
         }
      }
   }

   protected void notifyCountryCodeUpdated(int newCC) {
   }

   private static String[] getChoices() {
      StringBuffer buf = new StringBuffer();
      int count = COUNTRY_CODES.length + 1;
      String[] choices = new String[count];
      choices[0] = PhoneResources.getString(6046);

      for (int index = 1; index < count; index++) {
         buf.setLength(0);
         buf.append('+');
         buf.append(COUNTRY_CODES[index - 1]);
         choices[index] = buf.toString();
      }

      return choices;
   }

   private static int indexOf(int cc) {
      int count = COUNTRY_CODES.length;

      for (int index = 0; index < count; index++) {
         if (COUNTRY_CODES[index] == cc) {
            return index + 1;
         }
      }

      return 0;
   }
}
