package net.rim.device.apps.internal.blackberryemail.header;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.model.ContextObject;
import net.rim.device.apps.api.framework.model.FieldProvider;
import net.rim.device.apps.api.framework.model.RIMModel;
import net.rim.device.apps.api.framework.verb.Verb;

final class EditAddressInUIVerb extends Verb {
   private EmailHeaderModel _model;
   private Verb _verb;
   private Field _field;

   EditAddressInUIVerb(EmailHeaderModel model, Verb verb, Field field) {
      super(verb.getOrdering());
      this._model = model;
      this._verb = verb;
      this._field = field;
   }

   @Override
   public final String toString() {
      return this._verb.toString();
   }

   @Override
   public final Object invoke(Object context) {
      Object result = this._verb.invoke(context);
      if (result != null) {
         Object newContext = new ContextObject(1, 9);
         Field addressField = null;
         RIMModel address = this._model.getInsideModel();
         if (address instanceof FieldProvider) {
            FieldProvider provider = (FieldProvider)address;
            addressField = provider.getField(newContext);
         }

         if (addressField != null) {
            EmailHeaderEditField manager = (EmailHeaderEditField)this._field.getManager();
            manager.insert(addressField, this._field.getIndex());
            manager.delete(this._field);
            manager._insideField = addressField;
            addressField.setFocus();
         }
      }

      return result;
   }
}
