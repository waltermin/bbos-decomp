package net.rim.device.apps.api.utility.framework;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.apps.api.framework.verb.Verb;
import net.rim.device.apps.api.ui.CommonResources;

class FindNextVerb extends Verb {
   FindVerbManager _findVerbManager;

   FindNextVerb(FindVerbManager findVerbManager) {
      super(196640);
      this._findVerbManager = findVerbManager;
   }

   @Override
   public Object invoke(Object context) {
      FieldsSearchResult result = this._findVerbManager._fieldsSearchResult = this._findVerbManager
         ._fieldsSearch
         .searchFields(this._findVerbManager._fieldsSearchResult);
      if (result != null) {
         Field field = result.getField();
         field.setFocus();
         if (field instanceof EditField) {
            ((EditField)field).setCursorPosition(result.getOffset());
            return null;
         }

         if (field instanceof RichTextField) {
            ((RichTextField)field).setCursorPosition(result.getOffset());
         }
      }

      return null;
   }

   @Override
   public String toString() {
      return CommonResources.getString(9024);
   }
}
