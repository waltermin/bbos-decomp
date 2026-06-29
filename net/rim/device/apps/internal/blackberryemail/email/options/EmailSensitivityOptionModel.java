package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailSensitivityOptionModel implements RIMModel, FieldProvider {
   private EmailMessageModel _message;
   private byte _sensitivity;

   EmailSensitivityOptionModel(Object context) {
      this._message = (EmailMessageModel)ContextObject.get(context, 254);
      this._sensitivity = this._message.getSensitivity();
   }

   @Override
   public final Field getField(Object context) {
      int initialSensitivity;
      switch (this._sensitivity) {
         case 1:
            initialSensitivity = 0;
            break;
         case 2:
         default:
            initialSensitivity = 1;
            break;
         case 3:
            initialSensitivity = 2;
            break;
         case 4:
            initialSensitivity = 3;
      }

      String[] sensitivityChoices = new String[]{
         EmailResources.getString(109), EmailResources.getString(169), EmailResources.getString(170), EmailResources.getString(171)
      };
      ObjectChoiceField sensitivityChoiceField = new ObjectChoiceField(EmailResources.getString(177), sensitivityChoices, initialSensitivity);
      sensitivityChoiceField.setCookie(this);
      return sensitivityChoiceField;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      byte sensitivity;
      switch (((ObjectChoiceField)field).getSelectedIndex()) {
         case 0:
            sensitivity = 1;
            break;
         case 1:
         default:
            sensitivity = 2;
            break;
         case 2:
            sensitivity = 3;
            break;
         case 3:
            sensitivity = 4;
      }

      this._message.setSensitivity(sensitivity);
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 2070;
   }
}
