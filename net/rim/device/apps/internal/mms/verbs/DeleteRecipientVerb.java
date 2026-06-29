package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.ui.RecipientFieldManager;

public final class DeleteRecipientVerb extends Verb {
   private Field _field;
   private RecipientFieldManager _recipientFieldMgr;

   public DeleteRecipientVerb(Field field, RecipientFieldManager recipientListField) {
      super(16879616);
      this._field = field;
      this._recipientFieldMgr = recipientListField;
   }

   @Override
   public final String toString() {
      return CommonResources.getString(600);
   }

   @Override
   public final Object invoke(Object context) {
      Field field = this._field;

      while (field.getManager() != this._recipientFieldMgr) {
         field = field.getManager();
      }

      this._recipientFieldMgr.delete(field);
      this._recipientFieldMgr.setDirty(true);
      return null;
   }
}
