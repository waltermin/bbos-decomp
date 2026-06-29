package net.rim.device.apps.internal.addressbook.addresscard;

import net.rim.device.api.ui.component.EditField;

final class FirstNameEditField extends PersonalNameField {
   private EditField _lastNameField;

   FirstNameEditField(String label, String value, int maxLength, long flags, EditField lastNameField) {
      super(label, value, maxLength, flags);
      this._lastNameField = lastNameField;
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      if (key == '\n') {
         String firstName = this.getText().trim();
         String lastName = this._lastNameField.getText().trim();
         int firstNameLength = firstName.length();
         if (lastName.length() == 0 && firstNameLength > 0) {
            boolean flip = false;
            int index = firstName.indexOf(44);
            if (index != -1) {
               flip = true;
            } else {
               index = firstName.indexOf(32);
            }

            if (index != -1) {
               if (index != firstNameLength - 1) {
                  lastName = firstName.substring(index + 1).trim();
                  lastName = ((StringBuffer)(new Object())).append(Character.toUpperCase(lastName.charAt(0))).append(lastName.substring(1)).toString();
               }

               firstName = ((StringBuffer)(new Object())).append(Character.toUpperCase(firstName.charAt(0))).append(firstName.substring(1, index)).toString();
               if (flip) {
                  String temp = firstName;
                  firstName = lastName;
                  lastName = temp;
               }

               this.setText(firstName);
               this.setDirty(true);
               this._lastNameField.setText(lastName);
               this._lastNameField.setDirty(true);
               this._lastNameField.setFocus();
            }
         }
      }

      return super.keyChar(key, status, time);
   }
}
