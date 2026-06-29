package net.rim.device.internal.ui.component;

import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.text.TextFilter;
import net.rim.device.api.util.WeakReferenceUtilities;
import net.rim.vm.WeakReference;

public class PhoneNumberEditField extends EditField {
   private boolean _performValidation;
   private static WeakReference _validationBufferWR = new WeakReference(null);

   public PhoneNumberEditField(String label, String initialValue) {
      this(label, initialValue, Integer.MAX_VALUE, 6);
   }

   public PhoneNumberEditField(String label, String initialValue, int maxNumChars, int textFilterType) {
      super(label, initialValue, maxNumChars, 2202244481024L);
      this.setFilter(TextFilter.get(textFilterType));
      if (initialValue != null) {
         this._performValidation = super.validate(initialValue);
      } else {
         this._performValidation = true;
      }
   }

   @Override
   protected boolean keyRepeat(int keycode, int time) {
      boolean result = super.keyRepeat(keycode, time);
      if (Keypad.key(keycode) == 261) {
         result = true;
      }

      return result;
   }

   @Override
   protected boolean insert(char key, int status) {
      boolean isFromKeypad = (status & 32768) == 0;
      boolean isAlted = (status & 1) != 0;
      if (!this.validate(key) && isFromKeypad && !isAlted) {
         key = Keypad.getAltedChar(key);
      }

      return super.insert(key, status);
   }

   @Override
   public boolean validate(char character) {
      if (character == 149) {
         return false;
      } else {
         return !this._performValidation ? true : super.validate(character);
      }
   }

   @Override
   public boolean validate(String text) {
      return !this._performValidation ? true : super.validate(text);
   }

   public static String cleanPhoneNumber(String original) {
      if (original == null) {
         return null;
      }

      StringBuffer _validationBuffer = WeakReferenceUtilities.getStringBuffer(_validationBufferWR);
      synchronized (_validationBuffer) {
         return cleanPhoneNumber(original, _validationBuffer) ? _validationBuffer.toString() : original;
      }
   }

   private static boolean validCharacter(char c, int index) {
      if (Character.isDigit(c)) {
         return true;
      } else {
         return c == '+' ? index == 0 : c == '#' || c == '*';
      }
   }

   private static boolean cleanPhoneNumber(String phoneNumber, StringBuffer stringBuffer) {
      boolean invalidCharFound = false;
      synchronized (stringBuffer) {
         int count = phoneNumber.length();
         stringBuffer.setLength(0);

         for (int i = 0; i < count; i++) {
            char c = phoneNumber.charAt(i);
            if (validCharacter(c, i)) {
               stringBuffer.append(c);
            } else {
               invalidCharFound = true;
            }
         }

         return invalidCharFound;
      }
   }
}
