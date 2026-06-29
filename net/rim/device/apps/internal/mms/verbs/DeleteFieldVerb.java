package net.rim.device.apps.internal.mms.verbs;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;
import net.rim.device.apps.internal.mms.ui.MMSPresentationField;

public class DeleteFieldVerb extends Verb {
   private Field _field;

   public DeleteFieldVerb(Field field) {
      super(16879616);
      this._field = field;
   }

   @Override
   public String toString() {
      return CommonResources.getString(600);
   }

   @Override
   public Object invoke(Object context) {
      Manager mgr = this._field.getManager();
      mgr.delete(this._field);
      mgr.setDirty(true);

      while (mgr != null) {
         if (mgr instanceof MMSPresentationField) {
            MMSPresentationField presentationField = (MMSPresentationField)mgr;
            presentationField.checkSlideBreaks();
            return null;
         }

         mgr = mgr.getManager();
      }

      return null;
   }

   protected Field getField() {
      return this._field;
   }
}
