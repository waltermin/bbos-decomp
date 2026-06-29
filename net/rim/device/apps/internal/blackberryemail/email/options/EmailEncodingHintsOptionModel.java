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

final class EmailEncodingHintsOptionModel implements RIMModel, VerbProvider, FieldProvider {
   private EmailMessageModel _message;
   private byte _hints;

   @Override
   public final Field getField(Object context) {
      int initialValue;
      switch (this._hints) {
         case 16:
            initialValue = 1;
            break;
         case 32:
            initialValue = 2;
            break;
         case 48:
            initialValue = 3;
            break;
         case 64:
            initialValue = 4;
            break;
         default:
            initialValue = 0;
      }

      String[] choices = new Object[]{
         EmailResources.getString(117),
         EmailResources.getString(118),
         EmailResources.getString(119),
         EmailResources.getString(121),
         EmailResources.getString(123)
      };
      ObjectChoiceField choiceField = (ObjectChoiceField)(new Object(EmailResources.getString(125), choices, initialValue));
      choiceField.setCookie(this);
      return choiceField;
   }

   @Override
   public final Verb getVerbs(Object context, Verb[] verbs) {
      return null;
   }

   @Override
   public final boolean grabDataFromField(Field field, Object context) {
      ObjectChoiceField objectChoiceField = (ObjectChoiceField)field;
      int index = objectChoiceField.getSelectedIndex();
      byte hints;
      switch (index) {
         case 0:
         default:
            hints = 0;
            break;
         case 1:
            hints = 16;
            break;
         case 2:
            hints = 32;
            break;
         case 3:
            hints = 48;
            break;
         case 4:
            hints = 64;
      }

      byte encoding = (byte)(this._message.getEncoding() & -113);
      this._message.setEncoding((byte)(encoding | hints));
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

   EmailEncodingHintsOptionModel(Object context) {
      this._message = (EmailMessageModel)ContextObject.get(context, 254);
      this._hints = (byte)(this._message.getEncoding() & 112);
   }
}
