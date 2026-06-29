package net.rim.device.apps.internal.blackberryemail.email.emailsetting;

import net.rim.device.api.ui.component.ObjectChoiceField;

final class EmailSettingModelImpl$ObjectChoiceFieldWithId extends ObjectChoiceField {
   int _id;

   public EmailSettingModelImpl$ObjectChoiceFieldWithId(int id, String label, Object[] choices, int defaultIndex) {
      super(label, choices, defaultIndex);
      this._id = id;
   }
}
