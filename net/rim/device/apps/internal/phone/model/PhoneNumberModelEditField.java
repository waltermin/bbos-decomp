package net.rim.device.apps.internal.phone.model;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.internal.ui.component.PhoneNumberEditField;

public class PhoneNumberModelEditField extends PhoneNumberEditField {
   String[] _phoneNumberTypes;
   private static ResourceBundle _resources = ResourceBundle.getBundle(2699923441625099942L, "net.rim.device.apps.internal.resource.Phone");
   public static final int MAX_CHARS = 80;

   public PhoneNumberModelEditField(PhoneNumberModel model) {
      super(((String[])_resources.getObject(601))[model._type] + ": ", model.getValue(), 80, 6);
      this._phoneNumberTypes = (String[])_resources.getObject(601);
   }

   public PhoneNumberModelEditField(PhoneNumberModel model, int maxNumChars) {
      super(((String[])_resources.getObject(601))[model._type] + ": ", model.getValue(), maxNumChars, 6);
      this._phoneNumberTypes = (String[])_resources.getObject(601);
   }

   public int getPhoneNumberType() {
      String label = this.getLabel();
      String[] phoneNumberTypes = (String[])_resources.getObject(601);
      int index = 0;
      label = label.substring(0, label.length() - 2);

      for (int i = 0; i < phoneNumberTypes.length; i++) {
         if (phoneNumberTypes[i].equals(label)) {
            return i;
         }
      }

      return index;
   }

   @Override
   public int moveFocus(int amount, int status, int time) {
      if ((status & 2) == 0) {
         return super.moveFocus(amount, status, time);
      }

      String currentValue = this.getLabel();
      currentValue = currentValue.substring(0, currentValue.length() - 2);
      int index = 0;
      int length = this._phoneNumberTypes.length;

      for (int i = 0; i < length; i++) {
         if (currentValue.equals(this._phoneNumberTypes[i])) {
            index = i;
            break;
         }
      }

      index = (index + amount) % length;

      while (index < 0) {
         index += length;
      }

      this.setLabel(this._phoneNumberTypes[index] + ": ");
      return 0;
   }
}
