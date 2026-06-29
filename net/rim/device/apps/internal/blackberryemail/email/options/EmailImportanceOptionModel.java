package net.rim.device.apps.internal.blackberryemail.email.options;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.model.VerbProvider;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.internal.blackberryemail.email.EmailMessageModel;
import net.rim.device.apps.internal.blackberryemail.resources.EmailResources;

final class EmailImportanceOptionModel implements RIMModel, VerbProvider, FieldProvider {
   private EmailMessageModel _message;
   private byte _priority;

   @Override
   public final Field getField(Object context) {
      int initialValue;
      switch (this._priority) {
         case 1:
            initialValue = 1;
            break;
         case 2:
         default:
            initialValue = 2;
            break;
         case 3:
            initialValue = 0;
      }

      String[] importanceChoices = new Object[]{EmailResources.getString(124), EmailResources.getString(122), EmailResources.getString(120)};
      ObjectChoiceField importanceChoiceField = (ObjectChoiceField)(new Object(EmailResources.getString(620), importanceChoices, initialValue));
      importanceChoiceField.setCookie(this);
      return importanceChoiceField;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      ObjectChoiceField objectChoiceField = (ObjectChoiceField)field;
      int index = objectChoiceField.getSelectedIndex();
      byte priority;
      switch (index) {
         case 0:
            priority = 3;
            break;
         case 2:
            priority = 2;
            break;
         default:
            priority = 1;
      }

      this._message.setPriority(priority);
      return true;
   }

   @Override
   public final boolean validate(Field field, Object context) {
      return true;
   }

   @Override
   public final int getOrder(Object context) {
      return 2065;
   }

   EmailImportanceOptionModel(Object context) {
      this._message = (EmailMessageModel)ContextObject.get(context, 254);
      this._priority = this._message.getPriority();
   }
}
